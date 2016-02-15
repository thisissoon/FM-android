package com.soon.fm.backend.event;

import com.soon.fm.Constants;
import com.soon.fm.async.BaseAsync;
import com.soon.fm.async.CallbackInterface;
import com.soon.fm.backend.model.Uri;
import com.soon.fm.spotify.api.model.Item;

public class PerformAddTrack extends BaseAsync<Item> {

    private final String token;
    private final Item item;

    public PerformAddTrack(String token, Item item, CallbackInterface<Item> callback) {
        super(Constants.FM_API, callback);
        this.token = token;
        this.item = item;
    }

    @Override
    public Item performOperation() throws Exception {
        getBackend().addTrack(token, new Uri(item.getUri()));
        return null;
    }

}
