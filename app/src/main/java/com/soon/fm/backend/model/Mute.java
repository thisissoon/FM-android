package com.soon.fm.backend.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mute {

    @SerializedName("mute")
    @Expose
    private Boolean mute;

    public Boolean isMuted() {
        return mute;
    }
}
