package com.soon.fm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

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
        new PerformChangeVolumeApiCall(token, volume).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_current_track, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(getApplicationContext(), SpotifySearchActivity.class);
                this.startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
