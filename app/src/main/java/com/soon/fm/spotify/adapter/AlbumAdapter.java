package com.soon.fm.spotify.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.soon.fm.AlbumActivity;
import com.soon.fm.Constant;
import com.soon.fm.spotify.api.model.AlbumListItem;
import com.soon.fm.spotify.api.model.Albums;
import com.soon.fm.spotify.api.model.Item;


public class AlbumAdapter extends SearchAdapter<AlbumListItem> {

    private boolean isDetailsActivityStarted = false;

    private Activity activity;

    public AlbumAdapter(FragmentActivity activity, Context context, Albums results) {
        super(context, results);
        this.activity = activity;
    }

    @Override
    protected void performClickOnItem(View view, Item item) {
        Intent intent = new Intent(activity, AlbumActivity.class);
        intent.putExtra(Constant.EXTRA_STARTING_ALBUM_POSITION, item);
        activity.startActivity(intent);
//
//            isDetailsActivityStarted = true;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                activity.startActivity(
//                        intent,
//                        ActivityOptions.makeSceneTransitionAnimation(
//                            activity,
//                            view,
//                            view.getTransitionName())
//                                .toBundle());
//            }
    }

}
