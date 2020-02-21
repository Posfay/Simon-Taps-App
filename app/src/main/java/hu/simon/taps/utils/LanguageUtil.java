package hu.simon.taps.utils;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import hu.simon.taps.fragments.SettingsFragment;

public class LanguageUtil {

  public static Configuration preferredLanguage(Context context, Configuration mainConfiguration) {

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String languageToLoad =
        prefs.getString(SettingsFragment.PREF_LANGUAGE, Locale.getDefault().getDisplayLanguage());
    Locale locale = new Locale(languageToLoad);
    Locale.setDefault(locale);

    mainConfiguration.setLocale(locale);

    return mainConfiguration;
  }

  private LanguageUtil() {
  }
}
