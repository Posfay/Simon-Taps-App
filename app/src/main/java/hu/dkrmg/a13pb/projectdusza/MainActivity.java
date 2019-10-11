package hu.dkrmg.a13pb.projectdusza;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements AsyncResponse {

  public TextView textView;

  public static Date requestStartingAt;

  public OkHttpHandler okHttpHandler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    textView = findViewById(R.id.textout);
  }

  public void requestClick(View v) {

    String url = "http://szerver3.dkrmg.sulinet.hu:8080/TESZT/FirstTest";
    JSONObject payloadJson = new JSONObject();

    okHttpHandler = new OkHttpHandler(this);
    okHttpHandler.postRequest(url, payloadJson);
  }

  public void parseResponse(String responseJsonString, long latency) {

    try {
      JSONObject responseJson = new JSONObject(responseJsonString);
      Long number = responseJson.getLong("szam");

      if (number > 0) {
        textView.setText(number + "\n" + latency + " ms");
      } else {
        textView.setText(0 + "\n" + latency + " ms");
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onRequestComplete(String responseJsonString) {

    Date receivedResponseAt = new Date();
    long latency = receivedResponseAt.getTime() - requestStartingAt.getTime();

    Log.i("latency", latency + " ms");

    parseResponse(responseJsonString, latency);
  }
}
