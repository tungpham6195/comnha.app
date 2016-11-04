package com.app.ptt.comnha;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.ptt.comnha.Adapters.Thucdon_rcyler_adapter;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.ThucDon;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PickFoodDialogFragment extends DialogFragment {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<ThucDon> thucdonList;
    DatabaseReference dbRef;
    ChildEventListener thucdonChildEventListener;

    public PickFoodDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pick_food_dialog, container, false);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        anhxa(view);
        thucdonChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ThucDon thucDon = dataSnapshot.getValue(ThucDon.class);
                thucDon.setMonID(dataSnapshot.getKey());
                thucdonList.add(thucDon);
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
        dbRef.child(getResources().getString(R.string.thucdon_CODE))
                .addChildEventListener(thucdonChildEventListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(getResources().getString(R.string.text_pickFood));
    }

    public interface OnPickFoodListener {
        void onPickFood(ThucDon thucDon);
    }

    OnPickFoodListener onPickFoodListener;

    private void anhxa(View view) {
        thucdonList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_pickFood_rcylerV);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        layoutManager = gridLayoutManager;
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new Thucdon_rcyler_adapter(thucdonList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onPickFoodListener.onPickFood(thucdonList.get(position));
                dismiss();
            }
        }));
    }

    public void setOnPickFoodListener(OnPickFoodListener listener) {
        onPickFoodListener = listener;
    }
}
