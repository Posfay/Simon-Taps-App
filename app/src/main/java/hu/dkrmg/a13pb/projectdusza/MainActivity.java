package hu.dkrmg.a13pb.projectdusza;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.service.voice.AlwaysOnHotwordDetector;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

public class MainActivity extends Activity implements AsyncResponse {

  public TextView textView;
  public EditText roomIdEditText;

  public OkHttpHandler okHttpHandler;
  public String roomId;
  public String playerId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    textView = findViewById(R.id.textout);
    roomIdEditText = findViewById(R.id.editText);
  
  }

  public void requestClick(View v) {

    String url = "http://szerver3.dkrmg.sulinet.hu:8080/simon-taps/join";
    JSONObject payloadJson = new JSONObject();
    roomId = roomIdEditText.getText().toString();
    playerId = UUID.randomUUID().toString();

    try {
      payloadJson.put("room_id", roomId);
      payloadJson.put("player_id", this.playerId);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    okHttpHandler = new OkHttpHandler(this);
    okHttpHandler.postRequest(url, payloadJson);
  }

  public void gameOnClick(View v) {
    Intent intent = new Intent(this, GameActivity.class);
    startActivity(intent);
  }

  public void getStateClick(View v) {

    String url = "https://szerver3.dkrmg.sulinet.hu:8080/simon-taps/state?room_id="+roomId+"&player_id="+playerId;

  }

  @Override
  public void onRequestComplete(String responseJsonString) {

    String status = null;
    String reason = null;
    JSONObject payloadJson = null;
    long num = -1;

    try {
      payloadJson = new JSONObject(responseJsonString);
      Log.i("response",responseJsonString);
      Log.i("responseJson",payloadJson.toString());
      status = payloadJson.getString("status");

    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (status.equals("OK")) {
      num = payloadJson.optLong("number_of_players");
      textView.setText(num+"");
      Intent intent = new Intent(this, GameActivity.class);
      startActivity(intent);
    }
    else {
      reason = payloadJson.optString("reason");
      textView.setText(reason);
    }

  }
}
