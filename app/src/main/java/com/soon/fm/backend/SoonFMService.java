package com.soon.fm.backend;

import com.soon.fm.backend.model.AccessToken;
import com.soon.fm.backend.model.CurrentTrack;
import com.soon.fm.backend.model.GoogleToken;
import com.soon.fm.backend.model.QueueItem;
import com.squareup.okhttp.ResponseBody;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

public interface SoonFMService {

    @GET("player/queue")
    Call<List<QueueItem>> queue();

    @GET("player/current")
    Call<CurrentTrack> current();

    @POST("/oauth2/google/connect")
    Call<AccessToken> googleConnect(@Body GoogleToken token);

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("player/pause")
    Call<ResponseBody> pause(@Header("Access-Token") String token);

    @Headers("Content-Type: application/json; charset=utf-8")
    @DELETE("player/pause")
    Call<ResponseBody> play(@Header("Access-Token") String token);

}
