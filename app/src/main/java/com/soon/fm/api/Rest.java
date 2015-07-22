package com.soon.fm.api;

import com.soon.fm.api.http.HttpMethod;
import com.soon.fm.api.http.HttpRequest;

import org.json.JSONObject;

import java.net.URL;

public class Rest {

    public static HttpRequest<JSONObject> get(URL url) {
        return new HttpRequest<>(HttpMethod.GET, url);
    }

}
