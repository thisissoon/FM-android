package com.soon.fm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.soon.fm.backend.event.PerformChangeVolumeApiCall;
import com.soon.fm.helper.PreferencesHelper;
import com.soon.fm.utils.CircleTransform;
import com.soon.fm.utils.CurrentTrackCache;

import static com.squareup.picasso.Picasso.with;

public class QueueActivity extends BaseActivity {

    private PreferencesHelper preferences;
    private Integer volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerArrowToggle(toolbar);

        preferences = new PreferencesHelper(this);
        volume = CurrentTrackCache.getVolume();

        String avatarUrl = preferences.getUserAvatar();
        if (avatarUrl != null) {
            NavigationView navigation = (NavigationView) findViewById(R.id.navigation);
            ImageView avatar = (ImageView) navigation.getHeaderView(0).findViewById(R.id.img_user_avatar);
            setAvatar(avatarUrl, avatar);
        }
    }

    private void drawerArrowToggle(Toolbar toolbar) {
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    volume = Math.min(volume + 5, 100);
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    volume = Math.max(volume - 5, 0);
                    break;
            }
            changeVolume(volume);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void setAvatar(String url, ImageView avatar) {
        with(getApplicationContext()).load(url).transform(new CircleTransform()).placeholder(R.drawable.ic_person).into(avatar);
    }

    private void changeVolume(int volume) {
        String token = preferences.getUserApiToken();
        new PerformChangeVolumeApiCall(token, volume).execute();
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
                Intent intent = new Intent(getApplicationContext(), SpotifySearchActivity.class);
                this.startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
