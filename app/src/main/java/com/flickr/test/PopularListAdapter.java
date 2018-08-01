package com.flickr.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flickr.test.models.Item;

import java.util.List;

public class PopularListAdapter extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private List<Item> arrayList;
    private Context mContext;
    private ViewHolder viewHolder;

    public PopularListAdapter(Context context, List<Item> arrayList) {
        this.arrayList = arrayList;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_image_list, parent, false);

        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            viewHolder = (ViewHolder) holder;


            Glide.with(mContext)
                    .load(arrayList.get(holder.getAdapterPosition()).getMedia().getM())
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(viewHolder.image);

            viewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PopularListActivity) mContext).onClick(
                            arrayList.get(holder.getAdapterPosition()).getMedia().getM().replace("_m.jpg", "_h.jpg"));
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return arrayList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
        }
    }
}