package com.soon.fm.spotify.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soon.fm.R;
import com.soon.fm.async.CallbackInterface;
import com.soon.fm.helper.PreferencesHelper;
import com.soon.fm.spotify.api.model.Item;
import com.soon.fm.spotify.api.model.Results;
import com.soon.fm.spotify.api.model.Search;
import com.soon.fm.spotify.async.SearchTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public abstract class SearchAdapter<I extends Item> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = SearchAdapter.class.getName();
    protected final Context context;
    protected final PreferencesHelper preferences;
    protected Results<I> results;
    protected final List<I> items = new ArrayList<>();
    protected View view;

    protected boolean loading = false;

    protected final int PLACEHOLDER = R.drawable.ic_album;

    public SearchAdapter(Context context, Results<I> results) {
        this.context = context;
        this.preferences = new PreferencesHelper(context);
        this.results = results;
        this.items.addAll(results.getItems());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.spotify_row_details, parent, false);
        this.view = view;
        return new ViewHolder(view, new ViewHolder.SearchResultHolderClicks() {
            public void onRow(ImageView sharedView, int layoutPosition) {
                Item item = getItem(layoutPosition);
                performClickOnItem(sharedView, item);
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Item item = getItem(position);
        viewHolder.title.setText(item.getTitle());
        viewHolder.subtitle.setText(item.getSubTitle());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewHolder.image.setTransitionName(item.getTitle());
        }
        viewHolder.image.setTag(item.getTitle());
        try {
            loadImageFromCacheTo(getItem(position).getImages().get(2).getUrl(), viewHolder.image);
        } catch (IndexOutOfBoundsException ex) {
            viewHolder.image.setImageResource(PLACEHOLDER);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void loadImageFromCacheTo(String url, ImageView image) {
        Picasso.with(context).load(url).placeholder(PLACEHOLDER).into(image);
    }

    public Item getItem(Integer position) {
        return items.get(position);
    }

    protected abstract void performClickOnItem(View sharedView, final Item item);

    public void loadMore() {
        String next = results.getNext();
        if (!loading && next != null) {
            loading = true;
            new SearchTask(next, new CallbackInterface<Search>() {
                @Override
                public void onSuccess(Search obj) {
                    if (obj != null) {
//                        result.getItems().addAll(obj.getTracks().getItems());
                        notifyDataSetChanged();
                        Log.d(TAG, String.format("new %d items added to adapter. # of items %s", obj.getTracks().getItems().size(), items.size()));
                    }
                    loading = false;
                }

                @Override
                public void onFail() {
                    loading = false;
                    Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show();
                }
            }).execute();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView title;
        public final TextView subtitle;
        public final ImageView image;

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
            void onRow(ImageView sharedItem, int layoutPosition);
        }

        @Override
        public void onClick(View v) {
            mListener.onRow(image, this.getLayoutPosition());
        }

    }

}
