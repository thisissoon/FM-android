package com.soon.fm;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.soon.fm.spotify.SearchAdapter;
import com.soon.fm.spotify.SpotifyHelper;
import com.soon.fm.spotify.api.model.Item;
import com.soon.fm.spotify.api.model.Search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SpotifySearchActivity extends BaseActivity {

    private static final String TAG = SpotifySearchActivity.class.getName();
    private String query;
    private EditText searchInput;
    private RecyclerView searchResultList;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_search);

        searchInput = (EditText) this.findViewById(R.id.custom_bar_text);
        context = getApplicationContext();

        this.searchResultList = (RecyclerView) this.findViewById(R.id.cs_result_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        searchResultList.setLayoutManager(linearLayoutManager);

        implementSearchTextListener();

        ImageView custom_bar_return = (ImageView) this.findViewById(R.id.custom_bar_return);
        custom_bar_return.setOnClickListener(new View.OnClickListener() {
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
                if (!"".equals(searchInput.getText().toString())) {
                    query = searchInput.getText().toString();
                    mapResultsFromCustomProviderToList();
                } else {
                    searchResultList.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }

    private void mapResultsFromCustomProviderToList() {
        new AsyncTask<Void, Void, List<Item>>() {
            @Override
            protected List<Item> doInBackground(Void[] params) {
                List<Item> resultList = new ArrayList<>();
                SpotifyHelper spotifyHelper = new SpotifyHelper();
                try {
                    Search spotifyResult = spotifyHelper.search(query, 10);
//                    if (spotifyResult.getAlbums() != null) {
//                        resultList.addAll(spotifyResult.getAlbums().getItems());
//                    }
//                    if (spotifyResult.getArtists() != null) {
//                        resultList.addAll(spotifyResult.getArtists().getItems());
//                    }
                    if (spotifyResult.getTracks() != null) {
                        resultList.addAll(spotifyResult.getTracks().getItems());
                    }
                } catch (IOException e) {
                    Log.e(TAG, String.format("Something went wrong %s", e.getMessage()));
                } catch (NullPointerException e) {
                    // Nothing has been found
                }
                return resultList;
            }

            @Override
            protected void onPostExecute(List<Item> resultList) {
                if (resultList != null) {
                    SearchAdapter adapter = new SearchAdapter(context, resultList);
                    searchResultList.setAdapter(adapter);
                }
            }

        }.execute();
    }

}
