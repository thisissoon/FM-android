package com.soon.fm.backend.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleToken {

    @Expose
    @SerializedName("code")
    String token;

    public GoogleToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
