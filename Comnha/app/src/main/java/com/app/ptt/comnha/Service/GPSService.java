package com.app.ptt.comnha.Service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.app.ptt.comnha.Modules.DirectionFinder;
import com.app.ptt.comnha.Modules.DirectionFinderListener;
import com.app.ptt.comnha.Modules.Route;
import com.app.ptt.comnha.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class GPSService extends Service implements DirectionFinderListener, LocationListener {
    private static final String LOG = "GPSService";
    private IBinder iBinder;
    LocationManager locationManager;
    private Geocoder geocoder;
    Firebase ref;
    private Double latitude, longitude;
    ArrayList<Route> routes;


    @Override
    public void onCreate() {
        super.onCreate();
        routes = new ArrayList<>();
        Log.i(LOG, "onCreate");
        iBinder = new LocalBinder();
        geocoder = new Geocoder(this, Locale.getDefault());
        initGetLocation();
        getDataInFireBase();
    }

    public void initGetLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
    }
    public ArrayList<Route> returnRoute(){
        if(routes.size()>0){
            return routes;
        }
        else
            return null;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDirectionFinderStart() {
    }
    @Override
    public void onDirectionFinderSuccess(ArrayList<Route> routes) {

    }

    public class LocalBinder extends Binder {
        public GPSService getService() {
            return GPSService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;

    }
    public void findDirection(String orgin, String destination) {
        try {
            new DirectionFinder(this, orgin, destination, routes, geocoder).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadListPlace(String destination) {
        String origin = "";
        try {
            origin = returnLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        findDirection(destination, origin);

    }
    public void getDataInFireBase(){
        Firebase.setAndroidContext(this);
        ref = new Firebase(getString(R.string.firebase_path));

        ref.child(getString(R.string.locations_CODE)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // listPlace.add(dataSnapshot.child("diachi").getValue().toString()); //destination
                loadListPlace(dataSnapshot.child("diachi").getValue().toString());
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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }




    public String returnLocation() throws IOException {
        return GPSService.getLocation(geocoder,latitude,longitude);
    }
    public static String getLocation(Geocoder geocoder,Double latitude,Double longitude) throws IOException {
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
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        try {
            returnLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(LOG, "Latitude: " + latitude + ". Longtitude: " + longitude);
//
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
