package com.soon.fm.backend.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.soon.fm.Constants;
import com.soon.fm.backend.BackendHelper;
import com.soon.fm.backend.model.AccessToken;

import java.io.IOException;

    public class Authorize extends AsyncTask<String, AccessToken, AccessToken> {

    private static final String TAG = "Authorize";
        private final Context ctx;

        public Authorize(Context context) {
            this.ctx = context;
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
                Toast.makeText(ctx, "Cannot sign you in", Toast.LENGTH_LONG).show();
            }
        }

    }