package com.soon.fm.backend.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Player {

    @Expose
    @SerializedName("elapsed_time")
    private Integer elapsedTime;

    @Expose
    @SerializedName("elapsed_seconds")
    private Float elapsedSeconds;

    @Expose
    @SerializedName("elapsed_percentage")
    private Float elapsedPercentage;

    public Integer getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Integer elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Float getElapsedSeconds() {
        return elapsedSeconds;
    }

    public void setElapsedSeconds(Float elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
    }

    public Float getElapsedPercentage() {
        return elapsedPercentage;
    }

    public void setElapsedPercentage(Float elapsedPercentage) {
        this.elapsedPercentage = elapsedPercentage;
    }
}
