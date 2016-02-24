package com.soon.fm;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import com.soon.fm.backend.event.PerformChangeVolumeApiCall;
import com.soon.fm.helper.PreferencesHelper;
import com.soon.fm.utils.CurrentTrackCache;

public class QueueActivity extends BaseActivity {

    private PreferencesHelper preferences;
    private Integer volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = new PreferencesHelper(this);

        volume = CurrentTrackCache.getVolume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    volume = Math.min(volume + 5, 100);
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    volume = Math.max(volume - 5, 0);
                    break;
            }
            changeVolume(volume);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void changeVolume(int volume) {
        String token = preferences.getUserApiToken();
//        updateVolumeBar(volume);
        new PerformChangeVolumeApiCall(token, volume).execute();
    }

}
