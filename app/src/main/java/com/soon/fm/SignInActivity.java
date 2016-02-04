package com.soon.fm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.soon.fm.backend.async.Authorize;
import com.soon.fm.backend.model.AccessToken;
import com.soon.fm.helper.PreferencesHelper;

public class SignInActivity extends BaseActivity implements View.OnClickListener, OnTaskCompleted {

    private static final String TAG = "SignInActivity";

    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.skip).setOnClickListener(this);

        String serverClientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestServerAuthCode(serverClientId, false).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(TAG, "[Google::onConnectionFailed] " + connectionResult.isSuccess());
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.skip:
                performSkip();
                break;
        }
    }

    private void performSkip() {
        changeActivity(CurrentTrackActivity.class);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onSuccess(Object obj) {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }

        PreferencesHelper preferences = new PreferencesHelper(getApplicationContext());
        preferences.saveUserApiToken(((AccessToken) obj).getAccessToken());
        changeActivity(SpotifySearchActivity.class);
    }

    @Override
    public void onFailed() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
        Toast.makeText(getApplicationContext(), "Cannot log you in", Toast.LENGTH_LONG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, String.format("[getServerAuthCode] %s", acct.getServerAuthCode()));
            performBackendAuthorisation(acct);
        } else {
            Log.e(TAG, String.format("[sign in failed] status: %s", result.getStatus()));
        }
    }

    private void performBackendAuthorisation(GoogleSignInAccount acct) {
        blabla();
        new Authorize(this).execute(acct.getServerAuthCode());
    }

    private void blabla() {
        progress = new ProgressDialog(this);
        progress.setTitle("Signing in");
        progress.setMessage("Wait while loading...");
        progress.show();
    }

}