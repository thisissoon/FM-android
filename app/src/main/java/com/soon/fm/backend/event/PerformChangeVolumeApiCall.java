package com.soon.fm.backend.event;

import android.os.AsyncTask;

import com.soon.fm.Constants;
import com.soon.fm.backend.BackendHelper;

import java.io.IOException;

public class PerformChangeVolumeApiCall extends AsyncTask<Void, Void, Void> {

    private final String token;
    private final Integer volume;

    public PerformChangeVolumeApiCall(String token, Integer volume) {
        this.token = token;
        this.volume = volume;
    }

    @Override
    protected Void doInBackground(Void... params) {
        BackendHelper backend = new BackendHelper(Constants.FM_API);
        try {
            backend.setVolume(token, volume);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
