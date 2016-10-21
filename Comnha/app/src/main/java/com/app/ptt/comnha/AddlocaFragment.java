package com.app.ptt.comnha;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.github.clans.fab.FloatingActionButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddlocaFragment extends Fragment implements FloatingActionButton.OnClickListener {
    FloatingActionButton fab_save;
    EditText edt_tenquan, edt_diachi, edt_timeend, edt_timestart, edt_sdt, edt_giamin, edt_giamax;
    ProgressDialog mProgressDialog;
    Firebase ref;
    ImageView img_ic;

    public AddlocaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addloca, container, false);
        anhXa(view);
        Firebase.setAndroidContext(getActivity());
        ref = new Firebase("https://com-nha.firebaseio.com/");
        return view;
    }

    void anhXa(View view) {
        edt_tenquan = (EditText) view.findViewById(R.id.frg_addloction_edt_tenquan);
        edt_diachi = (EditText) view.findViewById(R.id.frg_addloction_edt_diachi);
        edt_timeend = (EditText) view.findViewById(R.id.frg_addloction_edt_giodong);
        edt_timestart = (EditText) view.findViewById(R.id.frg_addloction_edt_giomo);
        edt_sdt = (EditText) view.findViewById(R.id.frg_addloction_edt_sdt);
        edt_giamin = (EditText) view.findViewById(R.id.frg_addloction_edt_giamin);
        edt_giamax = (EditText) view.findViewById(R.id.frg_addloction_edt_giamax);
        fab_save = (FloatingActionButton) view.findViewById(R.id.frg_addloction_btn_save);
        img_ic = (ImageView) view.findViewById(R.id.frg_addloca_ic);
        img_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        fab_save.setOnClickListener(this);
    }

    private void addNewLoca() {
        MyLocation newLocation = new MyLocation();
        newLocation.setName(edt_tenquan.getText().toString());
        newLocation.setDiachi(edt_diachi.getText().toString());
        newLocation.setSdt(edt_sdt.getText().toString());
        newLocation.setTimestart(edt_timestart.getText().toString());
        newLocation.setTimeend(edt_timeend.getText().toString());
        newLocation.setGiamin(Long.valueOf(edt_giamin.getText().toString()));
        newLocation.setGiamax(Long.valueOf(edt_giamax.getText().toString()));
        mProgressDialog = ProgressDialog.show(getActivity(),
                getResources().getString(R.string.txt_plzwait),
                getResources().getString(R.string.txt_addinloca), true, true);
        ref.child("Locations").push().setValue(newLocation, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), "Lỗi: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.text_noti_addloca_succes)
                            , Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frg_addloction_btn_save:
                if (edt_tenquan.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getResources().getText(R.string.txt_notenquan), Snackbar.LENGTH_SHORT).show();
                } else if (edt_diachi.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getResources().getText(R.string.txt_nodiachi), Snackbar.LENGTH_SHORT).show();
                } else if (edt_giamin.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getResources().getText(R.string.txt_nogia), Snackbar.LENGTH_SHORT).show();
                } else if (edt_giamax.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getResources().getText(R.string.txt_nogia), Snackbar.LENGTH_SHORT).show();
                } else if (edt_sdt.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getResources().getText(R.string.txt_nosdt), Snackbar.LENGTH_SHORT).show();
                } else if (edt_timestart.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getResources().getText(R.string.txt_noopentime), Snackbar.LENGTH_SHORT).show();
                } else if (edt_timeend.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getResources().getText(R.string.txt_noopentime), Snackbar.LENGTH_SHORT).show();
                } else if (Long.valueOf(edt_giamax.getText().toString()) <= Long.valueOf(edt_giamin.getText().toString())) {
                    Snackbar.make(view, getResources().getText(R.string.txt_giawarn), Snackbar.LENGTH_SHORT).show();
                } else {
                    addNewLoca();
                }
                break;
        }
    }
}
