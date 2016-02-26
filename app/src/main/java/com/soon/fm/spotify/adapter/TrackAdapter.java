package com.soon.fm.spotify.adapter;

import android.content.Context;

import com.soon.fm.spotify.api.model.Results;
import com.soon.fm.spotify.api.model.TrackItem;


public class TrackAdapter extends SearchAdapter<TrackItem> {

    public TrackAdapter(Context context, Results<TrackItem> result) {
        super(context, result);
    }

}
