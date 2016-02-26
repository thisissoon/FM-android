package com.soon.fm;

import android.os.Bundle;
import android.util.Log;

import com.soon.fm.async.CallbackInterface;
import com.soon.fm.async.FetchCurrent;
import com.soon.fm.async.FetchQueue;
import com.soon.fm.async.GetCurrentVolume;
import com.soon.fm.async.IsMuted;
import com.soon.fm.backend.model.CurrentTrack;
import com.soon.fm.backend.model.QueueItem;
import com.soon.fm.utils.CurrentTrackCache;

import java.util.List;

public class PreLoadingActivity extends BaseActivity {

    private static final String TAG = PreLoadingActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preloading);

        String backendUrl = getString(R.string.fm_api);
        preloadQueue(backendUrl);
        preloadIsMute(backendUrl);
        preloadCurrentTrack(backendUrl);
        preloadCurrentVolume(backendUrl);
    }

    private void goToQueueIfLoaded() {
        if (CurrentTrackCache.isEverythingSet()) {
            changeActivity(QueueActivity.class);
            finish();
        }
    }

    private void preloadCurrentTrack(final String backendUrl) {
        Log.d(TAG, "preloadCurrentTrack - started");
        new FetchCurrent(backendUrl, new CallbackInterface<CurrentTrack>() {
            @Override
            public void onSuccess(CurrentTrack obj) {
                Log.d(TAG, "preloadCurrentTrack - finished");
                CurrentTrackCache.setCurrentTrack(obj);
                goToQueueIfLoaded();
            }

            @Override
            public void onFail() {
                Log.d(TAG, "preloadCurrentTrack - failed");
                preloadCurrentTrack(backendUrl);
            }
        }).execute();
    }

    private void preloadIsMute(final String backendUrl) {
        Log.d(TAG, "preloadIsMute - started");
        new IsMuted(backendUrl, new CallbackInterface<Boolean>() {
            @Override
            public void onSuccess(Boolean obj) {
                Log.d(TAG, "preloadIsMute - finished");
                CurrentTrackCache.setIsMuted(obj);
                goToQueueIfLoaded();
            }

            @Override
            public void onFail() {
                Log.d(TAG, "preloadIsMute - failed");
                preloadIsMute(backendUrl);
            }
        }).execute();
    }

    private void preloadCurrentVolume(final String backendUrl) {
        Log.d(TAG, "preloadCurrentVolume - started");
        new GetCurrentVolume(backendUrl, new CallbackInterface<Integer>() {
            @Override
            public void onSuccess(Integer obj) {
                Log.d(TAG, "preloadCurrentVolume - finished");
                CurrentTrackCache.setVolume(obj);
                goToQueueIfLoaded();
            }

            @Override
            public void onFail() {
                Log.d(TAG, "preloadCurrentVolume - failed");
                preloadCurrentVolume(backendUrl);
            }
        }).execute();
    }

    private void preloadQueue(final String backendUrl) {
        Log.d(TAG, "preloadQueue - started");
        new FetchQueue(backendUrl, new CallbackInterface<List<QueueItem>>() {
            @Override
            public void onSuccess(List<QueueItem> obj) {
                Log.d(TAG, "preloadQueue - finished");
                CurrentTrackCache.setQueue(obj);
                goToQueueIfLoaded();
            }

            @Override
            public void onFail() {
                Log.d(TAG, "preloadQueue - failed");
                preloadQueue(backendUrl);
            }
        }).execute();
    }

}
