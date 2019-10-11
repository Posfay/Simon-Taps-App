package hu.dkrmg.a13pb.projectdusza;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity {

  public TextView textView;

  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    textView = findViewById(R.id.textout);
  }

  public void requestClick(View v) {

    JSONObject json = new JSONObject();
    String url = "http://szerver3.dkrmg.sulinet.hu:8080/TESZT/firsttest";

    postRequest(url, json);
  }

  public void postRequest(String url, JSONObject json) {

    Request request = createRequest(url, json);

    new OkHttpAsyncTask().execute(request);

  }

  public void parseResponse(String responseJsonString) {

    JSONObject responseJson;

    try {

      responseJson = new JSONObject(responseJsonString);
      Long number = responseJson.getLong("szam");

      if (number > 0) {
        textView.setText(number.toString());
      }

    } catch (JSONException e) {
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

  public class OkHttpAsyncTask extends AsyncTask<Request, Void, String> {

    @Override
    protected String doInBackground(Request... request) {

      OkHttpClient client = new OkHttpClient();

      try {

        Response response = client.newCall(request[0]).execute();

        if (response.isSuccessful() && response.code() == 200) {

          return response.body().string();
        } else {

          return "{}";
        }

      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    }

    @Override
    protected void onPostExecute(String responseJsonString) {

      parseResponse(responseJsonString);
    }
  }

}
