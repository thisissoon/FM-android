package com.soon.fm.spotify;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.soon.fm.R;
import com.soon.fm.spotify.adapter.AlbumAdapter;
import com.soon.fm.spotify.adapter.ArtistsAdapter;
import com.soon.fm.spotify.adapter.SearchAdapter;
import com.soon.fm.spotify.adapter.TrackAdapter;
import com.soon.fm.spotify.api.Type;
import com.soon.fm.spotify.api.model.Search;

import java.io.IOException;

public class SpotifySearchFragment extends Fragment {

    private static final String TAG = SpotifySearchFragment.class.getName();
    private final Type type;
    private RecyclerView rw;
    private Context context;

    public SpotifySearchFragment(Type type) {
        super();
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void triggerSearching(final String value){
        new AsyncTask<Void, Void, Search>() {
            @Override
            protected Search doInBackground(Void... params) {
                SpotifyHelper spotifyHelper = new SpotifyHelper();
                Search spotifyResult = null;
                try {
                    spotifyResult = spotifyHelper.search(value, 25, type);
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
                    SearchAdapter adapter = null;
                    switch (type) {
                        case TRACKS:
                            adapter = new TrackAdapter(context, result.getTracks());
                            break;
                        case ALBUMS:
                            adapter = new AlbumAdapter(context, result.getAlbums());
                            break;
                        case ARTISTS:
                            adapter = new ArtistsAdapter(context, result.getArtists());
                            break;
                    }
                    if (adapter != null) {
                        rw.setAdapter(adapter);
                    }
                } else {
                    Log.e(TAG, "result is null");
                }
            }

        }.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue_list, container, false);
        context = inflater.getContext();
        rw = (RecyclerView) view.findViewById(R.id.rw_track_queue);

        final LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rw.setLayoutManager(llm);
        rw.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    SearchAdapter adapter = (SearchAdapter) recyclerView.getAdapter();
                    hideKeyboard(recyclerView);
                    int totalItemCount = llm.getItemCount();
                    int lastVisibleItem = llm.findLastVisibleItemPosition();
                    if (totalItemCount <= (lastVisibleItem + adapter.getItemCount())) {
                        adapter.loadMore();
                    }
                }
            }

        });

        return view;
    }

    private void hideKeyboard(RecyclerView v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//        searchInput.clearFocus();
    }

}
