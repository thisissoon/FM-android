package com.soon.fm.async;

public class GetCurrentVolume extends BaseAsync<Integer> {

    public GetCurrentVolume(String backendUrl, CallbackInterface<Integer> callback) {
        super(backendUrl, callback);
    }

    @Override
    public Integer performOperation() throws Exception {
        return getBackend().getVolume();
    }

}
