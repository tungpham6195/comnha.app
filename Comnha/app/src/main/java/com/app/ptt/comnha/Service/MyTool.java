package com.app.ptt.comnha.Service;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

/**
 * Created by cuong on 10/27/2016.
 */

public class MyTool implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DirectionFinderListener, com.google.android.gms.location.LocationListener {
    private Context mContext;
    private static final String LOG = MyTool.class.getSimpleName();
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Double latitude=null, longtitude=null;
    Geocoder geocoder;
    Firebase ref;
    ArrayList<MyLocation> listLocation;
    Intent broadcastIntent;
    ArrayList<Route> routes;
    String yourLocation=null;
    LatLng yourLatLng;
    int flag;
    public MyTool(Context context) {
        Log.i(LOG +".MyTool","Khoi chay MyTool");
        this.mContext=context;
        routes=new ArrayList<>();
        listLocation=new ArrayList<>();
        broadcastIntent =new Intent();
        geocoder = new Geocoder(mContext, Locale.getDefault());
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDirectionFinderStart() {

    }
    public void startGoogleApi() {
        Log.i(LOG+".startGoogleApi","Khoi dong GoogleApiClient");
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

    }
    public void stopGoogleApi(){
        Log.i(LOG+".stopGoogleApi","Tat GoogleApiClient");
        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }
    @Override
    public void onDirectionFinderSuccess(ArrayList<Route> routes) {
        Log.i(LOG+".FinderSuccess","Them route thanh cong");
        this.routes=routes;
        flag=1;
//        routes.get(routes.size()-1).setEndAddress(returnLocationByLatLng(
//                geocoder,
//                routes.get(routes.size()-1).getEndLocation().latitude,
//                routes.get(routes.size()-1).getEndLocation().longitude
//        ));
        sendBroadcast(routes.get(routes.size()-1).getLocalID());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOG+".onConnected", "Ket noi thanh cong googleApiClient");
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
            this.latitude = l.getLatitude();
            this.longtitude = l.getLongitude();
            returnYourLatLng(l.getLatitude(),l.getLongitude());
            yourLocation =returnLocationByLatLng(geocoder,l.getLatitude(),l.getLongitude());
            flag=2;
            sendBroadcast("Location");

        }
        startLocationUpdate();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getLatitude() != this.latitude && location.getLongitude() != this.longtitude) {
            Log.i(LOG+".onLocationChanged","Vi tri cua ban bi thay doi");
            this.latitude = location.getLatitude();
            this.longtitude = location.getLongitude();
            returnYourLatLng(location.getLatitude(),location.getLongitude());
            yourLocation= returnLocationByLatLng(geocoder,location.getLatitude(),location.getLongitude());
            Log.i(LOG+".onLocationChanged","Vi tri moi: "+ yourLocation+". Lat= "+yourLatLng.latitude+"va lng= "+yourLatLng.longitude);
            flag=2;
            sendBroadcast("LocationChange");
            routes=new ArrayList<>();
            for (MyLocation a : listLocation) {
                loadListPlace(a.getDiachi(),a.getLocaID());
            }
        }
    }
    public MyLocation returnMyLocationByID(String ID){
        for(MyLocation location: listLocation){
            Log.i(LOG+".returnMyLocationByID"," ID truyen vao"+ID);
            if(location.getLocaID()==(ID)) {
                Log.i(LOG+".returnMyLocationByID","location can tim"+location.getDiachi());
                return location;
            }
        }
        return null;
    }
    public void sendBroadcast(String a){
        Log.i(LOG+".sendBroadcast","gui broadcast voi flag="+flag);

        if(routes.size()>0 &&flag==1) {
            Log.i(LOG+".sendBroadcast", "gui place id: "+a);
            broadcastIntent.setAction(MapFragment.mBroadcastSendAddress);
            broadcastIntent.putExtra("PlaceID",a);
            broadcastIntent.putExtra("STT",1);
            mContext.sendBroadcast(broadcastIntent);
            flag=-1;
        }
        if(flag==2) {
            if (a == "LocationChange") {
                Log.i(LOG+".sendBroadcast", "Co su thay doi vi tri");
                broadcastIntent.setAction(MapFragment.mBroadcastSendAddress);
                broadcastIntent.putExtra("LocationChange", true);
                broadcastIntent.putExtra("STT",2);
                mContext.sendBroadcast(broadcastIntent);
            }
            if (a == "Location") {
                Log.i(LOG+".sendBroadcast", "Vi tri cua ban");
                broadcastIntent.setAction(MapFragment.mBroadcastSendAddress);
                broadcastIntent.putExtra("Location", true);
                broadcastIntent.putExtra("STT",3);
                mContext.sendBroadcast(broadcastIntent);
            }
            flag=-1;
        }
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setFastestInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void startLocationUpdate() {
        initLocationRequest();
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

    }
    public void getDataInFireBase(){
        Log.i(LOG+".getDataInFireBase", "Lay du lieu tren firebase");
        routes=new ArrayList<>();
        listLocation=new ArrayList<>();
        Firebase.setAndroidContext(mContext);
        ref = new Firebase(mContext.getString(R.string.firebase_path));
        ref.child(mContext.getString(R.string.locations_CODE)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyLocation myLocation=dataSnapshot.getValue(MyLocation.class);
                myLocation.setLocaID(dataSnapshot.getKey());
                listLocation.add(myLocation);
                Log.i(LOG+".onChildAdded", "Ten quan: "+dataSnapshot.getValue(MyLocation.class).getName());
                Log.i(LOG+".onChildAdded", "Dia chi: "+dataSnapshot.getValue(MyLocation.class).getDiachi());
                loadListPlace(myLocation.getDiachi(),myLocation.getLocaID());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String  s) {
                flag=2;
                sendBroadcast("Location");
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
        Log.i(LOG+".returnLocation", "Lay vi tri cua ban");
        return yourLocation;
    }

    public String returnLocationByLatLng(Geocoder geocoder, Double latitude, Double longitude) {
        Log.i(LOG+".returnLocationByLatLng", "ReturnLocationByLatLng");
        List<Address> addresses=new ArrayList<>();
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
                }
                return null;
            }
            return null;

        } catch (IOException e) {
        }
        return null;
    }
    public LatLng getYourLatLng(){
        return yourLatLng;
    }
    public void returnYourLatLng(Double latitude,Double longtitude){
        yourLatLng= new LatLng(latitude,longtitude);
    }
    public LatLng returnLatLngByName(String address) {
        List<Address> addresses=new ArrayList<>();
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
    public String returnLocationByName(String address){
        List<Address> addresses=new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            Address temp=addresses.get(0);
            String a = temp.getAddressLine(0);
            String b = temp.getSubLocality();
            String c = temp.getSubAdminArea();
            String d = temp.getAdminArea();
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
        }
        return null;
    }
    public Route getRouteByID(String ID){
        Log.i(LOG,"ID DAY:"+ID);
        if(routes.size()>0) {
            for (Route route : routes){
                if(route.getLocalID().equals(ID)){
                    Log.i(LOG,"CO ROUTE NE:"+route.getEndAddress());
                    return route;
                }
            }
            return null;
        } else{
            return null;
        }
    }

}
