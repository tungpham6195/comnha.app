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

import com.app.ptt.comnha.Adapters.Reviewlist_rcyler_adapter;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.app.ptt.comnha.SingletonClasses.DoPost;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by PTT on 10/25/2016.
 */

public class ActivityFragment extends Fragment {
    FirebaseDatabase database;
    DatabaseReference dbRef;
    Button btn_chooseloca;
    TextView txt_locaName;
    int whatFilter = -1;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mlayoutManager;
    ArrayList<Post> postList;
    ChildEventListener userPostEventListener;

    public ActivityFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        Log.d("onCreateViewActFrag", "createdView");
        anhxa(view);
        try {
            Log.d("LocaID", ChooseLoca.getInstance().getLocaID());
        } catch (NullPointerException mess) {

        }
        userPostEventListener = new ChildEventListener() {
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
            public void onCancelled(DatabaseError databaseError) {

            }
        };
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
//                Toast.makeText(getActivity(), "none", Toast.LENGTH_SHORT).show();
                dbRef.child(getResources().getString(R.string.userpost_CODE)
                        + "/" + LoginSession.getInstance().getUserID()).addChildEventListener(userPostEventListener);
                break;
            case 2://hiện tất cả review theo vị trí xác định
//                Toast.makeText(getActivity(), ChooseLoca.getInstance().getLocaID(), Toast.LENGTH_SHORT).show();
                dbRef.child(getResources().getString(R.string.locauserpost_CODE)
                        + "/" + ChooseLoca.getInstance().getLocaID() + "/" +
                        LoginSession.getInstance().getUserID()).addChildEventListener(userPostEventListener);
                break;
        }
        return view;
    }

    void anhxa(View view) {
        txt_locaName = (TextView) view.findViewById(R.id.frg_activity_txtLoca);
        postList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_activity_recyler);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mlayoutManager = linearLayoutManager;
        mRecyclerView.setLayoutManager(mlayoutManager);
        mAdapter = new Reviewlist_rcyler_adapter(postList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        try {
            if (!ChooseLoca.getInstance().getName().equals("")) {
                txt_locaName.setText(ChooseLoca.getInstance().getName());
            }
        } catch (NullPointerException mess) {
            Log.e("nullLocaName", mess.getMessage());
        }
        final PopupMenu popup = new PopupMenu(getActivity(), txt_locaName, Gravity.START);
        popup.getMenuInflater().inflate(R.menu.popup_menu_view_activity, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_activity_none:
                        ChooseLoca.getInstance().setLocaID("");
                        break;
                    case R.id.action_activity_choseLoca:
                        Intent intent = new Intent(getActivity(), AdapterActivity.class);
                        intent.putExtra(getResources().getString(R.string.fragment_CODE),
                                getResources().getString(R.string.frag_chooseloca_CODE));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        btn_chooseloca = (Button) view.findViewById(R.id.frg_activity_btnChoseloca);
        btn_chooseloca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                popup.show();
            }
        });
        txt_locaName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String key = postList.get(position).getPostID();
                Intent intent = new Intent(getActivity().getApplicationContext(), AdapterActivity.class);
                intent.putExtra(getResources().getString(R.string.fragment_CODE),
                        getResources().getString(R.string.frg_viewpost_CODE));
                intent.putExtra(getResources().getString(R.string.key_CODE), key);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().getApplicationContext().startActivity(intent);
            }
        }));
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("onStopFromActFrag", "Stoped");
        DoPost.getInstance().setLocaID(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dbRef.removeEventListener(userPostEventListener);
    }
}
