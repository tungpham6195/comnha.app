package com.app.ptt.comnha;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.PlaceAPI;
import com.app.ptt.comnha.Modules.Route;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddlocaFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener,
        DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    FloatingActionButton fab_save;
    EditText edt_tenquan, edt_diachi, edt_timeend, edt_timestart, edt_sdt, edt_giamin, edt_giamax;
    ImageButton imageButton;
    ProgressDialog mProgressDialog;
    Firebase ref;
    ImageView img_ic;
    Calendar now;
    TimePickerDialog tpd;
    int edtID;
    int hour, min;
    Geocoder gc;
    AutoCompleteTextView autoCompleteText;
    public AddlocaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addloca, container, false);
        now = Calendar.getInstance();
         gc=new Geocoder(getContext(), Locale.getDefault());
        anhXa(view);
        Firebase.setAndroidContext(getActivity());
        ref = new Firebase("https://com-nha.firebaseio.com/");
        autoCompleteText=(AutoCompleteTextView) view.findViewById(R.id.autocomplete);
        autoCompleteText.setAdapter(new PlacesAutoCompleteAdapter(getActivity(),R.layout.autocomplete_list_item));
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
           autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view,parent.getItemIdAtPosition(position)+"", Snackbar.LENGTH_SHORT).show();

            }
        });
    }

    void anhXa(View view) {
        imageButton=(ImageButton) view.findViewById(R.id.iv_check_location);
        edt_tenquan = (EditText) view.findViewById(R.id.frg_addloction_edt_tenquan);
        //edt_diachi = (EditText) view.findViewById(R.id.frg_addloction_edt_diachi);
        edt_timeend = (EditText) view.findViewById(R.id.frg_addloction_edt_giodong);
        edt_timestart = (EditText) view.findViewById(R.id.frg_addloction_edt_giomo);
        edt_sdt = (EditText) view.findViewById(R.id.frg_addloction_edt_sdt);
        edt_giamin = (EditText) view.findViewById(R.id.frg_addloction_edt_giamin);
        edt_giamax = (EditText) view.findViewById(R.id.frg_addloction_edt_giamax);
        fab_save = (FloatingActionButton) view.findViewById(R.id.frg_addloction_btn_save);
        img_ic = (ImageView) view.findViewById(R.id.frg_addloca_ic);
        tpd = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), true);
        tpd.setOnDismissListener(this);
        tpd.setOnCancelListener(this);
        img_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        fab_save.setOnClickListener(this);
        edt_timestart.setOnClickListener(this);
        edt_timeend.setOnClickListener(this);
        imageButton.setOnClickListener(this);
    }
    class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        ArrayList<String> resultList;
        Context mContext;
        int mResource;
        PlaceAPI mPlaceAPI=new PlaceAPI();
        public PlacesAutoCompleteAdapter(Context context, int resource) {
            super(context, resource);
            mContext=context;
            mResource=resource;
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return resultList.get(position);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            final Filter filter=new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults= new FilterResults();
                    if(constraint!=null){
                        resultList=mPlaceAPI.autocomplete(constraint.toString(),getContext());
                        filterResults.values=resultList;
                        filterResults.count=resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if(results!=null &&results.count>0){
                        notifyDataSetChanged();
                    } else{
                        notifyDataSetInvalidated();
                    }

                }

            };
            return filter;
        }
    }

    private void addNewLoca() {
        MyLocation newLocation = new MyLocation();
        newLocation.setName(edt_tenquan.getText().toString());
        newLocation.setDiachi(autoCompleteText.getText().toString());
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
        String yourLocation=null;
        switch (view.getId()) {
            case R.id.frg_addloction_btn_save:
                if (edt_tenquan.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getResources().getText(R.string.txt_notenquan), Snackbar.LENGTH_SHORT).show();
                } else if (autoCompleteText.getText().toString().trim().equals("")) {
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


                    try {
                        Snackbar.make(view, autoCompleteText.getText()+" T thu ti duoc khong?", Snackbar.LENGTH_SHORT).show();
                        List<Address> list=gc.getFromLocationName(autoCompleteText.getText().toString(),1);
                        if(list.size()>0) {
                            Address address = list.get(0);
                            yourLocation = checkPlaceInput(address);
                        }
                        else{
                            Snackbar.make(view, "Địa chỉ không hợp lệ. Xin thử lại", Snackbar.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(yourLocation!="") {
                        addNewLoca();

                    } else{
                        Snackbar.make(view, "Địa chỉ không hợp lệ. Xin thử lại", Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.frg_addloction_edt_giomo:
                edtID = R.id.frg_addloction_edt_giomo;
                tpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.frg_addloction_edt_giodong:
                edtID = R.id.frg_addloction_edt_giodong;
                tpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        switch (edtID) {
            case R.id.frg_addloction_edt_giomo:
                hour = hourOfDay;
                min = minute;
                break;
            case R.id.frg_addloction_edt_giodong:
                hour = hourOfDay;
                min = minute;
                break;
        }
    }
    public String checkPlaceInput(Address address){
        String e = "";
        if(address!=null) {
            String a = address.getAddressLine(0);
            String b = address.getSubLocality();
            String c = address.getSubAdminArea();
            String d = address.getAdminArea();

            if (a != "") {
                e += a;
                if (b != "") {
                    if (a != "")
                        e += ", " + b;
                    else
                        e += b;
                }
                if (c != "") {
                    if ((a != "" || b != ""))
                        e += ", " + c;
                    else {
                        e += c;
                    }
                    if (d != "") {
                        if ((a != "" || b != "" || c != ""))
                            e += ", " + d;
                        else
                            e += d;
                    }
                }
            }
        }
        return e;
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        hour = -1;
        Log.d("cancel" + String.valueOf(edtID), String.valueOf(hour));
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Log.d("dismiss" + String.valueOf(edtID), String.valueOf(hour));
        switch (edtID) {
            case R.id.frg_addloction_edt_giomo:
                if (hour > -1) {
                    edt_timestart.setText(hour + "h" + min);
                }
                break;
            case R.id.frg_addloction_edt_giodong:
                if (hour > -1) {
                    edt_timeend.setText(hour + "h" + min);
                }
                break;
        }
    }
}
