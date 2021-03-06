package hu.simon.taps.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import hu.simon.taps.R;
import hu.simon.taps.utils.LanguageUtil;
import hu.simon.taps.utils.ScreenUtil;
import hu.simon.taps.utils.VibrationUtil;

public class SettingsActivity extends AppCompatActivity {

  public static Activity settingsActivity;

  Vibrator vibrator;
  Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    // Changing language
    Configuration mainConfiguration = new Configuration(getResources().getConfiguration());
    getResources().updateConfiguration(LanguageUtil.preferredLanguage(this, mainConfiguration),
        getResources().getDisplayMetrics());

    ScreenUtil.setFlags(this, this);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    toolbar = findViewById(R.id.settingsToolbar);

    ScreenUtil.setToolbarColor(this, toolbar);

    settingsActivity = this;

    PreferenceManager.setDefaultValues(this, R.xml.preference_settings, false);

    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(getString(R.string.settings));
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
