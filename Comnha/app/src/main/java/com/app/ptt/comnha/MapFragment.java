package com.app.ptt.comnha;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.ptt.comnha.Modules.Route;
import com.app.ptt.comnha.Service.MyService;
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
    MyService myService;
    private ArrayList<Route> list = new ArrayList<>();
    private ArrayList<String> listName = new ArrayList<>();
    TextView txt_TenQuan, txt_DiaChi, txt_GioMo, txt_DiemGia, txt_DiemPhucVu, txt_DiemVeSinh;
    GoogleMap myGoogleMap;
    LatLng yourLatLng;
    String yourLocation;
    boolean isBound = false;
    MarkerOptions yourMarker = null;

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
        return view;
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
        doBinService();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG, "onStop");
        getActivity().unregisterReceiver(mBroadcastReceiver);
        doUnbinService();
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
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
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
                                    Route a = returnRoute(marker);
                                    if (a == null) {
                                    } else {
                                        txt_TenQuan.setText(a.getTenQuan());

                                        txt_DiaChi.setText(a.getEndAddress());
                                        txt_GioMo.setText(a.getGioMo() + ":" + a.getGioDong());
                                        txt_DiemVeSinh.setText("5");
                                        txt_DiemGia.setText("6");
                                        txt_DiemPhucVu.setText("8");
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

                Log.i(LOG, "DANHAN");
                listName.add(intent.getStringExtra("PlaceID"));
                //Log.i(LOG, "NHAN DUNG MA"+intent.getStringExtra("PlaceID"));
                Route route;
                route = myService.getRouteByID(intent.getStringExtra("PlaceID"));
                if (route != null) {
                    //Log.i(LOG, "DIA CHI:" + route.getEndAddress());
                    list.add(route);
                    addMarker(route);
                }

            }

        }
    };

    public void addMarker(Route route) {
        //myGoogleMap.animateCamera(CameraUpdateFactory.zoomIn());
        myGoogleMap.addMarker(new MarkerOptions()
                .position(route.getEndLocation()));
    }

    public Route returnRoute(Marker marker) {
        for (Route a : list) {
            if (marker.getPosition().latitude == a.getEndLocation().latitude && marker.getPosition().longitude == a.getEndLocation().longitude)
                return a;
        }
        return null;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            isBound = true;
            yourLatLng = myService.getYourLatLng();
            yourLocation = myService.getYourLocation();
            try {
                if (yourMarker == null) {
                    Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
                    BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                    yourMarker = new MarkerOptions()
                            .position(yourLatLng)
                            .title(yourLocation)
                            .icon(markerIcon);
                    myGoogleMap.addMarker(yourMarker);
                    myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myService.getYourLatLng(), 13));
                }
            } catch (NullPointerException mess) {

            }

            if (myService.returnRoutes() == null) {
                Log.i(LOG, "getDataInFireBase");
                myService.getDataInFireBase();
            } else {
                Log.i(LOG, "existing route");
                myService.returnRoutes();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void doBinService() {
        if (!isBound) {
            Intent intent = new Intent(getActivity(), MyService.class);
            getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    public void doUnbinService() {
        if (isBound) {
            getActivity().unbindService(serviceConnection);
            isBound = false;
        }
    }


}
