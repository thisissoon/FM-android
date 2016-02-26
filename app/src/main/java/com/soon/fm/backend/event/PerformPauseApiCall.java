package com.soon.fm.backend.event;

import android.os.AsyncTask;

import com.soon.fm.Constants;
import com.soon.fm.backend.BackendHelper;

import java.io.IOException;

public class PerformPauseApiCall extends AsyncTask<Void, Void, Void> {

    private final String token;
    private final Boolean isChecked;

    public PerformPauseApiCall(String token, Boolean isChecked) {
        this.token = token;
        this.isChecked = isChecked;
    }

    @Override
    protected Void doInBackground(Void... params) {
        BackendHelper backend = new BackendHelper(Constants.FM_API);
        try {
            if (isChecked) {
                backend.pause(token);
            } else {
                backend.play(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
