package com.app.ptt.comnha;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ptt.comnha.Adapters.Postlist_rcyler_adapter;
import com.app.ptt.comnha.FireBase.Location;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostlistFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Firebase ref;
    private ArrayList<Location> list_item;

    public PostlistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postlist, container, false);
        Firebase.setAndroidContext(getActivity().getApplicationContext());
        ref = new Firebase("https://com-nha.firebaseio.com/");
        list_item = new ArrayList<Location>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_postlist);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new Postlist_rcyler_adapter(list_item);
        mRecyclerView.setAdapter(mAdapter);
        ref.child("Locations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Location newLocation = dataSnapshot.getValue(Location.class);
                list_item.add(newLocation);
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
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return view;
    }

}
