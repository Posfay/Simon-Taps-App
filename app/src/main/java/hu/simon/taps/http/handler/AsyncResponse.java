package hu.simon.taps.http.handler;

public interface AsyncResponse {

  void onRequestComplete(String responseJsonString);

}
