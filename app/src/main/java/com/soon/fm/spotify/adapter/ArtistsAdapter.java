package com.soon.fm.spotify.adapter;

import android.content.Context;

import com.soon.fm.R;
import com.soon.fm.spotify.api.model.ArtistItem;
import com.soon.fm.spotify.api.model.Item;
import com.soon.fm.spotify.api.model.Results;


public class ArtistsAdapter extends SearchAdapter<ArtistItem> {

    protected final int PLACEHOLDER = R.drawable.ic_person;

    public ArtistsAdapter(Context context, Results<ArtistItem> result) {
        super(context, result);
    }

    @Override
    protected void performClickOnItem(Item item) {

    }

}
