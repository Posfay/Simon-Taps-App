package hu.dkrmg.a13pb.projectdusza;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import okhttp3.OkHttpClient;

public class MainActivity extends Activity implements AsyncResponse {

  public TextView textView;
  public EditText roomIdEditText;

  public OkHttpHandler okHttpHandler;
  public String roomId;
  public String playerId;

  public OkHttpClient client;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    textView = findViewById(R.id.textout);
    roomIdEditText = findViewById(R.id.editText);

    client = new OkHttpClient();
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

    okHttpHandler = new OkHttpHandler(this, client);
    okHttpHandler.postRequest(url, payloadJson);
  }

  public void gameOnClick(View v) {
    Intent intent = new Intent(this, GameActivity.class);
    startActivity(intent);
  }

  String status;
  String reason;

  @Override
  public void onRequestComplete(String responseJsonString) {

    status = null;
    reason = null;
    JSONObject payloadJson = null;
    long num = -1;

    try {
      payloadJson = new JSONObject(responseJsonString);
      status = payloadJson.getString("status");

    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (status.equals("OK")) {
      num = payloadJson.optLong("number_of_players");
      textView.setText(num + "");
      Intent intent = new Intent(getBaseContext(), GameActivity.class);
      Log.i("name", playerId);
      intent.putExtra("EXTRA_PLAYER_ID", playerId);
      intent.putExtra("EXTRA_ROOM_ID", roomId);
      startActivity(intent);
    } else {
      reason = payloadJson.optString("reason");
      textView.setText(reason);
    }
  }
}
