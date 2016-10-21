package com.app.ptt.comnha;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;

public class MapFragment extends Fragment {
    private static final String LOG = "MapFragment";
    private SupportMapFragment supportMapFragment;
    private AutoCompleteTextView acText;
    private ArrayList<Route> list;
    private ArrayList<String> listName;

    TextView txt_TenQuan,txt_DiaChi,txt_GioMo,txt_DiemGia,txt_DiemPhucVu,txt_DiemVeSinh;

    MarkerOptions yourLocation = null;

    public void getRoute(ArrayList<Route> list) {
        this.list = new ArrayList<>();
        listName=new ArrayList<>();

        if(list!=null &&list.size()>0) {
            for (Route a : list) {
                listName.add(a.getEndAddress());
            }
            this.list = list;
        }
    }
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
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        acText=(AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        ArrayAdapter arrayAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,listName);
        acText.setAdapter(arrayAdapter);
        acText.setThreshold(1);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (list != null &&list.size()>0) {
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
                                    return null;
                                }

                                @Override
                                public View getInfoContents(Marker marker) {
                                    View view1=getLayoutInflater(savedInstanceState).inflate(R.layout.infowindowlayout,null);
                                   // view1.setLayoutParams(new RelativeLayout.LayoutParams(500,RelativeLayout.LayoutParams.WRAP_CONTENT));
                                    txt_TenQuan=(TextView) view1.findViewById(R.id.txt_TenQuan);
                                    txt_DiaChi=(TextView)view1.findViewById(R.id.txt_DiaChi);
                                    txt_GioMo=(TextView)view1.findViewById(R.id.txt_GioMo);
                                    txt_DiemGia=(TextView) view1.findViewById(R.id.txt_DiemGia);
                                    txt_DiemPhucVu=(TextView) view1.findViewById(R.id.txt_DiemPhucVu);
                                    txt_DiemVeSinh=(TextView) view1.findViewById(R.id.txt_DiemVeSinh);
                                    Route a= returnRoute(marker);
                                    if(a==null){
                                    }else{
                                        txt_TenQuan.setText(a.getTenQuan());
                                        txt_DiaChi.setText(a.getEndAddress());
                                        txt_GioMo.setText(a.getGioMo()+":"+a.getGioDong());
                                        txt_DiemVeSinh.setText("5");
                                        txt_DiemGia.setText("6");
                                        txt_DiemPhucVu.setText("8");
                                    }
                                    txt_DiemPhucVu.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(getContext(),"NHU CC",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    return view1;
                                }
                            });
                            googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                            Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
                            BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                            yourLocation = new MarkerOptions()
                                    .position(list.get(0).getStartLocation())
                                    .title(list.get(0).getStartAddress())
                                    .icon(markerIcon);
                            googleMap.addMarker(yourLocation);

                            for (int i = 0; i < list.size(); i++) {
                                googleMap.addMarker(new MarkerOptions()
                                        .position(list.get(i).getEndLocation())
                                        .title(list.get(i).getEndAddress())
                                );


                            }

                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(0).getStartLocation(), 13));
                        }
                    }

                });
            }
        }
    }
    public Route returnRoute(Marker marker){
        for(Route a:list){
            if(marker.getPosition().latitude==a.getEndLocation().latitude&& marker.getPosition().longitude==a.getEndLocation().longitude)
                return a;
        }
            return null;
    }

}
