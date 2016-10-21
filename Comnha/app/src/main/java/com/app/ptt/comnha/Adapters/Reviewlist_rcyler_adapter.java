package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.R;

import java.util.ArrayList;

/**
 * Created by PTT on 10/4/2016.
 */

public class Reviewlist_rcyler_adapter extends RecyclerView.Adapter<Reviewlist_rcyler_adapter.ViewHolder> {
    ArrayList<Post> list;
    Activity activity;
    LayoutInflater inflater;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_un, txt_postdate, txt_tittle, txt_content, txt_likenumb, txt_time;
        public ImageView img_user;
        public LinearLayout btn_like, btn_comment;

        public ViewHolder(View view) {
            super(view);
            txt_time = (TextView) view.findViewById(R.id.review_txtv_time);
            txt_un = (TextView) view.findViewById(R.id.review_txtv_username);
            txt_tittle = (TextView) view.findViewById(R.id.review_txtv_tittle);
            txt_postdate = (TextView) view.findViewById(R.id.review_txtv_postdate);
            txt_content = (TextView) view.findViewById(R.id.review_txtv_content);
            txt_likenumb = (TextView) view.findViewById(R.id.review_txtv_likenumb);
            img_user = (ImageView) view.findViewById(R.id.review_img_user);
            btn_comment = (LinearLayout) view.findViewById(R.id.review_btn_comment);
            btn_like = (LinearLayout) view.findViewById(R.id.review_btn_like);
        }
    }

    public Reviewlist_rcyler_adapter(ArrayList<Post> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcyler_review, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txt_tittle.setText(list.get(position).getTitle());
        holder.txt_content.setText(list.get(position).getContent());
        holder.txt_postdate.setText(list.get(position).getDate());
        holder.txt_time.setText(list.get(position).getTime());
        holder.txt_un.setText(list.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
