package com.app.ptt.comnha;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ptt.comnha.Adapters.Locatlist_rcyler_adapter;
import com.app.ptt.comnha.Adapters.Reviewlist_rcyler_adapter;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActListFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<Post> postArrayList;
    ArrayList<MyLocation> locationArrayList;
    DatabaseReference dbRef;
    ChildEventListener postChildEventListener, locationChildEventListener;
    int filter, whatlist;
    String tinh, huyen, locaID;

    public void setHuyen(String huyen) {
        this.huyen = huyen;
    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public void setWhatlist(int whatlist) {
        this.whatlist = whatlist;
    }

    public ActListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_act_list, container, false);
        anhxa(view);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.firebase_path));
        postChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                post.setPostID(dataSnapshot.getKey());
                postArrayList.add(post);
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
        locationChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyLocation myLocation = dataSnapshot.getValue(MyLocation.class);
                myLocation.setLocaID(dataSnapshot.getKey());
                locationArrayList.add(myLocation);
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
        switch (whatlist) {
            case 1:
                switch (filter) {
                    case 1:
                        dbRef.child(tinh + "/" + huyen + "/" + getString(R.string.usertrackloca_CODE)
                        +LoginSession.getInstance().getUserID())
                                .addChildEventListener(locationChildEventListener);
                        break;
                }
                break;
            case 2:
                switch (filter) {
                    case 1:
                        dbRef.child(tinh + "/" + huyen + "/"
                                + getResources().getString(R.string.userpost_CODE)
                                + LoginSession.getInstance().getUserID())
                                .addChildEventListener(postChildEventListener);
                        break;
                    case 2:
                        dbRef.child(tinh + "/" + huyen + "/" +
                                getResources().getString(R.string.userpost_CODE)
                                + LoginSession.getInstance().getUserID())
                                .orderByChild("commentCount")
                                .addChildEventListener(postChildEventListener);
                        break;
                    case 3:
                        dbRef.child(tinh + "/" + huyen + "/" +
                                getResources().getString(R.string.userpost_CODE)
                                + LoginSession.getInstance().getUserID())
                                .orderByChild("likeCount")
                                .addChildEventListener(postChildEventListener);
                        break;
                }

                break;
        }

        return view;
    }

    private void anhxa(View view) {
        postArrayList = new ArrayList<>();
        locationArrayList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_actlist_rcylerV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        layoutManager = linearLayoutManager;
        mRecyclerView.setLayoutManager(layoutManager);
        switch (whatlist) {
            case 1:
                mAdapter = new Locatlist_rcyler_adapter(locationArrayList);
                break;
            case 2:
                mAdapter = new Reviewlist_rcyler_adapter(postArrayList, getActivity());
                break;
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), Adapter2Activity.class);
                switch (whatlist) {
                    case 1:
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(getString(R.string.fragment_CODE)
                                , getString(R.string.frag_locadetail_CODE));
                        ChooseLoca.getInstance().setLocaID(locationArrayList.get(position).getLocaID());
                        ChooseLoca.getInstance().setHuyen(huyen);
                        ChooseLoca.getInstance().setTinh(tinh);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(getResources().getString(R.string.fragment_CODE),
                                getResources().getString(R.string.frg_viewpost_CODE));
                        ChoosePost.getInstance().setPostID(postArrayList.get(position).getPostID());
                        ChoosePost.getInstance().setTinh(tinh);
                        ChoosePost.getInstance().setHuyen(huyen);
                        startActivity(intent);
                        break;
                }

            }
        }));
    }

}
