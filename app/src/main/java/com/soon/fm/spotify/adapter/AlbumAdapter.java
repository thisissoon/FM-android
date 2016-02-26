package com.soon.fm.spotify.adapter;

import android.content.Context;

import com.soon.fm.spotify.api.model.AlbumItem;
import com.soon.fm.spotify.api.model.Results;


public class AlbumAdapter extends SearchAdapter<AlbumItem> {

    public AlbumAdapter(Context context, Results<AlbumItem> result) {
        super(context, result);
    }

}
