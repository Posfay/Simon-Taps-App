package hu.simon.taps.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import hu.simon.taps.R;

public class ScreenUtil {

    public static void setFlags(Activity activity, Context context) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().setStatusBarColor(context.getResources().getColor(R.color.colorPrimary,null));
    }

    private ScreenUtil() {
    }
}
