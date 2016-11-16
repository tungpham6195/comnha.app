package com.app.ptt.comnha;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.SingletonClasses.DoPost;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by PTT on 10/25/2016.
 */

public class ActivityFragment extends Fragment implements View.OnClickListener, PickDistrictDialogFragment.OnPickDistricListener, PickProvinceDialogFragment.OnnPickProvinceListener {
    FirebaseDatabase database;
    DatabaseReference dbRef;
    TextView txt_tinh, txt_quan;
    SwitchCompat switchCompat;
    PickProvinceDialogFragment pickProvinceDialog;
    PickDistrictDialogFragment pickDistrictDialog;
    int whatProvince;
    FragmentManager fm;
    private String tinh = "", huyen = "";
    ActListFragment actListFragment;
    boolean isStoreView = false;

    public ActivityFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        Log.d("onCreateViewActFrag", "createdView");
        anhxa(view);

        return view;
    }

    void anhxa(View view) {
        fm = getActivity().getSupportFragmentManager();
        pickProvinceDialog = new PickProvinceDialogFragment();
        pickDistrictDialog = new PickDistrictDialogFragment();
        switchCompat = (SwitchCompat) view.findViewById(R.id.frg_activity_viewChangeSwitch);
        txt_tinh = (TextView) view.findViewById(R.id.frg_activity_txttinh);
        txt_quan = (TextView) view.findViewById(R.id.frg_activity_txtquan);
        txt_tinh.setOnClickListener(this);
        txt_quan.setOnClickListener(this);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!tinh.equals("") && !huyen.equals("")) {
                    if (isChecked) {
                        actListFragment = new ActListFragment();
                        switchCompat.setText(getString(R.string.txt_localview));
                        actListFragment.setTinh(tinh);
                        actListFragment.setHuyen(huyen);
                        actListFragment.setFilter(1);
                        actListFragment.setWhatlist(1);
//                        actListFragment.setArguments(getActivity().getIntent().getExtras());
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frg_activity_frame_adapter, actListFragment)
                                .commit();
                        isStoreView = true;
                    } else {
                        isStoreView = false;
                        actListFragment = new ActListFragment();
                        switchCompat.setText(getString(R.string.txt_reviewview));
                        actListFragment.setWhatlist(2);
                        actListFragment.setFilter(1);
                        actListFragment.setTinh(tinh);
                        actListFragment.setHuyen(huyen);
//                        actListFragment.setArguments(getActivity().getIntent().getExtras());
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frg_activity_frame_adapter, actListFragment)
                                .commit();
                    }
                }
            }
        });
        pickProvinceDialog.setOnPickProvinceListener(this);
        pickDistrictDialog.setOnPickDistricListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("onStopFromActFrag", "Stoped");
        DoPost.getInstance().setMyLocation(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_activity_txttinh:
                pickProvinceDialog.show(fm, "fragment_pickProvince");
                break;
            case R.id.frg_activity_txtquan:
                if (tinh.equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.txt_noChoseProvince),
                            Toast.LENGTH_SHORT).show();
                } else {
                    pickDistrictDialog.setWhatprovince(whatProvince);
                    pickDistrictDialog.show(fm, "fragment_pickDistrict");
                }
                break;
        }

    }

    @Override
    public void onPickDistrict(String district) {
        txt_quan.setText(district);
        huyen = district;
        if (!isStoreView) {
            if (getActivity().findViewById(R.id.frg_activity_frame_adapter) != null) {
                actListFragment = new ActListFragment();
                actListFragment.setWhatlist(2);
                actListFragment.setFilter(1);
                actListFragment.setTinh(tinh);
                actListFragment.setHuyen(huyen);
//            actListFragment.setArguments(getActivity().getIntent().getExtras());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frg_activity_frame_adapter, actListFragment)
                        .commit();
            }
        } else {
            if (getActivity().findViewById(R.id.frg_activity_frame_adapter) != null) {
                actListFragment = new ActListFragment();
                actListFragment.setWhatlist(1);
                actListFragment.setFilter(1);
                actListFragment.setTinh(tinh);
                actListFragment.setHuyen(huyen);
//            actListFragment.setArguments(getActivity().getIntent().getExtras());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frg_activity_frame_adapter, actListFragment)
                        .commit();
            }
        }
    }

    @Override
    public void onPickProvince(String province, int position) {
        whatProvince = position;
        txt_tinh.setText(province);
        tinh = province;
    }
}
