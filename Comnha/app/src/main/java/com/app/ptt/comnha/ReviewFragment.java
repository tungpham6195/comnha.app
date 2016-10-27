package com.app.ptt.comnha;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ptt.comnha.Adapters.Reviewlist_rcyler_adapter;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.Post;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment {


    public ReviewFragment() {
        // Required empty public constructor
    }

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    RecyclerView.Adapter mAdapter;
    DatabaseReference dbRef;
    ArrayList<Post> postlist;
    ChildEventListener lastnewsChildEventListener;
    int sortType;

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        anhxa(view);
        lastnewsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                post.setPostID(dataSnapshot.getKey());
                postlist.add(post);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        switch (sortType) {
            case 1://lastnews
                dbRef.child(getResources().getString(R.string.posts_CODE))
                        .limitToLast(100)
                        .addChildEventListener(lastnewsChildEventListener);
                break;
            case 2://mostcomment
                dbRef.child(getResources().getString(R.string.posts_CODE))
                        .orderByChild("commentCount")
                        .addChildEventListener(lastnewsChildEventListener);
                break;
            case 3://mostlike
                break;
        }


        return view;
    }

    private void anhxa(View view) {
        postlist = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_review);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerViewLayoutManager = linearLayoutManager;
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mAdapter = new Reviewlist_rcyler_adapter(postlist, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), AdapterActivity.class);
                intent.putExtra(getResources().getString(R.string.fragment_CODE),
                        getResources().getString(R.string.frg_viewpost_CODE));
                intent.putExtra(getResources().getString(R.string.key_CODE),
                        postlist.get(position).getPostID());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }));

    }
}
