package hu.simon.taps.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import hu.simon.taps.fragments.SettingsFragment;

public class VibrationUtil {

  public static long VIBRATION_LENGTH = 50;

  public static void preferredVibration(Context context, Vibrator vibrator) {

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    boolean vibrationsState = prefs.getBoolean(SettingsFragment.PREF_VIBRATION, true);

    if (vibrationsState) {
      vibrator.vibrate(VIBRATION_LENGTH);
    }
  }

  private VibrationUtil() {
  }
}
