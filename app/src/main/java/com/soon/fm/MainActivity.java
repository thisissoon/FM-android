package com.soon.fm;

import android.os.Bundle;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.changeActivity(SpotifySearchActivity.class);
//
//        PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
//        if(preferencesHelper.getUserApiToken() == null) {
//            this.changeActivity(SignInActivity.class);
//        } else {
//            this.changeActivity(CurrentTrackActivity.class);
//        }
    }

}
