package com.app.ptt.comnha.Service;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.MainActivity;
import com.app.ptt.comnha.MapFragment;
import com.app.ptt.comnha.Modules.ConnectionDetector;
import com.app.ptt.comnha.Modules.LocationFinderListener;
import com.app.ptt.comnha.Modules.PlaceAPI;
import com.app.ptt.comnha.Modules.PlaceAttribute;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.R;
import com.app.ptt.comnha.SplashActivity;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by cuong on 10/27/2016.
 */

public class MyTool implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,
        LocationFinderListener {
    private Context mContext;
    private static final String LOG = MyTool.class.getSimpleName();
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private Double latitude = null, longtitude = null;
    private String fileName = "note.json";
    Geocoder geocoder;
    ProgressDialog progressDialog;
    Firebase ref;
    ArrayList<MyLocation> listLocation;
    PlaceAPI placeAPI;
    Intent broadcastIntent;
    MyLocation yourLocation = new MyLocation();
    int temp = 1;
    String classSend;
    int flag;
    boolean getLocationFail=false;
    int pos = 0;
    public static final String mBroadcastSendAddress1 = "mBroadcastSendAddress1";
    public MyTool(Context context, String classSend) {
        Log.i(LOG + ".MyTool", "Khoi chay MyTool");
        this.classSend = classSend;
        this.mContext = context;
        listLocation = new ArrayList<>();
        broadcastIntent = new Intent();
        geocoder = new Geocoder(mContext, Locale.getDefault());

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void startGoogleApi() {
        {
                Log.i(LOG + ".startGoogleApi", "Khoi dong GoogleApiClient");
                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();

                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }


    }

    public void stopGoogleApi() {
        if (mGoogleApiClient != null) {
            Log.i(LOG + ".stopGoogleApi", "Tat GoogleApiClient");
            if (mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();
        }
    }

    public double getDistance(LatLng LatLng1, LatLng LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
        distance = locationA.distanceTo(locationB);
        return distance;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOG + ".onConnected", "Ket noi thanh cong googleApiClient");
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            Log.i(LOG + ".onConnected", "l!=null");
            this.latitude = l.getLatitude();
            this.longtitude = l.getLongitude();
            flag = 2;
            MyLocation location = new MyLocation();
            try {
                location = returnLocationByLatLng(l.getLatitude(), l.getLongitude());
            } catch (Exception e) {
                Log.i(LOG + ".onConnected", "Exception");
            }
            if (location != null)
                placeAPI = new PlaceAPI(location.getDiachi(), this);
            else {
                Log.i(LOG + ".onConnected", "LOI BI NULL");
                flag=-4;
                sendBroadcast("GetLocationError");
            }

            getLocationFail=false;
        }
        else{
            getLocationFail=true;
            latitude = null;
            longtitude = null;
            Log.i(LOG + ".onConnected", "LOI BI NULL");
        }
        startLocationUpdate();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG + ".onLocationChanged", "Giam sat su thay doi vi tri. lat="+location.getLatitude()+" - lng="+location.getLongitude());
        if (latitude == null && longtitude == null) {
            this.latitude = location.getLatitude();
            this.longtitude = location.getLongitude();
            MyLocation myLocation;
            myLocation = returnLocationByLatLng(location.getLatitude(), location.getLongitude());
            flag=2;
            placeAPI = new PlaceAPI(myLocation.getDiachi(), this);

        }
        if (location != null) {
            if (location.getLatitude() != this.latitude && location.getLongitude() != this.longtitude &&
                    getDistance(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(this.latitude, this.longtitude)) > 2000) {
                Log.i(LOG + ".onLocationChanged", "Vi tri cua ban bi thay doi: " + getDistance(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(this.latitude, this.longtitude)) + "m");
                this.latitude = location.getLatitude();
                this.longtitude = location.getLongitude();
                flag = 3;
                placeAPI = new PlaceAPI(returnLocationByLatLng(location.getLatitude(), location.getLongitude()).getDiachi(), this);
            }
        }
    }

    public void sendBroadcast(String a) {
        Log.i(LOG + ".sendBroadcast", "Gui broadcast toi " + classSend);
        temp = 0;
        if (flag == 2) {
            if (classSend.equals("MapFragment")) {
                Log.i(LOG + ".sendBroadcast", "Gui vi tri cua ban (" + classSend + ".mBroadcastSendAddress" + ")");
                broadcastIntent.setAction(mBroadcastSendAddress1);
                broadcastIntent.putExtra("STT", flag);
                mContext.sendBroadcast(broadcastIntent);
            }
            if (classSend.equals("MyService")) {
                Log.i(LOG + ".sendBroadcast", "Gui vi tri cua ban (" + classSend + ".mBroadcastSendAddress" + ")");
                broadcastIntent.setAction(mBroadcastSendAddress1);
                broadcastIntent.putExtra("STT", flag);
                mContext.sendBroadcast(broadcastIntent);
            }
            if (classSend.equals("SplashActivity")) {
                Log.i(LOG + ".sendBroadcast", "Gui vi tri cua ban (" + classSend + ".mBroadcastSendAddress" + ")");
                broadcastIntent.setAction(mBroadcastSendAddress1);
                broadcastIntent.putExtra("STT", flag);
                mContext.sendBroadcast(broadcastIntent);
            }

        }
        if (flag == -4 ) {
            if (classSend.equals("MainActivity")) {
                Log.i(LOG + ".sendBroadcast", "Khong gps (" + classSend + ".mBroadcastSendAddress" + ")");
                broadcastIntent.setAction(mBroadcastSendAddress1);
                broadcastIntent.putExtra("STT", flag);
                mContext.sendBroadcast(broadcastIntent);
            }
            if (classSend.equals("MapFragment")) {
                Log.i(LOG + ".sendBroadcast", "GetLocationError (" + classSend + ".mBroadcastSendAddress" + ")");
                broadcastIntent.setAction(mBroadcastSendAddress1);
                broadcastIntent.putExtra("STT", flag);
                mContext.sendBroadcast(broadcastIntent);
            }
        }
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public boolean isGoogleApiConnected() {
        if (mGoogleApiClient != null)
            return mGoogleApiClient.isConnected();
        else return false;
    }

    public void startLocationUpdate() {
        initLocationRequest();
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   p
            //
            // ublic void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void stopLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public MyLocation getYourLocation() {
        Log.i(LOG + ".returnLocation", "Lay vi tri cua ban");
        if (yourLocation != null)
            return yourLocation;
        else
            return null;
    }

    public MyLocation returnLocationByLatLng(Double latitude, Double longitude) {
        MyLocation myLocation = new MyLocation();

        List<Address> addresses;
        Double lat = latitude;
        Double lon = longitude;
        try {
            if (lat != null && lon != null) {
                addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String a = address.getAddressLine(0);
                    String b = address.getSubLocality();
                    String c = address.getSubAdminArea();
                    String d = address.getAdminArea();
                    String e = "";
                    if (a != null) {
                        e += a;
                    }
                    if (b != null) {
                        if (a == null)
                            e += b;
                        else
                            e += ", " + b;

                    }

                    if (c != null) {
                        Scanner kb = new Scanner(c);
                        String name;
                        while (kb.hasNext()) {
                            name = kb.next();
                            if (name.equals("District") || name.equals("Quận")) {
                                c = "Quận";
                                while (kb.hasNext()) {
                                    c += " " + kb.next();
                                }
                            }
                        }
                        if (a == null && b == null)
                            e += c;
                        else
                            e += ", " + c;

                        myLocation.setQuanhuyen(c);
                    }
                    if (d != null) {
                        if (a == null && b == null && c == null)
                            e += d;
                        else
                            e += ", " + d;
                        myLocation.setTinhtp(d);
                    }
                    myLocation.setDiachi(e);
                    myLocation.setLat(lat);
                    myLocation.setLng(lon);
                    Log.i(LOG + ".returnLocationByLatLng", "Location can tim" + myLocation.getDiachi());
                    return myLocation;
                }
                return null;
            }
        } catch (IOException e) {
        }
        return null;
    }

    public LatLng returnLatLngByName(String address) {
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            Log.i(LOG, "convert: latitude=" + addresses.get(0).getLatitude() + "longitude=" + addresses.get(0).getLongitude());
            return new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        } else return null;
    }

    public ArrayList<PlaceAttribute> returnPlaceAttributeByName(String mAddress) {
        ArrayList<PlaceAttribute> listPlaceAttribute = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(mAddress, 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            for (Address address : addresses) {
                PlaceAttribute placeAttribute = new PlaceAttribute();
                String a = address.getAddressLine(0);
                String b = address.getSubLocality();
                String c = address.getSubAdminArea();
                String d = address.getAdminArea();
                String e = "";
                if (a != null) {
                    e += a;
                    placeAttribute.setAddressNum(a);
                }
                if (b != null) {
                    if (a == null)
                        e += b;
                    else
                        e += ", " + b;
                    placeAttribute.setLocality(b);
                }
                if (c != null) {
                    Scanner kb = new Scanner(c);
                    String name;
                    while (kb.hasNext()) {
                        name = kb.next();
                        if (name.equals("District") || name.equals("Quận")) {
                            c = "Quận";
                            while (kb.hasNext()) {
                                c += " " + kb.next();
                            }
                        }
                    }
                    if (a == null && b == null)
                        e += c;
                    else
                        e += ", " + c;
                    placeAttribute.setDistrict(c);
                }
                if (d != null) {
                    if (a == null && b == null && c == null)
                        e += d;
                    else
                        e += ", " + d;
                    placeAttribute.setState(d);
                }
                placeAttribute.setFullname(e);
                placeAttribute.setPlaceLatLng(new LatLng(address.getLatitude(), address.getLongitude()));
                Log.i(LOG + ".addtoPlaceAttribute", "Full name: " + placeAttribute.getFullname());
                Log.i(LOG + ".addtoPlaceAttribute", "Address Number:" + placeAttribute.getAddressNum());
                Log.i(LOG + ".addtoPlaceAttribute", "Locality: " + placeAttribute.getLocality());
                Log.i(LOG + ".addtoPlaceAttribute", "District: " + placeAttribute.getDistrict());
                Log.i(LOG + ".addtoPlaceAttribute", "State: " + placeAttribute.getState());
                listPlaceAttribute.add(placeAttribute);
            }
            if (listPlaceAttribute.size() > 0)
                return listPlaceAttribute;
        }
        return null;
    }

    @Override
    public void onLocationFinderStart() {

    }

    @Override
    public void onLocationFinderSuccess(PlaceAttribute placeAttribute) {
        if (placeAttribute != null) {
            yourLocation = new MyLocation();
            yourLocation.setDiachi(placeAttribute.getFullname());
            yourLocation.setQuanhuyen(placeAttribute.getDistrict());
            yourLocation.setTinhtp(placeAttribute.getState());
            LatLng a=(returnLatLngByName(placeAttribute.getFullname()));
            yourLocation.setLat(a.latitude);
            yourLocation.setLng(a.longitude);
            if (flag == 2)
                sendBroadcast("Location");
            if (flag == 3) {
                ArrayList<MyLocation> mList=new ArrayList<>();
                mList.add(yourLocation);
                Storage.writeFile(mContext, Storage.parseMyLocationToJson(mList).toString(), "myLocation");
            }
        } else {
            Log.i(LOG + ".onLocationFinder", "State: null");
        }
        flag=-10;
    }



}
