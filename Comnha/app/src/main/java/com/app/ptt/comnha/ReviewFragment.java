package com.app.ptt.comnha;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.ptt.comnha.Adapters.Reviewlist_rcyler_adapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment implements View.OnClickListener {
    private static int STATUS_START = 0;

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
    Button btn_refresh;
    String tinh, huyen;
    @Override
    public void onStart() {
        super.onStart();

    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public void setHuyen(String huyen) {
        this.huyen = huyen;
    }

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
                if (STATUS_START > 0) {
                    btn_refresh.setVisibility(View.VISIBLE);
                    AnimationUtils.animatbtnRefreshIfChange(btn_refresh);
                }
                STATUS_START = 1;
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
                dbRef.child(tinh + "/"
                        + huyen + "/" +
                        getResources().getString(R.string.posts_CODE))
                        .limitToLast(100)
                        .addChildEventListener(lastnewsChildEventListener);
                break;
            case 2://mostcomment
                dbRef.child(tinh + "/"
                        + huyen + "/" +
                        getResources().getString(R.string.posts_CODE))
                        .orderByChild("commentCount").limitToLast(100)
                        .addChildEventListener(lastnewsChildEventListener);
                break;
            case 3://mostlike
                break;
        }


        return view;
    }

    private void anhxa(View view) {
        btn_refresh = (Button) view.findViewById(R.id.frg_review_btn_refresh);
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
                Intent intent = new Intent(getActivity(), Adapter2Activity.class);
                intent.putExtra(getResources().getString(R.string.fragment_CODE),
                        getResources().getString(R.string.frg_viewpost_CODE));
                ChoosePost.getInstance().setPostID(postlist.get(position).getPostID());
                ChoosePost.getInstance().setTinh(tinh);
                ChoosePost.getInstance().setHuyen(huyen);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }));
        btn_refresh.setVisibility(View.GONE);
        btn_refresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_review_btn_refresh:
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                AnimationUtils.animatbtnRefreshIfClick(btn_refresh);
                btn_refresh.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        ChoosePost.getInstance().setPostID(null);
        ChoosePost.getInstance().setTinh(null);
        ChoosePost.getInstance().setHuyen(null);
    }
}
