package com.app.ptt.comnha;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.LocatlistFilter_rcyler_adapter;
import com.app.ptt.comnha.Classes.RecyclerItemClickListener;
import com.app.ptt.comnha.FireBase.Food;
import com.app.ptt.comnha.FireBase.FoodCategory;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends Fragment implements View.OnClickListener, PickProvinceDialogFragment.OnnPickProvinceListener, PickDistrictDialogFragment.OnPickDistricListener, PickFoodDialogFragment.OnPickFoodListener, PickFoodCategoDialogFragment.PickFoodCategoryListener {

    TextView txt_tinh, txt_quan, txt_mon, txt_loaimon;
    Button btn_tim;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<MyLocation> locaList;
    int whatProvince;
    Food mon = null;
    DatabaseReference dbRef;
    ChildEventListener locaMenuChildEventListener;
    String tinh = "", quan = "";
    String foodCateID;

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
        txt_loaimon = (TextView) view.findViewById(R.id.frg_filter_txtloaimon);
        txt_tinh = (TextView) view.findViewById(R.id.frg_filter_txttinh);
        txt_quan = (TextView) view.findViewById(R.id.frg_filter_txtquan);
        txt_mon = (TextView) view.findViewById(R.id.frg_filter_txtmon);
        btn_tim = (Button) view.findViewById(R.id.frg_filter_btnTim);
        locaList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.frg_filter_recyler);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new LocatlistFilter_rcyler_adapter(locaList);
        mRecyclerView.setAdapter(mAdapter);
        txt_tinh.setOnClickListener(this);
        txt_quan.setOnClickListener(this);
        txt_mon.setOnClickListener(this);
        btn_tim.setOnClickListener(this);
        txt_loaimon.setOnClickListener(this);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String key = locaList.get(position).getLocaID();
                Intent intent = new Intent(getActivity().getApplicationContext(), Adapter2Activity.class);
                intent.putExtra(getResources().getString(R.string.fragment_CODE),
                        getResources().getString(R.string.frag_locadetail_CODE));
                ChooseLoca.getInstance().setHuyen(quan);
                ChooseLoca.getInstance().setLocaID(key);
                ChooseLoca.getInstance().setTinh(tinh);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }));
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
                if (tinh.equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.txt_noChoseProvince), Toast.LENGTH_SHORT).show();
                } else {
                    PickDistrictDialogFragment pickDistrictFrg = new PickDistrictDialogFragment();
                    pickDistrictFrg.setWhatprovince(whatProvince);
                    pickDistrictFrg.show(fm, "fragment_pickDistrict");
                    pickDistrictFrg.setOnPickDistricListener(this);
                }
                break;
            case R.id.frg_filter_txtmon:
                if (tinh.equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.txt_noChoseProvince), Toast.LENGTH_SHORT).show();
                } else if (quan.equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.txt_noChoseDistrict), Toast.LENGTH_SHORT).show();
                } else {
                    PickFoodDialogFragment pickFoodFrg = new PickFoodDialogFragment();
                    pickFoodFrg.setTinh(tinh);
                    pickFoodFrg.setHuyen(quan);
                    pickFoodFrg.setFoodCateID(foodCateID);
                    pickFoodFrg.show(fm, "fragment_pickFood");
                    pickFoodFrg.setOnPickFoodListener(this);
                }
                break;
            case R.id.frg_filter_btnTim:
                if (tinh.equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.txt_noChoseProvince), Toast.LENGTH_SHORT).show();
                } else if (quan.equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.txt_noChoseDistrict), Toast.LENGTH_SHORT).show();
                } else {
                    querySomething();
                }
                break;
            case R.id.frg_filter_txtloaimon:
                if (tinh.equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.txt_noChoseProvince), Toast.LENGTH_SHORT).show();
                } else if (quan.equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.txt_noChoseDistrict), Toast.LENGTH_SHORT).show();
                } else {
                    PickFoodCategoDialogFragment pickFoodCategoDialogFragment = new PickFoodCategoDialogFragment();
                    pickFoodCategoDialogFragment.show(fm, "fragment_pickFoodCategory");
                    pickFoodCategoDialogFragment.setFoodCategoryChildEventListener(this);
                }
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
        whatProvince = position ;
        tinh = province;
    }

    @Override
    public void onPickFood(Food food) {
        txt_mon.setText(food.getTenmon());
        mon = food;
    }

    private void querySomething() {
        locaList.clear();
        mAdapter.notifyDataSetChanged();
        if (mon == null) {
            dbRef.child(tinh + "/" + quan + "/" +
                    getString(R.string.locations_CODE))
                    .addChildEventListener(locaMenuChildEventListener);
        } else {
            dbRef.child(tinh + "/" + quan + "/"
                    + getString(R.string.locations_CODE)).orderByKey()
                    .equalTo(mon.getLocaID())
                    .addChildEventListener(locaMenuChildEventListener);
        }


    }

    @Override
    public void onPickFoodCategory(FoodCategory foodCategory) {
        txt_loaimon.setText(foodCategory.getName());
        foodCateID = foodCategory.getFoodCategoryID();
    }
}
