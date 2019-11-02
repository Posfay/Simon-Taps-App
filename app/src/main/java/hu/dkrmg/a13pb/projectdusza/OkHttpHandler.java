package hu.dkrmg.a13pb.projectdusza;

import java.io.IOException;

import org.json.JSONObject;

import android.os.AsyncTask;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpHandler extends AsyncTask<Request, Void, String> {

  private AsyncResponse asyncResponse;

  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  @Override
  protected String doInBackground(Request... request) {

    OkHttpClient client = new OkHttpClient();

    try (Response response = client.newCall(request[0]).execute()) {

      if (response.isSuccessful() && response.code() == 200) {

        return response.body().string();
      } else {

        return new JSONObject().toString();
      }

    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  protected void onPostExecute(String responseJsonString) {

    asyncResponse.onRequestComplete(responseJsonString);

  }

  public void postRequest(String url, JSONObject json) {

    Request request = createPostRequest(url, json);

    this.execute(request);
  }

  public void getRequest(String url) {

    Request request = createGetRequest(url);

    this.execute(request);
  }

  private Request createPostRequest(String url, JSONObject json) {

    RequestBody body = RequestBody.create(JSON, json.toString());
    return new Request.Builder()
        .url(url)
        .post(body)
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .build();
  }

  private Request createGetRequest(String url) {

    return new Request.Builder()
        .url(url)
        .build();
  }

  public OkHttpHandler(AsyncResponse asyncResponse) {
    this.asyncResponse = asyncResponse;
  }
}
