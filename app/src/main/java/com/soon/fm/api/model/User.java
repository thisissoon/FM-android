package com.soon.fm.api.model;

import org.json.JSONException;
import org.json.JSONObject;


public class User implements APIObject {

    private String displayName;
    private String familyName;
    private String givenName;
    private String id;
    private Image avatar;

    public User(JSONObject jsonObject) throws JSONException {
        loadFromJson(jsonObject);
    }

    private void loadFromJson(JSONObject jsonObject) throws JSONException {
        this.displayName = jsonObject.getString("display_name");
        this.familyName = jsonObject.getString("family_name");
        this.givenName = jsonObject.getString("given_name");
        this.id = jsonObject.getString("id");
        this.avatar = new Image(jsonObject.getString("avatar_url"));
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

    public Image getAvatar() {
        return avatar;
    }
}
