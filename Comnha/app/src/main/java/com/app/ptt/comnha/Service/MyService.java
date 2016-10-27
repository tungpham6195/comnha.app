package com.app.ptt.comnha.Service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.app.ptt.comnha.AdapterActivity;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.MainActivity;
import com.app.ptt.comnha.MapFragment;
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
    ArrayList<MyLocation> listLocation;
    Intent broadcastIntent;
    ArrayList<Route> routes=new ArrayList<>();
    Boolean isFinishLoad=false;
    String yourLocation=null;
    LatLng yourLatLng;
    int flag;
    @Override
    public void onCreate() {
        super.onCreate();
        listLocation=new ArrayList<>();
        broadcastIntent =new Intent();
        mBinder = new LocalBinder();
        geocoder = new Geocoder(this, Locale.getDefault());
        startGoogleApi();
        Log.i(LOG, "onCreate");
    }
    public void startGoogleApi(){

            buildGoogleApiClient();
            if(!mGoogleApiClient.isConnected()){
                mGoogleApiClient.connect();
            }

    }

    public String returnRoutes(){
        if(routes.size()>0){
           for (Route route: routes){
               sendBroadcast(route.getLocalID());
           }
            return "OK";

        }
        else {
            Log.i(LOG, "returnRoute=null");
            return null;
        }
    }
    @Override
    public void onDirectionFinderStart() {
    }
    @Override
    public void onDirectionFinderSuccess(ArrayList<Route> routes) {
        this.routes=routes;
        addToRoute();
        flag=1;
        Log.i(LOG,"ID CUA ROUTE:" +routes.get(routes.size()-1).getLocalID());
        routes.get(routes.size()-1).setEndAddress(returnLocationByLatLng(
                        geocoder,
                        routes.get(routes.size()-1).getEndLocation().latitude,
                        routes.get(routes.size()-1).getEndLocation().longitude
                ));
        sendBroadcast(routes.get(routes.size()-1).getLocalID());
        Log.i(LOG,"Routes size="+routes.size());
    }

    public void findDirection(String orgin, String destination,String ID) {
        Log.i(LOG, "findDirection");
        try {
            new DirectionFinder(this, orgin, destination, routes, geocoder,ID).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadListPlace(String destination,String ID) {
        Log.i(LOG, "loadListPlace");
        String origin =null;
        origin = getYourLocation();
        if(origin!=null &&destination!=null) {
            findDirection(origin, destination,ID);
        }
    }
    public String getYourLocation() {
        Log.i(LOG, "returnLocation");
        return yourLocation;
    }
    public void getDataInFireBase(){
        Log.i(LOG, "getDataInFireBase");

        Firebase.setAndroidContext(this);
        ref = new Firebase(getString(R.string.firebase_path));
        ref.child(getString(R.string.locations_CODE)).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                listLocation.add(dataSnapshot.getValue(MyLocation.class));
                Log.i(LOG, "Ten quan: "+dataSnapshot.getValue(MyLocation.class).getName());
                Log.i(LOG, "Địa chỉ: "+dataSnapshot.getValue(MyLocation.class).getDiachi());
                listLocation.get(listLocation.size()-1).setLocaID(dataSnapshot.getKey());
                loadListPlace(listLocation.get(listLocation.size()-1).getDiachi(),listLocation.get(listLocation.size()-1).getLocaID());
                Log.i(LOG, "Routes size= " + routes.size());
                Log.i(LOG, "onChildAdded.getDataInFireBase: " + dataSnapshot.getValue(MyLocation.class).getName()+ ". ID="+listLocation.get(listLocation.size()-1).getLocaID());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String  s) {

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

    public String returnLocationByLatLng(Geocoder geocoder,Double latitude,Double longitude) {
        Log.i(LOG, "ReturnLocationByLatLng");
        List<Address> addresses;
        Double lat = latitude;
        Double lon = longitude;
        try {
            if (lat != null && lon != null) {
                addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses.size() > 0) {
                    Address address=addresses.get(0);
                    String a = address.getAddressLine(0);
                    String b = address.getSubLocality();
                    String c = address.getSubAdminArea();
                    String d = address.getAdminArea();
                    String e="";
                    if(a!=null){
                        e+=a;
                        if(b!=null ){
                            if(a!="")
                                e+= ", "+b;
                            else
                                e+=b;
                        }
                        if(c!="" ){
                            if((a!=""||b!=""))
                                e+=", "+c;
                            else{
                                e+=c;
                            }
                            if(d!=null){
                                if((a!=""||b!=""||c!=""))
                                    e+=", "+d;
                                else
                                    e+=d;
                            }
                        }
                    }
                    return e;


//                    yourLocation = addresses.get(0).getAddressLine(0) +
//                            " " + addresses.get(0).getSubLocality() +
//                            " " + addresses.get(0).getSubAdminArea() +
//                            " " + addresses.get(0).getAdminArea();
                }
                return null;
            }
            return null;

        } catch (IOException e) {
        }
        return null;
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
                returnYourLatLng(l.getLatitude(),l.getLongitude());
                yourLocation =returnLocationByLatLng(geocoder,l.getLatitude(),l.getLongitude());
            }
            startLocationUpdate();
    }
    public LatLng getYourLatLng(){
        return yourLatLng;
    }
    public void returnYourLatLng(Double latitude,Double longtitude){
        yourLatLng= new LatLng(latitude,longtitude);
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG, "onConnectionSuspended " + i);

    }
    public void sendBroadcast(String a){
        if(flag==4){
            Log.i(LOG, "SendBroadcast");
            broadcastIntent.setAction(MainActivity.mBroadcast);
            broadcastIntent.putExtra("LocationError",false);
            sendBroadcast(broadcastIntent);
        }
        if(routes.size()>0 &&flag==1) {
            Log.i(LOG,"sendToMapFragment");
            //Log.i(LOG,"ID gửi đi nè: "+a);
            broadcastIntent.setAction(MapFragment.mBroadcastSendAddress);
            broadcastIntent.putExtra("PlaceID",a);
            sendBroadcast(broadcastIntent);
        }
        if(a=="LocationChange"){
            Log.i(LOG,"sendToAdapterActivityAboutChangingLocation");
            broadcastIntent.setAction(AdapterActivity.mBroadcast);
            broadcastIntent.putExtra("LocationChange",true);
            sendBroadcast(broadcastIntent);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        if (latitude == null && longtitude == null) {
            this.latitude = location.getLatitude();
            this.longtitude = location.getLongitude();
        }
        if (location.getLatitude() != latitude && location.getLongitude() != longtitude) {
            Log.i(LOG, "latitude: " + location.getLatitude() + ". Longitude: " + location.getLongitude());
            this.latitude = location.getLatitude();
            this.longtitude = location.getLongitude();
            returnYourLatLng(location.getLatitude(),location.getLongitude());
            yourLocation= returnLocationByLatLng(geocoder,location.getLatitude(),location.getLongitude());
            routes=new ArrayList<>();
            for (MyLocation a : listLocation) {
                Log.i(LOG,"onLocationChanged: "+a.getDiachi());
                loadListPlace(a.getDiachi(),a.getLocaID());

            }
//            sendBroadcast("LocationChange");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient!=null)
            if(mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();
    }
    public void addToRoute(){
        Log.i(LOG,"addToRoute");
        Route a=routes.get(routes.size()-1);
        for(MyLocation location: listLocation){
            if(a.getLocalID()==location.getLocaID()){
                Log.i(LOG,"Da zo day"+location.getName()+" ."+location.getTimestart()+" ."+location.getTimeend()+"."+location.getSdt());
                a.setTenQuan(location.getName());
                a.setGioMo(location.getTimestart());
                a.setGioDong(location.getTimeend());
                a.setSdt(location.getSdt());
                a.setGiaMin(location.getGiamin());
                a.setGiaMax(location.getGiamax());
            }
        }
    }
    public LatLng getLatLngByName(String address) {
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
    public Route getRouteByID(String ID){
        Log.i(LOG,"ID DAY:"+ID);
        if(routes.size()>0) {
            for (Route route : routes){
                if(route.getLocalID().equals(ID)){
                    //Log.i(LOG,"CO ROUTE NE:"+route.getEndAddress());
                    return route;
                }
            }
            return null;
        } else{
            return null;
        }
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
