package hu.dkrmg.a13pb.projectdusza;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewStructure;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.OkHttpClient;

public class MainActivity extends Activity implements AsyncResponse {

  //--------------------------------------DECLARING VARIABLES---------------------------------------
  public OkHttpClient client;
  public OkHttpHandler okHttpHandler;

  public TextView textView;
  public EditText roomIdEditText;
  public String roomId;
  public String playerId;
  Boolean connected = false;
  String status;
  String reason;

  public static final String BASE_URL = ServerUtil.PROTOCOL+ServerUtil.HOSTNAME+":"+ServerUtil.PORT+"/";

  public Vibrator vibrator;
  private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    textView = findViewById(R.id.textout);
    roomIdEditText = findViewById(R.id.editText);

    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    //Always uppercase in textbox
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

  //Checking internet connection
  public void connectionCheck() {
    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
      //we are connected to a network
      connected = true;
    }
    else {
      connected = false;
      alertDialog();
    }
    Log.i("connected",connected.toString() );
  }

  //Alert Dialog when there's no internet
  public void alertDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("No internet connection!");
    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        vibrator.vibrate(250);
      }
    });
    builder.setCancelable(false);
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  //Joining room
  public void joinClick(View v) {

    connectionCheck();
    if (!connected) {
      //no internet
    }

    if (connected) {
      Log.i("internet", connected.toString());
      Log.i("request","sent");
      String url = BASE_URL+ServerUtil.Endpoint.JOIN.toString();
      JSONObject payloadJson = new JSONObject();
      roomId = roomIdEditText.getText().toString();
      playerId = UUID.randomUUID().toString();

      if (roomId.equals("") || (roomId.length() != 5)) {
        Toast.makeText(this, "Invalid room name", Toast.LENGTH_LONG).show();
        v.startAnimation(buttonClick);
        vibrator.vibrate(250);
        return;
      }

      try {
        payloadJson.put(ServerUtil.RequestParameter.ROOM_ID.toString(), roomId);
        payloadJson.put(ServerUtil.RequestParameter.PLAYER_ID.toString(), this.playerId);
      } catch (JSONException e) {
        e.printStackTrace();
      }

      okHttpHandler = new OkHttpHandler(this, client);
      okHttpHandler.postRequest(url, payloadJson);
    }

    v.startAnimation(buttonClick);
    vibrator.vibrate(250);
  }

  //Creating room
  public void createClick(View v){

    connectionCheck();
    if (!connected) {
      //no internet
    }

    if (connected) {
    String url = BASE_URL+ServerUtil.Endpoint.CREATE.toString();
      JSONObject payloadJson = new JSONObject();
      roomId = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
      playerId = UUID.randomUUID().toString();

      try {
        payloadJson.put(ServerUtil.RequestParameter.ROOM_ID.toString(), roomId);
        payloadJson.put(ServerUtil.RequestParameter.PLAYER_ID.toString(), this.playerId);
      } catch (JSONException e) {
        e.printStackTrace();
      }

      okHttpHandler = new OkHttpHandler(this, client);
      okHttpHandler.postRequest(url, payloadJson);
    }

    v.startAnimation(buttonClick);
    vibrator.vibrate(250);
  }

  //Open settings
  public void settingsClick(View v) {
    finish();
    Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
    startActivity(intent);

    v.startAnimation(buttonClick);
    vibrator.vibrate(250);
  }

  //Successful request
  @Override
  public void onRequestComplete(String responseJsonString) {

    status = null;
    reason = null;
    JSONObject payloadJson = null;

    try {
      Log.i("JoinResponse", responseJsonString);

      payloadJson = new JSONObject(responseJsonString);
      status = payloadJson.getString(ServerUtil.ResponseParameter.STATUS.toString());

    } catch (JSONException e) {
      e.printStackTrace();
    }


    connectionCheck();
    if ((connected) && (status.equals("OK"))) {

      Intent intent = new Intent(getBaseContext(), GameActivity.class);
      Log.i("name", playerId);

      finish();
      intent.putExtra("EXTRA_PLAYER_ID", playerId);
      intent.putExtra("EXTRA_ROOM_ID", roomId);
      startActivity(intent);
    } else {
      reason = payloadJson.optString(ServerUtil.ResponseParameter.REASON.toString());
      textView.setText(reason);
    }
  }
}
