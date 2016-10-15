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

import com.app.ptt.comnha.MainActivity;
import com.app.ptt.comnha.Manifest;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,DirectionFinderListener {

    private static final String LOG = "MyService";
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static final String LOGSERVICE = "#######";
    private IBinder mBinder;
    private Double latitude=null, longtitude=null;
    Geocoder geocoder;
    Firebase ref;
    List<Address> addresses;
    private String yourLocation;
    Intent broadcastIntent;
    ArrayList<Route> routes=new ArrayList<>();
    ArrayList<String> listPlace=new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        broadcastIntent =new Intent();
        mBinder = new LocalBinder();
        geocoder = new Geocoder(this, Locale.getDefault());
        buildGoogleApiClient();
        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
        //getDataInFireBase();
        Log.i(LOG, "onCreate");
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
    @Override
    public void onDirectionFinderStart() {
    }
    @Override
    public void onDirectionFinderSuccess(ArrayList<Route> routes) {
        this.routes=routes;
        Log.i(LOG,"Routes size="+routes.size());
        sendBroadcast();
    }

    public void findDirection(String orgin, String destination) {
        Log.i(LOG, "findDirection");
        try {
            new DirectionFinder(this, orgin, destination, routes, geocoder).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadListPlace(String destination) {
        Log.i(LOG, "loadListPlace");
        String origin = "";
        try {
            origin = returnLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(origin!="")
            findDirection(origin, destination);

    }
    public String returnLocation() throws IOException {
        Log.i(LOG, "returnLocation");
        return getLocation(geocoder,latitude,longtitude);
    }
    public void getDataInFireBase(){
        Log.i(LOG, "getDataInFireBase");
        Firebase.setAndroidContext(this);
        ref = new Firebase(getString(R.string.firebase_path));
        ref.child(getString(R.string.locations_CODE)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //routes=new ArrayList<Route>();
                listPlace.add(dataSnapshot.child("diachi").getValue().toString());
                Log.i(LOG,"listPlace.size= "+listPlace.size()+"");
                Log.i(LOG,"getDataInFireBase: "+dataSnapshot.child("diachi").getValue().toString());
               loadListPlace(listPlace.get(listPlace.size()-1));

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
        String yourLocation="";
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
            try {
                returnLocation();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG, "onConnectionSuspended " + i);

    }
    public void sendBroadcast(){
        if(listPlace.size()==routes.size()) {
            Log.i(LOG,"listPlace.size==routes.size");
            broadcastIntent.setAction(MainActivity.mBroadcast);
            broadcastIntent.putExtra("LoadingComplete",true);
            sendBroadcast(broadcastIntent);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
       if(location.getLatitude()!=latitude &&location.getLongitude()!=longtitude) {
            Log.i(LOG, "latitude: " + location.getLatitude()+". Longitude: "+location.getLongitude());

            this.latitude = location.getLatitude();
            this.longtitude = location.getLongitude();
            try {
                returnLocation();
            } catch (IOException e) {
                e.printStackTrace();
            }
       }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();

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
