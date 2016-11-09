package com.app.ptt.comnha;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.ptt.comnha.Adapters.Locatlist_rcyler_adapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.Service.MyTool;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment implements View.OnClickListener {
    private static final String LOG = StoreFragment.class.getSimpleName();
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    private IntentFilter mIntentFilter;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Boolean isBound = false;
    private MyService myService;

    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference dbRef;

    private ArrayList<MyLocation> listLocation;
    ChildEventListener locaListChildEventListener;
    MyLocation myLocation;
    MyTool myTool;
    View mView;
    int filter;
    Button btn_refresh;
    private static int STATUS_START = 0;

    public void setFilter(int filter) {
        this.filter = filter;
    }

    Handler handler;
    int listSize = 0, count;
    String tinh, huyen;

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public void setHuyen(String huyen) {
        this.huyen = huyen;
    }
    public void setYourLocation(MyLocation myLocation){
        this.myLocation=myLocation;
    }
    public StoreFragment() {
    }

    @Override
    public void onStart() {
        Log.i(LOG, "onStart");
        super.onStart();
        myTool = new MyTool(getActivity(),StoreFragment.class.getSimpleName());
       // myTool.startGoogleApi();
    }
    @Override
    public void onStop() {
        Log.i(LOG, "onStop");
        super.onStop();
        //myTool.stopGoogleApi();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        mView = view;
        listSize = 0;
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://com-nha.firebaseio.com/");
        anhxa(view);
        locaListChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                MyLocation newLocation = dataSnapshot.getValue(MyLocation.class);
                Log.i("Dia chi", "RUN:" + newLocation.getDiachi());
                newLocation.setLocaID(dataSnapshot.getKey());
                //newLocation.setLocationLatLng(myTool.returnLatLngByName(newLocation.getDiachi()));
                float kc = (float) myTool.getDistance(new LatLng(myLocation.getLat(),myLocation.getLng()),new LatLng(newLocation.getLat(), newLocation.getLng()));
//                Log.i("dia chi cua ban:"+myTool.returnLocationByLatLng(myLocation.getLat(),myLocation.getLng()).getDiachi(),
//                        "dia chi:"+myTool.returnLocationByLatLng(newLocation.getLat(),newLocation.getLng()).getDiachi());
                int c = Math.round(kc);
                Log.i(LOG+".tinh khoang cach",c+"");
                int d = c / 1000;
                int e = c % 1000;
                int f = e / 100;
                newLocation.setKhoangcach(d + "," + f + " km");
                Log.i("Dia chi", "CC:" + newLocation.getKhoangcach());
                listLocation.add(newLocation);
                if (STATUS_START > 0) {
                    btn_refresh.setVisibility(View.VISIBLE);
                    AnimationUtils.animatbtnRefreshIfChange(btn_refresh);
                }
                STATUS_START = 1;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
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
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        switch (filter) {
            case 1:
                dbRef.child(tinh + "/" + huyen + "/"
                        + getString(R.string.locations_CODE)).addChildEventListener(locaListChildEventListener);
                break;
            case 2:
                dbRef.child(tinh + "/" + huyen + "/" +
                        getResources().getString(R.string.locations_CODE))
                        .orderByChild("giaAVG")
                        .limitToLast(200)
                        .addChildEventListener(locaListChildEventListener);
                break;
            case 3:
                dbRef.child(tinh + "/" + huyen + "/" +
                        getResources().getString(R.string.locations_CODE))
                        .orderByChild("pvAVG")
                        .limitToLast(200)
                        .addChildEventListener(locaListChildEventListener);
                break;
            case 4:
                dbRef.child(tinh + "/" + huyen + "/" +
                        getResources().getString(R.string.locations_CODE))
                        .orderByChild("vsAVG")
                        .limitToLast(200)
                        .addChildEventListener(locaListChildEventListener);
                break;
        }

        return view;
    }

    private void anhxa(View view) {
        Log.i(LOG, "anhxa");
        //list_item=new ArrayList<>();
        listLocation = new ArrayList<>();
        btn_refresh = (Button) view.findViewById(R.id.frg_store_btn_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_store_recyclerView_localist);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mLayoutManager = linearLayoutManager;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new Locatlist_rcyler_adapter(listLocation);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String key = listLocation.get(position).getLocaID();
                        Intent intent = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
                        intent.putExtra(getResources().getString(R.string.fragment_CODE),
                                getResources().getString(R.string.frag_locadetail_CODE));
                        ChooseLoca.getInstance().setHuyen(huyen);
                        ChooseLoca.getInstance().setLocaID(key);
                        ChooseLoca.getInstance().setTinh(tinh);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }));
        btn_refresh.setVisibility(View.GONE);
        btn_refresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_store_btn_refresh:
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                btn_refresh.setVisibility(View.GONE);
                break;
        }
    }
}
