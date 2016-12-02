package com.app.ptt.comnha;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Reviewlist_rcyler_adapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.Post;
import com.app.ptt.comnha.Modules.ConnectionDetector;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.ChoosePost;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment implements View.OnClickListener {
    private static int STATUS_START = 0;

    public ReviewFragment() {
        // Required empty public constructor
    }
    private static final String LOG=ReviewFragment.class.getSimpleName();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    RecyclerView.Adapter mAdapter;
    DatabaseReference dbRef;
    ArrayList<Post> postlist;
    boolean isSaved=false;
    ChildEventListener lastnewsChildEventListener;
    int sortType;
    Context context;
    Button btn_refresh;
    String tinh, huyen;
    Boolean first,second;
    boolean isConnected=false;
    IntentFilter mIntentFilter;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG+".onReceive form Service","isConnected= "+ intent.getBooleanExtra("isConnected", false));
                if (intent.getBooleanExtra("isConnected", false)) {
                    isConnected = true;
                } else
                    isConnected = false;
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();

        Log.i(LOG,"onStart");
        isConnected= MyService.returnIsConnected();
        if(!isConnected){
            Toast.makeText(getActivity().getApplicationContext(),"Offline mode",Toast.LENGTH_SHORT).show();
        }
        Log.i(LOG, "onStart= "+isConnected);
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver,mIntentFilter);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG,"onResume");
        sortType = MyService.getPosOfReviewType();
//        if(MyService.getChangeContent()!="") {
//            sortType = MyService.getPosOfReviewType();
//            MyService.setChangeContent("");
//            if (MyService.saveToListSaved("postlist_" + sortType + "_" + tinh + "_" + huyen) == 1)
//                getDataOffline();
//            else
//                getData();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG,"onStop");
        MyService.setPosOfReviewType(sortType);
        ChoosePost.getInstance().setPostID(null);
        ChoosePost.getInstance().setTinh(null);
        ChoosePost.getInstance().setHuyen(null);
        getContext().unregisterReceiver(broadcastReceiver);
    }
    public void setContext(Context mContext){
        this.context=mContext;
        Log.i(LOG+".setContext","OK");
    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public void setHuyen(String huyen) {
        this.huyen = huyen;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
        Log.i(LOG + ".setSortType", "sortType="+sortType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isConnected= MyService.returnIsConnected();
        Log.i(LOG + ".onCreateView", "OK");

        // Inflate the layout for this fragment
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        anhxa(view);
        if(!isConnected){
            getDataOffline();
        }else {

            Log.i(LOG,"onResume");
            if(MyService.getChangeContent()!="") {
                sortType = MyService.getPosOfReviewType();
                MyService.setChangeContent("");
            }
            if (MyService.saveToListSaved("postlist_" + sortType + "_" + tinh + "_" + huyen) == 1)
                getDataOffline();
            else
                getData();

        }


        return view;
    }
    public void getDataOffline(){
        Log.i(LOG + ".getDataOffline", "OK");
        if(Storage.readFile(context,"postlist_"+sortType+"_"+tinh+"_"+huyen)!=null){
            String a=Storage.readFile(context,"postlist_"+sortType+"_"+tinh+"_"+huyen);
            //Log.i(LOG + ".onCreateView - " + "postlist_"+sortType+"_"+tinh+"_"+huyen, ""+a.toString());
            ArrayList<Post> posts= null;
            if(a!=null) {
                try {
                    posts=new ArrayList<>();
                    posts=Storage.readJSONPost1(a);
                } catch (Exception e) {
                    getData();
                    e.printStackTrace();
                }

                if(posts.size()>0) {
                    for (Post post : posts) {
                        postlist.add(post);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            } else{
                if(isConnected)
                    getData();
            }
        }
    }
    public void getData(){
        Log.i(LOG + ".getData", "OK");
        lastnewsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                post.setPostID(dataSnapshot.getKey());
                postlist.add(post);
                isSaved=false;
                try {
                        Storage.parsePostToJson(postlist);
                        if (Storage.parsePostToJson(postlist).toString() != null) {
                            Storage.writeFile(context, Storage.parsePostToJson(postlist).toString(), "postlist_" + sortType + "_" + tinh + "_" + huyen);
                            MyService.saveToListSaved("postlist_" + sortType + "_" + tinh + "_" + huyen);
                        }
                    Log.i(LOG + ".getData ", "size = " + postlist.size());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
    }
    private void anhxa(View view) {
        Log.i(LOG+".anhxa","OK");
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
                if(isConnected) {
                    Intent intent = new Intent(getActivity(), Adapter2Activity.class);
                    intent.putExtra(getResources().getString(R.string.fragment_CODE),
                            getResources().getString(R.string.frg_viewpost_CODE));
                    ChoosePost.getInstance().setPostID(postlist.get(position).getPostID());
                    ChoosePost.getInstance().setTinh(tinh);
                    ChoosePost.getInstance().setHuyen(huyen);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else
                Toast.makeText(context,"You are offline",Toast.LENGTH_LONG).show();

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


}
