package hu.simon.taps.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import hu.simon.taps.R;
import hu.simon.taps.fragments.SettingsFragment;
import hu.simon.taps.http.handler.AsyncResponse;
import hu.simon.taps.http.handler.OkHttpHandler;
import hu.simon.taps.utils.GameUtil;
import hu.simon.taps.utils.LanguageUtil;
import hu.simon.taps.utils.ScreenUtil;
import hu.simon.taps.utils.ServerUtil;
import hu.simon.taps.utils.VibrationUtil;
import okhttp3.OkHttpClient;

public class GameActivity extends Activity implements AsyncResponse {

  // ------------------------------------DECLARING VARIABLES---------------------------------------
  public static final String BASE_URL =
      ServerUtil.PROTOCOL + ServerUtil.HOSTNAME + ":" + ServerUtil.PORT + "/";

  public OkHttpHandler okHttpHandler;
  public OkHttpClient client;

  TextView feedbackText;
  TextView roomIdText;
  TextView roundText;
  TextView playersText;

  ImageView bustImage;

  Button greenButton;
  Button redButton;
  Button yellowButton;
  Button blueButton;
  Button yourButton;

  Vibrator vibrator;

  ConstraintLayout layout;

  String playerId;
  String roomId;
  String wordPattern = "";

  long numOfPlayers = -1;
  long prevNumOfPlayers = 5;
  long intervalMilli = ServerUtil.WAITING_STATE_REQUEST_INTERVAL;
  long offlineTime = 0;
  long tileId = 0;
  long countdownTime = GameUtil.PREPARING_TIME_SECONDS;

  boolean shown = false;
  boolean exitCondition = false;
  boolean leavable = false;
  boolean counted = false;
  boolean counting = false;
  boolean didColorAnimate = false;

  int colorFrom;
  int colorTo;

  List<Integer> pattern;

  Handler getStateTimerHandler = new Handler();
  Handler timerHandler = new Handler();
  Handler delayHandler = new Handler();
  Handler countdownHandler = new Handler();

  private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.75F);

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    // Changing language
    Configuration mainConfiguration = new Configuration(getResources().getConfiguration());
    getResources().updateConfiguration(LanguageUtil.preferredLanguage(this, mainConfiguration),
        getResources().getDisplayMetrics());

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game);

    ScreenUtil.setFlags(this, this);

    // ----------------------------------FINDING COMPONENTS-----------------------------------------
    feedbackText = findViewById(R.id.feedBackText);
    roomIdText = findViewById(R.id.roomIdText);
    roundText = findViewById(R.id.roundText);
    playersText = findViewById(R.id.playersNumber);
    greenButton = findViewById(R.id.greenButton);
    redButton = findViewById(R.id.redButton);
    yellowButton = findViewById(R.id.yellowButton);
    blueButton = findViewById(R.id.blueButton);
    layout = findViewById(R.id.layout);
    bustImage = findViewById(R.id.bustImage);

    pattern = new ArrayList<>();

    client = new OkHttpClient();

    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    getStateTimerHandler.postDelayed(getStateTimerRunnable, 0);

    leavable = true;

    // Getting Player ID and Room ID from MainActivity
    playerId = getIntent().getStringExtra("EXTRA_PLAYER_ID");
    roomId = getIntent().getStringExtra("EXTRA_ROOM_ID");
    roomIdText.setText(getString(R.string.room_id) + " " + roomId);
  }

  // -----------------------------------GETSTATE REQUEST (REPEATED)---------------------------------
  Runnable getStateTimerRunnable = new Runnable() {

    @Override
    public void run() {

      if (exitCondition) {
        return;
      }

      boolean connected = ServerUtil.connectionCheck(GameActivity.this);

      if (!connected) {

        offlineTime += intervalMilli;
        Toast.makeText(GameActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT)
            .show();
      }

      if (offlineTime >= GameUtil.MAX_OFFLINE_TIME) {

        Log.i("offline", String.valueOf(offlineTime));
        backToMainActivity();
      }

      // Getstate request
      if (connected) {

        offlineTime = 0;
        String url =
            BASE_URL + ServerUtil.Endpoint.STATE.toString() + "/" + roomId + "/" + playerId;

        okHttpHandler = new OkHttpHandler(GameActivity.this, client);
        okHttpHandler.getRequest(url);
      }

      getStateTimerHandler.postDelayed(getStateTimerRunnable, intervalMilli);
    }
  };

  Runnable countdownRunnable = new Runnable() {

    @Override
    public void run() {

      if (counted) {
        feedbackText.setText("");
        return;
      }

      counting = true;
      feedbackText.setText(getString(R.string.prepare) + "\n" + countdownTime);
      countdownTime--;

      if (countdownTime >= 0) {
        countdownHandler.postDelayed(countdownRunnable, 1000);
      }
      if (countdownTime < 0) {
        counted = true;
      }
    }
  };

  @Override
  protected void onPause() {

    super.onPause();

    // No more getstate requests, when exits this activity
    exitCondition = true;
    getStateTimerHandler.removeCallbacks(getStateTimerRunnable);

    countdownHandler.removeCallbacks(countdownRunnable);
  }

  @Override
  public void onRequestComplete(String responseJsonString) {

    JSONObject responseJson = null;
    String status = null;
    String state = null;

    Log.i("GameResponse", responseJsonString);

    try {
      responseJson = new JSONObject(responseJsonString);
      status = responseJson.optString(ServerUtil.ResponseParameter.STATUS.toString());
      state = responseJson.optString(ServerUtil.ResponseParameter.GAME_STATE.toString());

    } catch (JSONException e) {
      e.printStackTrace();
    }

    // -----------------------------------EXAMINING GAME STATES-------------------------------------
    if (!status.equals("OK")) {
      return;
    }

    // WAITING
    if (state.equals(ServerUtil.State.WAITING.toString())) {

      gameWaiting(responseJson);
    }

    // PREPARING
    if (state.equals(ServerUtil.State.PREPARING.toString())) {

      gamePreparing(responseJson);
    }

    // SHOWING_PATTERN
    if (state.equals(ServerUtil.State.SHOWING_PATTERN.toString())) {

      gameShowingPattern(responseJson);
    }

    // PLAYING
    if (state.equals(ServerUtil.State.PLAYING.toString())) {

      gamePlaying();
    }

    // SUCCESSFUL_END
    if (state.equals(ServerUtil.State.END.toString())) {

      gameEnd(responseJson);
      return;
    }

    // LEAVE ROOM
    if (responseJson.optBoolean(ServerUtil.ResponseParameter.LEFT.toString())) {

      backToMainActivity();
    }
  }

  // -----------------------------------------GAME STATES-------------------------------------------
  public void gameWaiting(JSONObject payloadJson) {

    numOfPlayers = payloadJson.optLong(ServerUtil.ResponseParameter.NUMBER_OF_PLAYERS.toString());
    playersText.setText(numOfPlayers + "/4");

    if (numOfPlayers > prevNumOfPlayers) {
      VibrationUtil.preferredVibration(GameActivity.this, vibrator);
    }

    prevNumOfPlayers = numOfPlayers;
  }

  public void gamePreparing(JSONObject payloadJson) {

    leavable = false;
    bustImage.setVisibility(View.INVISIBLE);
    playersText.setVisibility(View.INVISIBLE);

    if (!counting) {
      countdownHandler.postDelayed(countdownRunnable, 0);
    }

    roomIdText.setVisibility(View.INVISIBLE);

    final ConstraintLayout layout = findViewById(R.id.layout);

    intervalMilli = ServerUtil.GAME_STATE_REQUEST_INTERVAL;

    tileId = payloadJson.optLong(ServerUtil.ResponseParameter.TILE_ID.toString());

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String backgroundState = prefs.getString(SettingsFragment.PREF_BACKGROUND_COLOR, "dark");

    setBackground(backgroundState, tileId);

    yourButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        youOnClick(v);
      }
    });

    yourButton.setEnabled(false);
  }

  public void setBackground(String backgroundState, Long tileId) {

    if (backgroundState.equals("dark")) {

      if (tileId == 1) {

        yourButton = findViewById(R.id.greenButton);
        colorFrom = getResources().getColor(R.color.colorPrimary, null);
        colorTo = getResources().getColor(R.color.green_bg, null);

        feedbackText.setTextColor(Color.WHITE);
        roundText.setTextColor(Color.WHITE);
      }
      if (tileId == 2) {

        yourButton = findViewById(R.id.redButton);
        colorFrom = getResources().getColor(R.color.colorPrimary, null);
        colorTo = getResources().getColor(R.color.red_bg, null);

        feedbackText.setTextColor(Color.WHITE);
        roundText.setTextColor(Color.WHITE);
      }
      if (tileId == 3) {

        yourButton = findViewById(R.id.yellowButton);
        colorFrom = getResources().getColor(R.color.colorPrimary, null);
        colorTo = getResources().getColor(R.color.yellow_bg, null);

        feedbackText.setTextColor(Color.WHITE);
        roundText.setTextColor(Color.WHITE);
      }
      if (tileId == 4) {

        yourButton = findViewById(R.id.blueButton);
        colorFrom = getResources().getColor(R.color.colorPrimary, null);
        colorTo = getResources().getColor(R.color.blue_bg, null);

        feedbackText.setTextColor(Color.WHITE);
        roundText.setTextColor(Color.WHITE);
      }

      if (!didColorAnimate) {

        didColorAnimate = true;

        ValueAnimator colorAnimation =
                ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(GameUtil.ANIMATE_DURATION); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

          @Override
          public void onAnimationUpdate(ValueAnimator animator) {
            layout.setBackgroundColor((int) animator.getAnimatedValue());
          }

        });

        colorAnimation.start();
      }
    }

    if (backgroundState.equals("light")) {

      if (tileId == 1) {

        yourButton = findViewById(R.id.greenButton);
        colorFrom = getResources().getColor(R.color.colorPrimary, null);
        colorTo = getResources().getColor(R.color.green_pale, null);

        feedbackText.setTextColor(Color.BLACK);
        roundText.setTextColor(Color.BLACK);
      }
      if (tileId == 2) {

        yourButton = findViewById(R.id.redButton);
        colorFrom = getResources().getColor(R.color.colorPrimary, null);
        colorTo = getResources().getColor(R.color.red_pale, null);

        feedbackText.setTextColor(Color.BLACK);
        roundText.setTextColor(Color.BLACK);
      }
      if (tileId == 3) {

        yourButton = findViewById(R.id.yellowButton);
        colorFrom = getResources().getColor(R.color.colorPrimary, null);
        colorTo = getResources().getColor(R.color.yellow_pale, null);

        feedbackText.setTextColor(Color.BLACK);
        roundText.setTextColor(Color.BLACK);
      }
      if (tileId == 4) {

        yourButton = findViewById(R.id.blueButton);
        colorFrom = getResources().getColor(R.color.colorPrimary, null);
        colorTo = getResources().getColor(R.color.blue_pale, null);

        feedbackText.setTextColor(Color.BLACK);
        roundText.setTextColor(Color.BLACK);
      }

      if (!didColorAnimate) {

        didColorAnimate = true;

        ValueAnimator colorAnimation =
                ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(GameUtil.ANIMATE_DURATION); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

          @Override
          public void onAnimationUpdate(ValueAnimator animator) {
            layout.setBackgroundColor((int) animator.getAnimatedValue());
          }

        });

        colorAnimation.start();
      }
    }
  }

  @SuppressLint("SetTextI18n")
  public void gameShowingPattern(JSONObject payloadJson) {

    feedbackText.setVisibility(View.INVISIBLE);
    yourButton.setEnabled(false);

    if (!shown) {

      wordPattern = payloadJson.optString(ServerUtil.ResponseParameter.PATTERN.toString());
      pattern.clear();

      for (int i = 0; i < wordPattern.length(); i++) {
        pattern.add(Integer.valueOf(String.valueOf(wordPattern.charAt(i))));
      }

      timerHandler.postDelayed(new Runnable() {

        @Override
        public void run() {
          displayPattern();
        }
      }, GameUtil.DELAY_BETWEEN_ROUNDS);

      roundText.setText(getString(R.string.round) + ": " + wordPattern.length());

      shown = true;
    }
  }

  public void displayPattern() {

    greenButton.setBackgroundResource(R.drawable.button_green);
    redButton.setBackgroundResource(R.drawable.button_red);
    yellowButton.setBackgroundResource(R.drawable.button_yellow);
    blueButton.setBackgroundResource(R.drawable.button_blue);

    Runnable timerRunnable = new Runnable() {

      Integer counter = pattern.size();

      @Override
      public void run() {

        Integer current = pattern.remove(0);
        counter--;

        VibrationUtil.preferredVibration(GameActivity.this, vibrator);

        if (current == 1) {

          greenButton.setBackgroundResource(R.drawable.button_green_active);
          delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              greenButton.setBackgroundResource(R.drawable.button_green);
            }
          }, GameUtil.FLASH_DURATION);
        }
        if (current == 2) {

          redButton.setBackgroundResource(R.drawable.button_red_active);
          delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              redButton.setBackgroundResource(R.drawable.button_red);
            }
          }, GameUtil.FLASH_DURATION);
        }
        if (current == 3) {

          yellowButton.setBackgroundResource(R.drawable.button_yellow_active);
          delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              yellowButton.setBackgroundResource(R.drawable.button_yellow);
            }
          }, GameUtil.FLASH_DURATION);
        }
        if (current == 4) {

          blueButton.setBackgroundResource(R.drawable.button_blue_active);
          delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              blueButton.setBackgroundResource(R.drawable.button_blue);
            }
          }, GameUtil.FLASH_DURATION);
        }
        if (counter <= 0) {

          startGame();
          return;
        }

        timerHandler.postDelayed(this, GameUtil.FLASH_DURATION + GameUtil.DELAY_BETWEEN_FLASHES);
      }
    };

    timerHandler.postDelayed(timerRunnable, 0);
  }

  public void startGame() {

    String url = BASE_URL + ServerUtil.Endpoint.START.toString();

    JSONObject payloadJson = new JSONObject();

    try {
      payloadJson.put(ServerUtil.RequestParameter.ROOM_ID.toString(), roomId);
      payloadJson.put(ServerUtil.RequestParameter.PLAYER_ID.toString(), playerId);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    okHttpHandler = new OkHttpHandler(this, client);
    okHttpHandler.postRequest(url, payloadJson);
  }

  public void gamePlaying() {

    feedbackText.setVisibility(View.VISIBLE);
    feedbackText.setText(getString(R.string.round_started));

    yourButton.setEnabled(true);

    shown = false;
  }

  // OWN BUTTON CLICKED
  public void youOnClick(View v) {

    v.startAnimation(buttonClick);
    VibrationUtil.preferredVibration(GameActivity.this, vibrator);

    Log.i("press", "true");

    String url = BASE_URL + ServerUtil.Endpoint.GAME.toString();

    JSONObject payloadJson = new JSONObject();

    try {
      payloadJson.put(ServerUtil.RequestParameter.ROOM_ID.toString(), roomId);
      payloadJson.put(ServerUtil.RequestParameter.PLAYER_ID.toString(), playerId);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    okHttpHandler = new OkHttpHandler(this, client);
    okHttpHandler.postRequest(url, payloadJson);
  }

  public void leaveRoomOnClick() {

    VibrationUtil.preferredVibration(GameActivity.this, vibrator);

    String url = BASE_URL + ServerUtil.Endpoint.LEAVE.toString();

    JSONObject payloadJson = new JSONObject();

    try {
      payloadJson.put(ServerUtil.RequestParameter.PLAYER_ID.toString(), playerId);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    okHttpHandler = new OkHttpHandler(this, client);
    okHttpHandler.postRequest(url, payloadJson);
  }

  // BACK BUTTON PRESSED
  public boolean onKeyDown(int keyCode, KeyEvent event) {

    if (keyCode == KeyEvent.KEYCODE_BACK) {

      VibrationUtil.preferredVibration(GameActivity.this, vibrator);

      if (!leavable) {
        return false;
      }

      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(getString(R.string.leave));
      builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          VibrationUtil.preferredVibration(GameActivity.this, vibrator);
          leaveRoomOnClick();
        }
      });
      builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          VibrationUtil.preferredVibration(GameActivity.this, vibrator);
        }
      });
      builder.setCancelable(false);

      AlertDialog dialog = builder.create();

      dialog.show();

      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  // END OF THE GAME
  public void gameEnd(JSONObject payloadJson) {

    exitCondition = true;
    getStateTimerHandler.removeCallbacks(getStateTimerRunnable);

    feedbackText.setText("");

    yourButton.setEnabled(false);

    String couponCode = payloadJson.optString(ServerUtil.ResponseParameter.COUPON.toString());

    finish();

    Intent intent = new Intent(getBaseContext(), EndScreenActivity.class);

    intent.putExtra("successfulRounds", (long) wordPattern.length()-1);
    intent.putExtra("couponCode", couponCode);
    intent.putExtra("playerColourCode", tileId);
    intent.putExtra("EXTRA_PLAYER_ID", playerId);
    intent.putExtra("EXTRA_ROOM_ID", roomId);

    startActivity(intent);
  }

  // LEAVING ROOM
  public void backToMainActivity() {

    finish();

    Intent intent = new Intent(getBaseContext(), MainActivity.class);
    startActivity(intent);
  }
}
