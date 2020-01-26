package hu.simon.taps.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import hu.simon.taps.R;
import hu.simon.taps.utils.LanguageUtil;
import hu.simon.taps.utils.LayoutUtil;
import hu.simon.taps.utils.VibrationUtil;

public class EndScreenActivity extends AppCompatActivity {

  public Vibrator vibrator;

  TextView resultText;

  ImageView resultImage;

  long successfulRounds;

  boolean win;

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

    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    win = getIntent().getBooleanExtra("win", false);
    successfulRounds = getIntent().getLongExtra("successfulRounds", 0);

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
}
