package com.soon.fm;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.soon.fm.sdk.CurrentTrack;
import com.soon.fm.sdk.entity.Track;


public class CurrentTrackActivity extends Activity {

    private TextView totalTime;
    private TextView trackName;
    private TextView artistName;
    private CurrentTrack currentTrackSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_track);

        currentTrackSDK = new CurrentTrack();
        totalTime = (TextView) findViewById(R.id.total_time);
        trackName = (TextView) findViewById(R.id.track_name);
        artistName = (TextView) findViewById(R.id.artist_name);

        updateView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_current_track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateView() {
        Track currentTrack = currentTrackSDK.getTrack();
        totalTime.setText(Integer.toString(currentTrack.getDuration()));
        trackName.setText(currentTrack.getName());
        artistName.setText(TextUtils.join(", ", currentTrack.getArtists()));
    }
}
