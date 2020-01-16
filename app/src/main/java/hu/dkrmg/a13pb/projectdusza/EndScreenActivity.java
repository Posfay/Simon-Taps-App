package hu.dkrmg.a13pb.projectdusza;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

    //BACK BUTTON PRESSED
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            preferredVibration();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Back to menu?");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    preferredVibration();
                    backToMainActivity();
                }
            });
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    preferredVibration();
                }
            });
            builder.setCancelable(false);

            AlertDialog dialog = builder.create();

            dialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //LEAVING ACTIVITY
    public void backToMainActivity() {

        preferredVibration();

        finish();

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }
}
