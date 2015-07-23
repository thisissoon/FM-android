package com.soon.fm;

import android.os.Bundle;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.changeActivity(CurrentTrackActivity.class);
    }

}
