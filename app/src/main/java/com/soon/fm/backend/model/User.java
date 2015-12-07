package com.soon.fm.backend.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class User {

    @SerializedName("family_name")
    @Expose
    private String familyName;

    @SerializedName("display_name")
    @Expose
    private String displayName;

    @SerializedName("avatar_url")
    @Expose
    private String avatarUrl;

    @SerializedName("spotify_playlists")
    @Expose
    private Object spotifyPlaylists;

    @SerializedName("given_name")
    @Expose
    private String givenName;

    @SerializedName("id")
    @Expose
    private String id;

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Object getSpotifyPlaylists() {
        return spotifyPlaylists;
    }

    public void setSpotifyPlaylists(Object spotifyPlaylists) {
        this.spotifyPlaylists = spotifyPlaylists;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
