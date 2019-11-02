package hu.dkrmg.a13pb.projectdusza;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import okhttp3.OkHttpClient;

public class GameActivity extends Activity implements AsyncResponse {

  public OkHttpHandler okHttpHandler;
  public OkHttpClient client;
  public String playerId;
  public String roomId;

  Button greenButton;
  Button redButton;
  Button yellowButton;
  Button blueButton;
  Button yourButton;
  ConstraintLayout layout;

  long numOfPlayers = -1;
  Long tileId = null;
  List<Integer> pattern;
  String wordPattern = "";
  Boolean shown = false;
  Boolean exitCondition = false;

  Handler getStateTimerHandler = new Handler();
  long intervalMilli = 1000;

  Handler timerHandler = new Handler();
  Handler delayHandler = new Handler();
  public static final long DELAY_MILLIS = 500;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game);

    greenButton = findViewById(R.id.button3);
    redButton = findViewById(R.id.button4);
    yellowButton = findViewById(R.id.button5);
    blueButton = findViewById(R.id.button6);
    yourButton = findViewById(R.id.button7);
    layout = findViewById(R.id.layout);

    yourButton.setEnabled(false);

    playerId = getIntent().getStringExtra("EXTRA_PLAYER_ID");
    roomId = getIntent().getStringExtra("EXTRA_ROOM_ID");

    pattern = new ArrayList<>();

    client = new OkHttpClient();

    getStateTimerHandler.postDelayed(getStateTimerRunnable, 0);
  }

  Runnable getStateTimerRunnable = new Runnable() {
    @Override
    public void run() {

      if (exitCondition) {
        return;
      }

      String url = "http://szerver3.dkrmg.sulinet.hu:8080/simon-taps/state?room_id=" + roomId
          + "&player_id=" + playerId;

      okHttpHandler = new OkHttpHandler(GameActivity.this, client);
      okHttpHandler.getRequest(url);

      getStateTimerHandler.postDelayed(getStateTimerRunnable, intervalMilli);
    }
  };

  @Override
  public void onRequestComplete(String responseJsonString) {

    JSONObject payloadJson = null;
    String status = null;
    String state = null;

    Log.i("GameResponse", responseJsonString);

    try {

      payloadJson = new JSONObject(responseJsonString);
      status = payloadJson.optString("status");
      state = payloadJson.optString("game_state");

    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (!status.equals("OK")) {
      return;
    }

    // WAITING
    if (state.equals(StateUtils.WAITING)) {
      gameWaiting(payloadJson);
    }
    // PREPARING
    if (state.equals(StateUtils.PREPARING)) {
      gamePreparing(payloadJson);
    }
    // SHOWING_PATTERN
    if (state.equals(StateUtils.SHOWING_PATTERN)) {
      gameShowingPattern(payloadJson);
    }
    // PLAYING
    if (state.equals(StateUtils.PLAYING)) {
      gamePlaying(payloadJson);
    }
    // SUCCESSFUL_END
    if (state.equals(StateUtils.SUCCESSFUL_END)) {
      gameEnd(true);
      return;
    }
    // FAIL_END
    if (state.equals(StateUtils.FAIL_END)) {
      gameEnd(false);
      return;
    }
  }

  public void gameWaiting(JSONObject payloadJson) {

    numOfPlayers = payloadJson.optLong("number_of_players");
    yourButton.setText(numOfPlayers + "");
  }

  public void gamePreparing(JSONObject payloadJson) {

    // intervalMilli = 200;
    tileId = payloadJson.optLong("tile_id");

    if (tileId == 1) {
      yourButton.setBackgroundColor(getResources().getColor(R.color.green));
    }
    if (tileId == 2) {
      yourButton.setBackgroundColor(getResources().getColor(R.color.red));
    }
    if (tileId == 3) {
      yourButton.setBackgroundColor(getResources().getColor(R.color.yellow));
    }
    if (tileId == 4) {
      yourButton.setBackgroundColor(getResources().getColor(R.color.blue));
    }
  }

  public void gameShowingPattern (JSONObject payloadJson) {

    if (!shown) {

      wordPattern = payloadJson.optString("pattern");
      pattern.clear();

      for (int i = 0; i < wordPattern.length(); i++) {
        pattern.add(Integer.valueOf(String.valueOf(wordPattern.charAt(i))));
      }

      displayPattern();
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
    String url = "http://szerver3.dkrmg.sulinet.hu:8080/simon-taps/start";
    JSONObject payloadJson = new JSONObject();

    try {
      payloadJson.put("room_id", roomId);
      payloadJson.put("player_id", playerId);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    okHttpHandler = new OkHttpHandler(this, client);
    okHttpHandler.postRequest(url, payloadJson);
  }

  public void gamePlaying(JSONObject payloadJson) {
    yourButton.setEnabled(true);
  }

  public void youOnClick(View v) {

    Log.i("press", "true");

    String url = "http://szerver3.dkrmg.sulinet.hu:8080/simon-taps/game";
    JSONObject payloadJson = new JSONObject();

    try {
      payloadJson.put("room_id", roomId);
      payloadJson.put("player_id", playerId);
      payloadJson.put("action", "button_press");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    okHttpHandler = new OkHttpHandler(this, client);
    okHttpHandler.postRequest(url, payloadJson);
  }

  public void gameEnd(Boolean success) {
    if (success) {
      layout.setBackgroundColor(Color.GREEN);
      exitCondition = true;
    }
    if (!success) {
      layout.setBackgroundColor(Color.RED);
      exitCondition = true;
    }
  }
}
