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

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.soon.fm.Constants;
import com.soon.fm.R;
import com.soon.fm.backend.BackendHelper;
import com.soon.fm.backend.model.QueueItem;
import com.soon.fm.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class QueueFragment extends Fragment {

    private static final String TAG = "QueueFragment";
    private final Socket mSocket;
    private AbsListView mListView;
    private QueueAdapter mAdapter;
    private Emitter.Listener onQueueChange = new Emitter.Listener() {
        @Override
        public void call(Object... args) {  // TODO some locker
            Log.i(TAG, "Queue changed");
            asyncUpdate();
        }
    };

    private Context context;

    {
        try {
            mSocket = IO.socket(Constants.SOCKET);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public QueueFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new QueueAdapter(getActivity(), new ArrayList<QueueItem>());
        asyncUpdate();

        mSocket.on(Constants.SocketEvents.ADD, onQueueChange);
        mSocket.on(Constants.SocketEvents.PLAY, onQueueChange);
        mSocket.connect();

        context = getActivity().getApplicationContext();
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
    public void onDestroy() {
        super.onDestroy();
        mSocket.off(Constants.SocketEvents.ADD, onQueueChange);
        mSocket.off(Constants.SocketEvents.PLAY, onQueueChange);
        mSocket.disconnect();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void asyncUpdate() {
        new FetchQueue().execute();
    }

    private void updateList(List<QueueItem> userTrack) {
        mAdapter.clear();
        mAdapter.addAll(userTrack);
    }

    private class FetchQueue extends AsyncTask<Void, Void, List<QueueItem>> {

        protected List<QueueItem> doInBackground(Void... params) {
            try {
                BackendHelper backend = new BackendHelper(Constants.FM_API.toString());
                return backend.getPlayerQueue();
            } catch (MalformedURLException e) {
                Log.wtf(TAG, e.getMessage());
            } catch (IOException e) {
                Log.wtf(TAG, e.getMessage());
                // TODO device is offline do something reasonable
            }

            return null;
        }

        protected void onPostExecute(List<QueueItem> userTrackList) {
            updateList(userTrackList);
        }

    }

    private class QueueAdapter extends ArrayAdapter<QueueItem> {

        public QueueAdapter(Context context, List<QueueItem> objects) {
            super(context, R.layout.queue_item, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            QueueItem userTrack = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.queue_item, parent, false);
            }
            TextView trackName = (TextView) convertView.findViewById(R.id.track_name);
            TextView artistName = (TextView) convertView.findViewById(R.id.artist_name);
            ImageView userAvatar = (ImageView) convertView.findViewById(R.id.img_user);
            ImageView albumImage = (ImageView) convertView.findViewById(R.id.img_album);
            trackName.setText(userTrack.getTrack().getName());
            artistName.setText(TextUtils.join(", ", userTrack.getTrack().getArtists()));

            Picasso.with(context).load(userTrack.getUser().getAvatarUrl()).transform(new CircleTransform()).into(userAvatar);
            Picasso.with(context).load(userTrack.getTrack().getAlbum().getImages().get(2).getUrl()).into(albumImage);
            return convertView;
        }
    }
}
