package hu.dkrmg.a13pb.projectdusza;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends Activity implements AsyncResponse {

    public static final long DELAY_MILLIS = 500;
    public static final long ROUNDS = 2;

    public OkHttpHandler okHttpHandler;
    public String playerId;
    public String roomId;



    Button greenButton;
    Button redButton;
    Button yellowButton;
    Button blueButton;
    Button yourButton;

    Long tileId = null;
    List<Integer> pattern;
    String wordPattern = "";
    Handler timerHandler = new Handler();
    Handler getStateTimerHandler = new Handler();
    Handler delayHandler = new Handler();
    Integer counter = 0;

    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            Integer current = pattern.remove(0);
            counter++;
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
            if (counter == ROUNDS*4) {
                return;
            }
            timerHandler.postDelayed(this, DELAY_MILLIS+100);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        greenButton = (Button) findViewById(R.id.button3);
        redButton = (Button) findViewById(R.id.button4);
        yellowButton = (Button) findViewById(R.id.button5);
        blueButton = (Button) findViewById(R.id.button6);
        yourButton = (Button) findViewById(R.id.button7);

        playerId = getIntent().getStringExtra("EXTRA_PLAYER_ID");
        roomId = getIntent().getStringExtra("EXTRA_ROOM_ID");

        pattern = new ArrayList<Integer>();
        for (int n = 1; n <= ROUNDS; n++) {
            for (int i = 1; i <= 4; i++) {
                pattern.add(i);
            }
        }
        Collections.shuffle(pattern);

        for (Integer current : pattern) {
            wordPattern += current;
        }
        Log.i("minta", wordPattern);


        Log.i("minta2", pattern.toString());
    }

    Runnable getStateTimerRunnable = new Runnable() {
        @Override
        public void run() {

            String url = "https://szerver3.dkrmg.sulinet.hu:8080/simon-taps/state?room_id="+roomId+"&player_id="+playerId;
            okHttpHandler = new OkHttpHandler(GameActivity.this);
            okHttpHandler.getRequest(url);


            getStateTimerHandler.postDelayed(this, 300);
        }
    };


    @Override
    public void onRequestComplete(String responseJsonString) {

        JSONObject payloadJson = null;
        String status = null;
        String state = null;
        long num = -1;

        try {
            payloadJson = new JSONObject(responseJsonString);
            status = payloadJson.getString("status");
            state = payloadJson.getString("game_state");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (status.equals("OK")) {

            //WAITING
            if (state == "waiting") {
                num = payloadJson.optLong("number_of_players");
                yourButton.setText(num+"");
            }
            //PREPARING
            if (state == "preparing") {
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
            //SHOWING PATTERN
            if (state == "showing_pattern") {
                wordPattern = payloadJson.optString("pattern");
                pattern.clear();
                for (int i=0; i<wordPattern.length(); i++) {
                    pattern.add(Integer.valueOf(String.valueOf(wordPattern.charAt(i))));
                }
                timerHandler.postDelayed(timerRunnable, 1000);
            }


        }
        else {
            return;
        }

    }


    public void youOnClick(View v) {






        getStateTimerHandler.postDelayed(getStateTimerRunnable, 1000);
    }

}
