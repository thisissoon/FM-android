package com.soon.fm.backend;

import com.soon.fm.backend.model.AccessToken;
import com.soon.fm.backend.model.CurrentTrack;
import com.soon.fm.backend.model.GoogleToken;
import com.soon.fm.backend.model.Mute;
import com.soon.fm.backend.model.QueueItem;
import com.soon.fm.backend.model.Uri;
import com.soon.fm.backend.model.Volume;

import java.io.IOException;
import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class BackendHelper {

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
        return response.body();
    }

    public void pause(String authToken) throws IOException {
        service.pause(authToken).execute();
    }

    public void play(String authToken) throws IOException {
        service.play(authToken).execute();
    }

    public void addTrack(String token, Uri uri) throws IOException {
        service.add(token, uri).execute();
    }

    public void deleteTrack(String token, QueueItem item) throws IOException {
        service.delete(token, item.getUuid()).execute();
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
        service.skip(token).execute();
    }
}
