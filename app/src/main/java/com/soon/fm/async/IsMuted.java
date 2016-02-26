package com.soon.fm.async;

public class IsMuted extends BaseAsync<Boolean> {

    public IsMuted(String backendUrl, CallbackInterface callback) {
        super(backendUrl, callback);
    }

    @Override
    public Boolean performOperation() throws Exception {
        return getBackend().isMuted();
    }

}
