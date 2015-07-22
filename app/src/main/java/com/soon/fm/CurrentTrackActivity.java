package com.soon.fm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.soon.fm.api.CurrentTrack;
import com.soon.fm.api.model.Track;
import com.soon.fm.api.model.User;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;


public class CurrentTrackActivity extends BaseActivity {

    private static final String TAG = "CurrentTrackActivity";
    private TextView totalTime;
    private TextView trackName;
    private TextView artistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isDeviceOnline()) {
            Toast.makeText(getApplicationContext(), "Device is not online", Toast.LENGTH_LONG);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_track);

        totalTime = (TextView) findViewById(R.id.total_time);
        trackName = (TextView) findViewById(R.id.track_name);
        artistName = (TextView) findViewById(R.id.artist_name);


        asyncUpdateView();
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

    private void asyncUpdateView() {
        new FetchCurrent().execute();
    }

    private void updateView(CurrentTrackWrapper currentTrack) {
        totalTime.setText(Integer.toString(currentTrack.track.getDuration()));
        trackName.setText(currentTrack.track.getName());
        artistName.setText(TextUtils.join(", ", currentTrack.track.getArtists()));
    }

    private class CurrentTrackWrapper {
        public Track track;
        public User user;
    }

    private class FetchCurrent extends AsyncTask<Void, Void, CurrentTrackWrapper> {

        protected CurrentTrackWrapper doInBackground(Void... params) {
            try {
                CurrentTrackWrapper currentTrackWrapper = new CurrentTrackWrapper();
                CurrentTrack currentTrack = new CurrentTrack("https://api.thisissoon.fm/");
                currentTrackWrapper.track = currentTrack.getTrack();
                currentTrackWrapper.user = currentTrack.getUser();
                return currentTrackWrapper;
            } catch (MalformedURLException e) {
                Log.wtf(TAG, e.getMessage());
            } catch (IOException e) {
                // TODO device is offline do something reasonable
            } catch (JSONException e) {
                Log.wtf(TAG, e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(CurrentTrackWrapper currentTrack) {
            if (currentTrack != null) {
                updateView(currentTrack);
            }
        }

    }

}
