package hu.simon.taps.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import hu.simon.taps.R;
import hu.simon.taps.http.handler.AsyncResponse;
import hu.simon.taps.http.handler.OkHttpHandler;
import hu.simon.taps.utils.LanguageUtil;
import hu.simon.taps.utils.ScreenUtil;
import hu.simon.taps.utils.ServerUtil;
import hu.simon.taps.utils.VibrationUtil;
import okhttp3.OkHttpClient;

public class CouponsActivity extends AppCompatActivity implements AsyncResponse {

  public OkHttpHandler okHttpHandler;
  public OkHttpClient client;

  Vibrator vibrator;
  Toolbar toolbar;
  ListView couponList;

  TextView couponCode;
  TextView couponExpiration;

  ArrayList<String> coupons = new ArrayList<>();
  ArrayAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Changing language
    Configuration mainConfiguration = new Configuration(getResources().getConfiguration());
    getResources().updateConfiguration(LanguageUtil.preferredLanguage(this, mainConfiguration),
        getResources().getDisplayMetrics());

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_coupons);

    SettingsActivity.settingsActivity.finish();

    ScreenUtil.setFlags(this, this);

    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    client = new OkHttpClient();

    toolbar = findViewById(R.id.toolbar);
    couponList = findViewById(R.id.couponsList);

    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(R.string.coupons);

    getCoupons();

    // adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,
    // coupons);
    adapter = new CustomAdapter(this, R.layout.list_item_coupons, coupons);
  }

  // BACK BUTTON PRESSED
  public boolean onKeyDown(int keyCode, KeyEvent event) {

    if (keyCode == KeyEvent.KEYCODE_BACK) {

      VibrationUtil.preferredVibration(CouponsActivity.this, vibrator);

      backToSettingsActivity();
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  // LEAVING ACTIVITY
  public void backToSettingsActivity() {

    VibrationUtil.preferredVibration(CouponsActivity.this, vibrator);

    finish();

    Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
    startActivity(intent);
  }

  private void getCoupons() {

    Log.i("coupons", "requested");

    String url = GameActivity.BASE_URL + ServerUtil.Endpoint.COUPON.toString() + "/" + "user" + "/"
        + ServerUtil.PLAYER_ID;

    okHttpHandler = new OkHttpHandler(CouponsActivity.this, client);
    okHttpHandler.getRequest(url);
  }

  public void onRequestComplete(String responseJsonString) {

    JSONObject responseJson;
    String status = null;

    try {

      responseJson = new JSONObject(responseJsonString);

      Log.i("CouponsResponse", responseJsonString);

      status = responseJson.getString(ServerUtil.ResponseParameter.STATUS.toString());
      // coupons = responseJson.optJSONArray(ServerUtil.ResponseParameter.COUPONS.toString());

      JSONArray couponsJsonArray =
          responseJson.getJSONArray(ServerUtil.ResponseParameter.COUPONS.toString());

      for (int i = 0; i < couponsJsonArray.length(); i++) {

        coupons.add(couponsJsonArray.getString(i));
        couponList.setAdapter(adapter);
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (!status.equals("OK")) {
      // TODO error response
    }
  }

  public class CustomAdapter extends ArrayAdapter<String> {

    CustomAdapter(Context context, int resource, List<String> objects) {

      super(context, resource, objects);
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {

      View v = LayoutInflater.from(getContext()).inflate(R.layout.list_item_coupons, parent, false);

      couponCode = v.findViewById(R.id.couponCode);
      couponExpiration = v.findViewById(R.id.couponExpiration);

      String actualCode = getItem(position);

      final String code = actualCode.substring(0, actualCode.indexOf("-"));
      String date = actualCode.substring(actualCode.indexOf("-") + 1);
      String[] dateValues = date.split(" ");

      long day = Long.valueOf(dateValues[0]);
      long hour = Long.valueOf(dateValues[1]);
      long minute = Long.valueOf(dateValues[2]);

      String dayS = dateValues[0] + getString(R.string.day);
      String hourS = dateValues[1] + getString(R.string.hour);
      String minuteS = dateValues[2] + getString(R.string.minute);

      String expirationText = getString(R.string.valid);

      if (day > 0) {
        expirationText += " " + dayS;
      }
      if (hour > 0) {
        expirationText += " " + hourS;
      }
      if (minute > 0) {
        expirationText += " " + minuteS;
      }

      couponCode.setText(code);
      couponExpiration.setText(expirationText);

      randomColor(v);

      return v;
    }
  }

  public void randomColor(View v) {

    Random randomBetweenOneFour = new Random();
    int randomSwitchNum = randomBetweenOneFour.nextInt(5 - 1) + 1;

    switch (randomSwitchNum) {

      case 1:
        v.setBackgroundColor(getResources().getColor(R.color.blue_pale));
        break;

      case 2:
        v.setBackgroundColor(getResources().getColor(R.color.red_pale));
        break;

      case 3:
        v.setBackgroundColor(getResources().getColor(R.color.green_pale));
        break;

      case 4:
        v.setBackgroundColor(getResources().getColor(R.color.yellow_pale));
        break;
    }
  }
}
