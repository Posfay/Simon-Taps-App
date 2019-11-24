package hu.dkrmg.a13pb.projectdusza;

public class ServerUtil {

    public static String PROTOCOL = "http://";
    public static String HOSTNAME = "szerver3.dkrmg.sulinet.hu";
    public static Long PORT = 8081L;
    public static enum Endpoint {
        CREATE("create"), JOIN("join"), LEAVE("leave"), STATE("state"), START("start"), GAME("game");

        private String value;
        Endpoint(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    };
    public static enum ResponseParameter {
        STATUS("status"), NUMBER_OF_PLAYERS("numberOfPlayers"), LEFT("left"), TILE_ID("tileId"), PATTERN("pattern"), REASON("reason"), GAME_STATE("gameState");

        private String value;
        ResponseParameter(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    };
    public static enum RequestParameter {
        ROOM_ID("roomId"), PLAYER_ID("playerId");

        private String value;
        RequestParameter(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    };
    public static enum State {
        WAITING("1_waiting"), PREPARING("2_preparing"), SHOWING_PATTERN("3_showing_pattern"), PLAYING("4_playing"), SUCCESSFUL_END("5_successful_end"), FAIL_END("5_fail_end");

        private String value;
        State(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
    private ServerUtil() { }
}
