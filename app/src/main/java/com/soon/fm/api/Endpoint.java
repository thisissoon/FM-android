package com.soon.fm.api;

import com.soon.fm.api.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class Endpoint {

    private String uri;
    private String apiHostName;
    private HttpResponse<JSONObject> jsonResponse;

    public Endpoint(String apiHostName, String uri) {
        this.apiHostName = apiHostName;
        this.uri = uri;
    }

    public URL getUrl() throws MalformedURLException {
        return new URL(new URL(apiHostName), uri);
    }

    public void setJsonResponse(HttpResponse<JSONObject> jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    public HttpResponse<JSONObject> getResponse() throws MalformedURLException {
        if (jsonResponse == null) {
            jsonResponse = Rest.get(getUrl()).call();
        }
        return jsonResponse;
    }

    public JSONArray getJsonArray() throws IOException, JSONException {
        return getResponse().asJsonArray();
    }

    public JSONObject getJsonObject() throws IOException, JSONException {
        return getResponse().asJson();
    }

}
