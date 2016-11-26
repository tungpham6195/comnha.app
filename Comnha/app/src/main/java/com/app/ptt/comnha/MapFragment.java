package com.app.ptt.comnha;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.ConnectionDetector;
import com.app.ptt.comnha.Modules.LocationFinderListener;
import com.app.ptt.comnha.Modules.PlaceAPI;
import com.app.ptt.comnha.Modules.PlaceAttribute;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.Service.MyTool;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.github.clans.fab.FloatingActionButton;
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

import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;


public class MapFragment extends Fragment implements View.OnClickListener,
        LocationFinderListener,
        PickLocationBottomSheetDialogFragment.onPickListener,
        View.OnKeyListener {
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
    int pos = -1, option = 1;
    boolean isNearest = false;
    int temp = 1;
    MarkerOptions yourMarker = null;
    ImageButton btn_search;
    AutoCompleteTextView edt_content;
    NetworkChangeReceiver mBroadcastReceiver;
    FloatingActionButton fab_filter, fab_location, fab_refresh;
    CardView card_pickProvince, card_pickDistrict, card_filterlabel, card_mylocation;
    TextView txt_tinh, txt_huyen, txt_filterLabel;
    PickLocationBottomSheetDialogFragment pickLocationDialog;
    FragmentManager fm;
    int whatProvince = -1;
    String tinh, huyen;
    ProgressDialog progressDialog;
    Boolean isConnected = false,locationSaved=false;

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    public void setLocationSaved(Boolean a){
        locationSaved=a;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG, "onCreate");
        list = new ArrayList<>();

        myTool = new MyTool(getContext(), MapFragment.class.getSimpleName());
        // myTool.startGoogleApi();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG, "onResume");
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
        card_mylocation = (CardView) view.findViewById(R.id.frg_map_cardV_mylocation);
        fab_refresh = (FloatingActionButton) view.findViewById(R.id.frg_map_fabrefresh);
        btn_search = (ImageButton) view.findViewById(R.id.frg_map_btnsearch);
        pickLocationDialog = new PickLocationBottomSheetDialogFragment();
        fm = getActivity().getSupportFragmentManager();
        card_pickProvince = (CardView) view.findViewById(R.id.frg_map_cardV_chonProvince);
        card_pickDistrict = (CardView) view.findViewById(R.id.frg_map_cardV_chonDistrict);
        card_filterlabel = (CardView) view.findViewById(R.id.frg_map_cardV_filterLabel);
        txt_huyen = (TextView) view.findViewById(R.id.frg_map_txtDistrict);
        txt_tinh = (TextView) view.findViewById(R.id.frg_map_txtProvince);
        txt_filterLabel = (TextView) view.findViewById(R.id.frg_map_txtfilterLabel);

        fab_filter = (FloatingActionButton) view.findViewById(R.id.frg_map_fabfilter);
        fab_location = (FloatingActionButton) view.findViewById(R.id.frg_map_fablocation);
        edt_content = (AutoCompleteTextView) view.findViewById(R.id.frg_map_edtsearch);
        edt_content.setAdapter(new PlaceAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item, 1));
        edt_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
            }
        });
        btn_search.setOnClickListener(this);
        fab_filter.setOnClickListener(this);
        fab_location.setOnClickListener(this);
        fab_refresh.setOnClickListener(this);
        card_pickDistrict.setOnClickListener(this);
        card_pickProvince.setOnClickListener(this);
        card_mylocation.setOnClickListener(this);
        pickLocationDialog.setOnPickListener(this);
        edt_content.setOnKeyListener(this);
        edt_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn_search.setImageResource(R.drawable.ic_close_grey_600_24dp);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    btn_search.setImageResource(R.drawable.ic_search_grey_600_24dp);
                }
            }
        });
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            search();
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        Log.i(LOG, "onStart");
        super.onStart();
        mIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mIntentFilter.addAction("android.location.PROVIDERS_CHANGED");
        mIntentFilter.addAction(mBroadcastSendAddress);
        mBroadcastReceiver = new NetworkChangeReceiver();
        mIntentFilter.addAction(mBroadcastChangeLocation);
        getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Override
    public void onClick(View v) {

            switch (v.getId()) {
                case R.id.frg_map_cardV_mylocation:
                    if (isConnected) {
                        if (myLocationSearch != null && isNearest) {
                            Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_location_black_24dp);
                            BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                            yourMarker = new MarkerOptions()
                                    .position(new LatLng(yourLocation.getLat(), yourLocation.getLng()))
                                    .title(yourLocation.getDiachi())
                                    .icon(markerIcon);
                            myGoogleMap.addMarker(yourMarker);
                        }
                        if (yourLocation != null)
                            myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(yourLocation.getLat(), yourLocation.getLng()), 13));
                    }else {
                        Toast.makeText(getContext(), "You are offline", Toast.LENGTH_LONG).show();
                    }
                        break;
                case R.id.frg_map_fabrefresh:
                    if (card_pickDistrict.getTranslationY() == 0
                            && card_pickProvince.getTranslationX() == 0) {
                        AnimationUtils.animatHideTagMap(card_pickProvince, card_pickDistrict);
                    }
                    if (card_filterlabel.getTranslationX() == 0) {
                        AnimationUtils.animatHideTagMap2(card_filterlabel);
                    }
                    reloadMap();
                    break;
                case R.id.frg_map_fabfilter:
                    if(isConnected) {
                        PopupMenu popupMenu = new PopupMenu(getActivity(), fab_filter, Gravity.TOP | Gravity.END);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_viewquan, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.popup_viewquan_none:
                                        if (card_filterlabel.getTranslationX() == 0) {
                                            AnimationUtils.animatHideTagMap2(card_filterlabel);
                                        }
                                        txt_filterLabel.setText(item.getTitle());
                                        option = 1;
                                        if (tinh != null && huyen != null)
                                            getDataInFireBase(tinh, huyen);
                                        else
                                            getDataInFireBase(yourLocation.getTinhtp(), yourLocation.getQuanhuyen());

                                        break;
                                    case R.id.popup_viewquan_gia:
                                        if (card_filterlabel.getTranslationX() != 0) {
                                            AnimationUtils.animatShowTagMap2(card_filterlabel);
                                        }
                                        txt_filterLabel.setText(item.getTitle());
                                        option = 2;


                                        if (tinh != null && huyen != null)
                                            getDataInFireBase(tinh, huyen);
                                        else
                                            getDataInFireBase(yourLocation.getTinhtp(), yourLocation.getQuanhuyen());
                                        break;
                                    case R.id.popup_viewquan_pv:
                                        if (card_filterlabel.getTranslationX() != 0) {
                                            AnimationUtils.animatShowTagMap2(card_filterlabel);
                                        }
                                        txt_filterLabel.setText(item.getTitle());
                                        option = 3;

                                        if (tinh != null && huyen != null)
                                            getDataInFireBase(tinh, huyen);
                                        else
                                            getDataInFireBase(yourLocation.getTinhtp(), yourLocation.getQuanhuyen());
                                        break;
                                    case R.id.popup_viewquan_vs:
                                        if (card_filterlabel.getTranslationX() != 0) {
                                            AnimationUtils.animatShowTagMap2(card_filterlabel);
                                        }
                                        txt_filterLabel.setText(item.getTitle());
                                        option = 4;
                                        if (tinh != null && huyen != null)
                                            getDataInFireBase(tinh, huyen);
                                        else
                                            getDataInFireBase(yourLocation.getTinhtp(), yourLocation.getQuanhuyen());
                                        break;

                                }

                                return true;
                            }
                        });
                        popupMenu.show();
                    }else {
                        Toast.makeText(getContext(), "You are offline", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.frg_map_fablocation:
                    if(isConnected){
                    if (card_pickDistrict.getTranslationY() == 0
                            && card_pickProvince.getTranslationX() == 0) {
//                    Log.i("transi", "pro: " + card_pickProvince.getTranslationX()
//                            + " dis: " + card_pickDistrict.getTranslationY());
                        AnimationUtils.animatHideTagMap(card_pickProvince, card_pickDistrict);
                    } else {
                        AnimationUtils.animatShowTagMap(card_pickProvince, card_pickDistrict);
                    }
            }else {
            Toast.makeText(getContext(), "You are offline", Toast.LENGTH_LONG).show();
        }
                    break;
                case R.id.frg_map_cardV_chonProvince:
                    pickLocationDialog.show(fm, "pickProvinceDialog");
                    break;
                case R.id.frg_map_cardV_chonDistrict:
                    if (whatProvince >= 0) {
                        Log.i("province", whatProvince + "");
                        pickLocationDialog.setWhatProvince(whatProvince);
                        pickLocationDialog.show(fm, "pickDistrictDialog");
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.txt_noChoseProvince), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.frg_map_btnsearch:
                    if(isConnected){
                    if (edt_content.getText().toString().equals("")) {
                        Toast.makeText(getContext(), getString(R.string.txt_noaddress),
                                Toast.LENGTH_LONG).show();
                    } else {
                        edt_content.setText("");
                        edt_content.clearFocus();
                        btn_search.setImageResource(R.drawable.ic_search_grey_600_24dp);
                    }
            }else {
            Toast.makeText(getContext(), "You are offline", Toast.LENGTH_LONG).show();
        }
                    break;


            }

    }

    private void search() {
        if (isConnected) {
            if (edt_content.getText().toString().trim().equals("")) {
                Toast.makeText(getActivity(),
                        getString(R.string.txt_noaddress),
                        Toast.LENGTH_SHORT).show();
            } else {
                if (isNearest)
                    isNearest = false;
                Log.i(LOG + ".onClick ", edt_content.getText().toString());
                if (edt_content.getText().toString() == "") {
                    Toast.makeText(getActivity(),
                            "Chưa có địa điểm",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(LOG + ".onClick ", "loadListPlace" + pos);
                    if (pos != -1) {
                        placeAPI = new PlaceAPI(placeAttributes.get(pos).getFullname(), this);
                    } else {
                        placeAPI = new PlaceAPI(edt_content.getText().toString(), this);
                    }
                }
            }
        } else {
            Toast.makeText(getContext(), "You are offline", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPicProvince(String province, int position) {
        whatProvince = position;
        tinh = province;
        txt_tinh.setText(province);
    }

    @Override
    public void onPickDistrict(String district) {
        txt_huyen.setText(district);
        huyen = district;
        isNearest = false;
        if(isConnected) {
            if (tinh != null && huyen != null) {
                myGoogleMap.clear();
                addMarkerYourLocation();
                getDataInFireBase(tinh, huyen);
            }
        }else{
            Toast.makeText(getContext(),"You are offline",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onStop() {
        Log.i(LOG, "onStop");
        super.onStop();
        getActivity().unregisterReceiver(mBroadcastReceiver);
        if(myTool.isGoogleApiConnected())
            myTool.stopLocationUpdate();
    }
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(LOG, "onViewCreated");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        //progressDialog.show();
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
                    myGoogleMap = googleMap;
                    googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            //myTool.startGoogleApi();
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
                            myGoogleMap.getUiSettings().setZoomControlsEnabled(false);
                            myGoogleMap.getUiSettings().setCompassEnabled(true);
                            myGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                            myGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
                            myGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
                            myGoogleMap.getUiSettings().setTiltGesturesEnabled(true);
                            myGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
                            myGoogleMap.setTrafficEnabled(true);
                            myGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                                @Override
                                public void onCameraMove() {
                                    if (card_pickDistrict.getTranslationY() == 0
                                            && card_pickProvince.getTranslationX() == 0) {
                                        AnimationUtils.animatHideTagMap(card_pickProvince, card_pickDistrict);
                                    }
                                    if (card_filterlabel.getTranslationX() == 0) {
                                        AnimationUtils.animatHideTagMap2(card_filterlabel);
                                    }
                                }
                            });

                            myGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    if ((marker.getPosition().latitude != yourLocation.getLat())
                                            && (marker.getPosition().longitude != yourLocation.getLng())) {
                                        if (myLocationSearch == null || (myLocationSearch != null
                                                && (marker.getPosition().latitude != myLocationSearch.getPlaceLatLng().latitude
                                                && (marker.getPosition().longitude != myLocationSearch.getPlaceLatLng().longitude)))) {
                                            MyLocation a = returnLocation(marker);
                                            if(isConnected) {
                                                if (a != null && a.getQuanhuyen() != null && a.getLocaID() != null && a.getTinhtp() != null) {
                                                    Intent intent = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
                                                    intent.putExtra(getResources().getString(R.string.fragment_CODE),
                                                            getResources().getString(R.string.frag_locadetail_CODE));

                                                    ChooseLoca.getInstance().setHuyen(a.getQuanhuyen());
                                                    ChooseLoca.getInstance().setLocaID(a.getLocaID());
                                                    ChooseLoca.getInstance().setTinh(a.getTinhtp());
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                            } else{
                                                    Toast.makeText(getContext(),"You are offline",Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    }
                                }
                            });
                            myGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
//                                    Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_location_black_24dp);
//                                    BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
//                                    yourMarker = new MarkerOptions()
//                                            .position(latLng)
//                                            .title(myTool.returnLocationByLatLng(latLng.latitude,latLng.longitude).getDiachi())
//                                            .icon(markerIcon);
//                                    myGoogleMap.addMarker(yourMarker);
                                }
                            });
                            myGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
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
                                        Log.i(LOG + ".infoWindow", "location choose:" + myLocationSearch.getFullname());
                                        if (
                                                marker.getPosition().latitude == myLocationSearch.getPlaceLatLng().latitude
                                                        && marker.getPosition().longitude == myLocationSearch.getPlaceLatLng().longitude) {
                                            View view1 = getLayoutInflater(savedInstanceState).inflate(R.layout.infowindow_your_location, null);
                                            txt_DiaChi = (TextView) view1.findViewById(R.id.txt_DiaChi);
                                            txt_DiaChi.setText(" Vị trí bạn chọn");
                                            return view1;
                                        } else {
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
                                                txt_KhoangCach.setText(d + "," + f + " km");
                                                if (a.getSize() == 0) {
                                                    txt_DiemVeSinh.setText("0");
                                                    txt_DiemGia.setText("0");
                                                    txt_DiemPhucVu.setText("0");
                                                } else {
                                                    txt_DiemVeSinh.setText(a.getVsTong() / a.getSize() + "");
                                                    txt_DiemGia.setText(a.getGiaTong() / a.getSize() + "");
                                                    txt_DiemPhucVu.setText(a.getPvTong() / a.getSize() + "");
                                                }
                                            } else

                                                Log.i(LOG + ".infoWindow", "a=null");
                                            return view1;
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
                    });


                }

            });
        }
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
            txt_KhoangCach.setText(a.getKhoangcach() + " km");
            if (a.getSize() == 0) {
                txt_DiemVeSinh.setText("0");
                txt_DiemGia.setText("0");
                txt_DiemPhucVu.setText("0");
            } else {
                txt_DiemVeSinh.setText(a.getVsTong() / a.getSize() + "");
                txt_DiemGia.setText(a.getGiaTong() / a.getSize() + "");
                txt_DiemPhucVu.setText(a.getPvTong() / a.getSize() + "");
            }
        } else
            Log.i(LOG + ".infoWindow", "Không thể tìm được địa chỉ này");
        return view1;
    }

//    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(mBroadcastSendAddress)) {
//                if (intent.getIntExtra("STT", 0) == 2) {
//                    Log.i(LOG + ".BroadcastReceiver", "Nhan vi tri cua ban:");
//                    yourLocation = myTool.getYourLocation();
//                    Log.i(LOG + ".BroadcastReceiver", "Kiem tra list:" + list.size());
//                    if (list.size() == 0 && yourLocation!=null) {
//                        option = 1;
//                        getDataInFireBase(yourLocation.getTinhtp(), yourLocation.getQuanhuyen());
//                    }
//                    progressDialog.dismiss();
//                }
//                if (intent.getIntExtra("STT", 0) == 3 && intent.getBooleanExtra("LocationChange", false)) {
//                    Log.i(LOG + ".MainActivity", "Nhan su thay doi vi tri cua ban:");
//                    yourLocation = myTool.getYourLocation();
//                    getDataInFireBase(yourLocation.getTinhtp(), yourLocation.getQuanhuyen());
//                    //  myTool.stopGoogleApi();
//                }
//                if (intent.getIntExtra("STT", 0) == -1) {
//                    Log.i(LOG + ".BroadcastReceiver", "No connecttion");
//                    progressDialog.dismiss();
//                    ConnectionDetector.showNoConnectAlert(getContext());
//                }
//                if (intent.getIntExtra("STT", 0) == -2) {
//                    Log.i(LOG + ".BroadcastReceiver", "No internet");
//                    progressDialog.dismiss();
//                    ConnectionDetector.showNetworkAlert(getContext());
//                }
//                if (intent.getIntExtra("STT", 0) == -3) {
//                    Log.i(LOG + ".BroadcastReceiver", "No gps");
//                    progressDialog.dismiss();
//                    ConnectionDetector.showSettingAlert(getContext());
//                }
//                if (intent.getIntExtra("STT", 0) == -4) {
//                    Log.i(LOG + ".BroadcastReceiver", "No gps");
//                    ConnectionDetector.showGetLocationError(getContext());
//                    progressDialog.dismiss();
//                }
//            }
//
//        }
//    };

    public void reloadMap() {
        whatProvince = -1;
        txt_filterLabel.setText("");
        txt_tinh.setText("Tỉnh");
        txt_huyen.setText("Huyện");
        option = 1;
        isNearest = false;
        myLocationSearch = null;
        tinh = yourLocation.getTinhtp();
        huyen = yourLocation.getQuanhuyen();
        getDataInFireBase(tinh, huyen);
    }

    public void addMarkerCustomSearch() {
        Drawable circleDrawable = getResources().getDrawable(R.drawable.icon);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        Log.i(LOG + ".changeLocation", myLocationSearch.getFullname());
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
        if (list.size() > 0)
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
            tinh = placeAttribute.getState();
            huyen = placeAttribute.getDistrict();

            Log.i(LOG + ".onLocationFinder", "place:" + placeAttribute.getFullname());
            isNearest = true;
            Log.i(LOG + ".onClick ", "!=current quan huyen");
            getDataInFireBase(myLocationSearch.getState(), myLocationSearch.getDistrict());
            //}
            edt_content.setText(placeAttribute.getFullname());
            pos = -1;
        } else {
            Toast.makeText(getActivity(),
                    "Không tìm được",
                    Toast.LENGTH_SHORT).show();
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
            if (resultList != null && resultList.size() > 0) {
                return resultList.size();
            } else return 0;
        }

        @Nullable
        @Override
        public String getItem(int position) {
            if (resultList.size() > 0)
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
                        if (placeAttributes.size() > 0) {
                            for (PlaceAttribute placeAttribute : placeAttributes) {
                                resultList.add(placeAttribute.getFullname());
                            }
                            filterResults.values = resultList;
                            filterResults.count = resultList.size();
                        } else {
                            return null;
                        }

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

    public void getDataInFireBase(String tinh, String huyen) {
        Log.i(LOG + ".getDataInFireBase", "tinh:" + tinh + "- huyen:" + huyen);
        if (tinh != null && huyen != null) {
            list = new ArrayList<>();
            myGoogleMap.clear();
            if (isNearest && myLocationSearch != null) {
                addMarkerCustomSearch();
            } else {
               if(yourLocation!=null){
                   addMarkerYourLocation();

               }
            }
            dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
            if(!isConnected){
                Toast.makeText(getContext(),"Offline mode",Toast.LENGTH_LONG).show();
                ArrayList<MyLocation> locations = new ArrayList<>();
                String a = Storage.readFile(getContext(), "listLocation" + 1+"_"+tinh+"_"+huyen);
                if(a!=null) {
                    locations = Storage.readJSONMyLocation(a);
                    if(locations.size()>0) {
                        for (MyLocation location : locations) {
                            if (isNearest && myLocationSearch != null) {
                                Log.i(LOG + ".onClick ", "isNearest && myLocationSearch != null");
                                float kc = (float) myTool.getDistance(new LatLng(myLocationSearch.getPlaceLatLng().latitude, myLocationSearch.getPlaceLatLng().longitude), new LatLng(location.getLat(), location.getLng()));
                                if (kc < 5000) {
                                    addMarker(location);
                                }
                            } else {
                                Log.i(LOG + ".onClick ", "isNearest && myLocationSearch == null:" + myTool.getDistance(new LatLng(yourLocation.getLat(), yourLocation.getLng()), new LatLng(location.getLat(), location.getLng())));
                                float kc = (float) myTool.getDistance(new LatLng(yourLocation.getLat(), yourLocation.getLng()), new LatLng(location.getLat(), location.getLng()));
                                int c = Math.round(kc);
                                int d = c / 1000;
                                int e = c % 1000;
                                int f = e / 100;
                                if(location.getKhoangcach()==null)
                                    location.setKhoangcach(d + "," + f);
                                addMarker(location);
                            }
                            list.add(location);
                            myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLat(),location.getLng()), 13));
                        }
                    }else
                        Toast.makeText(getContext(),"Không tìm thấy dữ liệu",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(),"Không tìm thấy dữ liệu",Toast.LENGTH_LONG).show();
                }
            }else{
                ChildEventListener childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        MyLocation newLocation = dataSnapshot.getValue(MyLocation.class);
                        newLocation.setLocaID(dataSnapshot.getKey());

                        if (isNearest && myLocationSearch != null) {
                            //addMarkerCustomSearch();
                            Log.i(LOG + ".onClick ", "isNearest && myLocationSearch != null");
                            float kc = (float) myTool.getDistance(new LatLng(myLocationSearch.getPlaceLatLng().latitude, myLocationSearch.getPlaceLatLng().longitude), new LatLng(newLocation.getLat(), newLocation.getLng()));
                            if (kc < 5000) {
                                addMarker(newLocation);
                            }
                        } else {
                            float kc = (float) myTool.getDistance(new LatLng(yourLocation.getLat(), yourLocation.getLng()), new LatLng(newLocation.getLat(), newLocation.getLng()));
                            int c = Math.round(kc);
                            int d = c / 1000;
                            int e = c % 1000;
                            int f = e / 100;
                            newLocation.setKhoangcach(d + "," + f);
                            addMarker(newLocation);
                        }
                        Log.i(LOG + ".getDataInFireBase", "isConnected: -option=");
                        Log.i(LOG + ".getDataInFireBase", "isConnected: "+isConnected+"-option="+option);
                        list.add(newLocation);
                        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newLocation.getLat(),newLocation.getLng()), 13));

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

                };
                if (option == 2) {
                    dbRef.child(tinh + "/" + huyen + "/"
                            + getString(R.string.locations_CODE))
                            .orderByChild("giaAVG")
                            .startAt(6)
                            .addChildEventListener(childEventListener);
                }
                if (option == 1) {
                    dbRef.child(//LoginSession.getInstance().getTinh()
                            tinh + "/"
                                    +
                                    //LoginSession.getInstance().getHuyen()
                                    huyen + "/"
                                    + getString(R.string.locations_CODE))
                            .addChildEventListener(childEventListener);

                }
                if (option == 3) {
                    dbRef.child(tinh + "/" + huyen + "/"
                            + getString(R.string.locations_CODE))
                            .orderByChild("pvAVG")
                            .startAt(6)
                            .addChildEventListener(childEventListener);
                }
                if (option == 4) {
                    dbRef.child(tinh + "/" + huyen + "/"
                            + getString(R.string.locations_CODE))
                            .orderByChild("vsAVG")
                            .startAt(6)
                            .addChildEventListener(childEventListener);
                }
            }

        } else {
            Toast.makeText(getContext(), "Khong tim thay tinh va huyen", Toast.LENGTH_LONG).show();
        }
    }
    class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getIntExtra("STT", 0) == 2) {
                                    Log.i(LOG + ".BroadcastReceiver", "Nhan vi tri cua ban:");
                yourLocation = myTool.getYourLocation();
                //Log.i(LOG + ".BroadcastReceiver", "Kiem tra list:" + list.size());
                //Log.i(LOG + ".BroadcastReceiver", "locationSaved:" + locationSaved);
                if (list.size() == 0 && yourLocation!=null) {
                    option = 1;
                    getDataInFireBase(yourLocation.getTinhtp(), yourLocation.getQuanhuyen());

                }
                //myTool.stopLocationUpdate();
                //progressDialog.dismiss();
            }
            if (isNetworkAvailable(context) && canGetLocation(context)) {
                if(locationSaved) {
                    ArrayList<MyLocation> locations = new ArrayList<>();
                    String a = Storage.readFile(getContext(), "myLocation");
                    Log.i(LOG + ".BroadcastReceiver", "myLocation:" + a);
                    if (a != null) {
                        locations = Storage.readJSONMyLocation(a);
                        if (locations.size() > 0)
                            yourLocation = locations.get(0);
                        isConnected = true;
                        Log.i(LOG + ".BroadcastReceiver", "yourLocation:" + yourLocation.getDiachi());
                        if (list.size() == 0 && yourLocation != null) {
                            option = 1;
                            getDataInFireBase(yourLocation.getTinhtp(), yourLocation.getQuanhuyen());
                        }
                        //progressDialog.dismiss();
                    }
                }else{
                    yourLocation = null;
                }
                if (yourLocation == null) {
                    list=new ArrayList<>() ;
                    myTool.startGoogleApi();
                   // progressDialog.show();
                }

            } else {
                if (!canGetLocation(context) && !isNetworkAvailable(context)) {
                   // ConnectionDetector.showNoConnectAlert(getContext());

                } else {
                    if (!isNetworkAvailable(context)) {
                       // ConnectionDetector.showNetworkAlert(getContext());
                    } else {
                        //ConnectionDetector.showSettingAlert(getContext());
                    }
                }
                ArrayList<MyLocation> locations = new ArrayList<>();
                String a = Storage.readFile(getContext(), "myLocation");
                Log.i(LOG + ".BroadcastReceiver", "myLocation:"+a);
                if (a != null) {
                    locations = Storage.readJSONMyLocation(a);
                    if (locations.size() > 0)
                        yourLocation = locations.get(0);
                    Log.i(LOG + ".BroadcastReceiver", "yourLocation:" + yourLocation.getDiachi());
                    if (list.size() == 0 && yourLocation != null) {
                        option = 1;
                        getDataInFireBase(yourLocation.getTinhtp(), yourLocation.getQuanhuyen());
                    }
                }
                isConnected=false;
            }
            Log.i(LOG+".NetworkChangeReceiver", "isConnected: "+isConnected);

        }

        private boolean canGetLocation(Context mContext) {
            try {
                LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!isGPSEnabled) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }

        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();

                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                       // Toast.makeText(getContext(),"Ten:"+info[i].getTypeName()+"--TrangThai:"+info[i].getState().toString(),Toast.LENGTH_LONG).show();
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            if (!isConnected) {
                                Log.v(LOG, "Now you are connected to Internet!");
                                //Toast.makeText(getApplicationContext(), "Now you are connected to Internet!", Toast.LENGTH_SHORT).show();
                                isConnected = true;
                                //do your processing here ---
                                //if you need to post any data to the server or get status
                                //update from the server
                            }
                            return true;
                        }
                    }
                }
            }
            Log.v(LOG, "You are not connected to Internet!");
            // Toast.makeText(getApplicationContext(), "You are offline", Toast.LENGTH_SHORT).show();
            ;
            //networkStatus.setText("You are not connected to Internet!");
            isConnected = false;
            return false;
        }
    }
}
