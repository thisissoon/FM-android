package com.soon.fm.async;

import com.soon.fm.backend.model.QueueItem;

import java.util.List;

public class FetchQueue extends BaseAsync<List<QueueItem>> {

    public FetchQueue(String backendUrl, CallbackInterface<List<QueueItem>> callback) {
        super(backendUrl, callback);
    }

    @Override
    public List<QueueItem> performOperation() throws Exception {
        return getBackend().getPlayerQueue();
    }
}
