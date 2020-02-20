package hu.simon.taps.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.widget.Toolbar;

import hu.simon.taps.R;
import hu.simon.taps.fragments.SettingsFragment;

public class ScreenUtil {

  public static void setFlags(Activity activity, Context context) {

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String backgroundState = prefs.getString(SettingsFragment.PREF_BACKGROUND_COLOR, "dark");

    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    if (backgroundState.equals("dark")) {

      activity.setTheme(R.style.AppThemeDark);
      activity.getWindow()
              .setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark, null));
    }
    else if (backgroundState.equals("light")) {

      activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      activity.setTheme(R.style.AppTheme);
      activity.getWindow()
              .setStatusBarColor(context.getResources().getColor(R.color.colorPrimary, null));
    }
  }

  public static void setToolbarColor(Context context, Toolbar toolbar) {

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String backgroundState = prefs.getString(SettingsFragment.PREF_BACKGROUND_COLOR, "dark");

    if (backgroundState.equals("dark")) {
      toolbar.setBackgroundColor(context.getResources().getColor(R.color.toolbarGreyDark, null));
    }
    else if (backgroundState.equals("light")) {
      toolbar.setBackgroundColor(context.getResources().getColor(R.color.toolbarGrey, null));
    }
  }

  private ScreenUtil() {
  }
}
