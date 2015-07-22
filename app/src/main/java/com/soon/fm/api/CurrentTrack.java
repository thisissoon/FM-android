package com.soon.fm.api;

import com.soon.fm.api.http.HttpResponse;
import com.soon.fm.api.model.Track;
import com.soon.fm.api.model.User;
import com.soon.fm.api.model.field.Duration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class CurrentTrack {

    private static final String URI = "/player/current";
    private static URL API_URL;
    private JSONObject payload = null;

    private Track track;
    private User user;
    private Duration elapsedTime;

    public CurrentTrack(String apiHostName) throws MalformedURLException {
        API_URL = new URL(new URL(apiHostName), URI);
    }

    public JSONObject getPayload() throws IOException, JSONException {
        if (payload == null) {
            HttpResponse<JSONObject> jsonResponse = Rest.get(API_URL).call();
            payload = jsonResponse.asJson();
        }
        return payload;
    }

    public Track getTrack() throws IOException, JSONException {
        if (track == null) {
            track = new Track(getPayload().getJSONObject("track"));
        }
        return track;
    }

    public User getUser() throws IOException, JSONException {
        if (user == null) {
            user = new User(getPayload().getJSONObject("user"));
        }
        return user;
    }

    public Duration getElapsedTime() throws IOException, JSONException {
        if (elapsedTime == null) {
            elapsedTime = new Duration(getPayload().getJSONObject("player").getInt("elapsed_time"));
        }
        return elapsedTime;
    }

}
