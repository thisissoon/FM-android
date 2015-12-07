package com.soon.fm.backend;

import com.soon.fm.backend.model.QueueItem;

import java.io.IOException;
import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class BackendHelper {

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
}
