package hu.dkrmg.a13pb.projectdusza;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity {

  public TextView textView;

  public Handler handler;

  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    textView = findViewById(R.id.textout);

    handler = new Handler(Looper.getMainLooper());
  }

  public void requestClick(View v) {

    JSONObject json = new JSONObject();
    String url = "http://szerver3.dkrmg.sulinet.hu:8080/TESZT/firsttest";

    postRequest(url, json);
  }

  public void postRequest(String url, JSONObject json) {

    OkHttpClient client = new OkHttpClient();
    Request request = createRequest(url, json);

    client.newCall(request).enqueue(new OkHttpCallback());

  }

  public class OkHttpCallback implements Callback {

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
      String mMessage = e.getMessage();
      Log.w("failure Response", mMessage);
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull final Response response) {
      Log.i("response", response.toString());

      if (response.isSuccessful() && response.code() == 200) {

        handler.post(new Runnable() {
          @Override
          public void run() {
            parseResponse(response);
          }
        });
      }
    }
  }

  public void parseResponse(Response response) {

    JSONObject responseJson;

    try {

      responseJson = new JSONObject(response.body().string());
      textView.setText(responseJson.toString(4));

      Long number = responseJson.getLong("szam");

      if (number > 0) {
        textView.setText(number.toString());
      }

    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }

  }

  private Request createRequest(String url, JSONObject json) {
    RequestBody body = RequestBody.create(JSON, json.toString());
    return new Request.Builder()
        .url(url)
        .post(body)
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .build();
  }
}
