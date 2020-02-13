package hu.simon.taps.activities;

import androidx.appcompat.app.AppCompatActivity;
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
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CouponsActivity extends AppCompatActivity implements AsyncResponse {

  public OkHttpHandler okHttpHandler;
  public OkHttpClient client;

  Vibrator vibrator;
  Toolbar toolbar;
  ListView couponList;

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

    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, coupons);
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
}
