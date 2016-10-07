package com.app.ptt.comnha;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.ptt.comnha.Adapters.Comment_rcyler_adapter;
import com.app.ptt.comnha.FireBase.Comment;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewpostFragment extends Fragment {
    private String postID;
    TextView txt_un, txt_date, txt_title, txt_content, txt_likenumb;
    LinearLayout btn_like, btn_comment;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mlayoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<Comment> comment_List;
    Firebase ref;

    public ViewpostFragment() {
        // Required empty public constructor
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_viewpost, container, false);
        anhxa(view);
        comment_List = new ArrayList<Comment>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_viewrev_rcyler_comments);
        mRecyclerView.setHasFixedSize(true);
        mlayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mlayoutManager);
        mAdapter = new Comment_rcyler_adapter(comment_List);
        mRecyclerView.setAdapter(mAdapter);
        Firebase.setAndroidContext(getActivity().getApplicationContext());
        ref = new Firebase(getString(R.string.firebase_path));
        ref.child("Posts/" + postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txt_title.setText(dataSnapshot.child("title").getValue().toString());
                txt_date.setText(dataSnapshot.child("date").getValue().toString());
                txt_content.setText(dataSnapshot.child("content").getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return view;
    }

    private void anhxa(View view) {
        txt_un = (TextView) view.findViewById(R.id.frg_viewrev_txtv_username);
        txt_date = (TextView) view.findViewById(R.id.frg_viewrev_txtv_postdate);
        txt_title = (TextView) view.findViewById(R.id.frg_viewrev_txtv_tittle);
        txt_content = (TextView) view.findViewById(R.id.frg_viewrev_txtv_content);
        txt_likenumb = (TextView) view.findViewById(R.id.frg_viewrev_txtv_likenumb);
        btn_like = (LinearLayout) view.findViewById(R.id.frg_viewrev__btn_like);
        btn_comment = (LinearLayout) view.findViewById(R.id.frg_viewrev_btn_comment);
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
