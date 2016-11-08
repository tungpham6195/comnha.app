package com.app.ptt.comnha.Service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.app.ptt.comnha.AdapterActivity;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.MainActivity;
import com.app.ptt.comnha.Modules.DirectionFinder;
import com.app.ptt.comnha.Modules.DirectionFinderListener;
import com.app.ptt.comnha.Modules.Route;
import com.app.ptt.comnha.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,DirectionFinderListener {

    private static final String LOG = "MyService";
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private IBinder mBinder;
    private Double latitude=null, longtitude=null;
    Geocoder geocoder;
    Firebase ref;
    List<Address> addresses;
    private String yourLocation;
    Intent broadcastIntent;
    ArrayList<Route> routes;
    ArrayList<MyLocation> listPlace;
    ArrayList<MyLocation> listLocation;
    Boolean check=false;
    int flag,flag1=0;
    String id="";
    Handler handler;
    @Override
    public void onCreate() {
        super.onCreate();
        listPlace=new ArrayList<>();
        routes=new ArrayList<>();
        listLocation=new ArrayList<>();
        broadcastIntent =new Intent();
        mBinder = new LocalBinder();
        geocoder = new Geocoder(this, Locale.getDefault());
        startGoogleApi();
        getDataInFireBase();

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.i("loadListPlace;", listLocation.get(msg.arg1).getDiachi());
                loadListPlace(listLocation.get(msg.arg1).getDiachi(),listLocation.get(msg.arg1).getLocaID(),"");
                //flag1=msg.arg1;
                id=listLocation.get(msg.arg1).getLocaID();
            }
        };

        Log.i(LOG, "onCreate");
    }
    public void startGoogleApi(){
        //if(ConnectionDetector.canGetLocation(getApplicationContext()) &&ConnectionDetector.networkStatus(getApplicationContext())){
            buildGoogleApiClient();
            if(!mGoogleApiClient.isConnected()){
                mGoogleApiClient.connect();
            }
       // }
    }


    public ArrayList<Route> returnRoute(){

        if(routes.size()>0){
            Log.i(LOG, "returnRoute: "+routes.size()+"");
            return routes;

        }
        else {

            Log.i(LOG, "returnRoute=null");
            return null;
        }
    }
    public ArrayList<MyLocation> getListPlace(){

        if(listPlace.size()>0){

            Log.i(LOG, "getListPlace: "+listPlace.size()+"");
            return listLocation;

        }
        else {

            Log.i(LOG, "getListPlace=null");
            return null;
        }
    }
    @Override
    public void onDirectionFinderStart() {

    }


    @Override
    public void onDirectionFinderSuccess(Route route) {
        routes.add(route);
        if(route.getLocalID().equals(id)) {
            Log.i("onDirectionFinder","flag1: "+flag1);
            flag1 = 0;
        }
        for(MyLocation location:listLocation){
            if(route.getLocalID()==location.getLocaID()) {
                location.setKhoangcach(route.getDistance().text);
                Log.i(LOG+".FinderSuccess",location.getKhoangcach());
            }
        }


    }



    public void doST(){
        if(listLocation.size()>0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i("getDataInFireBase;", "RUN:");
                    for (int i = 0; i < listLocation.size(); i++) {
                        while(flag1!=0){
                            try {
                                Log.i("Sleep", "200s" );
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Message msg = handler.obtainMessage();
                        Log.i("loadListPlace;", "vi tri: " + i);
                        msg.arg1 = i;
                        flag1=i;
                        handler.sendMessage(msg);
                    }
                    flag1=10;
                }
            }).start();
        }
    }

    public void findDirection(String orgin, String destination,String ID,String type) {
        Log.i(LOG, "findDirection");
        try {
            new DirectionFinder(this, orgin, destination,ID, type).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadListPlace(String destination,String ID,String type) {
        Log.i(LOG, "loadListPlace");
        String origin =null;
        try {
            origin = returnLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(origin!=null &&destination!=null) {
            findDirection(origin, destination,ID,type);
        }
    }
    public String returnLocation() throws IOException {
        Log.i(LOG, "returnLocation");
        return getLocation(geocoder,latitude,longtitude);
    }
    public ArrayList<MyLocation> returnListLocation(){
        return listLocation;
    }
    public void getDataInFireBase(){
        Log.i(LOG, "getDataInFireBase");
        Firebase.setAndroidContext(this);
        ref = new Firebase(getString(R.string.firebase_path));
        ref.child(getString(R.string.locations_CODE)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyLocation myLocation = dataSnapshot.getValue(MyLocation.class);
                myLocation.setLocaID(dataSnapshot.getKey());
                listLocation.add(myLocation);
                flag=1;
                Log.i(LOG + ".onChildAdded", "Ten quan: " + dataSnapshot.getValue(MyLocation.class).getName());
                Log.i(LOG + ".onChildAdded", "Dia chi: " + dataSnapshot.getValue(MyLocation.class).getDiachi());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String  s) {
//                flag=1;
//                check=false;
//                loadListPlace(listLocation.get(listLocation.size()-1).getDiachi(),listLocation.get(listLocation.size()-1).getLocaID());
//                Log.i(LOG, "onChildChanged.getDataInFireBase: " + listLocation.get(listLocation.size()-1).getDiachi());
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


    }
    public class LocalBinder extends Binder {
        public MyService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG, "onStartCommand");
        return START_STICKY;
    }

    public static String getLocation(Geocoder geocoder,Double latitude,Double longitude) throws IOException {
        Log.i(LOG, "getLocation");
        List<Address> addresses;
        String yourLocation=null;
        Double lat=latitude;
        Double lon=longitude;
        try {
            if(lat!=null &&lon!=null) {
                addresses = geocoder.getFromLocation(lat,lon, 1);
                if (addresses.size() > 0) {
                    yourLocation = addresses.get(0).getAddressLine(0) +
                            " " + addresses.get(0).getSubLocality() +
                            " " + addresses.get(0).getSubAdminArea() +
                            " " + addresses.get(0).getAdminArea();
                }
            }
            return yourLocation;

        }catch (IOException e){
            return null;
        }

    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG, "onConnected");
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (l != null) {
                this.latitude = l.getLatitude();
                this.longtitude = l.getLongitude();
                Log.i(LOG, "lat1 " + latitude);
                Log.i(LOG, "lng1" + longtitude);
            } else{
                Log.i(LOG, "Location Error");
                flag=4;
                sendBroadcast();
            }
            startLocationUpdate();
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG, "onConnectionSuspended " + i);

    }
    public void sendBroadcast(){
//        if(flag==4){
//            Log.i(LOG, "SendBroadcast");
//            broadcastIntent.setAction(MainActivity.mBroadcast);
//            broadcastIntent.putExtra("LocationError",false);
//            sendBroadcast(broadcastIntent);
//        }
        if(routes.size()>0) {
            if (flag == 2) {
                Log.i(LOG, "SendBroadcast");
                broadcastIntent.setAction(AdapterActivity.mBroadcast);
                broadcastIntent.putExtra("LoadingComplete", 1);
                sendBroadcast(broadcastIntent);
            }
            if(flag==3){
                Log.i(LOG, "SendBroadcast");
                broadcastIntent.setAction(AdapterActivity.mBroadcast);
                broadcastIntent.putExtra("LoadingComplete", 0);
                sendBroadcast(broadcastIntent);
            }

        }
        else{
            if (flag == 2) {
                Log.i(LOG, "SendBroadcast");
                broadcastIntent.setAction(AdapterActivity.mBroadcast);
                broadcastIntent.putExtra("LoadingComplete", -1);
                sendBroadcast(broadcastIntent);

            }
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        if (latitude == null && longtitude == null) {
            this.latitude = location.getLatitude();
            this.longtitude = location.getLongitude();
        }
        Log.i(LOG, "latitude: " + location.getLatitude() + ". Longitude: " + location.getLongitude());
        if (location.getLatitude() != latitude && location.getLongitude() != longtitude) {
            Log.i(LOG, "latitude: " + location.getLatitude() + ". Longitude: " + location.getLongitude());
            this.latitude = location.getLatitude();
            this.longtitude = location.getLongitude();
//            for (MyLocation a : listLocation) {
//                routes=new ArrayList<>();
//                loadListPlace(a.getDiachi(),a.getLocaID());
//            }
        }
        if(flag==1)
            flag=3;
        else{
            if(flag==3) {
                doST();
                flag = 2;
            }
        }
//        if (flag == 1) {
//            flag = 3;
//            sendBroadcast();
//        } else if(flag==3) {
//            flag=2;
//            sendBroadcast();
//        }

    }


//    public void addToRoute(){
//        Log.i(LOG,"resetListPlace");
//        Route a=routes.get(routes.size()-1);
//       // for(Route a: routes){
//            for(MyLocation location: listLocation){
//                if(a.getLocalID()==location.getLocaID()){
//                    Log.i(LOG,"Da zo day"+location.getName()+" ."+location.getTimestart()+" ."+location.getTimeend()+"."+location.getSdt());
//                    a.setTenQuan(location.getName());
//                    a.setGioMo(location.getTimestart());
//                    a.setGioDong(location.getTimeend());
//                    a.setSdt(location.getSdt());
//                    a.setGiaMin(location.getGiamin());
//                    a.setGiaMax(location.getGiamax());
//                }
//            }
//       // }
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient!=null)
            if(mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();

    }
    public LatLng convertAddress(String address) {
        List<Address> addresses=null;
        try {
            addresses = geocoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            Log.i(LOG,"convert: latitude="+addresses.get(0).getLatitude()+"longitude="+addresses.get(0).getLongitude());
            return new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        } else return null;
    }

        @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOG, "onConnectionFailed ");

    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdate() {
        initLocationRequest();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

    }


}
