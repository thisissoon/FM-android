package com.soon.fm.spotify.adapter;

import android.content.Context;

import com.soon.fm.spotify.api.model.Results;
import com.soon.fm.spotify.api.model.TrackItem;


public class ArtistsAdapter extends SearchAdapter<TrackItem> {

    public ArtistsAdapter(Context context, Results<TrackItem> result) {
        super(context, result);
    }

}
