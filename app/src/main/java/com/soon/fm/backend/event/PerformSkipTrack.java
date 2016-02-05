package com.soon.fm.backend.event;

import android.os.AsyncTask;
import android.util.Log;

import com.soon.fm.Constants;
import com.soon.fm.backend.BackendHelper;

import java.io.IOException;

public class PerformSkipTrack extends AsyncTask<Void, Void, Void> {

    private static final String TAG = PerformSkipTrack.class.getName();
    private final String token;

    public PerformSkipTrack(String token) {
        this.token = token;
    }

    @Override
    protected Void doInBackground(Void... params) {
        BackendHelper backend = new BackendHelper(Constants.FM_API);
        try {
            backend.skipTrack(token);
        } catch (IOException e) {
            Log.wtf(TAG, e.getMessage());
        }

        return null;
    }

}
