package com.soon.fm.sdk;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.soon.fm.sdk.entity.Track;
import com.soon.fm.sdk.entity.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class CurrentTrack {

    private static final String URI = "/player/current";
    private static URL API_URL;
    private Track track;
    private User user;
    private int elapsedTime;
    private JsonNode payload = null;

    public CurrentTrack(String apiHostName) throws MalformedURLException {
        API_URL = new URL(new URL(apiHostName), URI);
    }

    public JSONObject getPayload() throws IOException, UnirestException {
        if (payload == null) {
            HttpResponse<JsonNode> jsonResponse = Unirest
                    .get("https://api.thisissoon.fm/player/current")
                    .header("accept", "application/json")
                    .asJson();
            payload = jsonResponse.getBody();
        }
        return payload.getObject();
    }

    public Track getTrack() throws IOException, UnirestException {
        if (track == null) {
            track = new Track(getPayload().getJSONObject("track"));
        }
        return track;
    }

    public User getUser() throws IOException, UnirestException {
        if (user == null) {
            user = new User(getPayload().getJSONObject("user"));
        }
        return user;
    }

    public int getElapsedTime() throws IOException, JSONException, UnirestException {
        if (user == null) {
            elapsedTime = getPayload().getJSONObject("player").getInt("elapsed_time");
        }
        return elapsedTime;
    }

}
