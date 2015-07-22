package com.soon.fm.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.soon.fm.R;
import com.soon.fm.api.Queue;
import com.soon.fm.api.model.UserTrack;
import com.soon.fm.fragment.dummy.DummyContent;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class QueueFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String TAG = "QueueFragment";
    private OnFragmentInteractionListener mListener;

    private AbsListView mListView;

    private ArrayAdapter mAdapter;

    public QueueFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
        asyncUpdate();
    }

    private void asyncUpdate(){
        new FetchQueue().execute();
    }

    private void updateList(List<DummyContent.DummyItem> usertrack) {
        mAdapter.clear();
        for (DummyContent.DummyItem object : usertrack) {
            mAdapter.add(object);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String id);
    }

    private class FetchQueue extends AsyncTask<Void, Void, List<UserTrack>> {

        protected List<UserTrack> doInBackground(Void... params) {
            try {
                Queue queue = new Queue("https://api.thisissoon.fm/");
                return queue.getTracks();
            } catch (MalformedURLException e) {
                Log.wtf(TAG, e.getMessage());
            } catch (IOException e) {
                // TODO device is offline do something reasonable
            } catch (JSONException e) {
                Log.wtf(TAG, e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(List<UserTrack> userTrackList) {
            ArrayList<DummyContent.DummyItem> lst = new ArrayList<>();
            for (UserTrack userTrack : userTrackList) {
                lst.add(new DummyContent.DummyItem(userTrack.track.getName(), userTrack.track.getName()));
            }
            updateList(lst);
        }

    }

}
