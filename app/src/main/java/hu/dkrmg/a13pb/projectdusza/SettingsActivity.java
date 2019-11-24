package hu.dkrmg.a13pb.projectdusza;

import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

  Switch vibrationsSwitch;
  Boolean vibrationsState;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    vibrationsSwitch = findViewById(R.id.vibrationsSwitch);
    vibrationsSwitch.setChecked(true);
  }
}
