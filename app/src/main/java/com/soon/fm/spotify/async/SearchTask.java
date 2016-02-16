package com.soon.fm.spotify.async;

import android.os.AsyncTask;
import android.util.Log;

import com.soon.fm.async.CallbackInterface;
import com.soon.fm.spotify.SpotifyHelper;
import com.soon.fm.spotify.api.model.Search;

import java.io.IOException;

public class SearchTask extends AsyncTask<Void, Void, Search> {

    private final String url;
    private final SpotifyHelper helper;
    private final CallbackInterface<Search> callback;

    public SearchTask(String url, CallbackInterface<Search> callback) {
        this.url = url;
        this.helper = new SpotifyHelper();
        this.callback = callback;
    }

    @Override
    protected Search doInBackground(Void... params) {
        try {
            return helper.search(url);
        } catch (IOException e) {
            callback.onFail();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Search search) {
        Log.d("SearchTask", "doInBackground");
        callback.onSuccess(search);
    }

}
