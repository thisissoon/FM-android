package com.soon.fm.backend.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentTrack {

    @Expose
    @SerializedName("track")
    private Track track;

    @Expose
    @SerializedName("player")
    private Player player;

    @Expose
    @SerializedName("user")
    private User user;

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
