package hu.simon.taps.activities;

import java.util.Random;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import hu.simon.taps.BuildConfig;
import hu.simon.taps.R;
import hu.simon.taps.http.handler.AsyncResponse;
import hu.simon.taps.http.handler.OkHttpHandler;
import hu.simon.taps.utils.GameUtil;
import hu.simon.taps.utils.LayoutUtil;
import hu.simon.taps.utils.ServerUtil;
import hu.simon.taps.utils.VibrationUtil;
import okhttp3.OkHttpClient;

public class MainActivity extends Activity implements AsyncResponse {

  // --------------------------------------DECLARING VARIABLES--------------------------------------
  public static final String BASE_URL =
      ServerUtil.PROTOCOL + ServerUtil.HOSTNAME + ":" + ServerUtil.PORT + "/";

  public static final String VERSION = BuildConfig.VERSION_NAME;

  public OkHttpClient client;
  public OkHttpHandler okHttpHandler;

  EditText roomIdEditText;

  Vibrator vibrator;

  Button joinBut;
  Button createBut;
  Button settingsBut;

  Random randomBetweenOneFour = new Random();

  String roomId;
  String playerId;
  String status;
  String reason;

  boolean versionChecked = false;

  private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);

  private class UppercaseTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      // pass
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      // pass
    }

    @Override
    public void afterTextChanged(Editable et) {

      String s = et.toString();

      if (!s.equals(s.toUpperCase())) {

        s = s.toUpperCase();
        roomIdEditText.setText(s);
        roomIdEditText.setSelection(roomIdEditText.length());
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    roomIdEditText = findViewById(R.id.editText);
    joinBut = findViewById(R.id.joinButton);
    createBut = findViewById(R.id.createButton);
    settingsBut = findViewById(R.id.settingsButton);

    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    client = new OkHttpClient();

    roomIdEditText.addTextChangedListener(new UppercaseTextWatcher());

    createBut.setEnabled(true);

    createBut.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        createClick(v);
      }
    });
    joinBut.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        joinClick(v);
      }
    });
    settingsBut.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        settingsClick(v);
      }
    });

    randomButtonColor();

    boolean connected = checkInternetOnCreate();

    if (connected) {
      checkVersionOnCreate();
    }
  }

  public void randomButtonColor() {

    int randomSwitchNum = randomBetweenOneFour.nextInt(5 - 1) + 1;
    switch (randomSwitchNum) {
      case 1:
        ViewCompat.setBackgroundTintList(joinBut,
            ContextCompat.getColorStateList(this, R.color.blue));
        ViewCompat.setBackgroundTintList(createBut,
            ContextCompat.getColorStateList(this, R.color.blue));
        createBut.setTextColor(getResources().getColor(R.color.blue));
        // "#00b0ff" alt. blue
        break;
      case 2:
        ViewCompat.setBackgroundTintList(joinBut,
            ContextCompat.getColorStateList(this, R.color.red));
        ViewCompat.setBackgroundTintList(createBut,
            ContextCompat.getColorStateList(this, R.color.red));
        createBut.setTextColor(getResources().getColor(R.color.red));
        // #f44336" alt. red
        break;
      case 3:
        ViewCompat.setBackgroundTintList(joinBut,
            ContextCompat.getColorStateList(this, R.color.green));
        ViewCompat.setBackgroundTintList(createBut,
            ContextCompat.getColorStateList(this, R.color.green));
        createBut.setTextColor(getResources().getColor(R.color.green));
        // "#64dd17" alt. green
        break;
      case 4:
        ViewCompat.setBackgroundTintList(joinBut,
            ContextCompat.getColorStateList(this, R.color.yellow));
        ViewCompat.setBackgroundTintList(createBut,
            ContextCompat.getColorStateList(this, R.color.yellow));
        createBut.setTextColor(getResources().getColor(R.color.yellow));
        // "#ffff00" alt. yellow
        break;
    }
  }

  private boolean checkInternetOnCreate() {

    boolean connected = connectionCheck();

    if (!connected) {

      alertDialog(GameUtil.NO_INTERNET_CONNECTION, true);
      return false;
    }

    return true;
  }

  private void checkVersionOnCreate() {

    okHttpHandler = new OkHttpHandler(this, client);
    okHttpHandler.getRequest(BASE_URL + ServerUtil.Endpoint.VERSION.toString() + "/" + VERSION);
  }

  // fullscreen
  public void onWindowFocusChanged(boolean hasFocus) {

    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) {
      View decorView = getWindow().getDecorView();
      LayoutUtil.hideSystemUI(decorView);
    }
  }

  // Checking internet connection
  private boolean connectionCheck() {

    ConnectivityManager connectivityManager =
        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

    // we are connected to a network
    return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        .getState() == NetworkInfo.State.CONNECTED ||
        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            .getState() == NetworkInfo.State.CONNECTED;
  }

  // Alert Dialog
  private void alertDialog(String message, boolean exitActivity) {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(message);

    if (exitActivity) {

      builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          finish();
        }
      });
    } else {

      builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          VibrationUtil.preferredVibration(MainActivity.this, vibrator);
        }
      });
    }

    builder.setCancelable(false);

    AlertDialog dialog = builder.create();

    dialog.show();
  }

  // Joining room
  private void joinClick(View v) {

    v.startAnimation(buttonClick);
    VibrationUtil.preferredVibration(MainActivity.this, vibrator);

    boolean connected = connectionCheck();

    if (!connected) {

      alertDialog(GameUtil.NO_INTERNET_CONNECTION, false);
      return;
    }

    Log.i("request", "sent");

    String url = BASE_URL + ServerUtil.Endpoint.JOIN.toString();

    JSONObject payloadJson = new JSONObject();

    roomId = roomIdEditText.getText().toString();
    playerId = UUID.randomUUID().toString();

    if (roomId.equals("") || (roomId.length() != 5)) {

      Toast.makeText(this, "Invalid room name", Toast.LENGTH_SHORT).show();
      return;
    }

    joinBut.setEnabled(false);

    try {
      payloadJson.put(ServerUtil.RequestParameter.ROOM_ID.toString(), roomId);
      payloadJson.put(ServerUtil.RequestParameter.PLAYER_ID.toString(), this.playerId);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    okHttpHandler = new OkHttpHandler(this, client);
    okHttpHandler.postRequest(url, payloadJson);
  }

  // Creating room
  private void createClick(View v) {

    v.startAnimation(buttonClick);
    VibrationUtil.preferredVibration(MainActivity.this, vibrator);

    createBut.setEnabled(false);

    boolean connected = connectionCheck();

    if (!connected) {

      alertDialog(GameUtil.NO_INTERNET_CONNECTION, false);
      return;
    }

    String url = BASE_URL + ServerUtil.Endpoint.CREATE.toString();

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

  // Open settings
  private void settingsClick(View v) {

    v.startAnimation(buttonClick);
    VibrationUtil.preferredVibration(MainActivity.this, vibrator);

    finish();

    Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
    startActivity(intent);
  }

  // BACK BUTTON PRESSED
  public boolean onKeyDown(int keyCode, KeyEvent event) {

    if (keyCode == KeyEvent.KEYCODE_BACK) {

      VibrationUtil.preferredVibration(MainActivity.this, vibrator);

      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Do you want to exit?");
      builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          VibrationUtil.preferredVibration(MainActivity.this, vibrator);
          finish();
          System.exit(0);
        }
      });
      builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          VibrationUtil.preferredVibration(MainActivity.this, vibrator);
        }
      });
      builder.setCancelable(false);

      AlertDialog dialog = builder.create();

      dialog.show();

      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  // Successful request
  @Override
  public void onRequestComplete(String responseJsonString) {

    joinBut.setEnabled(true);

    JSONObject payloadJson = null;
    status = null;
    reason = null;

    try {
      Log.i("JoinResponse", responseJsonString);

      payloadJson = new JSONObject(responseJsonString);

      status = payloadJson.getString(ServerUtil.ResponseParameter.STATUS.toString());

    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (!versionChecked) {

      versionChecked = true;

      boolean compatible =
          payloadJson.optBoolean(ServerUtil.ResponseParameter.COMPATIBLE.toString());

      if (!compatible) {

        alertDialog(GameUtil.OUTDATED_VERSION, true);
      }
    } else if (status.equals("OK")) {

      Log.i("name", playerId);

      finish();

      Intent intent = new Intent(getBaseContext(), GameActivity.class);
      intent.putExtra("EXTRA_PLAYER_ID", playerId);
      intent.putExtra("EXTRA_ROOM_ID", roomId);
      startActivity(intent);
    } else {

      reason = payloadJson.optString(ServerUtil.ResponseParameter.REASON.toString());

      if (reason.equals("ROOM_ALREADY_EXISTS")) {
        reason = "Room already exists";
      }
      if (reason.equals("ROOM_DOES_NOT_EXIST")) {
        reason = "Room does not exist";
      }
      if (reason.equals("ROOM_IS_FULL")) {
        reason = "Room is full";
      }

      Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
    }
  }
}
