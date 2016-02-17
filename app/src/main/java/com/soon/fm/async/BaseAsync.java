package com.soon.fm.async;

import android.os.AsyncTask;

import com.soon.fm.backend.BackendHelper;

public abstract class BaseAsync<T> extends AsyncTask<Void, Void, T> {

    private final CallbackInterface<T> callback;
    private final BackendHelper backend;

    public BaseAsync(String backendUrl, CallbackInterface<T> callback) {
        this.callback = callback;
        backend = new BackendHelper(backendUrl);
    }

    public abstract T performOperation() throws Exception;

    protected T doInBackground(Void... params) {
        try {
            return performOperation();
        } catch (Exception e) {
            callback.onFail();
        }
        return null;
    }

    protected void onPostExecute(T obj) {
        this.callback.onSuccess(obj);
    }

    public BackendHelper getBackend() {
        return backend;
    }

}
