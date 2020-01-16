package hu.dkrmg.a13pb.projectdusza;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndScreenActivity extends AppCompatActivity {

  public Vibrator vibrator;

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
    win = getIntent().getBooleanExtra("win", false);

    if (win) {
      resultText.setText("You won!");
      resultImage.setImageDrawable(res.getDrawable(R.drawable.godfathercat));
    } else {
      resultText.setText("You lost!");
      resultImage.setImageResource(R.drawable.cryingcat);
    }
  }

  // BACK BUTTON PRESSED
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {

      VibrationUtil.preferredVibration(EndScreenActivity.this, vibrator);

      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Back to menu?");
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
  public void backToMainActivity() {

    VibrationUtil.preferredVibration(EndScreenActivity.this, vibrator);

    finish();

    Intent intent = new Intent(getBaseContext(), MainActivity.class);
    startActivity(intent);
  }
}
