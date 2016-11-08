package com.app.ptt.comnha;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.app.ptt.comnha.Modules.Route;
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
    private ArrayList<MyLocation> list_item = new ArrayList<>();
    private ArrayList<MyLocation> listLocation;
    ArrayList<Route> list;
    ChildEventListener locaListChildEventListener;
    String yourLocation;
    MyTool myTool;
    View mView;
    int filter, temp;
    LatLng yourLatLng;
    Button btn_refresh;
    private static int STATUS_START = 0;
    ProgressDialog progressDialog1;

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

    public StoreFragment() {
    }

    @Override
    public void onStart() {
        Log.i("onStart", "RUN");
        super.onStart();
        // doBinService();

//        mIntentFilter = new IntentFilter();
//        mIntentFilter.addAction(mBroadcastSendAddress);
//        getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra("STT", 0) == 4) {
                Log.i(LOG + ".BroadcastReceiver", "RECEIVE BROASTCAST" + list_item.size());
                yourLocation = myTool.getYourLocation();
                yourLatLng = myTool.getYourLatLng();
                if (list_item != null && list_item.size() > 0) {
                    progressDialog1 = new ProgressDialog(getActivity());
                    progressDialog1.setMessage("LOADING");
                    progressDialog1.show();
                    count = 0;
                    for (MyLocation location : list_item) {

                        if (location.getKhoangcach() == null) {
                            Log.i("loadListPlace ", "START");
                            Log.i("get Count ", "count= " + count);
                            // location.setKhoangcach(myTool.getDistance(yourLatLng,myTool.returnLatLngByName(location.getDiachi()))+"");
                            myTool.loadListPlace(location.getDiachi(), location.getLocaID(), StoreFragment.class.getSimpleName());

                            Log.i("get Count ", "count= " + count);
                            count++;
                        }
                    }
                    if (count == list_item.size()) {
                        mAdapter.notifyDataSetChanged();
                        Log.i("Count= max ", "count= " + count);
                        progressDialog1.dismiss();
                    }

                }
            }
            if (intent.getIntExtra("STT", 0) == 1) {
                for (MyLocation location : list_item) {
                    if (location.getLocaID().equals(intent.getStringExtra("PlaceID"))) {
                        location.setKhoangcach(intent.getStringExtra("Distance"));
                        Log.i("ID:" + intent.getStringExtra("PlaceID"), "Distance= " + intent.getStringExtra("Distance"));
                        count++;
                        Log.i("get Count ", "count= " + count);
                    }
                }
                //  mAdapter.notifyDataSetChanged();

//                if(intent.getIntExtra("TEMP",0)==listSize) {
//                    mAdapter.notifyDataSetChanged();
//                    progressDialog1.dismiss();
//                    Log.i("listSize= ",listSize+" ");
//                }
                if (count == listSize) {
                    mAdapter.notifyDataSetChanged();
                    progressDialog1.dismiss();
                    Log.i("listSize= ", listSize + " ");
                }
                Log.i("listSize: ", listSize + " ");
                Log.i("TEMP: ", "" + intent.getIntExtra("TEMP", 0));
            }
        }
    };

    @Override
    public void onStop() {
        Log.i("onStop", "RUN");
        super.onStop();
//        myTool.stopGoogleApi();
//        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("onCreateView", "RUN");
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
                //newLocation.setKhoangcach(myTool.getDistance(yourLatLng,myTool.returnLatLngByName(newLocation.getDiachi()))+"");
                // myTool.loadListPlace(newLocation.getDiachi(), newLocation.getLocaID(),StoreFragment.class.getSimpleName());
                list_item.add(newLocation);
//                listSize++;
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
//        myTool = new MyTool(getActivity());
//        myTool.startGoogleApi();


        //new CCC().execute();
        return view;
    }

    private void anhxa(View view) {
        Log.i("anhxa", "RUN");
        //list_item=new ArrayList<>();
        btn_refresh = (Button) view.findViewById(R.id.frg_store_btn_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_store_recyclerView_localist);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mLayoutManager = linearLayoutManager;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new Locatlist_rcyler_adapter(list_item);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String key = list_item.get(position).getLocaID();
                        Intent intent = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
                        intent.putExtra(getResources().getString(R.string.fragment_CODE),
                                getResources().getString(R.string.frag_locadetail_CODE));
                        ChooseLoca.getInstance().setHuyen(huyen);
                        ChooseLoca.getInstance().setLocaID(listLocation.get(position).getLocaID());
                        ChooseLoca.getInstance().setTinh(tinh);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().getApplicationContext().startActivity(intent);
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


//    public void doBinService() {
//        if (!isBound) {
//            Intent intent = new Intent(getActivity(), MyService.class);
//           getActivity(). bindService(intent, serviceConnection,Context.BIND_AUTO_CREATE);
//            isBound = true;
//        }
//    }
//
//    public void doUnbinService() {
//        if (isBound) {
//            getActivity().unbindService(serviceConnection);
//            isBound = false;
//        }
//    }
//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            MyService.LocalBinder binder = (MyService.LocalBinder) service;
//            myService = binder.getService();
//            listLocation=new ArrayList<>();
//            if(listLocation.size()==0){
//                Log.i("listLocation==null", "RUN");
//                listLocation=myService.returnListLocation();
//            }
//            if(list_item!=null){
//                for (MyLocation location: list_item){
//                    if(location.getKhoangcach()==null){
//                        Log.i("getKhoangcach=null", "RUN");
//                        for (MyLocation location1:listLocation){
//                            if(location1.getLocaID().equals(location.getLocaID()))
//                                Log.i("setKhoangcach", "RUN");
//                            location.setKhoangcach(location1.getKhoangcach());
//                        }
//                    }
//                    mAdapter.notifyDataSetChanged();
//                }
//
//            }
//            isBound = true;
//            Log.i("d", "ServiceConnection");
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            isBound = false;
//        }
//    };

}
