package hu.dkrmg.a13pb.projectdusza;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

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
