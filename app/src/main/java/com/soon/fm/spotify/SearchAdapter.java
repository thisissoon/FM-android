package com.soon.fm.spotify;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.soon.fm.R;
import com.soon.fm.spotify.api.model.Item;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private List<Item> dataSet;

    public SearchAdapter(Context context, List<Item> dataSet) {
        this.dataSet = dataSet;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_searchable_row_details, parent, false);
        return new ViewHolder(view);
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

    private void loadImageFromCacheTo(String url, ImageView image) {
        Picasso.with(context).load(url).into(image);
    }

    public Item getItem(Integer position) {
        return dataSet.get(position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView title;
        private final TextView subtitle;
        private final ImageView image;

        public ViewHolder(View v) {
            super(v);
            this.title = (TextView) v.findViewById(R.id.item_title);
            this.subtitle = (TextView) v.findViewById(R.id.item_subtitle);
            this.image = (ImageView) v.findViewById(R.id.item_image);
        }
    }

}
