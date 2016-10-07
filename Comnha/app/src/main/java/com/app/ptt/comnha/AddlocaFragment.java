package com.app.ptt.comnha;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.app.ptt.comnha.Classes.Locations;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddlocaFragment extends Fragment {
    Button btn_save;
    EditText edt_tenquan, edt_diachi, edt_timeend, edt_timestart, edt_sdt, edt_giamin, edt_giamax;

    public AddlocaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addloca, container, false);
        anhXa(view);
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
        btn_save = (Button) view.findViewById(R.id.frg_addloction_btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locations newLocation = new Locations(getActivity().getApplicationContext());
                newLocation.setName(edt_tenquan.getText().toString());
                newLocation.setDiachi(edt_diachi.getText().toString());
                newLocation.setSdt(edt_sdt.getText().toString());
                newLocation.setTimestart(edt_timestart.getText().toString());
                newLocation.setTimeend(edt_timeend.getText().toString());
                newLocation.setGiamax(Long.valueOf(edt_giamax.getText().toString()));
                newLocation.setGiamin(Long.valueOf(edt_giamin.getText().toString()));
                newLocation.createNew();
                getActivity().finish();
            }
        });
    }
}
