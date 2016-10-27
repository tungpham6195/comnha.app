package com.app.ptt.comnha;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.ptt.comnha.Adapters.Reviewlist_rcyler_adapter;
import com.app.ptt.comnha.Classes.CalcuAVGRate;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Post;
import com.firebase.client.Firebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocadetailFragment extends Fragment {
    private String locaID;
    DatabaseReference dbRef;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mlayoutManager;
    TextView txt_name, txt_diachi, txt_gio, txt_gia, txt_vesinh, txt_phucvu, txt_sdt, txt_tien;
    LinearLayout btn_themanh, btn_dangreview;
    Reviewlist_rcyler_adapter reviewlist_rcyler_adapter;
    ArrayList<Post> postlist;
    ValueEventListener locationValueEventListener;
    ChildEventListener locapostChildEventListener;

    public LocadetailFragment() {
        // Required empty public constructor
    }

    public void setLocaID(String locaID) {
        this.locaID = locaID;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locadetail, container, false);
        Log.d("localID", locaID);
        andxa(view);
        Firebase.setAndroidContext(getActivity().getApplicationContext());
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        locationValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                MyLocation location = dataSnapshot.getValue(MyLocation.class);
                String gio = location.getTimestart() + " - " + location.getTimeend();
                String tenquan = location.getName();
                String diachi = location.getDiachi();
                String sdt = location.getSdt();
                txt_sdt.setText(sdt);
                txt_diachi.setText(diachi);
                txt_tien.setText(String.valueOf(location.getGiamin()) +
                        " - " + String.valueOf(location.getGiamax()));
                txt_gio.setText(gio);
                txt_name.setText(tenquan);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        locapostChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                post.setPostID(dataSnapshot.getKey());
                postlist.add(post);
                CalcuAVGRate newcalcu = new CalcuAVGRate(postlist);
                txt_gia.setText(String.valueOf(newcalcu.calcu().get(0)));
                txt_vesinh.setText(String.valueOf(newcalcu.calcu().get(1)));
                txt_phucvu.setText(String.valueOf(newcalcu.calcu().get(2)));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        Log.d(getResources().getString(R.string.key_CODE), locaID);
        dbRef.child("Locations/" + locaID).addValueEventListener(locationValueEventListener);
        dbRef.child(getResources().getString(R.string.locationpost_CODE) + "/" + locaID).addChildEventListener(locapostChildEventListener);
        return view;
    }

    private void andxa(View view) {
        txt_tien = (TextView) view.findViewById(R.id.frg_lcdetail_txt_tien);
        txt_diachi = (TextView) view.findViewById(R.id.frg_lcdetail_txt_diachi);
        txt_name = (TextView) view.findViewById(R.id.frg_lcdetail_txt_tenquan);
        txt_gio = (TextView) view.findViewById(R.id.frg_lcdetail_txt_thoigian);
        txt_gia = (TextView) view.findViewById(R.id.frg_lcdetail_txt_gia);
        txt_vesinh = (TextView) view.findViewById(R.id.frg_lcdetail_txt_vesinh);
        txt_phucvu = (TextView) view.findViewById(R.id.frg_lcdetail_txt_phucvu);
        btn_themanh = (LinearLayout) view.findViewById(R.id.frg_lcdetail_btn_themanh);
        btn_dangreview = (LinearLayout) view.findViewById(R.id.frg_lcdetail_dangreview);
        txt_sdt = (TextView) view.findViewById(R.id.frg_lcdetail_txt_sdt);
        postlist = new ArrayList<Post>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_lcdetail_rcyler_review);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mlayoutManager = linearLayoutManager;
        mRecyclerView.setLayoutManager(mlayoutManager);
        mAdapter = new Reviewlist_rcyler_adapter(postlist, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        btn_dangreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Adapter2Activity.class);
                intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frag_addpost_CODE));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext()
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frg_viewpost_CODE));
                intent.putExtra(getString(R.string.key_CODE), postlist.get(position).getPostID());
                startActivity(intent);
            }
        }));
    }

}
