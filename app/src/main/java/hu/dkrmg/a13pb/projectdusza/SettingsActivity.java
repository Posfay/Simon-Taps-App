package hu.dkrmg.a13pb.projectdusza;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.security.PublicKey;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

  Switch vibrationsSwitch;
  Boolean vibrationsState;
  SharedPreferences prefs;

  public Vibrator vibrator;
  public static Integer VIBRATION_LENGTH = 250;
  private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    final SharedPreferences.Editor editor = prefs.edit();

    vibrationsState = prefs.getBoolean("vibrations", true);

    vibrationsSwitch = findViewById(R.id.vibrationsSwitch);
    vibrationsSwitch.setChecked(vibrationsState);

    vibrationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          editor.putBoolean("vibrations", true);
        } else {
          editor.putBoolean("vibrations", false);
        }
        editor.commit();
      }
    });
  }

  //Vibration, checks settings
  public void preferredVibration() {

    //Vibrations check
    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    vibrationsState = prefs.getBoolean("vibrations", true);
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
