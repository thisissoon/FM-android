package com.soon.fm.spotify;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.soon.fm.R;
import com.soon.fm.async.CallbackInterface;
import com.soon.fm.backend.event.PerformAddTrack;
import com.soon.fm.helper.PreferencesHelper;
import com.soon.fm.spotify.api.model.Item;
import com.squareup.picasso.Picasso;

import java.util.List;


public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = SearchAdapter.class.getName();

    private final Context context;
    private final PreferencesHelper preferences;
    private List<Item> dataSet;
    private View view;

    public SearchAdapter(Context context, List<Item> dataSet) {
        this.dataSet = dataSet;
        this.context = context;
        preferences = new PreferencesHelper(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.spotify_row_details, parent, false);
        this.view = view;
        return new ViewHolder(view, new ViewHolder.SearchResultHolderClicks() {
            public void onRow(View caller, int layoutPosition) {
                Item item = getItem(layoutPosition);
                performAddTrack(item);
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.title.setText(getItem(position).getTitle());
        viewHolder.subtitle.setText(getItem(position).getSubTitle());
        loadImageFromCacheTo(getItem(position).getImages().get(2).getUrl(), viewHolder.image);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private void performAddTrack(final Item item) {
        Snackbar snackbar = Snackbar.make(view, String.format("%s - %s added", item.getTitle(), item.getSubTitle()), Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (event == DISMISS_EVENT_TIMEOUT) {
                    new PerformAddTrack(preferences.getUserApiToken(), item, new CallbackInterface<Item>() {
                        @Override
                        public void onSuccess(Item obj) {

                        }

                        @Override
                        public void onFail() {

                        }
                    }).execute();
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
            }
        }).setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbarUndo = Snackbar.make(view, "Track removed from the queue!", Snackbar.LENGTH_SHORT);
                snackbarUndo.show();
            }
        });
        snackbar.show();
    }

    private void loadImageFromCacheTo(String url, ImageView image) {
        Picasso.with(context).load(url).placeholder(R.drawable.ic_album).into(image);
    }

    public Item getItem(Integer position) {
        return dataSet.get(position);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView title;
        private final TextView subtitle;
        private final ImageView image;

        public SearchResultHolderClicks mListener;

        public ViewHolder(View v, SearchResultHolderClicks listener) {
            super(v);

            mListener = listener;
            title = (TextView) v.findViewById(R.id.item_title);
            subtitle = (TextView) v.findViewById(R.id.item_subtitle);
            image = (ImageView) v.findViewById(R.id.item_image);

            v.setOnClickListener(this);
        }

        public interface SearchResultHolderClicks {
            void onRow(View caller, int layoutPosition);
        }

        @Override
        public void onClick(View v) {
            mListener.onRow(v, this.getLayoutPosition());
        }

    }

}
