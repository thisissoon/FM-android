package com.soon.fm.api.model;


import com.soon.fm.api.model.field.Duration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Track {

    private Album album;
    private List<Artist> artists = new ArrayList<Artist>();
    private Duration duration;
    private String id;
    private String name;
    private String uri;

    public Track(JSONObject track) throws JSONException, IOException {
        name = track.getString("name");
        uri = track.getString("uri");
        duration = new Duration(track.getInt("duration"));
        id = track.getString("id");

        JSONArray jsonImages = track.getJSONArray("artists");
        for (int i = 0; i < jsonImages.length(); i++) {
            artists.add(new Artist(jsonImages.getJSONObject(i)));
        }
        album = new Album(track.getJSONObject("album"));
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public Duration getDuration() {
        return duration;
    }

    public Album getAlbum() {
        return album;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

}
