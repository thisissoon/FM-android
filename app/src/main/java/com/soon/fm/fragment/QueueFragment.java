package com.soon.fm.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.soon.fm.Constants;
import com.soon.fm.R;
import com.soon.fm.api.Queue;
import com.soon.fm.api.model.UserTrack;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class QueueFragment extends Fragment {

    private static final String TAG = "QueueFragment";

    private AbsListView mListView;

    private QueueAdapter mAdapter;

    public QueueFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new QueueAdapter(getActivity(), new ArrayList<UserTrack>());
        asyncUpdate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void asyncUpdate() {
        new FetchQueue().execute();
    }

    private void updateList(List<UserTrack> userTrack) {
        mAdapter.clear();
        mAdapter.addAll(userTrack);
    }

    private class FetchQueue extends AsyncTask<Void, Void, List<UserTrack>> {

        protected List<UserTrack> doInBackground(Void... params) {
            try {
                Queue queue = new Queue(Constants.FM_API);
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
            updateList(userTrackList);
        }

    }

    private class QueueAdapter extends ArrayAdapter<UserTrack> {

        public QueueAdapter(Context context, List<UserTrack> objects) {
            super(context, R.layout.queue_item, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserTrack userTrack = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.queue_item, parent, false);
            }
            TextView trackName = (TextView) convertView.findViewById(R.id.track_name);
            TextView artistName = (TextView) convertView.findViewById(R.id.artist_name);
            ImageView userAvatar = (ImageView) convertView.findViewById(R.id.img_user);
            ImageView albumImage = (ImageView) convertView.findViewById(R.id.img_album);
            trackName.setText(userTrack.track.getName());
            artistName.setText(TextUtils.join(", ", userTrack.track.getArtists()));
            userAvatar.setImageBitmap(userTrack.user.getAvatar());
            albumImage.setImageBitmap(userTrack.track.getAlbum().getImage());
            return convertView;
        }
    }
}
