package com.soon.fm.backend.event;

import android.os.AsyncTask;
import android.util.Log;

import com.soon.fm.Constants;
import com.soon.fm.backend.BackendHelper;
import com.soon.fm.backend.model.QueueItem;

import java.io.IOException;

public class PerformDeleteTrack extends AsyncTask<Void, Void, Void> {

    private static final String TAG = PerformDeleteTrack.class.getName();

    private final String token;
    private final QueueItem item;

    public PerformDeleteTrack(String token, QueueItem item) {
        this.token = token;
        this.item = item;
    }

    @Override
    protected Void doInBackground(Void... params) {
        BackendHelper backend = new BackendHelper(Constants.FM_API);
        try {
            backend.deleteTrack(token, item);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

}
