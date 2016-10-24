package com.app.ptt.comnha;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ptt.comnha.Adapters.Chooseloca_rcyler_adapter;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.SingletonClasses.DoPost;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooselocaFragment extends Fragment {
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mlayoutManager;
    ArrayList<MyLocation> list;
    Firebase ref;

    public interface onPassDatafromChooseLocaFrg {
        void passData(String data);
    }

    onPassDatafromChooseLocaFrg passingData;

    public ChooselocaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            passingData = (onPassDatafromChooseLocaFrg) activity;
        } catch (ClassCastException mess) {
            throw new ClassCastException(activity.toString() + "must implement onPassDatafromChooseLocaFrg");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chooseloca, container, false);
        Firebase.setAndroidContext(getActivity().getApplicationContext());
        ref = new Firebase(getString(R.string.firebase_path));
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_chooseloca);
        mRecyclerView.setHasFixedSize(true);
        mlayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mlayoutManager);
        list = new ArrayList<>();
        mAdapter = new Chooseloca_rcyler_adapter(list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String key = list.get(position).getLocaID();
                passingData.passData(key);
                DoPost.getInstance().setLocaID(key);
                DoPost.getInstance().setName(list.get(position).getName());
                DoPost.getInstance().setAddress(list.get(position).getDiachi());
                getActivity().finish();
            }
        }));

        ref.child("Locations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyLocation newLocation = dataSnapshot.getValue(MyLocation.class);
                newLocation.setLocaID(dataSnapshot.getKey());
                list.add(newLocation);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Location newLocation = dataSnapshot.getValue(Location.class);
//                String key = dataSnapshot.getKey();
//                for (Location lc : list
//                        ) {
//                    if (lc.getLocaID().equals(key)) {
//                        newLocation.setLocaID(key);
//                        list.set(list.indexOf(lc), newLocation);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("destroy", "destroy");
    }
}
