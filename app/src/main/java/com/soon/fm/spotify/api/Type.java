package com.soon.fm.spotify.api;

public enum Type {

    ALBUMS("album"), ARTISTS("artist"), TRACKS("track");

    private final String value;

    Type(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
