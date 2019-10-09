package hu.dkrmg.a13pb.projectdusza;

import java.io.IOException;

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

  public TextView text;

  private Handler handler;

  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    text = findViewById(R.id.textout);

    handler = new Handler(Looper.getMainLooper());
  }

  public void requestClick(View v) {

    JSONObject json = new JSONObject();
    String url = "http://example.com";

    postRequest(url, json);
  }

  public void postRequest(String url, JSONObject json) {

    OkHttpClient client = new OkHttpClient();
    Request request = createRequest(url, json);

    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        String mMessage = e.getMessage();
        Log.w("failure Response", mMessage);
      }

      @Override
      public void onResponse(Call call, final Response response) {
        Log.i("response", response.toString());

        if (response.isSuccessful() && response.code() == 200) {

          handler.post(new Runnable() {

            @Override
            public void run() {
              try {
                text.setText(response.body().string());
              } catch (IOException e) {
                Log.e("request parsing error", "error: ", e);
              }
            }
          });
        }
      }
    });

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
