package com.app.ptt.comnha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.Route;
import com.app.ptt.comnha.Service.MyTool;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapFragment extends Fragment {
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    public static final String mBroadcastChangeLocation = "mBroadcastChangeLocation";
    private IntentFilter mIntentFilter;
    private static final String LOG = MapFragment.class.getSimpleName();
    private SupportMapFragment supportMapFragment;
    private ArrayList<Route> list = new ArrayList<>();
    private ArrayList<String> listName = new ArrayList<>();
    private ArrayList<MyLocation> listLocation;
    TextView txt_TenQuan, txt_DiaChi, txt_GioMo, txt_DiemGia, txt_DiemPhucVu, txt_DiemVeSinh;
    GoogleMap myGoogleMap;
    LatLng yourLatLng;
    String yourLocation;
    MyTool myTool;
    MarkerOptions yourMarker = null;
    ImageButton btn_search;
    AutoCompleteTextView edt_content;

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(LOG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        anhxa(view);
        return view;
    }

    private void anhxa(View view) {
        btn_search = (ImageButton) view.findViewById(R.id.frg_map_btnsearch);
        edt_content = (AutoCompleteTextView) view.findViewById(R.id.frg_map_edtsearch);
    }

    @Override
    public void onStart() {
        Log.i(LOG, "onStart");
        super.onStart();
        list = new ArrayList<>();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        mIntentFilter.addAction(mBroadcastChangeLocation);
        getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
        myTool = new MyTool(getContext());
        myTool.startGoogleApi();
    }

    @Override
    public void onStop() {
        Log.i(LOG, "onStop");
        super.onStop();
        getActivity().unregisterReceiver(mBroadcastReceiver);
        myTool.stopGoogleApi();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(LOG, "onViewCreated");
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapwhere);
        if (supportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapwhere, supportMapFragment).commit();
        }
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        myGoogleMap = googleMap;
                        googleMap.getUiSettings().setZoomControlsEnabled(false);
                        googleMap.getUiSettings().setCompassEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                        googleMap.getUiSettings().setRotateGesturesEnabled(true);
                        googleMap.getUiSettings().setScrollGesturesEnabled(true);
                        googleMap.getUiSettings().setTiltGesturesEnabled(true);
                        googleMap.getUiSettings().setZoomGesturesEnabled(true);
                        googleMap.setTrafficEnabled(true);
                        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker marker) {
                                if ((marker.getPosition().latitude != yourLatLng.latitude)
                                        && (marker.getPosition().longitude != yourLatLng.longitude)) {
                                    View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindowlayout, null);
                                    txt_TenQuan = (TextView) view1.findViewById(R.id.txt_TenQuan);
                                    txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
                                    txt_GioMo = (TextView) view1.findViewById(R.id.txt_GioMo);
                                    txt_DiemGia = (TextView) view1.findViewById(R.id.txt_DiemGia);
                                    txt_DiemPhucVu = (TextView) view1.findViewById(R.id.txt_DiemPhucVu);
                                    txt_DiemVeSinh = (TextView) view1.findViewById(R.id.txt_DiemVeSinh);
                                    MyLocation a = returnMyLocation(marker);
                                    if (a != null) {
                                        txt_TenQuan.setText(a.getName());

                                        txt_DiaChi.setText(a.getDiachi());
                                        txt_GioMo.setText(a.getTimestart() + "-" + a.getTimeend());
                                        if (a.getSize() == 0) {
                                            txt_DiemVeSinh.setText("0");
                                            txt_DiemGia.setText("0");
                                            txt_DiemPhucVu.setText("0");
                                        } else {
                                            txt_DiemVeSinh.setText(a.getVsTong() / a.getSize() + "");
                                            txt_DiemGia.setText(a.getGiaTong() / a.getSize() + "");
                                            txt_DiemPhucVu.setText(a.getPvTong() / a.getSize() + "");
                                        }
                                    }
                                    return view1;
                                } else {
                                    View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindow_your_location, null);
                                    txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
                                    txt_DiaChi.setText(yourLocation);
                                    return view1;
                                }

                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                return null;
                            }
                        });

                    }
                }

            });
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastSendAddress)) {
                if (intent.getIntExtra("STT", 0) == 1) {
                    Log.i(LOG + ".BroadcastReceiver", "Nhan id: " + intent.getStringExtra("PlaceID"));
                    listName.add(intent.getStringExtra("PlaceID"));
                    Route route;
                    route = myTool.getRouteByID(intent.getStringExtra("PlaceID"));
                    if (route != null) {
                        Log.i(LOG + ".BroadcastReceiver", "Dia Chi Cua ID Vua Nhan: " + route.getEndAddress());
                        //route.setEndAddress(myTool.returnLocationByName(route.getEndAddress()));
                        list.add(route);
                        addMarker(route);
                        // listLocation=new ArrayList<>();
                        // listLocation=myTool.returnListLocation();
                    }
                }
                if (intent.getIntExtra("STT", 0) == 3 && intent.getBooleanExtra("Location", false)) {
                    Log.i(LOG + ".BroadcastReceiver", "Nhan vi tri cua ban:");
                    yourLatLng = myTool.getYourLatLng();
                    yourLocation = myTool.getYourLocation();
//                    PlaceAPI placeAPI=new PlaceAPI();
//                    PlaceAttribute a=new PlaceAttribute();
//                            a=placeAPI.autocomplete(yourLocation);
////                    Log.i(LOG+".BroadcastReceiver","Vi tri moi cua ban da nhan: "+ yourLocation + " voi  lat: " + yourLatLng.latitude + "lng: " + yourLatLng.longitude);
//                    Log.i(LOG+".BroadcastReceiver","Vi tri moi cua ban da nhan: "+ a.getFullname() + " voi  lat: " + yourLatLng.latitude + "lng: " + yourLatLng.longitude);

                    //Log.i(LOG+".BroadcastReceiver","Vi tri cua ban nhan duoc: "+yourLocation+"  voi lat: " +yourLatLng.latitude+"lng: "+yourLatLng.longitude);
                    if (yourMarker == null) {
                        Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
                        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                        Log.i(LOG + ".BroadcastReceiver", "Them marker vi tri cua ban vao map");
                        yourMarker = new MarkerOptions()
                                .position(yourLatLng)
                                .title(yourLocation)
                                .icon(markerIcon);
                        myGoogleMap.addMarker(yourMarker);
                        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLatLng, 13));
                    } else {
                        myGoogleMap.clear();
                        Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
                        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                        Log.i(LOG + ".BroadcastReceiver", "Them marker vi tri cua ban vao map");
                        yourMarker = new MarkerOptions()
                                .position(yourLatLng)
                                .title(yourLocation)
                                .icon(markerIcon);
                        myGoogleMap.addMarker(yourMarker);
                        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLatLng, 13));
                        list = new ArrayList<>();
                    }
                    if (list.size() == 0) {
                        myTool.getDataInFireBase();
                    }
                }
                if (intent.getIntExtra("STT", 0) == 2 && intent.getBooleanExtra("LocationChange", false)) {
                    Log.i(LOG + ".BroadcastReceiver", "Nhan su thay doi vi tri cua ban:");
                    yourLatLng = myTool.getYourLatLng();
                    yourLocation = myTool.getYourLocation();
//                    PlaceAPI placeAPI=new PlaceAPI();
//                    PlaceAttribute a=placeAPI.autocomplete(yourLocation);
////                    Log.i(LOG+".BroadcastReceiver","Vi tri moi cua ban da nhan: "+ yourLocation + " voi  lat: " + yourLatLng.latitude + "lng: " + yourLatLng.longitude);
//                    Log.i(LOG+".BroadcastReceiver","Vi tri moi cua ban da nhan: "+ a.getFullname() + " voi  lat: " + yourLatLng.latitude + "lng: " + yourLatLng.longitude);
                    Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
                    BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                    Log.i(LOG + ".BroadcastReceiver", "Clear All Marker");
                    myGoogleMap.clear();
                    list = new ArrayList<>();
                    listName = new ArrayList<>();
                    Log.i(LOG + ".BroadcastReceiver", "Them Marker vi tri cua ban");
                    yourMarker = new MarkerOptions()
                            .position(yourLatLng)
                            .title(yourLocation)
                            .icon(markerIcon);
                    myGoogleMap.addMarker(yourMarker);
                    myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLatLng, 13));

                }

            }

        }
    };

    public void addMarker(Route route) {
        Log.i(LOG + ".addMarker", "Them dia diem nhan duoc: " + route.getEndAddress());
        myGoogleMap.addMarker(new MarkerOptions()
                .position(route.getEndLocation()));
    }

    public MyLocation returnMyLocation(Marker marker) {
        Log.i(LOG + ".returnRoute", "Tra ve route ung voi marker");
        for (Route a : list) {
            if (marker.getPosition().latitude == a.getEndLocation().latitude && marker.getPosition().longitude == a.getEndLocation().longitude) {
                Log.i(LOG + ".returnMyLocation", a.getLocalID());

//                for(MyLocation b:listLocation){
//                    if(a.getLocalID()==b.getLocaID()) {
//                        Log.i(LOG + ".returnMyLocation", "location tra ve: " + b.getLocaID() + ". Dia chi:" + b.getDiachi());
//                        return b;
//                    }
//                }
                MyLocation myLocation = myTool.returnMyLocationByID(a.getLocalID());

                return myLocation;
            }
        }
        return null;
    }


}
