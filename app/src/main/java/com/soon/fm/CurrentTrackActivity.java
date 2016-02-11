package com.soon.fm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.soon.fm.async.CallbackInterface;
import com.soon.fm.async.FetchCurrent;
import com.soon.fm.backend.event.PerformChangeVolumeApiCall;
import com.soon.fm.backend.event.PerformMuteApiCall;
import com.soon.fm.backend.event.PerformPauseApiCall;
import com.soon.fm.backend.event.PerformSkipTrack;
import com.soon.fm.backend.model.CurrentTrack;
import com.soon.fm.backend.model.Player;
import com.soon.fm.backend.model.field.Duration;
import com.soon.fm.helper.PreferencesHelper;
import com.soon.fm.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

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
    private ImageButton skipButton;

    private Boolean isMute = false;
    private Boolean isPlaying = true;
    private Integer volume = 50;

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
    private Toast flash;
    private CurrentTrack currentTrack;
    private LinearLayout footerCurrentTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isDeviceOnline()) {
            showFlash("Device is not online");
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
        skipButton = (ImageButton) findViewById(R.id.cnt_skip);

        footerCurrentTrack = (LinearLayout) findViewById(R.id.footer);
        toggleMute.setOnClickListener(this);
        togglePlay.setOnClickListener(this);
        skipButton.setOnClickListener(this);

        try {
            mSocket = IO.socket(getString(R.string.socket));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.on(getString(R.string.socket_events_end), onEndOfTrack);
        mSocket.on(getString(R.string.socket_events_play), onPlay);
        mSocket.on(getString(R.string.socket_events_pause), onPause);
        mSocket.on(getString(R.string.socket_events_resume), onResume);
        mSocket.on(getString(R.string.socket_events_set_mute), onMute);
        mSocket.connect();

        context = getApplicationContext();
        preferences = new PreferencesHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentTrack = CurrentTrackCache.getCurrentTrack();
        isMute = CurrentTrackCache.getIsMuted();
        volume = CurrentTrackCache.getVolume();

        if (currentTrack == null) {
            footerCurrentTrack.post(new Runnable(){  // hide footer
                public void run(){
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) footerCurrentTrack.getLayoutParams();
                    params.bottomMargin = -footerCurrentTrack.getHeight();
                    footerCurrentTrack.setLayoutParams(params);
                    Log.d(TAG, String.format("Footer bottom margin: %s", params.bottomMargin));
                }
            });
        } else {
            updateCurrentTrack(currentTrack);
        }
        asyncFetchCurrentTrack();  // update current track anyway to sync progress bar
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off(getString(R.string.socket_events_end), onEndOfTrack);
        mSocket.off(getString(R.string.socket_events_play), onPlay);
        mSocket.off(getString(R.string.socket_events_pause), onPause);
        mSocket.off(getString(R.string.socket_events_resume), onResume);
        mSocket.off(getString(R.string.socket_events_set_mute), onMute);
        mSocket.disconnect();
    }

    private void showFlash(String text) {
        if (flash != null) {
            flash.cancel();
        }
        flash = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        flash.show();
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

            case R.id.cnt_skip:
                Log.d(TAG, "Clicked on skip");
                performSkip();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String token = preferences.getUserApiToken();
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    volume = Math.min(volume + 5, 100);
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    volume = Math.max(volume - 5, 0);
                    break;
            }
            new PerformChangeVolumeApiCall(token, volume).execute();
            showFlash(String.format("Volume set to %d", volume));
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
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

    private void performSkip() {
        String token = preferences.getUserApiToken();
        new PerformSkipTrack(token).execute();
    }

    private void asyncFetchCurrentTrack() {
        new FetchCurrent(getString(R.string.fm_api), new CallbackInterface<CurrentTrack>() {
            @Override
            public void onSuccess(CurrentTrack obj) {
                updateCurrentTrack(obj);
            }

            @Override
            public void onFail() {

            }
        }).execute();
    }

    private void updateCurrentTrack(final CurrentTrack currentTrack) {
        if(currentTrack == null) {
            final int initialHeight = footerCurrentTrack.getHeight();
            Log.d(TAG, String.format("initial height: %s", initialHeight));
            Animation anim = new Animation() {
                @Override
                public boolean willChangeBounds() {
                    return true;
                }

                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) footerCurrentTrack.getLayoutParams();
                    params.bottomMargin = (int) - (initialHeight * interpolatedTime);
                    footerCurrentTrack.setLayoutParams(params);
                    Log.d(TAG, String.format("bottom margin set to: %s", params.bottomMargin));
                }
            };
            anim.setDuration(2000);
            footerCurrentTrack.startAnimation(anim);
            this.currentTrack = null;
            return;
        }

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

        if(this.currentTrack == null) {
            final int initialHeight = footerCurrentTrack.getHeight();
            Log.d(TAG, String.format("initial height: %s", initialHeight));
            Animation anim = new Animation() {
                @Override
                public boolean willChangeBounds() {
                    return true;
                }

                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) footerCurrentTrack.getLayoutParams();
                    params.bottomMargin = (int) (initialHeight * interpolatedTime) - initialHeight;
                    footerCurrentTrack.setLayoutParams(params);
                    Log.d(TAG, String.format("bottom margin set to: %s", params.bottomMargin));
                }
            };
            anim.setDuration(2000);
            footerCurrentTrack.startAnimation(anim);
        }
        this.currentTrack = currentTrack;
    }

    private void setMuteToggle(Boolean state) {
        isMute = state;
        toggleMute.setChecked(isMute);
    }

    private void setPlayToggle(Boolean state) {
        isPlaying = state;
        togglePlay.setChecked(!isPlaying);
    }

}
