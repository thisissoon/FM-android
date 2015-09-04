package com.soon.fm.api;

import com.soon.fm.api.model.Track;
import com.soon.fm.api.model.User;
import com.soon.fm.api.model.UserTrack;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Queue extends Endpoint<JSONArray> {

    private static final String URI = "/player/queue";

    private List<UserTrack> tracks = new ArrayList<>();

    public Queue(String apiHostName) {
        super(apiHostName, URI);
    }

    public List<UserTrack> getTracks() throws IOException, JSONException {
        if (tracks.size() == 0) {
            JSONArray payloadsTracks = getPayload(JSONArray.class);
            for (int i = 0; i < payloadsTracks.length(); i++) {
                UserTrack userTrack = new UserTrack();
                userTrack.user = new User(payloadsTracks.getJSONObject(i).getJSONObject("user"));
                userTrack.track = new Track(payloadsTracks.getJSONObject(i).getJSONObject("track"));
                tracks.add(userTrack);
            }
        }
        return tracks;
    }
}
