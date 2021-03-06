package com.soon.fm.backend.event;

import android.os.AsyncTask;

import com.soon.fm.Constants;
import com.soon.fm.backend.BackendHelper;

import java.io.IOException;

public class PerformMuteApiCall extends AsyncTask<Void, Void, Void> {

    private final String token;
    private final Boolean isChecked;

    public PerformMuteApiCall(String token, Boolean isChecked) {
        this.token = token;
        this.isChecked = isChecked;
    }

    @Override
    protected Void doInBackground(Void... params) {
        BackendHelper backend = new BackendHelper(Constants.FM_API);
        try {
            if (isChecked) {
                backend.mute(token);
            } else {
                backend.unmute(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
