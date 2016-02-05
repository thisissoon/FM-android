package com.soon.fm;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.soon.fm.backend.BackendHelper;
import com.soon.fm.backend.event.PerformMuteApiCall;
import com.soon.fm.backend.event.PerformPauseApiCall;
import com.soon.fm.backend.model.CurrentTrack;
import com.soon.fm.backend.model.Player;
import com.soon.fm.backend.model.field.Duration;
import com.soon.fm.helper.PreferencesHelper;
import com.soon.fm.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;


public class CurrentTrackActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "CurrentTrackActivity";

    /* System */
    private PreferencesHelper preferences;

    /* UI */
    private TextView totalTime;
    private TextView trackName;
    private TextView artistName;
    private TextView albumName;
    private ProgressBar progressBar;
    private TextView txtElapsedTime;
    private ImageView userImage;
    private ImageView albumImage;
    private ToggleButton toggleMute;
    private ToggleButton togglePlay;

    private Boolean isMute = false;
    private Boolean isPlaying = true;

    private Socket mSocket;
    private CountDownTimer timer;

    /* socket listeners */
    private Emitter.Listener onEndOfTrack = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "[Event listener] Track finished");
            timer.cancel();
        }
    };
    private Emitter.Listener onPause = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "[Event listener] Paused");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setPlayToggle(false);
                }
            });
        }
    };

    private Emitter.Listener onPlay = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "[Event listener] Playing");
            asyncFetchCurrentTrack();
        }
    };
    private Emitter.Listener onResume = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "[Event listener] Resumed");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setPlayToggle(true);
                }
            });
        }
    };
    private Emitter.Listener onMute = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "[Event listener] Mute");
            try {
                JSONObject json = (JSONObject) args[0];
                final boolean muted = json.getBoolean("mute");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setMuteToggle(muted);
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, String.format("[Event listener] invalid json %s", args[0]));
            }
        }
    };

    private Context context;

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
        albumImage = (ImageView) findViewById(R.id.img_album);

        toggleMute = (ToggleButton) findViewById(R.id.toggle_mute_unmute);
        togglePlay = (ToggleButton) findViewById(R.id.toggle_pause_play);

        toggleMute.setOnClickListener(this);
        togglePlay.setOnClickListener(this);

        mSocket.on(Constants.SocketEvents.END, onEndOfTrack);
        mSocket.on(Constants.SocketEvents.PLAY, onPlay);
        mSocket.on(Constants.SocketEvents.PAUSE, onPause);
        mSocket.on(Constants.SocketEvents.RESUME, onResume);
        mSocket.on(Constants.SocketEvents.SET_MUTE, onMute);
        mSocket.connect();

        context = getApplicationContext();
        preferences = new PreferencesHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        asyncUpdateView();
        asyncIsMuted();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off(Constants.SocketEvents.END, onEndOfTrack);
        mSocket.off(Constants.SocketEvents.PLAY, onPlay);
        mSocket.off(Constants.SocketEvents.PAUSE, onPause);
        mSocket.off(Constants.SocketEvents.RESUME, onResume);
        mSocket.off(Constants.SocketEvents.SET_MUTE, onMute);
        mSocket.disconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_mute_unmute:
                Log.d(TAG, "Clicked on mute/unmute toggle");
                performMute((ToggleButton) v);
                break;

            case R.id.toggle_pause_play:
                Log.d(TAG, "Clicked on play/pause toggle");
                performPause((ToggleButton) v);
                break;
        }
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
                Intent intent = new Intent(this, SpotifySearchActivity.class);
                this.startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void performPause(ToggleButton btn) {
        String token = preferences.getUserApiToken();
        new PerformPauseApiCall(token, btn.isChecked()).execute();
    }

    private void performMute(ToggleButton btn) {
        String token = preferences.getUserApiToken();
        new PerformMuteApiCall(token, btn.isChecked()).execute();
    }

    private void asyncUpdateView() {
        asyncFetchCurrentTrack();
    }

    private void asyncFetchCurrentTrack() {
        new FetchCurrent().execute();
    }

    private void asyncIsMuted() {
        new IsMuted().execute();
    }

    private void updateCurrentTrack(final CurrentTrack currentTrack) {
        final Duration trackDuration = currentTrack.getTrack().getDuration();

        totalTime.setText(trackDuration.toString());
        trackName.setText(currentTrack.getTrack().getName());
        artistName.setText(TextUtils.join(", ", currentTrack.getTrack().getArtists()));
        albumName.setText(currentTrack.getTrack().getAlbum().getName());

        Picasso.with(context).load(currentTrack.getUser().getAvatarUrl()).transform(new CircleTransform()).into(userImage);
        Picasso.with(context).load(currentTrack.getTrack().getAlbum().getImages().get(0).getUrl()).into(albumImage);

        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(trackDuration.getMillis(), 1000) {
            int currentMilliseconds = 0;

            @Override
            public void onTick(long millisUntilFinished_) {
                Player player = currentTrack.getPlayer();
                int trackDuration = currentTrack.getTrack().getDuration().getMillis();

                if (currentMilliseconds == 0) {
                    currentMilliseconds = player.getElapsedTime();
                }
                if (isPlaying && currentMilliseconds <= trackDuration) {
                    currentMilliseconds += 1000;
                }
                double progress = (currentMilliseconds / (double) trackDuration) * 100.0;
                progressBar.setProgress((int) progress);
                txtElapsedTime.setText(new Duration(currentMilliseconds).toString());
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void setMuteToggle(Boolean state) {
        isMute = state;
        toggleMute.setChecked(isMute);
    }

    private void setPlayToggle(Boolean state) {
        isPlaying = state;
        togglePlay.setChecked(!isPlaying);
    }

    private class FetchCurrent extends AsyncTask<Void, Void, com.soon.fm.backend.model.CurrentTrack> {

        protected CurrentTrack doInBackground(Void... params) {
            try {
                BackendHelper backend = new BackendHelper(Constants.FM_API);
                return backend.getCurrentTrack();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(com.soon.fm.backend.model.CurrentTrack currentTrack) {
            if (currentTrack != null) {
                updateCurrentTrack(currentTrack);
            }
        }
    }

    private class IsMuted extends AsyncTask<Void, Void, Boolean> {

        protected Boolean doInBackground(Void... params) {
            BackendHelper backend = new BackendHelper(Constants.FM_API);
            Boolean isMuted = false;
            try {
                isMuted = backend.isMuted();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return isMuted;
        }

        protected void onPostExecute(Boolean muted) {
            setMuteToggle(muted);
        }

    }

}
