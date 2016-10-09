package com.app.ptt.comnha.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.FireBase.Comment;
import com.app.ptt.comnha.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by PTT on 10/5/2016.
 */

public class Comment_rcyler_adapter extends RecyclerView.Adapter<Comment_rcyler_adapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgV;
        public TextView txt_un, txt_content, txt_time;

        public ViewHolder(View itemView) {
            super(itemView);
            imgV = (ImageView) itemView.findViewById(R.id.comment_imguser);
            txt_un = (TextView) itemView.findViewById(R.id.comment_username);
            txt_content = (TextView) itemView.findViewById(R.id.comment_content);
            txt_time = (TextView) itemView.findViewById(R.id.comment_time);
        }
    }

    ArrayList<Comment> comment_list;
    Firebase ref;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(itemView);
    }

    String username;

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txt_content.setText(comment_list.get(position).getContent());
        holder.txt_time.setText(comment_list.get(position).getTime());


        ref.child("Users/" + comment_list.get(position).getUserID() + "/username")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        username = dataSnapshot.getValue().toString();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
        holder.txt_un.setText(username);

    }

    @Override
    public int getItemCount() {
        return comment_list.size();
    }

    public Comment_rcyler_adapter(ArrayList<Comment> comment_list, Context context) {
        this.comment_list = comment_list;
        Firebase.setAndroidContext(context);
        ref = new Firebase("https://com-nha.firebaseio.com/");
    }
}
