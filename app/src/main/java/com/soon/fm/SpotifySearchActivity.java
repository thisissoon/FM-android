package com.soon.fm;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.soon.fm.spotify.SpotifySearchFragment;
import com.soon.fm.spotify.api.Type;

import java.util.ArrayList;
import java.util.List;


public class SpotifySearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_search);

        searchInput = (EditText) this.findViewById(R.id.custom_bar_text);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        implementSearchTextListener();
        ImageView customBarReturn = (ImageView) this.findViewById(R.id.custom_bar_return);
        customBarReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void implementSearchTextListener() {
        searchInput.addTextChangedListener(new TextWatcher() {
            // DO NOTHING
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                SpotifySearchFragment fragment = getCurrentSpotifySearchFragment();
                if (!"".equals(searchInput.getText().toString())) {
                    String query = searchInput.getText().toString();
                    fragment.triggerSearching(query);
                } else {
                    fragment.triggerSearching("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }

    private SpotifySearchFragment getCurrentSpotifySearchFragment() {
        FragmentPagerAdapter adapter = (FragmentPagerAdapter) viewPager.getAdapter();
        return (SpotifySearchFragment) adapter.getItem(tabLayout.getSelectedTabPosition());
    }

    public void setupViewPager(ViewPager upViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SpotifySearchFragment(Type.ALBUMS), "Albums");
        adapter.addFragment(new SpotifySearchFragment(Type.TRACKS), "Tracks");
        adapter.addFragment(new SpotifySearchFragment(Type.ARTISTS), "Artists");
        upViewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragment.setRetainInstance(true);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
