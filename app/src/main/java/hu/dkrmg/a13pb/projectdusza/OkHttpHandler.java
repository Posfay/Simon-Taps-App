package hu.dkrmg.a13pb.projectdusza;

import java.io.IOException;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpHandler {

  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  public static String post(String url, JSONObject json) throws IOException {

    OkHttpClient client = new OkHttpClient();
    RequestBody body = RequestBody.create(JSON, json.toString());
    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();
    try (Response response = client.newCall(request).execute()) {
      return response.body().string();
    }
  }

  private OkHttpHandler() {
  }
}
