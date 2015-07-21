package com.soon.fm.api;

import com.soon.fm.api.http.HttpMethod;
import com.soon.fm.api.http.HttpRequest;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class Rest {

    public static HttpRequest<JSONObject> get(URL url){
        return new HttpRequest<JSONObject>(HttpMethod.GET, url);
    }

    public static HttpRequest<JSONObject> get(String url) throws MalformedURLException {
        return Rest.get(new URL(url));
    }

}
