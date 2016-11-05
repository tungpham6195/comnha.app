package com.app.ptt.comnha;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.ptt.comnha.Adapters.Locatlist_rcyler_adapter;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.ThucDon;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends Fragment implements View.OnClickListener, PickProvinceDialogFragment.OnnPickProvinceListener, PickDistrictDialogFragment.OnPickDistricListener, PickFoodDialogFragment.OnPickFoodListener {

    TextView txt_tinh, txt_quan, txt_mon;
    Button btn_tim;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<MyLocation> locaList;
    int whatProvince;
    ThucDon mon;
    DatabaseReference dbRef;
    ChildEventListener locaMenuChildEventListener;
    String tinh, quan;

    public FilterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(getResources().getString(R.string.firebase_path));
        locaMenuChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyLocation myLocation = dataSnapshot.getValue(MyLocation.class);
                myLocation.setLocaID(dataSnapshot.getKey());
                locaList.add(myLocation);
                mAdapter.notifyDataSetChanged();
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
        anhxa(view);
        return view;
    }

    private void anhxa(View view) {
        txt_tinh = (TextView) view.findViewById(R.id.frg_filter_txttinh);
        txt_quan = (TextView) view.findViewById(R.id.frg_filter_txtquan);
        txt_mon = (TextView) view.findViewById(R.id.frg_filter_txtmon);
        btn_tim = (Button) view.findViewById(R.id.frg_filter_btnTim);
        locaList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_filter_recyler);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new Locatlist_rcyler_adapter(locaList);
        mRecyclerView.setAdapter(mAdapter);
        txt_tinh.setOnClickListener(this);
        txt_quan.setOnClickListener(this);
        txt_mon.setOnClickListener(this);
        btn_tim.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        switch (v.getId()) {
            case R.id.frg_filter_txttinh:

                PickProvinceDialogFragment pickProvinceFrg = new PickProvinceDialogFragment();
                pickProvinceFrg.show(fm, "fragment_pickProvince");
                pickProvinceFrg.setOnPickProvinceListener(this);
                break;
            case R.id.frg_filter_txtquan:
                if (whatProvince < 1) {
                    Snackbar.make(v, getString(R.string.txt_noChoseProvince), Snackbar.LENGTH_SHORT).show();
                } else {
                    PickDistrictDialogFragment pickDistrictFrg = new PickDistrictDialogFragment();
                    pickDistrictFrg.setWhatprovince(whatProvince);
                    pickDistrictFrg.show(fm, "fragment_pickDistrict");
                    pickDistrictFrg.setOnPickDistricListener(this);
                }
                break;
            case R.id.frg_filter_txtmon:
                PickFoodDialogFragment pickFoodFrg = new PickFoodDialogFragment();
                pickFoodFrg.show(fm, "fragment_pickFood");
                pickFoodFrg.setOnPickFoodListener(this);
                break;
            case R.id.frg_filter_btnTim:
                querySomething();
                break;
        }
    }

    @Override
    public void onPickDistrict(String district) {
        txt_quan.setText(district);
        quan = district;
    }

    @Override
    public void onPickProvince(String province, int position) {
        txt_tinh.setText(province);
        whatProvince = position + 1;
        tinh = province;
    }

    @Override
    public void onPickFood(ThucDon thucDon) {
        txt_mon.setText(thucDon.getTenmon());
        mon = thucDon;
    }

    private void querySomething() {
        locaList.clear();
        mAdapter.notifyDataSetChanged();
        if (quan == null && mon != null) {//tìm món ở tất cả các quán
            dbRef.child(getResources().getString(R.string.menulocation_CODE) + mon.getMonID())
                    .addChildEventListener(locaMenuChildEventListener);
        } else if (tinh != null && mon == null) {//tìm tất cả các quán theo tỉnh
            dbRef.child(getString(R.string.locations_CODE))
                    .orderByChild("tinhtp")
                    .addChildEventListener(locaMenuChildEventListener);
        } else if (quan != null && mon == null) {
            dbRef.child(getString(R.string.locations_CODE))
                    .orderByChild("quanhuyen")
                    .addChildEventListener(locaMenuChildEventListener);
        } else if (quan != null && mon != null) {
            dbRef.child(getResources().getString(R.string.menulocation_CODE) + mon.getMonID())
                    .orderByChild("quanhuyen").equalTo(quan)
                    .addChildEventListener(locaMenuChildEventListener);
        }

    }
}
