package com.soon.fm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    
    protected void changeActivity(Class<? extends Activity> activity) {
        Intent intent = new Intent(this, activity);
        this.startActivity(intent);
        finish();
    }

}
