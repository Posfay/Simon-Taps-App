package hu.simon.taps.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import hu.simon.taps.R;
import hu.simon.taps.utils.LayoutUtil;
import hu.simon.taps.utils.VibrationUtil;

public class SettingsActivity extends AppCompatActivity {

  Switch vibrationsSwitch;

  Vibrator vibrator;

  SharedPreferences prefs;

  boolean vibrationsState;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    vibrationsSwitch = findViewById(R.id.vibrationsSwitch);

    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    final SharedPreferences.Editor editor = prefs.edit();

    vibrationsState = prefs.getBoolean("vibrations", true);

    vibrationsSwitch.setChecked(vibrationsState);

    vibrationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          editor.putBoolean("vibrations", true);
        } else {
          editor.putBoolean("vibrations", false);
        }
        editor.apply();
      }
    });
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

      VibrationUtil.preferredVibration(SettingsActivity.this, vibrator);

      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Back to menu?");
      builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          VibrationUtil.preferredVibration(SettingsActivity.this, vibrator);
          backToMainActivity();
        }
      });
      builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          VibrationUtil.preferredVibration(SettingsActivity.this, vibrator);
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

    VibrationUtil.preferredVibration(SettingsActivity.this, vibrator);

    finish();

    Intent intent = new Intent(getBaseContext(), MainActivity.class);
    startActivity(intent);
  }
}
