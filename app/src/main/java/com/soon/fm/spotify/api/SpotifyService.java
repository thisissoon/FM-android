package com.soon.fm.spotify.api;

import com.soon.fm.spotify.api.model.Search;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SpotifyService {

    @GET("/v1/search?market=GB&type=album,artist,track")
    Call<Search> search(@Query("q") String q, @Query("limit") Integer limit);

}
