package hu.simon.taps.activities;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import hu.simon.taps.BuildConfig;
import hu.simon.taps.R;
import hu.simon.taps.fragments.SettingsFragment;
import hu.simon.taps.http.handler.AsyncResponse;
import hu.simon.taps.http.handler.OkHttpHandler;
import hu.simon.taps.utils.LanguageUtil;
import hu.simon.taps.utils.ScreenUtil;
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

  Button joinButton;
  Button createButton;
  Button settingsButton;
  Button notAButton;

  Random randomBetweenOneFour = new Random();

  String roomId;
  String status;
  String reason;
  String joinButtonText;

  boolean versionChecked = false;
  boolean joinButtonVisible = true;

  private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);

  private class UppercaseTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      // pass
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

      checkJoinButtonAnimation();
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

    Log.i("playerid", ServerUtil.PLAYER_ID);

    Log.i("lang", Locale.getDefault().getDisplayLanguage());

    // Changing language
    Configuration mainConfiguration = new Configuration(getResources().getConfiguration());
    getResources().updateConfiguration(LanguageUtil.preferredLanguage(this, mainConfiguration),
        getResources().getDisplayMetrics());

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String backgroundState = prefs.getString(SettingsFragment.PREF_BACKGROUND_COLOR, "dark");

    ScreenUtil.setFlags(this, this);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    roomIdEditText = findViewById(R.id.editText);
    joinButton = findViewById(R.id.joinButton);
    notAButton = findViewById(R.id.unusedLayoutButtonShape);
    createButton = findViewById(R.id.createButton);
    settingsButton = findViewById(R.id.settingsButton);

    if (backgroundState.equals("dark")) {

      settingsButton.setBackground(getDrawable(R.drawable.settings_icon_light));

    } else if (backgroundState.equals("light")) {

      settingsButton.setBackground(getDrawable(R.drawable.settings_icon));
    }

    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    client = new OkHttpClient();

    roomIdEditText.addTextChangedListener(new UppercaseTextWatcher());

    createButton.setEnabled(false);
    joinButton.setEnabled(false);
    settingsButton.setEnabled(false);

    createButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        createClick(v);
      }
    });
    joinButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        joinClick(v);
      }
    });
    settingsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        settingsClick(v);
      }
    });

    joinButtonText = getString(R.string.join_button);

    randomButtonColor();

    boolean connected = checkInternetOnCreate();

    if (connected) {
      checkVersionOnCreate();
    }

    checkJoinButtonAnimation();
  }

  public void checkJoinButtonAnimation() {

    if ((!joinButtonVisible) && (roomIdEditText.getText().length() == 5)) {

      ConstraintLayout constraintLayout = findViewById(R.id.constraintLayoutMain);

      ConstraintSet constraintSet = new ConstraintSet();
      constraintSet.clone(constraintLayout);
      constraintSet.setGuidelinePercent(R.id.animatedGuideline, 0.5f);
      constraintSet.applyTo(constraintLayout);

      TransitionManager.beginDelayedTransition(constraintLayout);

      joinButton.setText(joinButtonText);

      joinButtonVisible = !joinButtonVisible;

    } else if ((joinButtonVisible) && (roomIdEditText.getText().length() < 5)) {

      ConstraintLayout constraintLayout = findViewById(R.id.constraintLayoutMain);

      ConstraintSet constraintSet = new ConstraintSet();
      constraintSet.clone(constraintLayout);
      constraintSet.setGuidelinePercent(R.id.animatedGuideline, 0.99f);
      constraintSet.applyTo(constraintLayout);

      TransitionManager.beginDelayedTransition(constraintLayout);

      joinButton.setText("");

      joinButtonVisible = !joinButtonVisible;
    }
  }

  public void randomButtonColor() {

    int randomSwitchNum = randomBetweenOneFour.nextInt(5 - 1) + 1;

    switch (randomSwitchNum) {

      case 1:
        ViewCompat.setBackgroundTintList(notAButton,
            ContextCompat.getColorStateList(this, R.color.blue));
        ViewCompat.setBackgroundTintList(joinButton,
            ContextCompat.getColorStateList(this, R.color.blue));
        ViewCompat.setBackgroundTintList(createButton,
            ContextCompat.getColorStateList(this, R.color.blue));
        createButton.setTextColor(getResources().getColor(R.color.blue));
        break;

      case 2:
        ViewCompat.setBackgroundTintList(notAButton,
            ContextCompat.getColorStateList(this, R.color.red));
        ViewCompat.setBackgroundTintList(joinButton,
            ContextCompat.getColorStateList(this, R.color.red));
        ViewCompat.setBackgroundTintList(createButton,
            ContextCompat.getColorStateList(this, R.color.red));
        createButton.setTextColor(getResources().getColor(R.color.red));
        break;

      case 3:
        ViewCompat.setBackgroundTintList(notAButton,
            ContextCompat.getColorStateList(this, R.color.green));
        ViewCompat.setBackgroundTintList(joinButton,
            ContextCompat.getColorStateList(this, R.color.green));
        ViewCompat.setBackgroundTintList(createButton,
            ContextCompat.getColorStateList(this, R.color.green));
        createButton.setTextColor(getResources().getColor(R.color.green));
        break;

      case 4:
        ViewCompat.setBackgroundTintList(notAButton,
            ContextCompat.getColorStateList(this, R.color.yellow));
        ViewCompat.setBackgroundTintList(joinButton,
            ContextCompat.getColorStateList(this, R.color.yellow));
        ViewCompat.setBackgroundTintList(createButton,
            ContextCompat.getColorStateList(this, R.color.yellow));
        createButton.setTextColor(getResources().getColor(R.color.yellow));
        break;
    }
  }

  private boolean checkInternetOnCreate() {

    boolean connected = ServerUtil.connectionCheck(this);

    if (!connected) {

      alertDialog(getString(R.string.no_internet), true);
      return false;
    }

    return true;
  }

  private void checkVersionOnCreate() {

    okHttpHandler = new OkHttpHandler(this, client);
    okHttpHandler.getRequest(BASE_URL + ServerUtil.Endpoint.VERSION.toString() + "/" + VERSION);
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

    boolean connected = ServerUtil.connectionCheck(this);

    if (!connected) {

      alertDialog(getString(R.string.no_internet), false);
      return;
    }

    Log.i("request", "sent");

    String url = BASE_URL + ServerUtil.Endpoint.JOIN.toString();

    JSONObject payloadJson = new JSONObject();

    roomId = roomIdEditText.getText().toString();

    if (roomId.equals("") || (roomId.length() != 5)) {

      Toast.makeText(this, getString(R.string.invalid_room), Toast.LENGTH_SHORT).show();
      return;
    }

    createButton.setEnabled(false);
    joinButton.setEnabled(false);
    settingsButton.setEnabled(false);

    try {
      payloadJson.put(ServerUtil.RequestParameter.ROOM_ID.toString(), roomId);
      payloadJson.put(ServerUtil.RequestParameter.PLAYER_ID.toString(), ServerUtil.PLAYER_ID);
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

    createButton.setEnabled(false);
    joinButton.setEnabled(false);
    settingsButton.setEnabled(false);

    boolean connected = ServerUtil.connectionCheck(this);

    if (!connected) {

      alertDialog(getString(R.string.no_internet), false);
      return;
    }

    String url = BASE_URL + ServerUtil.Endpoint.CREATE.toString();

    JSONObject payloadJson = new JSONObject();

    roomId = UUID.randomUUID().toString().substring(0, 5).toUpperCase();

    try {
      payloadJson.put(ServerUtil.RequestParameter.ROOM_ID.toString(), roomId);
      payloadJson.put(ServerUtil.RequestParameter.PLAYER_ID.toString(), ServerUtil.PLAYER_ID);
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
      builder.setMessage(getString(R.string.exit));
      builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          VibrationUtil.preferredVibration(MainActivity.this, vibrator);
          finish();
          android.os.Process.killProcess(android.os.Process.myPid());
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

    joinButton.setEnabled(true);

    JSONObject payloadJson;
    status = null;
    reason = null;

    try {

      if (responseJsonString != null) {
        Log.i("JoinResponse", responseJsonString);
      }

      payloadJson = new JSONObject(responseJsonString);

      status = payloadJson.getString(ServerUtil.ResponseParameter.STATUS.toString());

    } catch (JSONException e) {

      Toast.makeText(this, ServerUtil.UNKNOWN_SERVER_ERROR, Toast.LENGTH_SHORT).show();
      return;
    }

    if (!versionChecked) {

      versionChecked = true;

      boolean compatible =
          payloadJson.optBoolean(ServerUtil.ResponseParameter.COMPATIBLE.toString());

      if (compatible) {

        createButton.setEnabled(true);
        joinButton.setEnabled(true);
        settingsButton.setEnabled(true);

      } else {

        alertDialog(getString(R.string.outdated), true);
      }
    } else if ("OK".equals(status)) { // Create/Join endpoint

      finish();

      Intent intent = new Intent(getBaseContext(), GameActivity.class);
      intent.putExtra("EXTRA_ROOM_ID", roomId);
      startActivity(intent);
    } else {

      reason = payloadJson.optString(ServerUtil.ResponseParameter.REASON.toString());

      if (reason.equals("ROOM_ALREADY_EXISTS")) {
        reason = getString(R.string.room_exists);
      }
      if (reason.equals("ROOM_DOES_NOT_EXIST")) {
        reason = getString(R.string.room_not_exists);
      }
      if (reason.equals("ROOM_IS_FULL")) {
        reason = getString(R.string.room_full);
      }
      if (reason.equals("")) {
        reason = ServerUtil.UNKNOWN_SERVER_ERROR;
      }

      Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
    }
  }

}
