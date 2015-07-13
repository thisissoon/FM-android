package com.soon.fm.sdk.entity;

import org.json.JSONObject;


public class Image {

    private int width;
    private int height;
    private String url;

    public Image(JSONObject jsonObject) {
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
