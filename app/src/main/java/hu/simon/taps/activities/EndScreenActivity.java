package hu.simon.taps.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import hu.simon.taps.R;
import hu.simon.taps.http.handler.AsyncResponse;
import hu.simon.taps.http.handler.OkHttpHandler;
import hu.simon.taps.utils.GameUtil;
import hu.simon.taps.utils.LanguageUtil;
import hu.simon.taps.utils.LayoutUtil;
import hu.simon.taps.utils.ServerUtil;
import hu.simon.taps.utils.VibrationUtil;
import okhttp3.OkHttpClient;

public class EndScreenActivity extends AppCompatActivity implements AsyncResponse {

  public static final String BASE_URL =
          ServerUtil.PROTOCOL + ServerUtil.HOSTNAME + ":" + ServerUtil.PORT + "/";

  public OkHttpHandler okHttpHandler;
  public OkHttpClient client;

  public Vibrator vibrator;

  Button restartButton;
  TextView resultText;
  ImageView resultImage;

  long successfulRounds;
  long offlineTime = 0;
  long colourCode;

  String roomId;
  String playerId;

  Handler getStateTimerHandler = new Handler();

  boolean win;
  boolean exitCondition = false;

  private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.75F);

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    //Changing language
    Configuration mainConfiguration = new Configuration(getResources().getConfiguration());
    getResources().updateConfiguration(LanguageUtil.preferredLanguage(this, mainConfiguration), getResources().getDisplayMetrics());

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_end_screen);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    resultText = findViewById(R.id.result);
    resultImage = findViewById(R.id.resultImage);

    restartButton = findViewById(R.id.restartButton);
    restartButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        restartButtonOnClick(v);
      }
    });

    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    client = new OkHttpClient();

    win = getIntent().getBooleanExtra("win", false);
    successfulRounds = getIntent().getLongExtra("successfulRounds", 0);
    colourCode = getIntent().getLongExtra("playerColourCode",0);
    playerId = getIntent().getStringExtra("EXTRA_PLAYER_ID");
    roomId = getIntent().getStringExtra("EXTRA_ROOM_ID");

    setResultText();
    restartButtonColour();
  }

  @Override
  protected void onPause() {

    super.onPause();

    // No more getstate requests, when exits this activity
    exitCondition = true;
  }

  Runnable getStateTimerRunnable = new Runnable() {
    @Override
    public void run() {

      if (exitCondition) {
        return;
      }

      boolean connected = ServerUtil.connectionCheck(EndScreenActivity.this);

      if (!connected) {

        offlineTime += ServerUtil.END_SCREEN_REQUEST_INTERVAL;

      }

      if (offlineTime >= GameUtil.MAX_OFFLINE_TIME) {

        Toast.makeText(EndScreenActivity.this, ServerUtil.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT)
                .show();
      }

      // Getstate request
      if (connected) {

        offlineTime = 0;
        String url =
                BASE_URL + ServerUtil.Endpoint.STATE.toString() + "/" + roomId + "/" + playerId;

        okHttpHandler = new OkHttpHandler(EndScreenActivity.this, client);
        okHttpHandler.getRequest(url);
      }

      getStateTimerHandler.postDelayed(getStateTimerRunnable, ServerUtil.END_SCREEN_REQUEST_INTERVAL);
    }
  };

  public void setResultText() {
    Resources res = getResources();

    if (win) {

      resultText.setText(getString(R.string.score) + successfulRounds);

      resultImage.setImageDrawable(res.getDrawable(R.drawable.godfathercat));
    } else {

      resultText.setText(getString(R.string.score) + successfulRounds);

      if (successfulRounds >= 8) {

        resultText.setText(getString(R.string.pos1) + "\n" + getString(R.string.score) + successfulRounds);
      }
      if (successfulRounds >= 10) {

        resultText.setText(getString(R.string.pos2) + "\n" + getString(R.string.score) + successfulRounds);
      }
      if (successfulRounds >= 13) {

        resultText.setText(getString(R.string.pos3) + "\n" + getString(R.string.score) + successfulRounds);
      }
      if (successfulRounds >= 16) {

        resultText.setText(getString(R.string.pos4) + "\n" + getString(R.string.score) + successfulRounds);
      }
      if (successfulRounds >= 20) {

        resultText.setText(getString(R.string.pos5) + "\n" + getString(R.string.score) + successfulRounds);
      }
      if (successfulRounds >= 25) {

        resultText.setText(getString(R.string.pos6) + "\n" + getString(R.string.score) + successfulRounds);
      }
      if (successfulRounds >= 30) {

        resultText.setText(getString(R.string.pos7) + "\n" + getString(R.string.score) + successfulRounds);
      }

      resultImage.setImageResource(R.drawable.cryingcat);
    }
  }

  public void restartButtonColour() {

    switch ((int) colourCode) {
      case 0:
        Log.i("intentExtra", "couldn't process");
        Log.i("colorCode", String.valueOf(colourCode));
        break;
      case 1:
        ViewCompat.setBackgroundTintList(restartButton,
                ContextCompat.getColorStateList(this, R.color.green));
        break;
      case 2:
        ViewCompat.setBackgroundTintList(restartButton,
                ContextCompat.getColorStateList(this, R.color.red));
        break;
      case 3:
        ViewCompat.setBackgroundTintList(restartButton,
                ContextCompat.getColorStateList(this, R.color.yellow));
        break;
      case 4:
        ViewCompat.setBackgroundTintList(restartButton,
                ContextCompat.getColorStateList(this, R.color.blue));
        break;
    }
  }

  public void restartButtonOnClick(View v) {

    Log.i("restart", "pressed");

    v.startAnimation(buttonClick);
    VibrationUtil.preferredVibration(EndScreenActivity.this, vibrator);

    String url = BASE_URL + ServerUtil.Endpoint.RESTART.toString();

    JSONObject payloadJson = new JSONObject();

    if (!ServerUtil.connectionCheck(this)) {

      Toast.makeText(EndScreenActivity.this, ServerUtil.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT)
              .show();
    } else {
      restartButton.setEnabled(false);
    }

    try {
      payloadJson.put(ServerUtil.RequestParameter.ROOM_ID.toString(), roomId);
      payloadJson.put(ServerUtil.RequestParameter.PLAYER_ID.toString(), playerId);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    okHttpHandler = new OkHttpHandler(this, client);
    okHttpHandler.postRequest(url, payloadJson);

    getStateTimerHandler.postDelayed(getStateTimerRunnable, 0);
  }

  public void onRequestComplete(String responseJsonString) {

    JSONObject payloadJson = null;
    String status = null;
    String state = null;

    try {

      payloadJson = new JSONObject(responseJsonString);

      Log.i("restartResponse", responseJsonString);

      status = payloadJson.getString(ServerUtil.ResponseParameter.STATUS.toString());
      state = payloadJson.getString(ServerUtil.ResponseParameter.GAME_STATE.toString());

    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (!status.equals("OK")) {
      //TODO error response
      return;
    }

    if (ServerUtil.State.PREPARING.toString().equals(state)) {
      backToGameActivity();
    }
  }

  public void onWindowFocusChanged(boolean hasFocus) {

    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) {
      View decorView = getWindow().getDecorView();
      LayoutUtil.hideSystemUI(decorView);
    }
  }

  // BACK BUTTON PRESSED
  public boolean onKeyDown(int keyCode, KeyEvent event) {

    if (keyCode == KeyEvent.KEYCODE_BACK) {

      VibrationUtil.preferredVibration(EndScreenActivity.this, vibrator);

      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(getString(R.string.back_to_menu));
      builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          VibrationUtil.preferredVibration(EndScreenActivity.this, vibrator);
          backToMainActivity();
        }
      });
      builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          VibrationUtil.preferredVibration(EndScreenActivity.this, vibrator);
        }
      });
      builder.setCancelable(false);

      AlertDialog dialog = builder.create();

      dialog.show();

      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  // LEAVING ACTIVITY
  private void backToMainActivity() {

    VibrationUtil.preferredVibration(EndScreenActivity.this, vibrator);

    finish();

    Intent intent = new Intent(getBaseContext(), MainActivity.class);
    startActivity(intent);
  }

  private void backToGameActivity() {

    VibrationUtil.preferredVibration(EndScreenActivity.this, vibrator);

    finish();

    Intent intent = new Intent(getBaseContext(), GameActivity.class);
    intent.putExtra("EXTRA_PLAYER_ID", playerId);
    intent.putExtra("EXTRA_ROOM_ID", roomId);
    startActivity(intent);
  }
}
