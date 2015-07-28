package com.soon.fm.api.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class Image {

    private int width;
    private int height;
    private String url;

    public Image(String url) {
        this.url = url;
    }

    public Image(JSONObject jsonObject) throws JSONException, IOException {
        url = jsonObject.getString("url");
        width = jsonObject.getInt("width");
        height = jsonObject.getInt("height");
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getUrl() {
        return url;
    }

}
