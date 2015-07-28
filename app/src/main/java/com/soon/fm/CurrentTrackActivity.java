package com.soon.fm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.soon.fm.api.CurrentTrack;
import com.soon.fm.api.model.UserTrack;
import com.soon.fm.api.model.field.Duration;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;


public class CurrentTrackActivity extends BaseActivity {

    private static final String TAG = "CurrentTrackActivity";
    private Duration elapsedTime;
    private TextView totalTime;
    private TextView trackName;
    private TextView artistName;
    private TextView albumName;
    private ProgressBar progressBar;
    private TextView txtElapsedTime;
    private ImageView userImage;
    private ImageView trackImage;

    private Socket mSocket;
    private CountDownTimer timer;
    private Emitter.Listener onEndOfTrack = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "Track finished");
            timer.cancel();
        }
    };
    private Emitter.Listener onPause = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "Paused");
            if (timer != null) {
                try {
                    timer.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private Emitter.Listener onPlay = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "Playing");
            asyncFetchCurrentTrack();
        }
    };
    private Emitter.Listener onResume = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "Resumed");
            if (timer != null) {
                timer.start();
            }
        }
    };

    {
        try {
            mSocket = IO.socket(Constants.SOCKET);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

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
        albumName = (TextView) findViewById(R.id.album_name);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        txtElapsedTime = (TextView) findViewById(R.id.elapsed_time);
        userImage = (ImageView) findViewById(R.id.img_user);
        trackImage = (ImageView) findViewById(R.id.img_album);

        asyncUpdateView();
        mSocket.on(Constants.SocketEvents.END, onEndOfTrack);
        mSocket.on(Constants.SocketEvents.PLAY, onPlay);
        mSocket.on(Constants.SocketEvents.PAUSE, onPause);
        mSocket.on(Constants.SocketEvents.RESUME, onResume);
        mSocket.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off(Constants.SocketEvents.END, onEndOfTrack);
        mSocket.off(Constants.SocketEvents.PLAY, onPlay);
        mSocket.off(Constants.SocketEvents.PAUSE, onPause);
        mSocket.off(Constants.SocketEvents.RESUME, onResume);
        mSocket.disconnect();
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
        asyncFetchCurrentTrack();
    }

    private void asyncFetchCurrentTrack() {
        new FetchCurrent().execute();
    }

    private void updateView(final UserTrack currentTrack) {
        final Duration trackDuration = currentTrack.track.getDuration();

        totalTime.setText(trackDuration.toString());
        txtElapsedTime.setText(elapsedTime.toString());
        trackName.setText(currentTrack.track.getName());
        artistName.setText(TextUtils.join(", ", currentTrack.track.getArtists()));
        albumName.setText(currentTrack.track.getAlbum().getName());
//        userImage.setImageBitmap(currentTrack.user.getAvatar());
//        trackImage.setImageBitmap(currentTrack.track.getAlbum().getImage());

        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(trackDuration.getMillis(), 1000) {

            int currentMilliseconds = 0;

            @Override
            public void onTick(long millisUntilFinished_) {
                if (currentMilliseconds == 0) {
                    currentMilliseconds = elapsedTime.getMillis();
                }
                if (currentMilliseconds <= trackDuration.getMillis()) {
                    currentMilliseconds += 1000;
                }
                double progress = (currentMilliseconds / (double) trackDuration.getMillis()) * 100.0;
                progressBar.setProgress((int) progress);
                txtElapsedTime.setText(new Duration(currentMilliseconds).toString());
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private class FetchCurrent extends AsyncTask<Void, Void, UserTrack> {

        protected UserTrack doInBackground(Void... params) {
            try {
                UserTrack currentTrackWrapper = new UserTrack();
                CurrentTrack currentTrack = new CurrentTrack(Constants.FM_API);
                currentTrackWrapper.track = currentTrack.getTrack();
                currentTrackWrapper.user = currentTrack.getUser();

                elapsedTime = currentTrack.getElapsedTime();

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

        protected void onPostExecute(UserTrack currentTrack) {
            if (currentTrack != null) {
                updateView(currentTrack);
            }
        }
    }

}
