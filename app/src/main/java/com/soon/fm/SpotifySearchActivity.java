package com.soon.fm;

import android.app.SearchManager;
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

import com.soon.fm.spotify.ResultItem;
import com.soon.fm.spotify.SearchAdapter;

import java.util.ArrayList;
import java.util.List;


public class SpotifySearchActivity extends BaseActivity {

    private static final String TAG = SpotifySearchActivity.class.getName();
    private String query;
    private EditText searchInput;
    private RecyclerView searchResultList;

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
        new AsyncTask<Void, Void, List>() {

            @Override
            protected List doInBackground(Void[] params) {
                Log.d(TAG, String.format("Searching for %s", query));
                List<ResultItem> resultList = new ArrayList<>();
                for (int i = 0; i < query.length(); i++) {
                    resultList.add(new ResultItem());
                }
                return resultList;
            }

            @Override
            protected void onPostExecute(List resultList) {
                if (resultList != resultList) {
                    SearchAdapter adapter = new SearchAdapter(resultList);
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
