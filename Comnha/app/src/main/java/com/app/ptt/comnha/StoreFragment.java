package com.app.ptt.comnha;


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
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Locatlist_rcyler_adapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.Service.MyTool;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
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
public class StoreFragment extends Fragment implements View.OnClickListener {
    private static final String LOG = StoreFragment.class.getSimpleName();

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
    boolean first=false;
    Button btn_refresh;
    private static int STATUS_START = 0;

    public void setFilter(int filter) {
        this.filter = filter;
    }

    Handler handler;
    int listSize = 0, count;
    String tinh, huyen;
    Context mContext;
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




    public void setTinh(String tinh) {
        this.tinh = tinh;
    }
    public void setHuyen(String huyen) {
        this.huyen = huyen;
    }

    public void setContext(Context context){
        mContext=context;
    }
    public StoreFragment() {
    }
    @Override
    public void onStart() {
        Log.i(LOG, "onStart");
        super.onStart();
        String a= Storage.readFile(mContext, "myLocation");
        if(a!=null){
            ArrayList<MyLocation> list=new ArrayList<>();
            list=Storage.readJSONMyLocation(a);
            myLocation=list.get(0);
        }
        isConnected= MyService.returnIsConnected();
        if(!isConnected){
            Toast.makeText(mContext,"Offline mode",Toast.LENGTH_SHORT).show();
        }
        myTool = new MyTool(getActivity(),StoreFragment.class.getSimpleName());
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver,mIntentFilter);
    }
    @Override
    public void onStop() {
        Log.i(LOG, "onStop");
        super.onStop();
        //myTool.stopGoogleApi();
        getContext().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        mView = view;
        listSize = 0;
        isConnected= MyService.returnIsConnected();
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://com-nha.firebaseio.com/");
        anhxa(view);
        if (!isConnected){
           // Toast.makeText(mContext,"Offline mode",Toast.LENGTH_SHORT).show();
           getDataOffline();
        }else
        {
            if(MyService.saveToListSaved("listLocation" + filter+"_"+tinh+"_"+huyen)==1)
                getDataOffline();
            getData();
        }
        return view;
    }
    public void getDataOffline(){
        Log.i(LOG + ".getDataOffline", "OK");
        if (Storage.readFile(mContext, "listLocation" + filter+"_"+tinh+"_"+huyen) != null) {
            ArrayList<MyLocation> locations;
            String a = Storage.readFile(mContext, "listLocation" + filter+"_"+tinh+"_"+huyen);
            if(a!=null) {
                locations = Storage.readJSONMyLocation(a);
                if(locations.size()>0) {
                    for (MyLocation location : locations) {
                        listLocation.add(location);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }else{
                if(isConnected)
                    getData();
            }
        }
    }
    public void getData(){
        final StringWriter out = new StringWriter();
        locaListChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

                MyLocation newLocation = dataSnapshot.getValue(MyLocation.class);
                Log.i("Dia chi", "RUN:" + newLocation.getDiachi());
                newLocation.setLocaID(dataSnapshot.getKey());
                if (myLocation != null) {
                    float kc = (float) myTool.getDistance(new LatLng(myLocation.getLat(), myLocation.getLng()), new LatLng(newLocation.getLat(), newLocation.getLng()));
                    int c = Math.round(kc);
                    Log.i(LOG + ".tinh khoang cach", c + "");
                    int d = c / 1000;
                    int e = c % 1000;
                    int f = e / 100;
                    newLocation.setKhoangcach(d + "," + f + " km");
                    Log.i("Dia chi", "CC:" + newLocation.getKhoangcach());
                } else
                    Log.i(LOG + ".getDataInFireBase", "my Location==null");
                listLocation.add(newLocation);
                Storage.writeFile(mContext,  Storage.parseMyLocationToJson( listLocation).toString(), "listLocation" + filter+"_"+tinh+"_"+huyen);
                MyService.saveToListSaved("listLocation" + filter+"_"+tinh+"_"+huyen);
                Log.i(LOG + ".getData ","size = "+ listLocation.size());
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
    }
    private void anhxa(View view) {
        Log.i(LOG, "anhxa");
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
                      //  if(isConnected) {
                            String key = listLocation.get(position).getLocaID();
                            Intent intent = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
                            intent.putExtra(getResources().getString(R.string.fragment_CODE),
                                    getResources().getString(R.string.frag_locadetail_CODE));
                            ChooseLoca.getInstance().setHuyen(huyen);
                            ChooseLoca.getInstance().setLocaID(key);
                            ChooseLoca.getInstance().setTinh(tinh);
                            if(!isConnected) {
                                ChooseLoca.getInstance().setInfo("listLocation" + filter + "_" + tinh + "_" + huyen);
                                Log.i(LOG + ".anhxa", "listLocation" + filter + "_" + tinh + "_" + huyen);
                            }
                            else
                                ChooseLoca.getInstance().setInfo("");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
//                        }else{
//                            Toast.makeText(mContext,"You are offline",Toast.LENGTH_LONG).show();
//                        }
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
