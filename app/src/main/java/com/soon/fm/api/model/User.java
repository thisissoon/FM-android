package com.soon.fm.api.model;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class User implements APIObject {

    private String avatarUrl;
    private String displayName;
    private String familyName;
    private String givenName;
    private String id;
    private Bitmap avatar = null;

    public User(JSONObject jsonObject) throws JSONException {
        loadFromJson(jsonObject);
    }

    public Bitmap getAvatar() {
        if (avatar == null) {
            try {
                avatar = new Image(avatarUrl, Image.Mode.EAGER).getBitmap();
            } catch (IOException e) {
                avatar = null;  // TODO load some system image for avatar
            }
        }
        return avatar;
    }

    private void loadFromJson(JSONObject jsonObject) throws JSONException {
        this.avatarUrl = jsonObject.getString("avatar_url");
        this.displayName = jsonObject.getString("display_name");
        this.familyName = jsonObject.getString("family_name");
        this.givenName = jsonObject.getString("given_name");
        this.id = jsonObject.getString("id");
        this.avatar = getAvatar();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getId() {
        return id;
    }

}
