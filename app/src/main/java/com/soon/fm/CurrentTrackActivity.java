package com.soon.fm;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.soon.fm.backend.BackendHelper;
import com.soon.fm.backend.model.CurrentTrack;
import com.soon.fm.backend.model.Player;
import com.soon.fm.backend.model.field.Duration;
import com.soon.fm.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;


public class CurrentTrackActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "CurrentTrackActivity";

    /* UI */
    private TextView totalTime;
    private TextView trackName;
    private TextView artistName;
    private TextView albumName;
    private ProgressBar progressBar;
    private TextView txtElapsedTime;
    private ImageView userImage;
    private ImageView albumImage;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private Socket mSocket;
    private CountDownTimer timer;

    /* socket listeners */
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
    private GoogleApiClient mGoogleApiClient;
    private Context context;
    private Duration elapsedTime;

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

        findViewById(R.id.google_sign_in).setOnClickListener(this);

        asyncUpdateView();
        mSocket.on(Constants.SocketEvents.END, onEndOfTrack);
        mSocket.on(Constants.SocketEvents.PLAY, onPlay);
        mSocket.on(Constants.SocketEvents.PAUSE, onPause);
        mSocket.on(Constants.SocketEvents.RESUME, onResume);
        mSocket.connect();

        context = getApplicationContext();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void asyncUpdateView() {
        asyncFetchCurrentTrack();
    }

    private void asyncFetchCurrentTrack() {
        new FetchCurrent().execute();
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
                Player player= currentTrack.getPlayer();
                int trackDuration = currentTrack.getTrack().getDuration().getMillis();

                if (currentMilliseconds == 0) {
                    currentMilliseconds = player.getElapsedTime();
                }
                if (currentMilliseconds <= trackDuration) {
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                Toast.makeText(getApplicationContext(), connectionResult.toString(), Toast.LENGTH_LONG);
            }
        } else {
            // Show the signed-out UI
//            showSignedOutUI();
        }
    }

    private class FetchCurrent extends AsyncTask<Void, Void, com.soon.fm.backend.model.CurrentTrack> {

        protected com.soon.fm.backend.model.CurrentTrack doInBackground(Void... params) {
            try {
                BackendHelper backend = new BackendHelper(Constants.FM_API.toString());
                return backend.getCurrentTrack();
            } catch (MalformedURLException e) {
                Log.wtf(TAG, e.getMessage());
            } catch (IOException e) {
                // TODO device is offline do something reasonable
                Log.wtf(TAG, e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(com.soon.fm.backend.model.CurrentTrack currentTrack) {
            if (currentTrack != null) {
                updateCurrentTrack(currentTrack);
            }
        }
    }

}
