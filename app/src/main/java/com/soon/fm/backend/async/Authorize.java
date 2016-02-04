package com.soon.fm.backend.async;

import android.os.AsyncTask;
import android.util.Log;

import com.soon.fm.Constants;
import com.soon.fm.OnTaskCompleted;
import com.soon.fm.backend.BackendHelper;
import com.soon.fm.backend.model.AccessToken;

import java.io.IOException;

public class Authorize extends AsyncTask<String, AccessToken, AccessToken> {

    private static final String TAG = "Authorize";
    private final OnTaskCompleted callback;

    public Authorize(OnTaskCompleted callback) {
        this.callback = callback;
    }

    @Override
    protected AccessToken doInBackground(String... params) {
        BackendHelper helper = new BackendHelper(Constants.FM_API);
        try {
            return helper.getAccessToken(params[0]);
        } catch (IOException e) {
            Log.e(TAG, String.format("[Failed when getting access token to soon fm api] %s", e.getMessage()));
        }
        return null;
    }

    @Override
    protected void onPostExecute(AccessToken accessToken) {
        if (accessToken == null) {
            callback.onFailed(null);
        } else {
            callback.onSuccess(accessToken);
        }
    }

}
