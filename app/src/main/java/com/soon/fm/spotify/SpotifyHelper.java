package com.soon.fm.spotify;

import com.soon.fm.spotify.api.SpotifyService;
import com.soon.fm.spotify.api.model.Search;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SpotifyHelper {

    private static final String SPOTIFY_API_URL = "https://api.spotify.com/";
    private final SpotifyService service;

    public SpotifyHelper() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(SPOTIFY_API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(SpotifyService.class);
    }

    public Search search(String q, Integer limit) throws IOException {
        Response<Search> response = service.search(q, limit).execute();
        return response.body();
    }

}
