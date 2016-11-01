package com.app.ptt.comnha;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.ThucDon;
import com.app.ptt.comnha.SingletonClasses.ChooseLoca;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThemMonFragment extends Fragment implements View.OnClickListener {
    FloatingActionButton fab_themMon;
    EditText edt_tenMon, edt_giamon;
    DatabaseReference dbRef;
    private ProgressDialog mProgressDialog;
    TextView txt_tenquan, txt_diachi;

    public ThemMonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.firebase_path));
        View view = inflater.inflate(R.layout.fragment_them_mon, container, false);
        anhxa(view);
        return view;
    }

    private void anhxa(View view) {
        fab_themMon = (FloatingActionButton) view.findViewById(R.id.frg_themMon_btn_save);
        edt_giamon = (EditText) view.findViewById(R.id.frg_themMon_edt_giaMon);
        edt_tenMon = (EditText) view.findViewById(R.id.frg_themMon_edt_tenMon);
        txt_diachi = (TextView) view.findViewById(R.id.frg_themMon_txt_diachi);
        txt_tenquan = (TextView) view.findViewById(R.id.frg_themMon_txt_tenquan);
        txt_tenquan.setText(ChooseLoca.getInstance().getName());
        txt_diachi.setText(ChooseLoca.getInstance().getAddress());
        fab_themMon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_themMon_btn_save:
                if (edt_tenMon.getText().toString().trim().equals("")) {
                    Snackbar.make(v, getResources().getString(R.string.txt_noTenMon), Snackbar.LENGTH_SHORT).show();

                } else if (edt_giamon.getText().toString().trim().equals("")) {
                    Snackbar.make(v, getResources().getString(R.string.txt_nogiaMon), Snackbar.LENGTH_SHORT).show();
                } else {
                    DoSave();
                }
                break;
        }
    }

    private void DoSave() {
        mProgressDialog = ProgressDialog.show(getActivity(),
                getResources().getString(R.string.txt_plzwait),
                getResources().getString(R.string.txt_addinmon), true, false);
        ThucDon newThucDon = new ThucDon();
        newThucDon.setGia(Long.valueOf(edt_giamon.getText().toString()));
        newThucDon.setTenmon(edt_tenMon.getText().toString());
        newThucDon.setLocaID(ChooseLoca.getInstance().getLocaID());
        newThucDon.setLocaName(ChooseLoca.getInstance().getName());
        newThucDon.setDiachi(ChooseLoca.getInstance().getAddress());
        String key = dbRef.child(getResources().getString(R.string.thucdon_CODE)).push().getKey();
        Map<String, Object> thucdonValue = newThucDon.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(getResources().getString(R.string.thucdon_CODE)
                + key, thucdonValue);
        childUpdates.put(getResources().getString(R.string.locathucdon_CODE)
                + ChooseLoca.getInstance().getLocaID() + "/" + key, thucdonValue);
        dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.txt_addedMon), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ChooseLoca.getInstance().setLocaID(null);
        ChooseLoca.getInstance().setName(null);
        ChooseLoca.getInstance().setAddress(null);
    }
}
