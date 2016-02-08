package com.soon.fm.backend;

import android.util.Log;

import com.soon.fm.backend.model.AccessToken;
import com.soon.fm.backend.model.CurrentTrack;
import com.soon.fm.backend.model.GoogleToken;
import com.soon.fm.backend.model.Mute;
import com.soon.fm.backend.model.QueueItem;
import com.soon.fm.backend.model.Uri;
import com.soon.fm.backend.model.Volume;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class BackendHelper {

    private static final String TAG = "BackendHelper";

    private final SoonFMService service;

    public BackendHelper(String backendUrl) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(backendUrl).addConverterFactory(GsonConverterFactory.create()).build();
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

        if (response.code() != 200) {
            Log.e(TAG, String.format("[SFM api] %s", response.errorBody().string()));
        } else {
            Log.d(TAG, String.format("[SFM api] %s", response.raw().message()));
        }
        return response.body();
    }

    public void pause(String authToken) throws IOException {
        Response<ResponseBody> response = service.pause(authToken).execute();
        Log.d(TAG, String.format("[SFM api] %s", response.raw().message()));
    }

    public void play(String authToken) throws IOException {
        Response<ResponseBody> response = service.play(authToken).execute();
        Log.d(TAG, String.format("[SFM api] %s", response.raw().message()));
    }

    public void addTrack(String token, Uri uri) throws IOException {
        Response<ResponseBody> response = service.add(token, uri).execute();
        Log.d(TAG, String.format("[SFM api] %s", response.raw().message()));
    }

    public Boolean isMuted() throws IOException {
        Response<Mute> response = service.isMuted().execute();
        return response.body().isMuted();
    }

    public Boolean mute(String token) throws IOException {
        Response<Mute> response = service.mute(token).execute();
        return response.body().isMuted();
    }

    public Boolean unmute(String token) throws IOException {
        Response<Mute> response = service.unmute(token).execute();
        return response.body().isMuted();
    }

    public Integer getVolume() throws IOException {
        Response<Volume> response = service.getVolume().execute();
        return response.body().getVolume();
    }

    public Integer setVolume(String token, Integer volume) throws IOException {
        Response<Volume> response = service.setVolume(token, new Volume(volume)).execute();
        return response.body().getVolume();
    }

    public void skipTrack(String token) throws IOException {
        Response<ResponseBody> response = service.skip(token).execute();
    }
}
