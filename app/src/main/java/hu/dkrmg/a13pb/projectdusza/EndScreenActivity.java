package hu.dkrmg.a13pb.projectdusza;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class EndScreenActivity extends AppCompatActivity {

    public Vibrator vibrator;
    public static Integer VIBRATION_LENGTH = 250;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);

    Boolean win;
    TextView resultText;
    ImageView resultImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        resultText = findViewById(R.id.result);
        resultImage = findViewById(R.id.resultImage);
        Resources res = getResources();
        win = getIntent().getBooleanExtra("win",false);

        if (win) {
            resultText.setText("You won!");
            resultImage.setImageDrawable(res.getDrawable(R.drawable.godfathercat));
        } else {
            resultText.setText("You lost!");
            resultImage.setImageResource(R.drawable.cryingcat);
        }
    }

    //Vibration, checks settings
    public void preferredVibration() {

        //Vibrations check
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean vibrationsState = prefs.getBoolean("vibrations", true);
        if (vibrationsState) {
            vibrator.vibrate(VIBRATION_LENGTH);
        }
        if (!vibrationsState) {
            return;
        }
    }

    //LEAVING ACTIVITY
    public void backToMainActivity(View v) {

        v.startAnimation(buttonClick);
        preferredVibration();

        finish();

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }
}
