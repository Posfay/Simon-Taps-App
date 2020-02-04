package hu.simon.taps.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import hu.simon.taps.R;
import hu.simon.taps.activities.MainActivity;
import hu.simon.taps.activities.SettingsActivity;

public class SettingsFragment extends PreferenceFragmentCompat {

  private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

  public static final String PREF_LANGUAGE = "preferred_language";
  public static final String PREF_VIBRATION = "preferred_vibration";
  public static final String CURRENT_VERSION = "current_version";

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.preference_main, rootKey);

    Preference versionPref = findPreference(CURRENT_VERSION);
    versionPref.setSummary(MainActivity.VERSION);

    preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
      @Override
      public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(PREF_LANGUAGE)) {
          changeLanguageSummary();

          getActivity().finish();

          Intent intent = new Intent(getActivity(), SettingsActivity.class);
          startActivity(intent);
        }

        if (key.equals(PREF_VIBRATION)) {
          Preference vibrationPref = findPreference(PREF_VIBRATION);
          vibrationPref.setDefaultValue(sharedPreferences.getBoolean(key, true));
        }
      }
    };
  }

  public void changeLanguageSummary() {
    getPreferenceScreen().getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(preferenceChangeListener);

    Preference languagePref = findPreference(PREF_LANGUAGE);

    if (getPreferenceScreen().getSharedPreferences().getString(PREF_LANGUAGE, "").equals("en")) {
      languagePref.setSummary(R.string.language_english);
    }
    if (getPreferenceScreen().getSharedPreferences().getString(PREF_LANGUAGE, "").equals("hu")) {
      languagePref.setSummary(R.string.language_hungarian);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    changeLanguageSummary();
  }

  @Override
  public void onPause() {
    super.onPause();

    getPreferenceScreen().getSharedPreferences()
        .unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
  }
}
