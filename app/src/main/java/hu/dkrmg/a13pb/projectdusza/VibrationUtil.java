package hu.dkrmg.a13pb.projectdusza;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;

public class VibrationUtil {

  public static Integer VIBRATION_LENGTH = 10;

  // Vibrator, checking settings
  public static void preferredVibration(Context context, Vibrator vibrator) {

    // Vibrations check
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    boolean vibrationsState = prefs.getBoolean("vibrations", true);
    if (vibrationsState) {
      vibrator.vibrate(VIBRATION_LENGTH);
    }
  }

  private VibrationUtil() {
  }
}
