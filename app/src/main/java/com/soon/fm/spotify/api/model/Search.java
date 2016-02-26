package com.soon.fm.spotify.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Search {

    @SerializedName("albums")
    @Expose
    private Albums albums = new Albums();

    @SerializedName("artists")
    @Expose
    private Artists artists = new Artists();

    @SerializedName("tracks")
    @Expose
    private Tracks tracks = new Tracks();

    public Albums getAlbums() {
        return albums;
    }

    public void setAlbums(Albums albums) {
        this.albums = albums;
    }

    public Artists getArtists() {
        return artists;
    }

    public void setArtists(Artists artists) {
        this.artists = artists;
    }

    public Tracks getTracks() {
        return tracks;
    }

    public void setTracks(Tracks tracks) {
        this.tracks = tracks;
    }
}
