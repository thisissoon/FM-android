package com.soon.fm.backend.event;

import android.os.AsyncTask;

import com.soon.fm.Constants;
import com.soon.fm.backend.BackendHelper;
import com.soon.fm.backend.model.Uri;

import java.io.IOException;

public class PerformAddTrack extends AsyncTask<Void, Void, Void> {

    private final String token;
    private final Uri uri;

    public PerformAddTrack(String token, Uri uri) {
        this.token = token;
        this.uri = uri;
    }

    @Override
    protected Void doInBackground(Void... params) {
        BackendHelper backend = new BackendHelper(Constants.FM_API);
        try {
            backend.addTrack(token, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
