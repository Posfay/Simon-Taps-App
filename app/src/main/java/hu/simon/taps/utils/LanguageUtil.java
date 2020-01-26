package hu.simon.taps.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class LanguageUtil extends AppCompatActivity {

    public static Configuration preferredLanguage(Context context, Configuration mainConfiguration) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String languageToLoad = prefs.getString("language","en");
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);

        mainConfiguration.setLocale(locale);

        return mainConfiguration;
    }

    private LanguageUtil() {
    }
}
