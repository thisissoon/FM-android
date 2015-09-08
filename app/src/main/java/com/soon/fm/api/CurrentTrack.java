package com.soon.fm.api;

import com.soon.fm.api.model.Track;
import com.soon.fm.api.model.User;
import com.soon.fm.api.model.field.Duration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CurrentTrack extends Endpoint<JSONObject> {

    private static final String URI = "/player/current";

    private Track track;
    private User user;
    private Duration elapsedTime;

    public CurrentTrack(String apiHostName) {
        super(apiHostName, URI);
    }

    public Track getTrack() throws IOException, JSONException {
        if (track == null) {
            track = new Track(getJsonObject().getJSONObject("track"));
        }
        return track;
    }

    public User getUser() throws IOException, JSONException {
        if (user == null) {
            user = new User(getJsonObject().getJSONObject("user"));
        }
        return user;
    }

    public Duration getElapsedTime() throws IOException, JSONException {
        if (elapsedTime == null) {
            elapsedTime = new Duration(getJsonObject().getJSONObject("player").getInt("elapsed_time"));
        }
        return elapsedTime;
    }

    public String getBla() throws IOException, JSONException {
        return getJsonObject().getString("bla");
    }

}
