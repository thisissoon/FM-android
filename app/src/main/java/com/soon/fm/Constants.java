package com.soon.fm;

public class Constants {

    public static final String FM_API = "https://api.thisissoon.fm/";

    public static final String SOCKET = "https://sockets.thisissoon.fm/";

    public class SocketEvents {

        public static final String PLAY = "fm:player:play";
        public static final String END = "fm:player:end";
        public static final String PAUSE = "fm:player:pause";
        public static final String RESUME = "fm:player:resume";
        public static final String ADD = "fm:player:add";
        public static final String SET_MUTE = "fm:player:setMute";
    }

}
