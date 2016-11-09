package com.app.ptt.comnha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.app.ptt.comnha.Modules.LocationFinderListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clans.fab.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.PlaceAPI;
import com.app.ptt.comnha.Modules.PlaceAttribute;
import com.app.ptt.comnha.Modules.Route;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.Service.MyTool;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

public class MapFragment extends Fragment implements View.OnClickListener,LocationFinderListener {
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    public static final String mBroadcastChangeLocation = "mBroadcastChangeLocation";
    private IntentFilter mIntentFilter;
    private static final String LOG = MapFragment.class.getSimpleName();
    private SupportMapFragment supportMapFragment;
    private ArrayList<MyLocation> list;
    ChildEventListener locaListChildEventListener;
    DatabaseReference dbRef;
    PlaceAPI placeAPI;
    //private ArrayList<String> listName = new ArrayList<>();
    ArrayList<PlaceAttribute> placeAttributes;
    PlaceAttribute myLocationSearch = null;
    TextView txt_TenQuan, txt_DiaChi, txt_GioMo, txt_DiemGia, txt_DiemPhucVu, txt_DiemVeSinh, txt_KhoangCach;
    GoogleMap myGoogleMap;
    MyLocation yourLocation;
    MyTool myTool;
    Storage storage;
    int pos=-1;
    boolean isNearest=false;
    int temp = 1;
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
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.frg_map_fablocation);
        anhxa(view);
        return view;
    }

    private void anhxa(View view) {
        btn_search = (ImageButton) view.findViewById(R.id.frg_map_btnsearch);
//        btn_reload = (ImageButton) view.findViewById(R.id.frg_map_btnreload);
        edt_content = (AutoCompleteTextView) view.findViewById(R.id.frg_map_edtsearch);

//        edt_sort = (AutoCompleteTextView) view.findViewById(R.id.frg_map_edtsort);
        edt_content.setAdapter(new PlaceAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item, 1));
        // ArrayList<String> a = new ArrayList<>();
        //   ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.autocomplete_list_item, );
//        edt_sort.setThreshold(1);
//        edt_sort.setAdapter(arrayAdapter);
//        edt_sort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                myGoogleMap.clear();
//                addMarkerYourLocation();
//                temp = -1;
//                for (int i = 0; i < list.size(); i++) {
//                    if (myTool.checkDistrict(i, list.get(i).getLocalID(), parent.getItemAtPosition(position).toString().trim())) {
//                        addMarker(list.get(i));
//                        temp = i;
//                    }
//                }
//                if (temp != -1)
//                    myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(temp).getEndLocation(), 13));
//            }
//        });
        edt_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
            }
        });
        btn_search.setOnClickListener(this);
//        btn_reload.setOnClickListener(this);
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
        myTool = new MyTool(getContext(), MapFragment.class.getSimpleName());
        myTool.startGoogleApi();
       // storage = new Storage(getContext());

        //storage.writeFile();

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
                                Log.i(LOG + ".listSize", list.size() + "");
                                if ((marker.getPosition().latitude == yourLocation.getLat())
                                        && (marker.getPosition().longitude == yourLocation.getLng())) {
                                    View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindow_your_location, null);
                                    txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
                                    txt_DiaChi.setText(yourLocation.getDiachi());
                                    Log.i(LOG + ".infoWindow", "your location");
                                    return view1;
                                } else if (myLocationSearch != null) {
                                    if (
                                            marker.getPosition().latitude == myLocationSearch.getPlaceLatLng().latitude
                                                    && marker.getPosition().longitude == myLocationSearch.getPlaceLatLng().longitude) {
                                        View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindow_your_location, null);
                                        txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
                                        txt_DiaChi.setText(" Vị trí bạn chọn");
                                        Log.i(LOG + ".infoWindow", "Vị trí bạn chọn");
                                        return view1;
                                    } else {
                                        if (myLocationSearch != null) {
                                            View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindowlayout, null);
                                            txt_TenQuan = (TextView) view1.findViewById(R.id.txt_TenQuan);
                                            txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
                                            txt_GioMo = (TextView) view1.findViewById(R.id.txt_GioMo);
                                            txt_DiemGia = (TextView) view1.findViewById(R.id.txt_DiemGia);
                                            txt_DiemPhucVu = (TextView) view1.findViewById(R.id.txt_DiemPhucVu);
                                            txt_DiemVeSinh = (TextView) view1.findViewById(R.id.txt_DiemVeSinh);
                                            txt_KhoangCach = (TextView) view1.findViewById(R.id.txt_KhoangCach);
                                            MyLocation a = returnLocation(marker);
                                            if (a != null) {
                                                txt_TenQuan.setText(a.getName());
                                                txt_DiaChi.setText(a.getDiachi());
                                                txt_GioMo.setText(a.getTimestart() + "-" + a.getTimeend());
                                                float kc = (float) myTool.getDistance(new LatLng(myLocationSearch.getPlaceLatLng().latitude, myLocationSearch.getPlaceLatLng().longitude), new LatLng(a.getLat(), a.getLng()));
                                                int c = Math.round(kc);
                                                int d = c / 1000;
                                                int e = c % 1000;
                                                int f = e / 100;
                                                txt_KhoangCach.setText(d + "," + f+" km");
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
                                            Log.i(LOG + ".infoWindow", "a=null");
                                            return view1;
                                        } else return null;

                                    }

                                } else {
                                    return getInfoWindowOfMarker(marker, savedInstanceState);
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

    public double returnDistanceLocationSearch(MyLocation myLocation) {
        for (MyLocation location : list) {
            if (myLocation.getLocaID().equals(location.getLocaID())) {
                return myTool.getDistance(new LatLng(location.getLat(), location.getLng()), new LatLng(myLocationSearch.getPlaceLatLng().latitude, myLocationSearch.getPlaceLatLng().longitude));
            }
        }
        return 0;
    }

    public View getInfoWindowOfMarker(Marker marker, Bundle savedInstanceState) {
        View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindowlayout, null);
        txt_TenQuan = (TextView) view1.findViewById(R.id.txt_TenQuan);
        txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
        txt_GioMo = (TextView) view1.findViewById(R.id.txt_GioMo);
        txt_DiemGia = (TextView) view1.findViewById(R.id.txt_DiemGia);
        txt_DiemPhucVu = (TextView) view1.findViewById(R.id.txt_DiemPhucVu);
        txt_DiemVeSinh = (TextView) view1.findViewById(R.id.txt_DiemVeSinh);
        txt_KhoangCach = (TextView) view1.findViewById(R.id.txt_KhoangCach);
        MyLocation a = returnLocation(marker);
        if (a != null) {
            Log.i(LOG + ".infoWindow", a.getDiachi() + " : " + a.getKhoangcach());
            txt_TenQuan.setText(a.getName());
            txt_DiaChi.setText(a.getDiachi());
            txt_GioMo.setText(a.getTimestart() + "-" + a.getTimeend());
            txt_KhoangCach.setText(a.getKhoangcach()+" km");
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
        Log.i(LOG + ".infoWindow", "a=null");
        return view1;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastSendAddress)) {
                if (intent.getIntExtra("STT", 0) == 2 && intent.getBooleanExtra("Location", false)) {
                    Log.i(LOG + ".BroadcastReceiver", "Nhan vi tri cua ban:");
                    yourLocation = myTool.getYourLocation();
                    if (yourMarker == null) {
                        Log.i(LOG + ".BroadcastReceiver", "Them marker vi tri cua ban vao map");
                        addMarkerYourLocation();
                    }
                    Log.i(LOG + ".BroadcastReceiver", "Kiem tra list:" + list.size());
                    if (list.size() == 0) {
                        getDataInFireBase(yourLocation.getTinhtp(), yourLocation.getQuanhuyen());
                    }
                }
            }

        }
    };

    public void reloadMarker() {
        myGoogleMap.clear();
        addMarkerYourLocation();
        for (MyLocation location : list) {
            addMarker(location);
        }
        myLocationSearch = null;
    }

    public void changeLocation() {
        Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        myGoogleMap.clear();
        Log.i(LOG+".changeLocation",myLocationSearch.getFullname());
        yourMarker = new MarkerOptions()
                .position(myLocationSearch.getPlaceLatLng())
                .title(myLocationSearch.getFullname())
                .icon(markerIcon);
        myGoogleMap.addMarker(yourMarker);
        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocationSearch.getPlaceLatLng(), 13));

    }

    public void addMarker(MyLocation myLocation) {
        Log.i(LOG + ".addMarker", "Them dia diem nhan duoc: " + myLocation.getDiachi());
        LatLng locatioLatLng = new LatLng(myLocation.getLat(), myLocation.getLng());
        myGoogleMap.addMarker(new MarkerOptions()
                .position(locatioLatLng));
    }

    public void addMarkerYourLocation() {
        Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        yourMarker = new MarkerOptions()
                .position(new LatLng(yourLocation.getLat(), yourLocation.getLng()))
                .title(yourLocation.getDiachi())
                .icon(markerIcon);
        myGoogleMap.addMarker(yourMarker);
        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(yourLocation.getLat(), yourLocation.getLng()), 13));
    }

    public MyLocation returnLocation(Marker marker) {
        Log.i(LOG + ".returnRoute", "Tra ve route ung voi marker");
        for (MyLocation location : list) {
            if (marker.getPosition().latitude == location.getLat()
                    && marker.getPosition().longitude == location.getLng()) {
                return location;
            }
        }
        return null;
    }

    @Override
    public void onLocationFinderStart() {

    }

    @Override
    public void onLocationFinderSuccess(PlaceAttribute placeAttribute) {
        if (placeAttribute != null) {
            placeAttribute.setPlaceLatLng(myTool.returnLatLngByName(placeAttribute.getFullname()));
            myLocationSearch = placeAttribute;
            changeLocation();
            Log.i(LOG + ".onLocationFinder", "place:" + placeAttribute.getFullname());
            isNearest = true;
            if (myLocationSearch.getDistrict().equals(yourLocation.getQuanhuyen()) && myLocationSearch.getState().equals(yourLocation.getTinhtp())) {
                if (list.size() > 0) {
                    for (MyLocation location : list) {
                        Log.i(LOG + ".onClick ", "==current quan huyen");
                        float kc = (float) myTool.getDistance(new LatLng(myLocationSearch.getPlaceLatLng().latitude, myLocationSearch.getPlaceLatLng().longitude), new LatLng(location.getLat(), location.getLng()));
                        if (kc < 5000)
                            addMarker(location);
                    }
                }
            } else {
                Log.i(LOG + ".onClick ", "!=current quan huyen");
                getDataInFireBase(myLocationSearch.getState(), myLocationSearch.getDistrict());
            }
            edt_content.setText(placeAttribute.getFullname());
            pos = -1;
        } else {
            Snackbar.make(getView(), "Khong tim duoc", Snackbar.LENGTH_LONG).show();
        }
    }

    class PlaceAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        ArrayList<String> resultList;
        Context mContext;
        int mResource;
        int adapterRule;

        public PlaceAutoCompleteAdapter(Context context, int resource, int adapterRule) {
            super(context, resource);
            this.adapterRule = adapterRule;
            mContext = context;
            placeAttributes = new ArrayList<>();
            mResource = resource;
        }

        @Override
        public int getCount() {
            if (resultList != null&&resultList.size()>0) {
                return resultList.size();
            } else return 0;
        }

        @Nullable
        @Override
        public String getItem(int position) {
            if(resultList.size()>0)
                return resultList.get(position);
            else
                return null;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            final Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    resultList = new ArrayList<>();
                    if (constraint != null) {
                        placeAttributes = new ArrayList<>();
                        placeAttributes = myTool.returnPlaceAttributeByName(constraint.toString());
                        if (placeAttributes.size()>0) {
                            for (PlaceAttribute placeAttribute : placeAttributes) {
                                resultList.add(placeAttribute.getFullname());
                            }
                        } else {
                            return null;
                        }
                        if(resultList.size()==0)
                            return null;
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_map_btnsearch:
                if(isNearest)
                    isNearest=false;
                Log.i(LOG + ".onClick ", edt_content.getText().toString());
                if (edt_content.getText().toString() == "") {
                    Snackbar.make(getView(), "Chưa có địa điểm", Snackbar.LENGTH_LONG).show();
                } else {
                    Log.i(LOG + ".onClick ", "loadListPlace"+pos);
                    if(pos!=-1) {
                        placeAPI=new PlaceAPI(placeAttributes.get(pos).getFullname(),this);
                    }else{
                        Snackbar.make(getView(), "Khong tim duoc", Snackbar.LENGTH_LONG).show();
                    }

                }
                break;
        }
    }

    public void getDataInFireBase(String tinh, String huyen) {
        if(tinh!=null &&huyen!=null) {
            list = new ArrayList<>();
            dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
            dbRef.child(//LoginSession.getInstance().getTinh()
                    tinh + "/"
                            +
                            //LoginSession.getInstance().getHuyen()
                            huyen + "/"
                            + getString(R.string.locations_CODE))
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            MyLocation newLocation = dataSnapshot.getValue(MyLocation.class);
                            newLocation.setLocaID(dataSnapshot.getKey());
                            list.add(newLocation);
                            if (isNearest && myLocationSearch != null) {
                                Log.i(LOG + ".onClick ", "isNearest && myLocationSearch != null");
                                    float kc=(float) myTool.getDistance(new LatLng(myLocationSearch.getPlaceLatLng().latitude,myLocationSearch.getPlaceLatLng().longitude),new LatLng(newLocation.getLat(),newLocation.getLng()));
                                if (kc<5000) {
                                    addMarker(newLocation);
                                }
                            } else {
                                Log.i(LOG + ".onClick ", "isNearest && myLocationSearch == null:"+myTool.getDistance(new LatLng(yourLocation.getLat(), yourLocation.getLng()), new LatLng(newLocation.getLat(), newLocation.getLng())));
                                float kc=(float) myTool.getDistance(new LatLng(yourLocation.getLat(), yourLocation.getLng()),new LatLng(newLocation.getLat(),newLocation.getLng()));
                                int c = Math.round(kc);
                                int d = c / 1000;
                                int e = c % 1000;
                                int f = e / 100;
                                newLocation.setKhoangcach(d + "," + f);
                               // newLocation.setKhoangcach(myTool.getDistance(new LatLng(yourLocation.getLat(), yourLocation.getLng()), new LatLng(newLocation.getLat(), newLocation.getLng())) + "");
                                addMarker(newLocation);
                            }
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
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }else{
            Toast.makeText(getContext(),"Khong tim thay tinh va huyen",Toast.LENGTH_LONG).show();
        }
    }


}
