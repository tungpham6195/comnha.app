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
import android.widget.Toast;

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

import java.util.ArrayList;

public class MapFragment extends Fragment {
    private static final String LOG = "MapFragment";
    private SupportMapFragment supportMapFragment;
    private AutoCompleteTextView acText;
    private ArrayList<Route> list;
    MarkerOptions yourLocation = null;

    public void getMethod(ArrayList<Route> list) {
        this.list = new ArrayList<>();
        this.list = list;
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
//        acText=(AutoCompleteTextView) view.findViewById(R.id.acText);
//        ArrayAdapter arrayAdapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,listName);
//        acText.setAdapter(arrayAdapter);
//        acText.setThreshold(1);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                        googleMap.setMinZoomPreference(6.0f);
                        googleMap.setMaxZoomPreference(14.0f);
                        googleMap.animateCamera(CameraUpdateFactory.zoomIn());


                        if (list != null) {
                            Toast.makeText(getContext(),list.size()+"",Toast.LENGTH_LONG).show();
                            Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
                            BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                            yourLocation = new MarkerOptions()
                                    .position(list.get(0).startLocation)
                                    .title(list.get(0).startAddress)
                                    .icon(markerIcon);
                            googleMap.addMarker(yourLocation);
                        }
                        for (int i = 0; i < list.size(); i++) {
                            googleMap.addMarker(new MarkerOptions()
                                    .position(list.get(i).endLocation)
                                    .title(list.get(i).endAddress)
                            );


                        }

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(0).startLocation, 13));
                        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        googleMap.setMyLocationEnabled(true);
                    }
                }

            });
        }
    }

}
