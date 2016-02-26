package com.soon.fm.spotify.adapter;

import android.content.Context;

import com.soon.fm.spotify.api.model.Results;
import com.soon.fm.spotify.api.model.TrackItem;


public class AlbumAdapter extends SearchAdapter<TrackItem> {

    public AlbumAdapter(Context context, Results<TrackItem> result) {
        super(context, result);
    }

}
