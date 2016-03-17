package com.soon.fm.spotify.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.soon.fm.async.CallbackInterface;
import com.soon.fm.backend.event.PerformAddTrack;
import com.soon.fm.spotify.api.model.Item;
import com.soon.fm.spotify.api.model.Results;
import com.soon.fm.spotify.api.model.TrackItem;


public class TrackAdapter extends SearchAdapter<TrackItem> {

    public TrackAdapter(Context context, Results<TrackItem> result) {
        super(context, result);
    }

    @Override
    protected void performClickOnItem(View view, Item item) {
        performAddTrack(item);
    }

    private void performAddTrack(final Item item) {
        Snackbar snackbar = Snackbar.make(view, String.format("%s - %s added", item.getTitle(), item.getSubTitle()), Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (event == DISMISS_EVENT_CONSECUTIVE || event == DISMISS_EVENT_TIMEOUT) {
                    new PerformAddTrack(preferences.getUserApiToken(), item, new CallbackInterface<Item>() {
                        @Override
                        public void onSuccess(Item obj) {

                        }

                        @Override
                        public void onFail() {

                        }
                    }).execute();
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
            }
        }).setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbarUndo = Snackbar.make(view, "Track removed from the queue!", Snackbar.LENGTH_SHORT);
                snackbarUndo.show();
            }
        });
        snackbar.show();
    }

}
