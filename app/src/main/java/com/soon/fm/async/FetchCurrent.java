package com.soon.fm.async;

import com.soon.fm.backend.model.CurrentTrack;

public class FetchCurrent extends BaseAsync<CurrentTrack> {

    public FetchCurrent(String backendUrl, CallbackInterface<CurrentTrack> callback) {
        super(backendUrl, callback);
    }

    @Override
    public CurrentTrack performOperation() throws Exception {
        return getBackend().getCurrentTrack();
    }

}
