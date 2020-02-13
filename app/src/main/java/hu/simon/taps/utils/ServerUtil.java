package hu.simon.taps.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import hu.simon.taps.R;

public class ServerUtil {

  public static final String PROTOCOL = "http://";

  public static final String HOSTNAME = "szerver3.dkrmg.sulinet.hu";

  public static final Long PORT = 8081L;

  public static final String AUTHENTICATION_KEY = "rEJ1ME7MaojA4CPIOG2wJ3tX4U7lHAIgJqX6XGT3";

  public static final String AUTHENTICATION_HEADER = "Simon-Auth";

  public static final long WAITING_STATE_REQUEST_INTERVAL = 1000;

  public static final long GAME_STATE_REQUEST_INTERVAL = 250;

  public static final long END_SCREEN_REQUEST_INTERVAL = 1000;

  public static final String PLAYER_ID = getWifiMacAddress();

  public enum Endpoint {
    CREATE("create"), JOIN("join"), LEAVE("leave"), STATE("state"), START("start"), GAME(
        "game"), VERSION("version"), RESTART("restart"), COUPON("coupon");

    private String value;

    Endpoint(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }
  }

  public enum ResponseParameter {
    STATUS("status"), NUMBER_OF_PLAYERS("numberOfPlayers"), LEFT("left"), TILE_ID(
        "tileId"), PATTERN(
            "pattern"), REASON("reason"), GAME_STATE("gameState"), COMPATIBLE(
                "compatible"), NUMBER_OF_RESTART_PLAYERS("numberOfRestartPlayers"), COUPON("coupon"), COUPONS("coupons");

    private String value;

    ResponseParameter(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }
  }

  public enum RequestParameter {
    ROOM_ID("roomId"), PLAYER_ID("playerId");

    private String value;

    RequestParameter(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }
  }

  public enum State {
    WAITING("1_waiting"), PREPARING("2_preparing"), SHOWING_PATTERN("3_showing_pattern"), PLAYING(
        "4_playing"), END("5_end");

    private String value;

    State(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }
  }

  // Checking internet connection
  public static boolean connectionCheck(Activity myActivity) {

    ConnectivityManager connectivityManager =
        (ConnectivityManager) myActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

    // we are connected to a network
    return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        .getState() == NetworkInfo.State.CONNECTED ||
        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            .getState() == NetworkInfo.State.CONNECTED;
  }

  public static String getWifiMacAddress() {
    try {
      List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
      for (NetworkInterface intf : interfaces) {
        if (! intf.getName().equalsIgnoreCase("wlan0")){
          continue;
        }

        byte[] mac = intf.getHardwareAddress();
        if (mac==null){
          return "";
        }

        StringBuilder buf = new StringBuilder();
        for (byte aMac : mac) {
          buf.append(String.format("%02X-", aMac));
        }
        if (buf.length()>0) {
          buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
      }
    } catch (Exception e) { }
    return "";
  }

  private ServerUtil() {
  }
}
