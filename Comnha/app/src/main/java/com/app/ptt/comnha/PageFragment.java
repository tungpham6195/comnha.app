package com.app.ptt.comnha;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Reviewlist_rcyler_adapter;
import com.app.ptt.comnha.FireBase.Account;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage; //1.profile, 2.activity, 3.photo
    Firebase ref;

    public static PageFragment newInstance(int page) {
        Bundle agrs = new Bundle();
        agrs.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(agrs);
        return fragment;
    }

    public PageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Firebase.setAndroidContext(getActivity().getApplicationContext());
        ref = new Firebase(getResources().getString(R.string.firebase_path));
        switch (mPage) {
            case 1://frg_profile
                view = inflater.inflate(R.layout.fragment_profile, container, false);
                anhxaPage1(view);
                break;
            case 2://frg_activity
                view = inflater.inflate(R.layout.fragment_activity, container, false);
                anhxaPage2(view);
                break;
            case 3://frg_photo
                view = inflater.inflate(R.layout.fragment_photo, container, false);
                anhxaPage3(view);
                break;
        }
        return view;
    }

    void anhxaPage1(View view) {
        final TextView txt_un, txt_Hoten, txt_Ngsinh, txt_email;
        txt_un = (TextView) view.findViewById(R.id.frag_profile_txtUername);
        txt_Hoten = (TextView) view.findViewById(R.id.frag_profile_txtHoten);
        txt_Ngsinh = (TextView) view.findViewById(R.id.frag_profilel_txtNgsinh);
        txt_email = (TextView) view.findViewById(R.id.frag_profile_txtEmail);
        txt_email.setText(LoginSession.getInstance().getEmail());
        txt_un.setText(LoginSession.getInstance().getUsername());
        ref.child(getResources().getString(R.string.users_CODE) +//liệt kê tất cả
                LoginSession.getInstance().getUserID()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Account account = dataSnapshot.getValue(Account.class);
                        txt_Hoten.setText(account.getHo() + " " + account.getTenlot() + " " + account.getTen());
                        txt_Ngsinh.setText(account.getBirth());
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
    }

    void anhxaPage2(View view) {
        Button btn_chooseloca;
        TextView txt_locaName;
        txt_locaName = (TextView) view.findViewById(R.id.frg_activity_txtLoca);
        try {
            if (!ChooseLoca.getInstance().getName().equals("")) {
                txt_locaName.setText(ChooseLoca.getInstance().getName());
            }
        } catch (NullPointerException mess) {
            Log.e("nullLocaName", mess.getMessage());
        }
        loadPost();
        btn_chooseloca = (Button) view.findViewById(R.id.frg_activity_btnChoseloca);
        btn_chooseloca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(getActivity(), view, Gravity.END);
                popup.getMenuInflater().inflate(R.menu.profile_activity, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_activity_none:
                                ChooseLoca.getInstance().setLocaID("");
                                loadPost();
                                break;
                            case R.id.action_activity_choseLoca:
                                Intent intent = new Intent(getActivity(), AdapterActivity.class);
                                intent.putExtra(getResources().getString(R.string.fragment_CODE),
                                        getResources().getString(R.string.frag_chooseloca_CODE));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                loadPost();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    void loadPost() {
        int whatFilter = -1;
        RecyclerView mRecyclerView;
        final RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mlayoutManager;
        final ArrayList<Post> postList;
        postList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_activity_recyler);
        mRecyclerView.setHasFixedSize(true);
        mlayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mlayoutManager);
        try {
            if (!ChooseLoca.getInstance().getLocaID().equals("")) {
                whatFilter = 2;
            } else {
                whatFilter = 1;
            }
        } catch (NullPointerException mess) {
            whatFilter = 1;
        }
        switch (whatFilter) {
            case 1://hiện tất cả các review (chưa có vị trí xác định)
                Toast.makeText(getActivity(), "none", Toast.LENGTH_SHORT).show();
                postList.clear();
                mAdapter = new Reviewlist_rcyler_adapter(postList, getActivity());
                mRecyclerView.setAdapter(mAdapter);
                ref.child(getResources().getString(R.string.userpost_CODE)
                        + "/" + LoginSession.getInstance().getUserID()).
                        addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Post post = dataSnapshot.getValue(Post.class);
                                post.setPostID(dataSnapshot.getKey());
                                postList.add(post);
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
                break;
            case 2://hiện tất cả review theo vị trí xác định
                Toast.makeText(getActivity(), ChooseLoca.getInstance().getLocaID(), Toast.LENGTH_SHORT).show();
                postList.clear();
                mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_activity_recyler);
                mRecyclerView.setHasFixedSize(true);
                mlayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                mRecyclerView.setLayoutManager(mlayoutManager);
                mAdapter = new Reviewlist_rcyler_adapter(postList, getActivity());
                mRecyclerView.setAdapter(mAdapter);
                ref.child(getResources().getString(R.string.locauserpost_CODE)
                        + "/" + ChooseLoca.getInstance().getLocaID() + "/" +
                        LoginSession.getInstance().getUserID()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Post post = dataSnapshot.getValue(Post.class);
                        post.setPostID(dataSnapshot.getKey());
                        postList.add(post);
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
                break;
        }
    }

    void anhxaPage3(View view) {
    }

}
