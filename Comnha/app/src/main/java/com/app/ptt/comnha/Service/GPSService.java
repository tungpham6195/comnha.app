package com.app.ptt.comnha.Service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;import android.location.Geocoder;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by cuong on 10/5/2016.
 */

public class GPSService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final String LOG = "___MY LOG___";
    private IBinder iBinder;
    private GoogleApiClient mgoogleApiClient;
    private Geocoder geocoder;
    private LocationRequest mLocationRequest;
    private Double latitude, longitude;
    private String yourLocation;
    Boolean flag=false;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG, "onCreate");
        iBinder = new LocalBinder();
        buildGoogleApiClient();
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
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
        if (!mgoogleApiClient.isConnected())
            mgoogleApiClient.connect();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mgoogleApiClient.isConnected())
            mgoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
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
        Location location = LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.i(LOG, "Latitude: " + latitude + ". Longtitude: " + longitude);
            try {
                yourLocation=returnLocation();
                Log.i(LOG, yourLocation);
                flag=true;
            } catch (IOException e) {
                e.printStackTrace();
                flag=false;
            }
        }
        startLocationUpdate();

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
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient, mLocationRequest, this);
    }
    private void initLocationRequest(){
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    public String returnLocation() throws IOException {
        return GPSService.getLocation(geocoder,latitude,longitude);
    }
    public static String getLocation(Geocoder geocoder,Double latitude,Double longitude) throws IOException {
        List<Address> addresses=new ArrayList<Address>();
        String yourLocation="";
        Double lat=(Double)latitude;
        Double lon=(Double) longitude;
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
    protected synchronized void buildGoogleApiClient(){
        mgoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed( ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        try {
            yourLocation=returnLocation();
               } catch (IOException e) {
            flag=false;
            e.printStackTrace();
        }
        Log.i(LOG, "Latitude: " + latitude + ". Longtitude: " + longitude);
//
    }

}
