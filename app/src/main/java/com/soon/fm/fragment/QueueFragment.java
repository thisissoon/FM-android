package com.soon.fm.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.soon.fm.Constants;
import com.soon.fm.R;
import com.soon.fm.backend.BackendHelper;
import com.soon.fm.backend.model.QueueItem;
import com.soon.fm.helper.PreferencesHelper;
import com.soon.fm.utils.CircleTransform;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.squareup.picasso.Picasso.with;


public class QueueFragment extends Fragment {

    private static final String TAG = QueueFragment.class.getName();
    private final Socket mSocket;
    private PreferencesHelper preferences;
    private ArrayList<QueueItem> queue = new ArrayList<>();
    private QueueAdapter mAdapter;
    private Emitter.Listener onQueueChange = new Emitter.Listener() {
        @Override
        public void call(Object... args) {  // TODO some locker
            Log.i(TAG, "Queue changed");
            asyncUpdate();
        }
    };

    private Context context;
    private RecyclerView mListView;
    private LinearLayoutManager mLayoutManager;

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

        mSocket.on(getString(R.string.socket_events_add), onQueueChange);
        mSocket.on(getString(R.string.socket_events_play), onQueueChange);
        mSocket.on(getString(R.string.socket_events_delete), onQueueChange);
        mSocket.connect();

        context = getActivity().getApplicationContext();
        preferences = new PreferencesHelper(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue, container, false);
        mListView = (RecyclerView) view.findViewById(R.id.rw_track_queue);
        mListView.setHasFixedSize(true);
        mListView.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mListView.setLayoutManager(mLayoutManager);

        mAdapter = new QueueAdapter(queue);
        mListView.setAdapter(mAdapter);

        asyncUpdate();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off(getString(R.string.socket_events_add), onQueueChange);
        mSocket.off(getString(R.string.socket_events_play), onQueueChange);
        mSocket.disconnect();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void asyncUpdate() {
        new FetchQueue().execute();
    }

    private void deleteCell(final View v, final int index) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            public int initicalHeight;

            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.GONE);
                Log.d(TAG, "Animation done");
                initicalHeight = v.getLayoutParams().height;
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                v.getLayoutParams().height = initicalHeight;
                queue.remove(index);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };

        collapse(v, al);
    }

    private void collapse(final View v, Animation.AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();
        Animation anim = new Animation() {
            @Override
            public boolean willChangeBounds() {
                return true;
            }

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }

                Log.d(TAG, "applyTransformation");
            }
        };

        if (al != null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(200);
        v.startAnimation(anim);
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
            queue.clear();
            queue.addAll(userTrackList);
            mAdapter.notifyDataSetChanged();
        }

    }

    private class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

        private final List<QueueItem> items;

        public QueueAdapter(List<QueueItem> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "jfskjhfskdf sdhf sdksdh kf");
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View view = inflater.inflate(R.layout.queue_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            QueueItem userTrack = getItem(position);
            holder.trackName.setText(userTrack.getTrack().getName());
            holder.artistName.setText(TextUtils.join(", ", userTrack.getTrack().getArtists()));
            with(context).load(userTrack.getUser().getAvatarUrl()).transform(new CircleTransform()).into(holder.userAvatar);
            with(context).load(userTrack.getTrack().getAlbum().getImages().get(2).getUrl()).into(holder.albumImage);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public QueueItem getItem(Integer position) {
            return items.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView trackName;
            public final TextView artistName;
            public final ImageView userAvatar;
            public final ImageView albumImage;

            public ViewHolder(View v) {
                super(v);

                trackName = (TextView) v.findViewById(R.id.track_name);
                artistName = (TextView) v.findViewById(R.id.artist_name);
                userAvatar = (ImageView) v.findViewById(R.id.img_user);
                albumImage = (ImageView) v.findViewById(R.id.img_album);
            }
        }

//        public QueueAdapter(Context context, List<QueueItem> objects) {
//            super(context, R.layout.queue_item, objects);
//        }
//
//        private Listener adapterListener;
//
//        private final List<QueueItem> items;
//
//        public QueueAdapter(Listener adapterListener)
//        {
//            this.adapterListener = adapterListener;
//            this.items = new ArrayList<>();
//            this.stringViewHolderListener = new ViewHolderListener();
//
//            // important: enables animations
//            setHasStableIds(true);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.queue_item, parent, false);
//            }
//            TextView trackName = (TextView) convertView.findViewById(R.id.track_name);
//            TextView artistName = (TextView) convertView.findViewById(R.id.artist_name);
//            ImageView userAvatar = (ImageView) convertView.findViewById(R.id.img_user);
//            ImageView albumImage = (ImageView) convertView.findViewById(R.id.img_album);
//
//            QueueItem userTrack = getItem(position);
//            trackName.setText(userTrack.getTrack().getName());
//            artistName.setText(TextUtils.join(", ", userTrack.getTrack().getArtists()));
//
//            Picasso.with(context).load(userTrack.getUser().getAvatarUrl()).transform(new CircleTransform()).into(userAvatar);
//            Picasso.with(context).load(userTrack.getTrack().getAlbum().getImages().get(2).getUrl()).into(albumImage);
//
//            final AudioObjectHolder holder = getAudioObjectHolder(convertView);
//
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mainView.getLayoutParams();
//            params.rightMargin = 0;
//            params.leftMargin = 0;
//            holder.mainView.setLayoutParams(params);
//            convertView.setOnTouchListener(new SwipeDetector(holder, position));
//
//            return convertView;
//        }
//
//        private void performDelete(QueueItem item) {
//            String token = preferences.getUserApiToken();
//            new PerformDeleteTrack(token, item).execute();
//        }
//
//        private AudioObjectHolder getAudioObjectHolder(View workingView) {
//            AudioObjectHolder holder;
//            holder = new AudioObjectHolder();
//            holder.mainView = (RelativeLayout) workingView.findViewById(R.id.audio_object_mainview);
//            holder.deleteView = (RelativeLayout) workingView.findViewById(R.id.audio_object_deleteview);
//            workingView.setTag(holder);
//
//            return holder;
//        }
//
//        public class AudioObjectHolder {
//            public RelativeLayout mainView;
//            public RelativeLayout deleteView;
//        }
//
//        public class SwipeDetector implements View.OnTouchListener {
//
//            private static final int ACTION_DISTANCE = 400;
//            private static final int MIN_LOCK_DISTANCE = 30; // disallow motion intercept
//
//            private boolean motionInterceptDisallowed = false;
//            private float downX, upX;
//            private AudioObjectHolder holder;
//            private int position;
//
//            public SwipeDetector(AudioObjectHolder h, int pos) {
//                holder = h;
//                position = pos;
//            }
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        downX = event.getX();
//                        return true;
//                    }
//
//                    case MotionEvent.ACTION_MOVE: {
//                        upX = event.getX();
//                        float deltaX = downX - upX;
//                        // if a finger accidentally swiped the item in vertical direction, the
//                        // ListView would intercept that touch event and take control of it - onTouchListener would give ACTION_CANCEL
//                        if (Math.abs(deltaX) > MIN_LOCK_DISTANCE && mListView != null && !motionInterceptDisallowed) {
//                            mListView.requestDisallowInterceptTouchEvent(true);
//                            motionInterceptDisallowed = true;
//                        }
//
//                        if (deltaX > 0) {
//                            holder.deleteView.setVisibility(View.GONE);
//                        } else {
//                            holder.deleteView.setVisibility(View.VISIBLE);
//                        }
//                        swipe(Math.max(-(int) deltaX, 0));
//                        return true;
//                    }
//
//                    case MotionEvent.ACTION_UP:
//                        upX = event.getX();
//                        float deltaX = upX - downX;
//                        if (Math.abs(deltaX) > ACTION_DISTANCE && deltaX > 0) {
//                            swipeRemove(v);
//                        } else {
//                            swipe(0);
//                        }
//
//                        if (mListView != null) {
//                            mListView.requestDisallowInterceptTouchEvent(false);
//                            motionInterceptDisallowed = false;
//                        }
//
//                        holder.deleteView.setVisibility(View.VISIBLE);
//                        return true;
//
//                    case MotionEvent.ACTION_CANCEL:
//                        holder.deleteView.setVisibility(View.VISIBLE);
//                        return false;
//                }
//
//                return true;
//            }
//
//            private void swipe(int distance) {
//                View animationView = holder.mainView;
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) animationView.getLayoutParams();
//                params.rightMargin = -distance;
//                params.leftMargin = distance;
//                animationView.setLayoutParams(params);
//            }
//
//            private void swipeRemove(final View v) {
//                holder.mainView.animate().translationX(1000).setDuration(200).withEndAction(new Runnable() {
//                    @Override
//                    public void run() {
//                        deleteCell(v, position);
//                    }
//                });
//            }
//        }
//
//        private class ViewHolderListener implements StringViewHolder.Listener {
//
//            @Override
//            public void onClick(final String s)
//            {
//                adapterListener.onListItemClick(s);
//            }
//
//        }
    }

}
