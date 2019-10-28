package hu.dkrmg.a13pb.projectdusza;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends Activity {

    public static final long DELAY_MILLIS = 500;
    public static final long ROUNDS = 2;
    Button greenButton;
    Button redButton;
    Button yellowButton;
    Button blueButton;
    Button yourButton;
    Long tileId;
    List<Integer> pattern;
    String wordPattern = "";
    Handler timerHandler = new Handler();
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

        tileId = (long) Math.floor(Math.random()*4)+1;

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

        pattern.clear();
        for (int i=0; i<wordPattern.length(); i++) {
           pattern.add(Integer.valueOf(String.valueOf(wordPattern.charAt(i))));
        }
        Log.i("minta2", pattern.toString());
    }





    public void youOnClick(View v) {


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


        timerHandler.postDelayed(timerRunnable, 1000);

    }

}
