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
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Reviewlist_rcyler_adapter;
import com.app.ptt.comnha.Classes.CalcuAVGRate;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Post;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocadetailFragment extends Fragment {
    private String locaID;
    Firebase ref;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mlayoutManager;
    TextView txt_name, txt_diachi, txt_gio, txt_gia, txt_vesinh, txt_phucvu, txt_sdt, txt_tien;
    LinearLayout btn_themanh, btn_dangreview;
    Reviewlist_rcyler_adapter reviewlist_rcyler_adapter;
    ArrayList<Post> postlist;

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
        ref = new Firebase(getString(R.string.firebase_path));
        postlist = new ArrayList<Post>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_lcdetail_rcyler_review);
        mRecyclerView.setHasFixedSize(true);
        mlayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mlayoutManager);
        mAdapter = new Reviewlist_rcyler_adapter(postlist, getActivity());
        mRecyclerView.setAdapter(mAdapter);
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
        Log.d(getResources().getString(R.string.key_CODE), locaID);
        ref.child("Locations/" + locaID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        ref.child(getResources().getString(R.string.locationpost_CODE) + "/" + locaID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
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
//        ref.child("Locations/" + locaID + "/posts/").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if (dataSnapshot.getValue().equals(true)) {
//                    final String key = dataSnapshot.getKey();
//                    ref.child("Posts").addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                            if (dataSnapshot.getKey().equals(key)) {
//                                Post post = new Post();
//                                post.setGia(Long.valueOf(dataSnapshot.child("gia").getValue().toString()));
//                                post.setVesinh(Long.valueOf(dataSnapshot.child("vesinh").getValue().toString()));
//                                post.setPhucvu(Long.valueOf(dataSnapshot.child("phucvu").getValue().toString()));
//                                post.setDate(dataSnapshot.child("date").getValue().toString());
//                                post.setTitle(dataSnapshot.child("title").getValue().toString());
//                                post.setContent(dataSnapshot.child("content").getValue().toString());
//                                post.setPostID(key);
////                                txt_gia.setText(String.valueOf(post.getGia()));
////                                txt_phucvu.setText(String.valueOf(post.getPhucvu()));
////                                txt_vesinh.setText(String.valueOf(post.getVesinh()));
//                                postlist.add(post);
//                            }
//                            mAdapter.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                        }
//
//                        @Override
//                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(FirebaseError firebaseError) {
//
//                        }
//                    });
//                    ref.child("Posts/").child(key).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
////                            Toast.makeText(getActivity().getApplicationContext(),
////                                            dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
////                            Toast.makeText(getActivity().getApplicationContext(),
////                            dataSnapshot.child("users/").getKey(), Toast.LENGTH_SHORT).show();
//                            Post post = new Post();
//                            post.setGia(Long.valueOf(dataSnapshot.child("gia").getValue().toString()));
//                            post.setVesinh(Long.valueOf(dataSnapshot.child("vesinh").getValue().toString()));
//                            post.setPhucvu(Long.valueOf(dataSnapshot.child("phucvu").getValue().toString()));
//                            post.setDate(dataSnapshot.child("date").getValue().toString());
//                            post.setTitle(dataSnapshot.child("title").getValue().toString());
//                            post.setContent(dataSnapshot.child("content").getValue().toString());
//                            post.setPostID(key);
//                            txt_gia.setText(String.valueOf(post.getGia()));
//                            txt_phucvu.setText(String.valueOf(post.getPhucvu()));
//                            txt_vesinh.setText(String.valueOf(post.getVesinh()));
//
//                            for (Post pst : postlist) {
//                                if (pst.getPostID().equals(dataSnapshot.getKey())) {
//                                    post.setPostID(dataSnapshot.getKey());
//                                    postlist.set(postlist.indexOf(pst), post);
//                                }
//                            }
//                            mAdapter.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onCancelled(FirebaseError firebaseError) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
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
        btn_dangreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frag_addpost_CODE));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

}
