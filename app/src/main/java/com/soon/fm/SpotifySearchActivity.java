package com.soon.fm;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

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

        searchInput = (EditText) this.findViewById(R.id.custombar_text);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }

        context = getApplicationContext();

        this.searchResultList = (RecyclerView) this.findViewById(R.id.cs_result_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        searchResultList.setLayoutManager(linearLayoutManager);

        implementSearchTextListener();
    }

    private void doMySearch(String query) {
        Log.d(TAG, String.format("Search query %s", query));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_spotify_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Log.d(TAG, "Toolbar open settings hit");
                return true;

            case R.id.action_search:
                // User chose the "Favorite" action, mark the current item
                Log.d(TAG, "Toolbar search hit");
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void implementSearchTextListener() {
        // Gets the event of pressing search button on soft keyboard
        TextView.OnEditorActionListener searchListener = new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    sendSearchIntent();
                }
                return true;
            }
        };

        searchInput.setOnEditorActionListener(searchListener);

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

    private void sendSearchIntent() {
        Intent sendIntent = new Intent(this, SpotifySearchActivity.class);
        sendIntent.setAction(Intent.ACTION_SEARCH);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        sendIntent.putExtra(SearchManager.QUERY, query);
        startActivity(sendIntent);
        finish();
    }
}
