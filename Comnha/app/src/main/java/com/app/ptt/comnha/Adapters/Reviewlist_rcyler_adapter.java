package com.app.ptt.comnha.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.AnimationUtils;
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
    int previuosPosition = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_un, txt_postdate, txt_tittle,
                txt_content, txt_likenumb, txt_commentnumb, txt_time, txt_tenquan, txt_diachi;
        public ImageView img_user;

        public ViewHolder(View view) {
            super(view);
            txt_time = (TextView) view.findViewById(R.id.review_txtv_time);
            txt_un = (TextView) view.findViewById(R.id.review_txtv_username);
            txt_tittle = (TextView) view.findViewById(R.id.review_txtv_tittle);
            txt_postdate = (TextView) view.findViewById(R.id.review_txtv_postdate);
            txt_content = (TextView) view.findViewById(R.id.review_txtv_content);
            txt_likenumb = (TextView) view.findViewById(R.id.review_txtv_likenumb);
            txt_commentnumb = (TextView) view.findViewById(R.id.review_txtv_commentnumb);
            txt_tenquan = (TextView) view.findViewById(R.id.review_txtv_tenquan);
            txt_diachi = (TextView) view.findViewById(R.id.review_txtv_diachi);
            img_user = (ImageView) view.findViewById(R.id.review_img_user);
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
        holder.txt_likenumb.setText(String.valueOf(list.get(position).getLikeCount()) + " Likes");
        holder.txt_commentnumb.setText(String.valueOf(list.get(position).getCommentCount()) + " Comments");
        holder.txt_tenquan.setText(list.get(position).getLocaName());
        holder.txt_diachi.setText(list.get(position).getDiachi());
        if (position > previuosPosition) {
            AnimationUtils.animateItemRcylerV(holder, true);

        } else {
            AnimationUtils.animateItemRcylerV(holder, false);

        }
        previuosPosition = position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
