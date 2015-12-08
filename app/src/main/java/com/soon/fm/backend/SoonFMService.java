package com.soon.fm.backend;

import com.soon.fm.backend.model.CurrentTrack;
import com.soon.fm.backend.model.QueueItem;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

public interface SoonFMService {

    @GET("player/queue")
    Call<List<QueueItem>> queue();

    @GET("player/current")
    Call<CurrentTrack> current();

}
