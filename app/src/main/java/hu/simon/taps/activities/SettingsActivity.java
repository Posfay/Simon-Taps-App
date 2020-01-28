package hu.simon.taps.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import hu.simon.taps.R;
import hu.simon.taps.utils.GameUtil;
import hu.simon.taps.utils.LanguageUtil;
import hu.simon.taps.utils.VibrationUtil;

public class SettingsActivity extends AppCompatActivity {

  SharedPreferences prefs;
  SharedPreferences.Editor editor;

  Vibrator vibrator;

  Switch vibrationsSwitch;
  Switch languageSwitch;

  boolean vibrationsState;

  String languageToLoad;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    //Changing language
    Configuration mainConfiguration = new Configuration(getResources().getConfiguration());
    getResources().updateConfiguration(LanguageUtil.preferredLanguage(this, mainConfiguration), getResources().getDisplayMetrics());

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));

    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    editor = prefs.edit();

    vibrationsSwitch = findViewById(R.id.vibrationsSwitch);
    languageSwitch = findViewById(R.id.languageSwitch);

    vibrationsState = prefs.getBoolean("vibrations", true);
    languageToLoad = prefs.getString("language", Locale.getDefault().getDisplayLanguage());

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

    languageSwitch.setChecked(languageToLoad.equals("hu"));

    languageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          editor.putString("language", "hu");
        } else {
          editor.putString("language", "en");
        }
        editor.apply();

        reloadActivity();
      }
    });
  }

  // BACK BUTTON PRESSED
  public boolean onKeyDown(int keyCode, KeyEvent event) {

    if (keyCode == KeyEvent.KEYCODE_BACK) {
      VibrationUtil.preferredVibration(SettingsActivity.this, vibrator);

      backToMainActivity();
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
  public void reloadActivity() {

    VibrationUtil.preferredVibration(SettingsActivity.this, vibrator);

    finish();

    Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
    startActivity(intent);
  }
}
