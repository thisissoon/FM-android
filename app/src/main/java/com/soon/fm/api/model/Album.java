package com.soon.fm.api.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Album {

    private String id;
    private List<Image> images = new ArrayList<>();
    private String name;
    private String uri;

    public Album(JSONObject album) throws JSONException {
        id = album.getString("id");
        name = album.getString("name");
        uri = album.getString("uri");

        JSONArray jsonImages = album.getJSONArray("images");
        for (int i = 0; i < jsonImages.length(); i++) {
            images.add(new Image(jsonImages.getJSONObject(i)));
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

}
