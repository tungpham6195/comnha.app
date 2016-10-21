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
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocatlistFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Firebase ref;
    private ArrayList<MyLocation> list_item;

    public LocatlistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_localist, container, false);
        Firebase.setAndroidContext(getActivity().getApplicationContext());
        ref = new Firebase("https://com-nha.firebaseio.com/");
        list_item = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_localist);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new Locatlist_rcyler_adapter(list_item);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String key = list_item.get(position).getLocaID();
                Intent intent = new Intent(getActivity().getApplicationContext(), AdapterActivity.class);
                intent.putExtra(getResources().getString(R.string.fragment_CODE),
                        getResources().getString(R.string.frag_locadetail_CODE));
                intent.putExtra(getResources().getString(R.string.key_CODE), key);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().getApplicationContext().startActivity(intent);
//                Toast.makeText(getActivity().getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
            }
        }));
        ref.child("Locations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyLocation newLocation = dataSnapshot.getValue(MyLocation.class);
                newLocation.setLocaID(dataSnapshot.getKey());
                list_item.add(newLocation);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Location newLocation = dataSnapshot.getValue(Location.class);
//                String key = dataSnapshot.getKey();
//                for (Location lc : list_item) {
//                    if (lc.getLocaKey().equals(key)) {
//                        newLocation.setLocaKey(key);
//                        list_item.set(list_item.indexOf(lc), newLocation);
//                    }
//                }
//                mAdapter.notifyDataSetChanged();
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
