package com.app.ptt.comnha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
public class MapFragment extends Fragment implements View.OnClickListener {
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    public static final String mBroadcastChangeLocation = "mBroadcastChangeLocation";
    private IntentFilter mIntentFilter;
    private static final String LOG = MapFragment.class.getSimpleName();
    private SupportMapFragment supportMapFragment;
    private ArrayList<Route> list = new ArrayList<>();
    private ArrayList<String> listName = new ArrayList<>();
    ArrayList<PlaceAttribute> mPlaceAttribute;
    Route routeTemp=null;
    TextView txt_TenQuan, txt_DiaChi, txt_GioMo, txt_DiemGia, txt_DiemPhucVu, txt_DiemVeSinh,txt_KhoangCach;
    GoogleMap myGoogleMap;
    LatLng yourLatLng;
    String yourLocation;
    MyTool myTool;
    Storage storage;
    int pos;
    int temp=-1;
    MarkerOptions yourMarker = null;
    ImageButton btn_search, btn_reload;
    AutoCompleteTextView edt_content,edt_sort;

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
        btn_reload=(ImageButton) view.findViewById(R.id.frg_map_btnreload);
        edt_content = (AutoCompleteTextView) view.findViewById(R.id.frg_map_edtsearch);
        edt_sort= (AutoCompleteTextView) view.findViewById(R.id.frg_map_edtsort);
        edt_content.setAdapter(new PlaceAutoCompleteAdapter(getActivity(),R.layout.autocomplete_list_item,1));
        ArrayList<String> a=new ArrayList<>();
        a.add("Thủ Đức");
        a.add("Quận 10");
        a.add("Quận 1");
        a.add("Gò Vấp");
        a.add("Bình Thạnh");
        a.add("Quận 9");
        ArrayAdapter arrayAdapter=new ArrayAdapter(getActivity(),R.layout.autocomplete_list_item,a);
        edt_sort.setThreshold(1);
        edt_sort.setAdapter(arrayAdapter);
        edt_sort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myGoogleMap.clear();
                addMarkerYourLocation();
                temp=-1;
                for(int i=0;i<list.size();i++){
                    if(myTool.checkDistrict(i,list.get(i).getLocalID(),parent.getItemAtPosition(position).toString().trim())){
                        addMarker(list.get(i));
                        temp=i;
                    }
                }
                if(temp!=-1)
                     myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(temp).getEndLocation(), 13));
            }
        });
        edt_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos=position;
            }
        });
        btn_search.setOnClickListener(this);
        btn_reload.setOnClickListener(this);
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
        storage=new Storage(getContext());
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
                                if ((marker.getPosition().latitude == yourLatLng.latitude)
                                        && (marker.getPosition().longitude == yourLatLng.longitude)) {
                                    View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindow_your_location, null);
                                    txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
                                    txt_DiaChi.setText(yourLocation);
                                    Log.i(LOG+".infoWindow","your location");
                                    return view1;
                                }else
                                if(routeTemp!=null) {

                                    if (
                                            marker.getPosition().latitude == routeTemp.getEndLocation().latitude
                                                    && marker.getPosition().longitude == routeTemp.getEndLocation().longitude) {
                                        View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindow_your_location, null);
                                        txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
                                        txt_DiaChi.setText(" Vị trí bạn chọn");
                                        Log.i(LOG + ".infoWindow", "Vị trí bạn chọn");
                                        return view1;
                                    }else{
                                        if(routeTemp!=null){
                                            View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindowlayout, null);
                                            txt_TenQuan = (TextView) view1.findViewById(R.id.txt_TenQuan);
                                            txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
                                            txt_GioMo = (TextView) view1.findViewById(R.id.txt_GioMo);
                                            txt_DiemGia = (TextView) view1.findViewById(R.id.txt_DiemGia);
                                            txt_DiemPhucVu = (TextView) view1.findViewById(R.id.txt_DiemPhucVu);
                                            txt_DiemVeSinh = (TextView) view1.findViewById(R.id.txt_DiemVeSinh);
                                            txt_KhoangCach=(TextView) view1.findViewById(R.id.txt_KhoangCach);
                                            MyLocation a = returnMyLocation(marker);
                                            if (a != null) {
                                                txt_TenQuan.setText(a.getName());
                                                txt_DiaChi.setText(a.getDiachi());
                                                txt_GioMo.setText(a.getTimestart() + "-" + a.getTimeend());
                                                    float kc = (float) (returnDistanceWithRouteTemp(a));
                                                    int c = Math.round(kc);
                                                    int d = c / 1000;
                                                    int e = c % 1000;
                                                    int f = c / 100;
                                                    txt_KhoangCach.setText(d + "," + f + " km");
                                                Log.i(LOG+".infoWindow- routeTemp",(float)returnDistanceWithRouteTemp(a)/1000+" km");
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
                                            Log.i(LOG+".infoWindow","a=null");
                                            return view1;
                                        }
                                        else return null;

                                    }

                                }
                                else {
                                   return getInfoWindowOfMarker(marker,savedInstanceState);
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
    public double returnDistanceWithRouteTemp(MyLocation myLocation){
        for (Route route:list){
            if(myLocation.getLocaID().equals(route.getLocalID())){
               return myTool.getDistance(route.getEndLocation(),routeTemp.getEndLocation());
            }
        }
        return 0;
    }
    public View getInfoWindowOfMarker(Marker marker, Bundle savedInstanceState){
        View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindowlayout, null);
        txt_TenQuan = (TextView) view1.findViewById(R.id.txt_TenQuan);
        txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
        txt_GioMo = (TextView) view1.findViewById(R.id.txt_GioMo);
        txt_DiemGia = (TextView) view1.findViewById(R.id.txt_DiemGia);
        txt_DiemPhucVu = (TextView) view1.findViewById(R.id.txt_DiemPhucVu);
        txt_DiemVeSinh = (TextView) view1.findViewById(R.id.txt_DiemVeSinh);
        txt_KhoangCach=(TextView) view1.findViewById(R.id.txt_KhoangCach);
        MyLocation a = returnMyLocation(marker);
        if (a != null) {
            Log.i(LOG+".infoWindow",a.getDiachi()+" : "+a.getKhoangcach());
            txt_TenQuan.setText(a.getName());
            txt_DiaChi.setText(a.getDiachi());
            txt_GioMo.setText(a.getTimestart() + "-" + a.getTimeend());
            txt_KhoangCach.setText(a.getKhoangcach());
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
        Log.i(LOG+".infoWindow","a=null");
        return view1;
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
                        list.add(route);
                        addMarker(route);
                    }
                }
                if (intent.getIntExtra("STT", 0) == 2) {
                    routeTemp = myTool.returnCustomRoute();
                    changeLocation();

                }
                if (intent.getIntExtra("STT", 0) == 3 && intent.getBooleanExtra("Location", false)) {
                    Log.i(LOG + ".BroadcastReceiver", "Nhan vi tri cua ban:");
                    yourLatLng = myTool.getYourLatLng();
                    yourLocation = myTool.getYourLocation();
                    if (yourMarker == null) {
                        Log.i(LOG + ".BroadcastReceiver", "Them marker vi tri cua ban vao map");
                        addMarkerYourLocation();
                    } else {
                        myGoogleMap.clear();
                        Log.i(LOG + ".BroadcastReceiver", "Them marker vi tri cua ban vao map");
                        addMarkerYourLocation();
                        list = new ArrayList<>();
                    }
                    if (list.size() == 0) {
                        myTool.getDataInFireBase(MapFragment.class.getSimpleName());
                    }
                }
                if (intent.getIntExtra("STT", 0) == 2 && intent.getBooleanExtra("LocationChange", false)) {
                    Log.i(LOG + ".BroadcastReceiver", "Nhan su thay doi vi tri cua ban:");
                    yourLatLng = myTool.getYourLatLng();
                    yourLocation = myTool.getYourLocation();
                    Log.i(LOG + ".BroadcastReceiver", "Clear All Marker");
                    myGoogleMap.clear();
                    list = new ArrayList<>();
                    listName = new ArrayList<>();
                    Log.i(LOG + ".BroadcastReceiver", "Them Marker vi tri cua ban");
                    addMarkerYourLocation();

                }

            }

        }
    };
    public void reloadMarker(){
        myGoogleMap.clear();
        addMarkerYourLocation();
        for (Route route:list){
            addMarker(route);
        }
        routeTemp=null;
    }
    public void changeLocation(){
        Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        myGoogleMap.clear();
        Log.i(LOG + ".addMarker", "Them dia diem nhan duoc: " + routeTemp.getEndAddress());
        yourMarker = new MarkerOptions()
                .position(routeTemp.getEndLocation())
                .title(routeTemp.getEndAddress())
                .icon(markerIcon);
        myGoogleMap.addMarker(yourMarker);
        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(routeTemp.getEndLocation(), 13));
        for(Route route:list){
            if(myTool.getDistance(routeTemp.getEndLocation(),route.getEndLocation())<15000){
                addMarker(route);
            }
        }
    }
    public void addMarker(Route route) {
        Log.i(LOG + ".addMarker", "Them dia diem nhan duoc: " + route.getEndAddress());
        myGoogleMap.addMarker(new MarkerOptions()
                .position(route.getEndLocation()));
    }
    public void addMarkerYourLocation(){
        Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        yourMarker = new MarkerOptions()
                .position(yourLatLng)
                .title(yourLocation)
                .icon(markerIcon);
        myGoogleMap.addMarker(yourMarker);
        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLatLng, 13));
    }

    public MyLocation returnMyLocation(Marker marker) {
        Log.i(LOG + ".returnRoute", "Tra ve route ung voi marker");
        for (Route a : list) {
            if (marker.getPosition().latitude == a.getEndLocation().latitude && marker.getPosition().longitude == a.getEndLocation().longitude) {
                Log.i(LOG + ".returnMyLocation", a.getLocalID());
                MyLocation myLocation = myTool.returnMyLocationByID(a.getLocalID());
                return myLocation;
            }
        }
        return null;
    }

    class PlaceAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        ArrayList<String> resultList;
        Context mContext;
        int mResource;
        int adapterRule;
        PlaceAPI mPlaceAPI = new PlaceAPI();

        public PlaceAutoCompleteAdapter(Context context, int resource, int adapterRule) {
            super(context, resource);
            this.adapterRule = adapterRule;
            mContext = context;
            mPlaceAttribute = new ArrayList<>();
            mResource = resource;
        }

        @Override
        public int getCount() {
            if (resultList != null) {
                return resultList.size();
            } else return 0;
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return resultList.get(position);
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
                        mPlaceAttribute = new ArrayList<>();
                        mPlaceAttribute = mPlaceAPI.autocomplete(constraint.toString());
                        if (mPlaceAttribute != null) {

                            for (PlaceAttribute placeAttribute : mPlaceAttribute) {
                                    resultList.add(placeAttribute.getFullname());
                            }

                        } else {
                            return null;
                        }
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
//    public String returnFullname() {
//        String a = mPlaceAttribute.get(pos) .getStreet_number();
//        String b = mPlaceAttribute.get(pos) .getRoute();
//        String c = mPlaceAttribute.get(pos) .getLocality();
//        String d = mPlaceAttribute.get(pos) .getDistrict();
//        String f = mPlaceAttribute.get(pos) .getState();
//        String e = "";
//        if (a != null)
//            e += a;
//        if (b != null)
//            if (a == null)
//                e += b;
//            else
//                e += ", " + b;
//
//        if (c != null)
//            if (a == null && b == null)
//                e += c;
//            else
//                e += ", " + c;
//        if (d != null)
//            if (a == null && b == null && c == null)
//                e += d;
//            else
//                e += ", " + d;
//        if (f != null)
//            if (a == null && b == null && c == null)
//                e += f;
//            else
//                e += ", " + f;
//        return e;
//    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.frg_map_btnsearch:
                Log.i(LOG+".onClick ",edt_content.getText().toString());
                if(edt_content.getText().toString()==""){
                    Snackbar.make(getView(),"Chưa có địa điểm",Snackbar.LENGTH_LONG).show();
                }else{
                    Log.i(LOG+".onClick ","loadListPlace");
                    myTool.loadListPlace(edt_content.getText().toString(),"",MapFragment.class.getSimpleName());
                }
                break;
            case R.id.frg_map_btnreload:
                reloadMarker();
//                try {
//                    storage.readCompanyJSONFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                break;

        }
    }


}
