package com.soon.fm.backend.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.soon.fm.backend.model.field.Duration;

import java.util.ArrayList;
import java.util.List;


public class Track {

    @SerializedName("album")
    @Expose
    private Album album;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("uri")
    @Expose
    private String uri;

    @SerializedName("play_count")
    @Expose
    private Integer playCount;

    @SerializedName("artists")
    @Expose
    private List<Artist> artists = new ArrayList<Artist>();

    @SerializedName("duration")
    @Expose
    private Integer duration;

    @SerializedName("id")
    @Expose
    private String id;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
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

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public Duration getDuration() {
        return new Duration(duration);
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
