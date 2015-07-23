package com.soon.fm.api;

import com.soon.fm.api.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public abstract class Endpoint<J> {

    protected static URL API_URL = null;
    private J payload = null;

    public J getPayload(Class<?> endpointType) throws IOException, JSONException {
        if (payload == null) {
            HttpResponse<JSONObject> jsonResponse = Rest.get(API_URL).call();
            if (JSONObject.class.equals(endpointType)) {
                payload = (J) jsonResponse.asJson();
            } else if (JSONArray.class.equals(endpointType)) {
                payload = (J) jsonResponse.asJsonArray();
            }
        }
        return payload;
    }

}
