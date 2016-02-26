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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.soon.fm.spotify.SearchAdapter;
import com.soon.fm.spotify.SpotifyHelper;
import com.soon.fm.spotify.api.model.Search;

import java.io.IOException;


public class SpotifySearchActivity extends BaseActivity {

    private static final String TAG = SpotifySearchActivity.class.getName();

    private EditText searchInput;
    private RecyclerView searchResultList;
    private Context context;

    private Search spotifyResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_search);

        searchInput = (EditText) this.findViewById(R.id.custom_bar_text);
        context = getApplicationContext();

        this.searchResultList = (RecyclerView) this.findViewById(R.id.cs_result_list);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        searchResultList.setLayoutManager(linearLayoutManager);
        searchResultList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    SearchAdapter adapter = (SearchAdapter) recyclerView.getAdapter();
                    hideKeyboard(recyclerView);
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (totalItemCount <= (lastVisibleItem + adapter.getItemCount())) {
                        adapter.loadMore();
                    }
                }
            }

        });
        implementSearchTextListener();
        ImageView customBarReturn = (ImageView) this.findViewById(R.id.custom_bar_return);
        customBarReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        searchInput.clearFocus();
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
                    String query = searchInput.getText().toString();
                    mapResultsFromCustomProviderToList(query);
                } else {
                    searchResultList.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }

    private void mapResultsFromCustomProviderToList(final String query) {
        new AsyncTask<Void, Void, Search>() {
            @Override
            protected Search doInBackground(Void... params) {
                SpotifyHelper spotifyHelper = new SpotifyHelper();
                spotifyResult = null;
                try {
                    spotifyResult = spotifyHelper.search(query, 25);
//                    if (spotifyResult.getAlbums() != null) {
//                        resultList.addAll(spotifyResult.getAlbums().getItems());
//                    }
//                    if (spotifyResult.getArtists() != null) {
//                        resultList.addAll(spotifyResult.getArtists().getItems());
//                    }

                } catch (IOException e) {
                    Log.e(TAG, String.format("Something went wrong %s", e.getMessage()));
                } catch (NullPointerException e) {
                    // Nothing has been found
                }
                return spotifyResult;
            }

            @Override
            protected void onPostExecute(Search result) {
                if (result != null) {
                    SearchAdapter adapter = new SearchAdapter(context, result);
                    searchResultList.setAdapter(adapter);
                }
            }

        }.execute();
    }

}
