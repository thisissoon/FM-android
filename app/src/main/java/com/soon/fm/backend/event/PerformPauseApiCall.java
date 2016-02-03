package com.soon.fm.backend.event;

import android.os.AsyncTask;

import com.soon.fm.Constants;
import com.soon.fm.backend.BackendHelper;

import java.io.IOException;

public class PerformPauseApiCall extends AsyncTask<Void, Void, Void> {

    private final String token;

    public PerformPauseApiCall(String token) {
        this.token = token;
    }

    @Override
    protected Void doInBackground(Void... params) {
        BackendHelper backend = new BackendHelper(Constants.FM_API);
        try {
            backend.pause(token);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
