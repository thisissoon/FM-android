package com.soon.fm.backend.event;

import android.os.AsyncTask;

import com.soon.fm.Constants;
import com.soon.fm.OnTaskCompleted;
import com.soon.fm.backend.BackendHelper;
import com.soon.fm.backend.model.Uri;
import com.soon.fm.spotify.api.model.Item;

import java.io.IOException;

public class PerformAddTrack extends AsyncTask<Void, Void, Void> {

    private final String token;
    private final Item item;
    private OnTaskCompleted callback;

    public PerformAddTrack(OnTaskCompleted callback, String token, Item item) {
        this.token = token;
        this.item = item;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        BackendHelper backend = new BackendHelper(Constants.FM_API);
        try {
            backend.addTrack(token, new Uri(item.getUri()));
            callback.onSuccess(item);
        } catch (IOException e) {
            callback.onFailed(item);
        }

        return null;
    }

}
