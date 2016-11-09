package com.app.ptt.comnha.Service;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.app.ptt.comnha.Modules.PlaceAttribute;
import com.google.firebase.database.ChildEventListener;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.MainActivity;
import com.app.ptt.comnha.MapFragment;
import com.app.ptt.comnha.Modules.DirectionFinder;
import com.app.ptt.comnha.Modules.LocationFinderListener;
import com.app.ptt.comnha.Modules.Route;
import com.app.ptt.comnha.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by cuong on 10/27/2016.
 */

public class MyTool implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private Context mContext;
    private static final String LOG = MyTool.class.getSimpleName();
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Double latitude = null, longtitude = null;
    private String fileName = "note.json";
    Geocoder geocoder;
    Firebase ref;
    ArrayList<MyLocation> listLocation;
    //ArrayList<PlaceAttribute> listplaceAttribute;
    Intent broadcastIntent;
    MyLocation yourLocation = null;
    String yourDistrict=null,yourState=null;
    int temp = 1;
    String classSend;
    int flag;
    int pos=0,temp1=1;
    ChildEventListener locaListChildEventListener;
    DatabaseReference dbRef;
    public MyTool(Context context, String classSend) {
        Log.i(LOG + ".MyTool", "Khoi chay MyTool");
        this.classSend=classSend;
        this.mContext = context;

        listLocation = new ArrayList<>();
        //listplaceAttribute=new ArrayList<>();
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
        Log.i(LOG + ".startGoogleApi", "Khoi dong GoogleApiClient");
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void stopGoogleApi() {
        Log.i(LOG + ".stopGoogleApi", "Tat GoogleApiClient");
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
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
            this.latitude = l.getLatitude();
            this.longtitude = l.getLongitude();
            yourLocation = returnLocationByLatLng(l.getLatitude(), l.getLongitude());
            if(yourLocation!=null) {
                Log.i(LOG + ".onConnected", "Your Location: " + yourLocation.getDiachi() + "-" + yourLocation.getLocationLatLng());
                flag = 2;
                sendBroadcast("Location");
            }
        }
        startLocationUpdate();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && this.latitude!=null&&this.longtitude!=null) {
            if (location.getLatitude() != this.latitude && location.getLongitude() != this.longtitude &&
                    getDistance(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(this.latitude, this.longtitude)) > 2000) {
                Log.i(LOG + ".onLocationChanged", "Vi tri cua ban bi thay doi: " + getDistance(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(this.latitude, this.longtitude)) + "m");
                this.latitude = location.getLatitude();
                this.longtitude = location.getLongitude();
                yourLocation = returnLocationByLatLng(location.getLatitude(), location.getLongitude());
                Log.i(LOG + ".onLocationChanged", "Vi tri moi: " + yourLocation + ". Lat= " + yourLocation.getLocationLatLng().latitude + "va lng= " + yourLocation.getLocationLatLng().longitude);
            }
        }
    }

    public MyLocation returnLocationInListByID(String ID) {
        for (MyLocation location : listLocation) {
            if (location.getLocaID().equals(ID)) {
                Log.i(LOG + ".returnLocationInList", "Location can tim:" + location.getDiachi());
                return location;
            }
        }
        return null;
    }

    public void sendBroadcast(String a) {
        Log.i(LOG + ".sendBroadcast", "Gui broadcast toi "+classSend);
        temp=0;
        if (flag == 1) {
                Log.i(LOG + ".sendBroadcast", "gui place id: " + temp++);
                broadcastIntent.setAction(classSend+".mBroadcastSendAddress");
                broadcastIntent.putExtra("PlaceID", a);
                broadcastIntent.putExtra("STT", 1);
                mContext.sendBroadcast(broadcastIntent);
        }
//        if (flag == 5) {
//            Log.i(LOG + ".sendBroadcast", "gui place id: MapFragment: " + flag);
//            broadcastIntent.setAction(MapFragment.mBroadcastSendAddress);
//            broadcastIntent.putExtra("PlaceID", a);
//            broadcastIntent.putExtra("STT", 2);
//            mContext.sendBroadcast(broadcastIntent);
//        }
//        if(flag==2){
//            Log.i(LOG + ".sendBroadcast", "Gui vi tri cua ban ("+classSend+".mBroadcastSendAddress"+")");
//                broadcastIntent.setAction(classSend+".mBroadcastSendAddress");
//                broadcastIntent.putExtra("Location", true);
//                broadcastIntent.putExtra("STT",2);
//                mContext.sendBroadcast(broadcastIntent);
//        }
        if(flag==2){
            if(classSend.equals("MainActivity")) {
                Log.i(LOG + ".sendBroadcast", "Gui vi tri cua ban (" + classSend + ".mBroadcastSendAddress" + ")");
                broadcastIntent.setAction(MainActivity.mBroadcastSendAddress);
                broadcastIntent.putExtra("Location", true);
                broadcastIntent.putExtra("STT", 2);
                mContext.sendBroadcast(broadcastIntent);
            }
            if(classSend.equals("MapFragment")) {
                Log.i(LOG + ".sendBroadcast", "Gui vi tri cua ban (" + classSend + ".mBroadcastSendAddress" + ")");
                broadcastIntent.setAction(MapFragment.mBroadcastSendAddress);
                broadcastIntent.putExtra("Location", true);
                broadcastIntent.putExtra("STT", 2);
                mContext.sendBroadcast(broadcastIntent);
            }
        }
//        if ( flag == 3) {
//            Log.i(LOG + ".sendBroadcast", "gui place id: StoreFragment" + temp++);
//            broadcastIntent.setAction(StoreFragment.mBroadcastSendAddress);
//            broadcastIntent.putExtra("PlaceID", a);
//            broadcastIntent.putExtra("STT", 1);
//            String b = "0";
//            broadcastIntent.putExtra("Distance", b);
//            mContext.sendBroadcast(broadcastIntent);
//        }
//        if (flag == 2) {
//            if (a.equals("LocationChange")) {
//                Log.i(LOG + ".sendBroadcast", "Co su thay doi vi tri");
//                broadcastIntent.setAction(MapFragment.mBroadcastSendAddress);
//                broadcastIntent.putExtra("LocationChange", true);
//                broadcastIntent.putExtra("STT", 2);
//                mContext.sendBroadcast(broadcastIntent);
//            }
//            if (a.equals("Location")) {
//                Log.i(LOG + ".sendBroadcast", "Vi tri cua ban :MapFragment");
//                broadcastIntent.setAction(MapFragment.mBroadcastSendAddress);
//                broadcastIntent.putExtra("Location", true);
//                broadcastIntent.putExtra("STT", 3);
//                mContext.sendBroadcast(broadcastIntent);
//            }
//            if (a.equals("Location")) {
//                Log.i(LOG + ".sendBroadcast", "Vi tri cua ban:StoreFragment");
//                broadcastIntent.setAction(StoreFragment.mBroadcastSendAddress);
//                broadcastIntent.putExtra("Location", true);
//                broadcastIntent.putExtra("STT", 4);
//                mContext.sendBroadcast(broadcastIntent);
//            }
//            flag = -1;
//        }
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    public void getDataInFireBase(String tinh,String huyen) {
//        routes = new ArrayList<>();
        Log.i(LOG + ".getDataInFireBase","Tinh:"+tinh+"-Huyen:"+huyen);
//        listLocation = new ArrayList<>();
//        Firebase.setAndroidContext(mContext);
//        ref = new Firebase(mContext.getString(R.string.firebase_path));
//        ref.child(mContext.getString(R.string.locations_CODE))
//                .addChildEventListener(
//                        new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                                MyLocation myLocation = dataSnapshot.getValue(MyLocation.class);
//                                myLocation.setLocaID(dataSnapshot.getKey());
//                                listLocation.add(myLocation);
//                                loadListPlace(myLocation.getDiachi(), myLocation.getLocaID(), classSend);
//                                Log.i(LOG + ".onChildAdded", "Ten quan: " + dataSnapshot.getValue(MyLocation.class).getName());
//                                Log.i(LOG + ".onChildAdded", "Dia chi: " + dataSnapshot.getValue(MyLocation.class).getDiachi());
//                            }
//                            @Override
//                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                                flag = 2;
//                                sendBroadcast("Location");
//                            }
//                            @Override
//                            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                            }
//                            @Override
//                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                            }
//                            @Override
//                            public void onCancelled(FirebaseError firebaseError) {
//                            }
//                        }
//                );
//
        listLocation = new ArrayList<>();
       // listplaceAttribute=new ArrayList<>();
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(mContext.getString(R.string.firebase_path));
        locaListChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                MyLocation newLocation = dataSnapshot.getValue(MyLocation.class);
                Log.i("Dia chi", "RUN:" + newLocation.getDiachi());
                newLocation.setLocaID(dataSnapshot.getKey());
                listLocation.add(newLocation);
             //   listplaceAttribute.add(returnLocationByName(newLocation.getDiachi(),newLocation.getLocaID()));
                pos=listLocation.size()-1;
                flag = 1;
                sendBroadcast(newLocation.getLocaID());
            }
            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

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
        dbRef.child(tinh + "/" + huyen + "/" + mContext.getString(R.string.locations_CODE))
                .addChildEventListener(locaListChildEventListener);
        Log.i("Dia chi", "RUN:" + listLocation.size());

    }

    public void loadListPlace(String destination, String ID, String className) {
        Log.i(LOG + ".loadListPlace", "className: " + className);
        if (className.toString() != "")
            this.classSend = className;
        Log.i(LOG + ".loadListPlace", "classSend: " + classSend);
        String origin = null;
        origin = getYourLocation().getDiachi();
        if (origin != null && destination != null) {
            findDirection(origin, destination, ID, className);
        }
    }

    public void findDirection(String orgin, String destination, String ID, String type) {
        Log.i(LOG, "findDirection");
        try {
            //new DirectionFinder(this, orgin, destination, ID, type).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MyLocation getYourLocation() {
        Log.i(LOG + ".returnLocation", "Lay vi tri cua ban");
        return yourLocation;
    }

    public MyLocation returnLocationByLatLng(Double latitude, Double longitude) {
        MyLocation myLocation=new MyLocation();
        Log.i(LOG + ".returnLocationByLatLng", "ReturnLocationByLatLng");
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
                    Scanner kb = new Scanner(c);
                    String name;
                    while (kb.hasNext()) {
                        name = kb.next();
                        if (name.equals( "District" )|| name.equals("Quận")) {
                            c = "Quận";
                            while(kb.hasNext()){
                                c+=" "+kb.next();
                            }
                        }
                    }
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
                    myLocation.setLocationLatLng(new LatLng(lat,lon));
                    return myLocation;
                }
                return null;
            }
        }catch (IOException e) {
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
        if (addresses.size()>0) {
            Log.i(LOG, "convert: latitude=" + addresses.get(0).getLatitude() + "longitude=" + addresses.get(0).getLongitude());
            return new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        } else return null;
    }
    public ArrayList <PlaceAttribute> returnPlaceAttributeByName(String mAddress) {
        ArrayList<PlaceAttribute> listPlaceAttribute=new ArrayList<>();
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(mAddress,4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {

            for (Address address:addresses) {
                PlaceAttribute placeAttribute=new PlaceAttribute();
                String a = address.getAddressLine(0);
                String b = address.getSubLocality();
                String c = address.getSubAdminArea();
                String d = address.getAdminArea();
                String e = "";
                if (a != null) {
                    e += a;
                    placeAttribute.setAddressNum(a);
                }
                Log.i(LOG,"Phuong sao k ra gì z: "+b);
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
                        if (name.equals( "District" )|| name.equals("Quận")) {
                            c = "Quận";
                            while(kb.hasNext()){
                                c+=" "+kb.next();
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
                placeAttribute.setPlaceLatLng(new LatLng(address.getLatitude(),address.getLongitude()));
                listPlaceAttribute.add(placeAttribute);
                Log.i(LOG+".returnPlaceAttributeByName:"+temp1,"Full name: "+placeAttribute.getFullname());
                Log.i(LOG+".returnPlaceAttributeByName:"+temp1,"Address Number:"+placeAttribute.getAddressNum());
                Log.i(LOG+".returnPlaceAttributeByName:"+temp1,"Locality: "+placeAttribute.getLocality());
                Log.i(LOG+".returnPlaceAttributeByName:"+temp1,"District: "+placeAttribute.getDistrict());
                Log.i(LOG+".returnPlaceAttributeByName:"+temp1,"State: "+placeAttribute.getState());
                temp1++;
            }
            if(listPlaceAttribute.size()>0)
                return listPlaceAttribute;

        }
        return null;
    }
    public boolean checkDistrict(int pos, String ID, String district) {
        if (listLocation.get(pos).getLocaID().equals(ID)) {
            if (listLocation.get(pos).getQuanhuyen() != null && listLocation.get(pos).getQuanhuyen().equals(district))
                return true;
        } else {
            for (MyLocation location : listLocation) {
                if (location.getLocaID().equals(ID) && location.getQuanhuyen() != null && location.getQuanhuyen().equals(district)) {
                    return true;
                }
            }
        }
        return false;
    }
}
