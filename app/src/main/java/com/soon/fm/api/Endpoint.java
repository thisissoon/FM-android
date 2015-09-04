package com.soon.fm.api;

import com.soon.fm.api.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class Endpoint<J> {

    private String uri;
    private String apiHostName;
    private J payload = null;
    private HttpResponse<JSONObject> jsonResponse;

    public URL getUrl() throws MalformedURLException {
        return new URL(new URL(apiHostName), uri);
    }

    public Endpoint(String apiHostName, String uri) {
        this.apiHostName = apiHostName;
        this.uri = uri;
    }

    public HttpResponse<JSONObject> getResponse() throws MalformedURLException {
        return Rest.get(getUrl()).call();
    }

    public J getPayload(Class<?> endpointType) throws IOException, JSONException {
        if (payload == null) {
            jsonResponse = getResponse();
            if (JSONObject.class.equals(endpointType)) {
                payload = (J) jsonResponse.asJson();
            } else if (JSONArray.class.equals(endpointType)) {
                payload = (J) jsonResponse.asJsonArray();
            }
        }
        return payload;
    }
}
