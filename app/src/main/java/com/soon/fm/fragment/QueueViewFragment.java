package com.soon.fm.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.soon.fm.R;
import com.soon.fm.async.CallbackInterface;
import com.soon.fm.async.FetchCurrent;
import com.soon.fm.backend.event.PerformChangeVolumeApiCall;
import com.soon.fm.backend.event.PerformMuteApiCall;
import com.soon.fm.backend.event.PerformPauseApiCall;
import com.soon.fm.backend.event.PerformSkipTrack;
import com.soon.fm.backend.model.CurrentTrack;
import com.soon.fm.backend.model.Player;
import com.soon.fm.backend.model.QueueItem;
import com.soon.fm.backend.model.field.Duration;
import com.soon.fm.helper.PreferencesHelper;
import com.soon.fm.utils.CircleTransform;
import com.soon.fm.utils.CurrentTrackCache;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import static com.google.android.gms.internal.zzir.runOnUiThread;

public class QueueViewFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = QueueViewFragment.class.getName();

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
    private SeekBar volumeBar;

    private Boolean isMute = false;
    private Boolean isPlaying = true;
    private Integer volume = 50;

    private Socket mSocket;
    private CountDownTimer timer;
    private Emitter.Listener onPause = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "[listener.onPause] playing toggle updated");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setPlayToggle(false);
                }
            });
        }
    };
    private Emitter.Listener onResume = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "[listener.onResume] playing toggle updated");
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
            Log.i(TAG, "[listener.onMute] set muted flag");
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
                Log.e(TAG, String.format("[listener.onMute] invalid json %s", args[0]));
            }
        }
    };

    private Emitter.Listener onVolume = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, String.format("[listener.onVolume] set muted flag: %s", args[0]));
            try {
                JSONObject json = (JSONObject) args[0];
                final int volume = json.getInt("volume");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateVolumeBar(volume);
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, String.format("[listener.onMute] invalid json %s", args[0]));
            }
        }
    };
    private Context context;
    private CurrentTrack currentTrack;
    /* socket listeners */
    private Emitter.Listener onEndOfTrack = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "[listener.onEndOfTrack] Track finished");
            if (timer != null) {
                timer.cancel();
            }
            final CurrentTrack topTrack;
            if (CurrentTrackCache.getQueue().isEmpty()) {
                topTrack = null;
            } else {  // get first from the queue and remove it from there
                QueueItem item = CurrentTrackCache.getQueue().get(0);
                topTrack = new CurrentTrack(item);
                CurrentTrackCache.getQueue().remove(0);
            }
            Log.d(TAG, String.format("[listener.onEndOfTrack] hot track swap form the queue %s", topTrack));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCurrentTrack(topTrack);
                }
            });
        }
    };
    private Emitter.Listener onPlay = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "[listener.onPlay] fetch track from backend");
            asyncFetchCurrentTrack();
        }
    };
    private LinearLayout footerCurrentTrack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.queue_fragment, container, false);

        totalTime = (TextView) rootView.findViewById(R.id.total_time);
        trackName = (TextView) rootView.findViewById(R.id.track_name);
        artistName = (TextView) rootView.findViewById(R.id.artist_name);
        albumName = (TextView) rootView.findViewById(R.id.album_name);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        txtElapsedTime = (TextView) rootView.findViewById(R.id.elapsed_time);
        userImage = (ImageView) rootView.findViewById(R.id.img_user);
        albumImage = (ImageView) rootView.findViewById(R.id.img_album);

        toggleMute = (ToggleButton) rootView.findViewById(R.id.toggle_mute_unmute);
        togglePlay = (ToggleButton) rootView.findViewById(R.id.toggle_pause_play);
        skipButton = (ImageButton) rootView.findViewById(R.id.cnt_skip);

        volumeBar = (SeekBar) rootView.findViewById(R.id.volumeBar);

        footerCurrentTrack = (LinearLayout) rootView.findViewById(R.id.footer);
        toggleMute.setOnClickListener(this);
        togglePlay.setOnClickListener(this);
        skipButton.setOnClickListener(this);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int vol = seekBar.getProgress();
                if (volume != vol) {
                    changeVolume(vol);
                }
            }
        });

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
        mSocket.on(getString(R.string.socket_events_set_volume), onVolume);
        mSocket.connect();

        context = rootView.getContext();
        preferences = new PreferencesHelper(context);

        currentTrack = CurrentTrackCache.getCurrentTrack();
        isMute = CurrentTrackCache.getIsMuted();
        volume = CurrentTrackCache.getVolume();
        updateVolumeBar(volume);

        if (currentTrack == null) {  // hide footer
            footerCurrentTrack.post(new Runnable() {
                public void run() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) footerCurrentTrack.getLayoutParams();
                    params.bottomMargin = -footerCurrentTrack.getHeight();
                    footerCurrentTrack.setLayoutParams(params);
                }
            });
        } else {
            updateCurrentTrack(currentTrack);
        }
        updateCurrentTrack(currentTrack);
        asyncFetchCurrentTrack();  // update current track anyway to sync progress bar

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off(getString(R.string.socket_events_end), onEndOfTrack);
        mSocket.off(getString(R.string.socket_events_play), onPlay);
        mSocket.off(getString(R.string.socket_events_pause), onPause);
        mSocket.off(getString(R.string.socket_events_resume), onResume);
        mSocket.off(getString(R.string.socket_events_set_mute), onMute);
        mSocket.off(getString(R.string.socket_events_set_volume), onVolume);
        mSocket.disconnect();
    }

    private void changeVolume(int volume) {
        String token = preferences.getUserApiToken();
        updateVolumeBar(volume);
        new PerformChangeVolumeApiCall(token, volume).execute();
    }

    private void updateVolumeBar(int volume) {
        volumeBar.setProgress(volume);
    }

    private void updateCurrentTrack(final CurrentTrack track) {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) footerCurrentTrack.getLayoutParams();
//        if(track == null) {   // footer slide down
//            final int initialHeight = footerCurrentTrack.getHeight();
//            Animation anim = new Animation() {
//                @Override
//                public boolean willChangeBounds() {
//                    return true;
//                }
//
//                @Override
//                protected void applyTransformation(float interpolatedTime, Transformation t) {
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) footerCurrentTrack.getLayoutParams();
//                    params.bottomMargin = (int) - (initialHeight * interpolatedTime);
//                    footerCurrentTrack.setLayoutParams(params);
//                }
//            };
//            anim.setDuration(500);
//            footerCurrentTrack.startAnimation(anim);
//            this.currentTrack = null;
//            return;
//        }
        if (track == null) {  // TODO nothing to play
            return;
        }

        final Duration trackDuration = track.getTrack().getDuration();

        totalTime.setText(trackDuration.toString());
        trackName.setText(track.getTrack().getName());
        artistName.setText(TextUtils.join(", ", track.getTrack().getArtists()));
        albumName.setText(track.getTrack().getAlbum().getName());

        Picasso.with(context).load(track.getUser().getAvatarUrl()).placeholder(R.drawable.ic_person).transform(new CircleTransform()).into(userImage);
        Picasso.with(context).load(track.getTrack().getAlbum().getImages().get(2).getUrl()).placeholder(R.drawable.ic_album).into(albumImage);

        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(trackDuration.getMillis(), 1000) {
            int currentMilliseconds = 0;

            @Override
            public void onTick(long millisUntilFinished_) {
                Player player = track.getPlayer();
                int trackDuration = track.getTrack().getDuration().getMillis();

                if (currentMilliseconds == 0) {
                    currentMilliseconds = player.getElapsedTime();
                }
                currentMilliseconds += 1000;
                currentMilliseconds = Math.min(trackDuration, currentMilliseconds);
                double progress = (currentMilliseconds / (double) trackDuration) * 100.0;
                progressBar.setProgress((int) progress);
                txtElapsedTime.setText(new Duration(currentMilliseconds).toString());
            }

            @Override
            public void onFinish() {

            }
        }.start();

//        if(params.bottomMargin < 0) {  // footer slide up
//            final int initialHeight = footerCurrentTrack.getHeight();
//            Animation anim = new Animation() {
//                @Override
//                public boolean willChangeBounds() {
//                    return true;
//                }
//
//                @Override
//                protected void applyTransformation(float interpolatedTime, Transformation t) {
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) footerCurrentTrack.getLayoutParams();
//                    params.bottomMargin = (int) (initialHeight * interpolatedTime) - initialHeight;
//                    footerCurrentTrack.setLayoutParams(params);
//                }
//            };
//            anim.setDuration(500);
//            footerCurrentTrack.startAnimation(anim);
//        }
        this.currentTrack = track;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_mute_unmute:
                Log.d(TAG, "Clicked on mute/unmute toggle");
                performMute(((ToggleButton) v).isChecked());
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

    private void performMute(boolean mute) {
        String token = preferences.getUserApiToken();
        new PerformMuteApiCall(token, mute).execute();
    }

    private void performPause(ToggleButton btn) {
        String token = preferences.getUserApiToken();
        new PerformPauseApiCall(token, btn.isChecked()).execute();
    }

    private void performSkip() {
        String token = preferences.getUserApiToken();
        new PerformSkipTrack(token).execute();
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
