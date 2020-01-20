package hu.simon.taps;

public interface AsyncResponse {

  void onRequestComplete(String responseJsonString);

}
