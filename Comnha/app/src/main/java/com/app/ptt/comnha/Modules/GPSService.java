package com.app.ptt.comnha.Modules;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,com.google.android.gms.location.LocationListener {
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static final String LOGSERVICE = "#######";
    private IBinder mBinder;
    private Double latitude,longtitude;
    Geocoder geocoder;
    List<Address> addresses;
    private String yourLocation;
    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new LocalBinder();
        buildGoogleApiClient();
        Log.i(LOGSERVICE, "onCreate");



    }
    public void stopGoogleApi(){
        mGoogleApiClient.disconnect();
    }
    public void startGoogleApi(){
        mGoogleApiClient.reconnect();
    }
    public Double getLatitude(){
        mGoogleApiClient.disconnect();
        Double lat;
        if(latitude!=null) {
            lat=latitude;
            mGoogleApiClient.reconnect();
            return lat;
        }
        else {
            mGoogleApiClient.reconnect();
            return 0.0;
        }
    }
    public Double getLongtitude(){
        mGoogleApiClient.disconnect();
        Double lon;
        if(longtitude!=null){
            lon=longtitude;
            mGoogleApiClient.reconnect();
            return lon;
        }

        else {
            mGoogleApiClient.reconnect();
            return 0.0;
        }
    }
    public String getYourLocation(){
        mGoogleApiClient.disconnect();
        String yl;
        if(yourLocation!=null) {
            mGoogleApiClient.reconnect();
            yl=yourLocation;
            return yl;
        }
        else {
            mGoogleApiClient.reconnect();
            return null;
        }
    }
    public class LocalBinder extends Binder {
        public GPSService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GPSService.this;
        }
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOGSERVICE, "onStartCommand");

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        return START_STICKY;
    }
    public void getLocation(Double latitude,Double longtitude) throws IOException {
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longtitude, 1);
        if (addresses.size()>0) {
            yourLocation=addresses.get(0).getAddressLine(0) +
                    " " + addresses.get(0).getSubAdminArea() +
                    " " + addresses.get(0).getAdminArea();
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOGSERVICE, "onConnected");

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
        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (l != null) {
            this.latitude=l.getLatitude();
            this.longtitude=l.getLongitude();
            Log.i(LOGSERVICE, "lat1 " + l.getLatitude());
            Log.i(LOGSERVICE, "lng1" + l.getLongitude());
            try {
                getLocation(l.getLatitude(),l.getLongitude());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOGSERVICE, "onConnectionSuspended " + i);

    }
    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOGSERVICE, "lat " + location.getLatitude());
        Log.i(LOGSERVICE, "lng " + location.getLongitude());
        this.latitude=location.getLatitude();
        this.longtitude=location.getLongitude();
        try {
            getLocation(location.getLatitude(),location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOGSERVICE, "onConnectionFailed ");

    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void startLocationUpdate() {
        initLocationRequest();

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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

    }

}
