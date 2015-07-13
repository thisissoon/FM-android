package com.soon.fm.sdk.entity;


import org.json.JSONObject;


public class Artist {

    private String id;
    private String name;
    private String uri;

    public Artist(JSONObject artist) {
        id = artist.getString("name");
        name = artist.getString("name");
        uri = artist.getString("uri");
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return name;
    }
}
