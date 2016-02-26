package com.soon.fm.backend.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Volume {

    @SerializedName("volume")
    @Expose
    Integer volume;

    public Volume(Integer volume) {
        this.volume = volume;
    }

    public Integer getVolume() {
        return volume;
    }

}
