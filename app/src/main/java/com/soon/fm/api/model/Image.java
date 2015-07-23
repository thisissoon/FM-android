package com.soon.fm.api.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class Image {

    Bitmap bitmap = null;
    private int width;
    private int height;
    private String url;

    public Image(String url, Mode mode) throws IOException {
        this.url = url;
        if (mode == Mode.EAGER) {
            bitmap = loadImage();
        }
    }

    public Image(JSONObject jsonObject, Mode mode) throws JSONException, IOException {
        url = jsonObject.getString("url");
        width = jsonObject.getInt("width");
        height = jsonObject.getInt("height");
        if (mode == Mode.EAGER) {
            bitmap = loadImage();
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Bitmap loadImage() throws IOException {
        InputStream in = new URL(url).openStream();
        return BitmapFactory.decodeStream(in);
    }

    public Bitmap getBitmap() throws IOException {
        if (bitmap == null) {
            bitmap = loadImage();
        }
        return bitmap;
    }

    public String getUrl() {
        return url;
    }

    public enum Mode {
        EAGER, LAZY
    }
}
