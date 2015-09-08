package com.soon.fm.api.model;


import org.json.JSONException;
import org.json.JSONObject;


public class Artist {

    private String id;
    private String name;
    private String uri;

    public Artist(JSONObject artist) throws JSONException {
        id = artist.getString("id");
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
