package hu.dkrmg.a13pb.projectdusza;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
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

  private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.75F);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    textView = findViewById(R.id.textout);
    roomIdEditText = findViewById(R.id.editText);

    roomIdEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable et) {
        String s=et.toString();
        if(!s.equals(s.toUpperCase()))
        {
          s=s.toUpperCase();
          roomIdEditText.setText(s);
          roomIdEditText.setSelection(roomIdEditText.length());
        }
      }
    });

    client = new OkHttpClient();
  }

  public void requestClick(View v) {

    v.startAnimation(buttonClick);

    String url = "http://szerver3.dkrmg.sulinet.hu:8081/join";
    JSONObject payloadJson = new JSONObject();
    roomId = roomIdEditText.getText().toString();
    playerId = UUID.randomUUID().toString();

    try {
      payloadJson.put("roomId", roomId);
      payloadJson.put("playerId", this.playerId);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    okHttpHandler = new OkHttpHandler(this, client);
    okHttpHandler.postRequest(url, payloadJson);
  }

  public void createClick(View v){

    v.startAnimation(buttonClick);

    String url = "http://szerver3.dkrmg.sulinet.hu:8081/join";
    JSONObject payloadJson = new JSONObject();
    roomId = UUID.randomUUID().toString().substring(0,5).toUpperCase();
    playerId = UUID.randomUUID().toString();

    try {
      payloadJson.put("roomId", roomId);
      payloadJson.put("playerId", this.playerId);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    okHttpHandler = new OkHttpHandler(this, client);
    okHttpHandler.postRequest(url, payloadJson);
  }

  String status;
  String reason;

  @Override
  public void onRequestComplete(String responseJsonString) {

    status = null;
    reason = null;
    JSONObject payloadJson = null;

    try {
      Log.i("JoinResponse", responseJsonString);

      payloadJson = new JSONObject(responseJsonString);
      status = payloadJson.getString("status");

    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (status.equals("OK")) {

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
