package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.ptt.comnha.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by PTT on 10/24/2016.
 */

public class Photos_rcyler_adapter extends RecyclerView.Adapter<Photos_rcyler_adapter.Viewholder> {
    private ArrayList<Uri> photoList;

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcyler_photos, parent, false));
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        Picasso.with(activity)
                .load(photoList.get(position))
                .into(holder.imageView);
    }

    private Activity activity;

    public Photos_rcyler_adapter(ArrayList<Uri> photoList, Activity activity) {
        this.photoList = photoList;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public Viewholder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_rcyler_photo_imgV);
        }
    }


}
