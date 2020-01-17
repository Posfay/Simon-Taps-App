package hu.dkrmg.a13pb.projectdusza;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import okhttp3.OkHttpClient;

public class GameActivity extends Activity implements AsyncResponse {

  // ------------------------------------DECLARING VARIABLES---------------------------------------
  public OkHttpHandler okHttpHandler;
  public OkHttpClient client;
  public String playerId;
  public String roomId;
  public Vibrator vibrator;

  TextView feedbackText;
  TextView roomIdText;
  TextView roundText;
  Button greenButton;
  Button redButton;
  Button yellowButton;
  Button blueButton;
  Button yourButton;
  ConstraintLayout layout;

  long numOfPlayers = -1;
  long prevNumOfPlayers = 5;
  Long tileId = null;
  List<Integer> pattern;
  String wordPattern = "";
  Boolean shown = false;
  Boolean exitCondition = false;
  Boolean connected = false;
  Boolean leavable = false;

  Handler getStateTimerHandler = new Handler();
  long intervalMilli = 1000;
  long offlineTime = 0;

  Handler timerHandler = new Handler();
  Handler delayHandler = new Handler();
  public static final long DELAY_MILLIS = 400;
  public static final long DELAY_DISPLAY = 1000;

  public static final String BASE_URL =
      ServerUtil.PROTOCOL + ServerUtil.HOSTNAME + ":" + ServerUtil.PORT + "/";

  private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.75F);

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    // ----------------------------------FINDING COMPONENTS-----------------------------------------
    feedbackText = findViewById(R.id.feedBackText);
    roomIdText = findViewById(R.id.roomIdText);
    roundText = findViewById(R.id.roundText);
    greenButton = findViewById(R.id.greenButton);
    redButton = findViewById(R.id.redButton);
    yellowButton = findViewById(R.id.yellowButton);
    blueButton = findViewById(R.id.blueButton);
    yourButton = findViewById(R.id.gameButton);
    layout = findViewById(R.id.layout);

    pattern = new ArrayList<>();

    client = new OkHttpClient();
    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    getStateTimerHandler.postDelayed(getStateTimerRunnable, 0);

    yourButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        youOnClick(v);
      }
    });
    yourButton.setEnabled(false);
    leavable = true;

    // Getting Player ID and Room ID from MainACtivity
    playerId = getIntent().getStringExtra("EXTRA_PLAYER_ID");
    roomId = getIntent().getStringExtra("EXTRA_ROOM_ID");
    roomIdText.setText("Room ID: " + roomId);
  }

  // -----------------------------------GETSTATE REQUEST (REPEATED)---------------------------------
  Runnable getStateTimerRunnable = new Runnable() {
    @Override
    public void run() {

      if (exitCondition) {
        return;
      }

      connectionCheck();
      if (!connected) {
        offlineTime += intervalMilli;
        Toast.makeText(GameActivity.this, "No connection!", Toast.LENGTH_SHORT).show();
      }

      if (offlineTime >= 3000) {
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

  @Override
  protected void onPause() {
    super.onPause();
    exitCondition = true; // No more getstate requests, when exits this activity
  }

  // Checking internet connection
  public void connectionCheck() {

    ConnectivityManager connectivityManager =
        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    // We are connected to a network
    // No internet :(
    connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            .getState() == NetworkInfo.State.CONNECTED ||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .getState() == NetworkInfo.State.CONNECTED;
    Log.i("connected", connected.toString());
  }

  // SUCCESSFUL REQUEST
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
    if (state.equals(ServerUtil.State.SUCCESSFUL_END.toString())) {
      gameEnd(true);
      return;
    }

    // FAIL_END
    if (state.equals(ServerUtil.State.FAIL_END.toString())) {
      gameEnd(false);
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
    feedbackText.setText("Players in room: " + numOfPlayers + " ");

    if (numOfPlayers > prevNumOfPlayers) {
      VibrationUtil.preferredVibration(GameActivity.this, vibrator);
    }

    prevNumOfPlayers = numOfPlayers;

  }

  public void gamePreparing(JSONObject payloadJson) {

    leavable = false;
    feedbackText.setText("Prepare for the game (10 s)");

    intervalMilli = 250;

    tileId = payloadJson.optLong(ServerUtil.ResponseParameter.TILE_ID.toString());

    if (tileId == 1) {
      ViewCompat.setBackgroundTintList(yourButton,
          ContextCompat.getColorStateList(this, R.color.green));
    }
    if (tileId == 2) {
      ViewCompat.setBackgroundTintList(yourButton,
          ContextCompat.getColorStateList(this, R.color.red));
    }
    if (tileId == 3) {
      ViewCompat.setBackgroundTintList(yourButton,
          ContextCompat.getColorStateList(this, R.color.yellow));
    }
    if (tileId == 4) {
      ViewCompat.setBackgroundTintList(yourButton,
          ContextCompat.getColorStateList(this, R.color.blue));
    }
  }

  public void gameShowingPattern(JSONObject payloadJson) {

    feedbackText.setText("");

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
      }, DELAY_DISPLAY);

      roundText.setText("ROUND: " + wordPattern.length());

      shown = true;
    }
  }

  public void displayPattern() {

    Runnable timerRunnable = new Runnable() {

      Integer counter = pattern.size();

      @Override
      public void run() {
        Integer current = pattern.remove(0);
        counter--;

        if (current == 1) {

          greenButton.setBackgroundResource(R.drawable.button_green_active);
          delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              greenButton.setBackgroundResource(R.drawable.button_green);
            }
          }, DELAY_MILLIS);
        }
        if (current == 2) {

          redButton.setBackgroundResource(R.drawable.button_red_active);
          delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              redButton.setBackgroundResource(R.drawable.button_red);
            }
          }, DELAY_MILLIS);
        }
        if (current == 3) {

          yellowButton.setBackgroundResource(R.drawable.button_yellow_active);
          delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              yellowButton.setBackgroundResource(R.drawable.button_yellow);
            }
          }, DELAY_MILLIS);
        }
        if (current == 4) {

          blueButton.setBackgroundResource(R.drawable.button_blue_active);
          delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              blueButton.setBackgroundResource(R.drawable.button_blue);
            }
          }, DELAY_MILLIS);
        }
        if (counter <= 0) {
          startGame();
          return;
        }

        timerHandler.postDelayed(this, DELAY_MILLIS + 100);
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

    feedbackText.setText("The round has started!");
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
      builder.setMessage("Do you want to leave the room?");
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
  public void gameEnd(Boolean success) {

    feedbackText.setText("");
    yourButton.setEnabled(false);
    exitCondition = true;

    finish();
    Intent intent = new Intent(getBaseContext(), EndScreenActivity.class);

    if (success) {
      intent.putExtra("win", true);
      intent.putExtra("successfulRounds", (long) wordPattern.length());
    } else {
      intent.putExtra("win", false);
      intent.putExtra("successfulRounds", (long) wordPattern.length()-1);
    }

    startActivity(intent);
  }

  // LEAVING ROOM
  public void backToMainActivity() {

    Log.i("leave", "true");

    finish();

    Intent intent = new Intent(getBaseContext(), MainActivity.class);
    startActivity(intent);
  }
}
