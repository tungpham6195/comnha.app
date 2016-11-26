package com.app.ptt.comnha;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Adapters.Report_listview_adapter;
import com.app.ptt.comnha.Adapters.Store_listview_adapter;
import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Report;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener, PickProvinceDialogFragment.OnnPickProvinceListener, PickDistrictDialogFragment.OnPickDistricListener {
    TextView txt_prov, txt_dist;
    ListView lv_store, lv_reports;
    ArrayList<MyLocation> myLocations = new ArrayList<>();
    ArrayList<Report> reports = new ArrayList<>();
    DatabaseReference dbRef;
    ChildEventListener storeChildEventListener, reportChildEventListener;
    Store_listview_adapter store_adapter;
    Report_listview_adapter report_adapter;
    String tinh = "", quan = "";
    int whatProvince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.firebase_path));
        txt_prov = (TextView) findViewById(R.id.act_admin_txttinh);
        txt_prov.setOnClickListener(this);
        txt_dist = (TextView) findViewById(R.id.act_admin_txtquan);
        txt_dist.setOnClickListener(this);
        lv_reports = (ListView) findViewById(R.id.act_admin_listvreports);
        lv_store = (ListView) findViewById(R.id.act_admin_listvstore);
        store_adapter = new Store_listview_adapter(myLocations);
        report_adapter = new Report_listview_adapter(reports);
        lv_reports.setAdapter(report_adapter);
        lv_store.setAdapter(store_adapter);
        report_adapter.setOnItemClickListener(new Report_listview_adapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Report report) {

            }
        });
        store_adapter.setOnItemClickLiestner(new Store_listview_adapter.OnItemClickLiestner() {
            @Override
            public void onItemClick(MyLocation myLocation) {
                reports.clear();
                report_adapter.notifyDataSetChanged();
                dbRef.child(tinh + "/" + quan + "/"
                        + getString(R.string.reports_CODE))
                        .orderByChild("localID")
                        .equalTo(myLocation.getLocaID())
                        .addChildEventListener(reportChildEventListener);
                AnimationUtils.animatMoveListForward(lv_store, lv_reports);
            }
        });
        storeChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyLocation myLocation = dataSnapshot.getValue(MyLocation.class);
                myLocation.setLocaID(dataSnapshot.getKey());
                myLocations.add(myLocation);
                store_adapter.notifyDataSetChanged();
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
        reportChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Report report = dataSnapshot.getValue(Report.class);
                report.setReportID(dataSnapshot.getKey());
                reports.add(report);
                report_adapter.notifyDataSetChanged();
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
    }

    @Override
    public void onBackPressed() {

        if (lv_store.getTranslationX() < 0) {
            dbRef.removeEventListener(reportChildEventListener);
            reports.clear();
            report_adapter.notifyDataSetChanged();
            AnimationUtils.animatMoveListForback(lv_store, lv_reports);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_admin_txttinh:
                PickProvinceDialogFragment pickProvinceFrg = new PickProvinceDialogFragment();
                pickProvinceFrg.show(getSupportFragmentManager(), "fragment_pickProvince");
                pickProvinceFrg.setOnPickProvinceListener(this);
                break;
            case R.id.act_admin_txtquan:
                if (tinh.equals("")) {
                    Toast.makeText(this, getString(R.string.txt_noChoseProvince), Toast.LENGTH_SHORT).show();
                } else {
                    PickDistrictDialogFragment pickDistrictFrg = new PickDistrictDialogFragment();
                    pickDistrictFrg.setWhatprovince(whatProvince);
                    pickDistrictFrg.show(getSupportFragmentManager(), "fragment_pickDistrict");
                    pickDistrictFrg.setOnPickDistricListener(this);
                }
                break;
        }
    }

    @Override
    public void onPickProvince(String province, int position) {
        txt_prov.setText(province);
        whatProvince = position;
        tinh = province;
    }

    @Override
    public void onPickDistrict(String district) {
        txt_dist.setText(district);
        quan = district;
        myLocations.clear();
        store_adapter.notifyDataSetChanged();
        dbRef.child(tinh + "/" + quan + "/"
                + getString(R.string.locations_CODE))
                .addChildEventListener(storeChildEventListener);
    }
}
