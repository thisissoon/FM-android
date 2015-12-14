package com.soon.fm.backend;

import com.soon.fm.backend.model.AccessToken;
import com.soon.fm.backend.model.CurrentTrack;
import com.soon.fm.backend.model.GoogleToken;
import com.soon.fm.backend.model.QueueItem;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface SoonFMService {

    @GET("player/queue")
    Call<List<QueueItem>> queue();

    @GET("player/current")
    Call<CurrentTrack> current();

    @POST("/oauth2/google/connect")
    Call<AccessToken> googleConnect(@Body GoogleToken token);

}
