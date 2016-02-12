package com.soon.fm.fragment;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.soon.fm.Constants;
import com.soon.fm.utils.CurrentTrackCache;
import com.soon.fm.R;
import com.soon.fm.async.CallbackInterface;
import com.soon.fm.async.FetchQueue;
import com.soon.fm.backend.event.PerformDeleteTrack;
import com.soon.fm.backend.model.QueueItem;
import com.soon.fm.helper.PreferencesHelper;
import com.soon.fm.utils.CircleTransform;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.squareup.picasso.Picasso.with;


public class QueueFragment extends Fragment {

    private static final String TAG = QueueFragment.class.getName();
    private final Socket mSocket;
    private PreferencesHelper preferences;
    private List<QueueItem> queue = new ArrayList<>();
    private QueueAdapter mAdapter;
    private Emitter.Listener onQueueChange = new Emitter.Listener() {
        @Override
        public void call(Object... args) {  // TODO some locker
            Log.i(TAG, "[listener.onQueueChange] update queue");
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

        queue = CurrentTrackCache.getQueue();
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
        new FetchQueue(getString(R.string.fm_api), new CallbackInterface<List<QueueItem>>() {
            @Override
            public void onSuccess(List<QueueItem> obj) {
                QueueAdapter adapter = new QueueAdapter(obj);
                mListView.setAdapter(adapter);
            }

            @Override
            public void onFail() {

            }
        }).execute();
    }

    private void deleteCell(final View v, final QueueAdapter.ViewHolder holder) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                performDelete(holder.qi);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };

        collapse(v, al);
    }

    private void performDelete(QueueItem item) {
        String token = preferences.getUserApiToken();
        new PerformDeleteTrack(token, item).execute();
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
            }
        };

        if (al != null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(200);
        v.startAnimation(anim);
    }

    private class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

        private final List<QueueItem> items;

        public QueueAdapter(List<QueueItem> items) {
            if (items == null) {
                items = new ArrayList<>();
            }
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View view = inflater.inflate(R.layout.queue_item, parent, false);
            ViewHolder vh = new ViewHolder(view);
            view.setOnTouchListener(new SwipeDetector(vh));
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            QueueItem userTrack = getItem(position);
            holder.qi = userTrack;
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

        public class SwipeDetector implements View.OnTouchListener {

            private static final int ACTION_DISTANCE = 400;
            private static final int MIN_LOCK_DISTANCE = 30; // disallow motion intercept

            private boolean motionInterceptDisallowed = false;
            private float downX, upX;
            private ViewHolder holder;

            public SwipeDetector(ViewHolder vh) {
                holder = vh;
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        downX = event.getX();
                        return true;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        upX = event.getX();
                        float deltaX = downX - upX;
                        if (Math.abs(deltaX) < MIN_LOCK_DISTANCE) {
                            return true;
                        }

                        // if a finger accidentally swiped the item in vertical direction, the
                        // ListView would intercept that touch event and take control of it - onTouchListener would give ACTION_CANCEL
                        if (Math.abs(deltaX) > MIN_LOCK_DISTANCE && mListView != null && !motionInterceptDisallowed) {
                            mListView.requestDisallowInterceptTouchEvent(true);
                            motionInterceptDisallowed = true;
                        }

                        if (deltaX > 0) {
                            holder.deleteView.setVisibility(View.GONE);
                        } else {
                            holder.deleteView.setVisibility(View.VISIBLE);
                        }
                        swipe(Math.max(-(int) deltaX - MIN_LOCK_DISTANCE, 0));
                        return true;
                    }

                    case MotionEvent.ACTION_UP:
                        upX = event.getX();
                        float deltaX = upX - downX;
                        if (Math.abs(deltaX) > ACTION_DISTANCE && deltaX > 0) {
                            swipeRemove(v);
                        } else {
                            swipeBack(v);
                        }

                        if (mListView != null) {
                            mListView.requestDisallowInterceptTouchEvent(false);
                            motionInterceptDisallowed = false;
                        }

                        holder.deleteView.setVisibility(View.VISIBLE);
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        holder.deleteView.setVisibility(View.VISIBLE);
                        swipeBack(v);
                        return false;
                }

                return true;
            }

            private void swipe(int distance) {
                View animationView = holder.mainView;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) animationView.getLayoutParams();
                params.rightMargin = -distance;
                params.leftMargin = distance;
                animationView.setLayoutParams(params);
            }

            private void swipeBack(final View v) {
                View animationView = holder.mainView;
                final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) animationView.getLayoutParams();
                ValueAnimator animator = ValueAnimator.ofInt(params.leftMargin, 0);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        params.leftMargin = (Integer) valueAnimator.getAnimatedValue();
                        params.rightMargin = -(Integer) valueAnimator.getAnimatedValue();
                        v.requestLayout();
                    }
                });
                animator.setDuration(300);
                animator.start();
            }

            private void swipeRemove(final View v) {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                holder.mainView.animate().translationX(metrics.widthPixels).setDuration(200).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        deleteCell(v, holder);
                    }
                });
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView trackName;
            public final TextView artistName;
            public final ImageView userAvatar;
            public final ImageView albumImage;
            public final RelativeLayout mainView;
            public final RelativeLayout deleteView;
            public QueueItem qi;

            public ViewHolder(View v) {
                super(v);
                trackName = (TextView) v.findViewById(R.id.track_name);
                artistName = (TextView) v.findViewById(R.id.artist_name);
                userAvatar = (ImageView) v.findViewById(R.id.img_user);
                albumImage = (ImageView) v.findViewById(R.id.img_album);

                mainView = (RelativeLayout) v.findViewById(R.id.audio_object_mainview);
                deleteView = (RelativeLayout) v.findViewById(R.id.audio_object_deleteview);
            }
        }

    }

}
