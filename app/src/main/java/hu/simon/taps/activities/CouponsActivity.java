package hu.simon.taps.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import hu.simon.taps.R;
import hu.simon.taps.http.handler.AsyncResponse;
import hu.simon.taps.http.handler.OkHttpHandler;
import hu.simon.taps.utils.GameUtil;
import hu.simon.taps.utils.LanguageUtil;
import hu.simon.taps.utils.ScreenUtil;
import androidx.appcompat.widget.Toolbar;
import hu.simon.taps.utils.ServerUtil;
import hu.simon.taps.utils.VibrationUtil;
import okhttp3.OkHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CouponsActivity extends AppCompatActivity implements AsyncResponse {

  public OkHttpHandler okHttpHandler;
  public OkHttpClient client;

  Vibrator vibrator;
  Toolbar toolbar;
  ListView couponList;

  TextView couponCode;
  TextView couponExpiration;

  ArrayList <String> coupons = new ArrayList<String>();
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

    //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, coupons);
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

    Log.i("coupons","requested");

    String url = GameActivity.BASE_URL + ServerUtil.Endpoint.COUPON.toString() + "/" + "user" + "/" + ServerUtil.PLAYER_ID;

    JSONObject payloadJson = new JSONObject();

    okHttpHandler = new OkHttpHandler(CouponsActivity.this, client);
    okHttpHandler.getRequest(url);
  }

  public void onRequestComplete(String responseJsonString) {

    JSONObject payloadJson;
    String status = null;

    JSONObject responseJson = null;

    try {

      payloadJson = new JSONObject(responseJsonString);

      Log.i("CouponsResponse", responseJsonString);

      status = payloadJson.getString(ServerUtil.ResponseParameter.STATUS.toString());
      //coupons = payloadJson.optJSONArray(ServerUtil.ResponseParameter.COUPONS.toString());

      JSONArray couponsJsonArray = payloadJson.getJSONArray(ServerUtil.ResponseParameter.COUPONS.toString());


      for (int i = 0; i < couponsJsonArray.length(); i++) {
        coupons.add(couponsJsonArray.getString(i));
        couponList.setAdapter(adapter);
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (!status.equals("OK")) {
      // TODO error response
      return;
    }
  }

  public class CustomAdapter extends ArrayAdapter<String> {

    CustomAdapter(Context context, int resource, List<String> objects) {
      super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View v = LayoutInflater.from(getContext()).inflate(R.layout.list_item_coupons, parent, false);

      couponCode = v.findViewById(R.id.couponCode);
      couponExpiration = v.findViewById(R.id.couponExpiration);

      String actualCode = getItem(position);

      couponCode.setText(actualCode.substring(0, actualCode.indexOf("+")));
      couponExpiration.setText("Valid for: " + actualCode.substring(actualCode.indexOf("+")+1));

      randomColor();

      return v;
    }
  }

  public void randomColor() {

    Random randomBetweenOneFour = new Random();
    int randomSwitchNum = randomBetweenOneFour.nextInt(5 - 1) + 1;

    switch (randomSwitchNum) {

      case 1:
        couponCode.setTextColor(getResources().getColor(R.color.blue));
        couponExpiration.setTextColor(getResources().getColor(R.color.blue));
        break;

      case 2:
        couponCode.setTextColor(getResources().getColor(R.color.red));
        couponExpiration.setTextColor(getResources().getColor(R.color.red));
        break;

      case 3:
        couponCode.setTextColor(getResources().getColor(R.color.green));
        couponExpiration.setTextColor(getResources().getColor(R.color.green));
        break;

      case 4:
        couponCode.setTextColor(getResources().getColor(R.color.yellow));
        couponExpiration.setTextColor(getResources().getColor(R.color.yellow));
        break;
    }
  }
}
