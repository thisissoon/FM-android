package com.soon.fm.backend;

import android.util.Log;

import com.soon.fm.backend.model.AccessToken;
import com.soon.fm.backend.model.CurrentTrack;
import com.soon.fm.backend.model.GoogleToken;
import com.soon.fm.backend.model.QueueItem;

import java.io.IOException;
import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class BackendHelper {

    private static final String TAG = "BackendHelper";

    private final SoonFMService service;
    private final Retrofit retrofit;

    public BackendHelper(String backendUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(backendUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(SoonFMService.class);
    }

    public List<QueueItem> getPlayerQueue() throws IOException {
        Response<List<QueueItem>> response = service.queue().execute();
        return response.body();
    }

    public CurrentTrack getCurrentTrack() throws IOException {
        Response<CurrentTrack> response = service.current().execute();
        return response.body();
    }

    public AccessToken getAccessToken(String googleAccessToken) throws IOException {
        Response<AccessToken> response = service.googleConnect(new GoogleToken(googleAccessToken)).execute();

        if(response.code() != 200){
            Log.e(TAG, String.format("[Backend error] %s", response.errorBody().string()));
        } else {
            Log.d(TAG, String.format("[SFM api] %s", response.raw().message()));
        }
        return response.body();
    }

}
