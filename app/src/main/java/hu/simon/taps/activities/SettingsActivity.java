package hu.simon.taps.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import hu.simon.taps.R;
import hu.simon.taps.utils.LanguageUtil;
import hu.simon.taps.utils.VibrationUtil;

public class SettingsActivity extends AppCompatActivity {

  Vibrator vibrator;
  Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

      // Changing language
      Configuration mainConfiguration = new Configuration(getResources().getConfiguration());
      getResources().updateConfiguration(LanguageUtil.preferredLanguage(this, mainConfiguration),
              getResources().getDisplayMetrics());

      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_settings);

      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));

      vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

      toolbar = findViewById(R.id.toolbar);

      setSupportActionBar(toolbar);
      getSupportActionBar().setTitle(getString(R.string.settings));

      PreferenceManager.setDefaultValues(this, R.xml.preference_main, false);

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
}
