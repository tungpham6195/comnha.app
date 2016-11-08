package com.app.ptt.comnha;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.PlaceAPI;
import com.app.ptt.comnha.Modules.PlaceAttribute;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddlocaFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener,
        DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    FloatingActionButton fab_save;
    public static final String LOG = AddlocaFragment.class.getSimpleName();
    ArrayList<PlaceAttribute> mPlaceAttribute;
    EditText edt_tenquan, edt_sdt,
            edt_giamin, edt_giamax;
    Button btn_timestart, btn_timeend;
    ProgressDialog mProgressDialog;
    DatabaseReference dbRef;
    ImageView img_ic;
    Calendar now;
    TimePickerDialog tpd;
    int edtID,pos;
    int hour, min;
    Geocoder gc;
    AutoCompleteTextView autoCompleteText;
    String a = "";

    public AddlocaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addloca, container, false);
        now = Calendar.getInstance();
        gc = new Geocoder(getContext(), Locale.getDefault());
        anhXa(view);
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://com-nha.firebaseio.com/");
        autoCompleteText = (AutoCompleteTextView) view.findViewById(R.id.autocomplete);
        autoCompleteText.setAdapter(new PlaceAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item,1));
        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos=position;
                Log.i(LOG+".onCreateView","pos= "+pos);
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, parent.getItemIdAtPosition(position) + "", Snackbar.LENGTH_SHORT).show();

            }
        });
    }

    void anhXa(View view) {
        edt_tenquan = (EditText) view.findViewById(R.id.frg_addloction_edt_tenquan);
        //edt_diachi = (EditText) view.findViewById(R.id.frg_addloction_edt_diachi);
        btn_timeend = (Button) view.findViewById(R.id.frg_addloction_btn_giodong);
        btn_timestart = (Button) view.findViewById(R.id.frg_addloction_btn_giomo);
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
        btn_timeend.setOnClickListener(this);
        btn_timestart.setOnClickListener(this);
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
                    if (constraint != null) {
                        mPlaceAttribute = new ArrayList<>();
                        mPlaceAttribute = mPlaceAPI.autocomplete(constraint.toString());
                        if (mPlaceAttribute != null) {
                            resultList = new ArrayList<>();
                            for (PlaceAttribute placeAttribute : mPlaceAttribute)
                                resultList.add(placeAttribute.getFullname());
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
    public String returnFullname() {
        String a = mPlaceAttribute.get(pos) .getStreet_number();
        String b = mPlaceAttribute.get(pos) .getRoute();
        String c = mPlaceAttribute.get(pos) .getLocality();
        String d = mPlaceAttribute.get(pos) .getDistrict();
        String f = mPlaceAttribute.get(pos) .getState();
        String e = "";
        if (a != null)
            e += a;
        if (b != null)
            if (a == null)
                e += b;
            else
                e += ", " + b;

        if (c != null)
            if (a == null && b == null)
                e += c;
            else
                e += ", " + c;
        if (d != null)
            if (a == null && b == null && c == null)
                e += d;
            else
                e += ", " + d;
        if (f != null)
            if (a == null && b == null && c == null)
                e += f;
            else
                e += ", " + f;
        return e;
    }

    private void addNewLoca() {
        Log.i(LOG + ".addNewLoca", "Them dia diem");
        MyLocation newLocation = new MyLocation();
        newLocation.setName(edt_tenquan.getText().toString());
        newLocation.setDiachi(returnFullname());
        newLocation.setSdt(edt_sdt.getText().toString());
        newLocation.setTimestart(btn_timestart.getText().toString());
        newLocation.setTimeend(btn_timeend.getText().toString());
        newLocation.setGiamin(Long.valueOf(edt_giamin.getText().toString()));
        newLocation.setGiamax(Long.valueOf(edt_giamax.getText().toString()));
        newLocation.setTinhtp(mPlaceAttribute.get(pos).getState());
        newLocation.setQuanhuyen(mPlaceAttribute.get(pos).getDistrict());
        String key = dbRef.child(getResources().getString(R.string.locations_CODE)).push().getKey();
        Map<String, Object> newLocaValue = newLocation.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(getResources().getString(R.string.locations_CODE)
                + key, newLocaValue);
        mProgressDialog = ProgressDialog.show(getActivity(),
                getResources().getString(R.string.txt_plzwait),
                getResources().getString(R.string.txt_addinloca), true, true);
        dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
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
                } else if (autoCompleteText.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getResources().getText(R.string.txt_nodiachi), Snackbar.LENGTH_SHORT).show();
                } else if (edt_giamin.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getResources().getText(R.string.txt_nogia), Snackbar.LENGTH_SHORT).show();
                } else if (edt_giamax.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getResources().getText(R.string.txt_nogia), Snackbar.LENGTH_SHORT).show();
                } else if (edt_sdt.getText().toString().trim().equals("")) {
                    Snackbar.make(view, getResources().getText(R.string.txt_nosdt), Snackbar.LENGTH_SHORT).show();
                } else if (Long.valueOf(edt_giamax.getText().toString()) <= Long.valueOf(edt_giamin.getText().toString())) {
                    Snackbar.make(view, getResources().getText(R.string.txt_giawarn), Snackbar.LENGTH_SHORT).show();
                } else {


                    if (a != null) {
                        Log.i(LOG + ".onClick", a);
                        if (mPlaceAttribute.get(pos) .getStreet_number() == null)
                            Snackbar.make(view, "Không có số nhà, xin thử lại", Snackbar.LENGTH_SHORT).show();
                        else if (mPlaceAttribute.get(pos) .getRoute() == null)
                            Snackbar.make(view, "Không có tên đường, xin thử lại", Snackbar.LENGTH_SHORT).show();

                        else
                            addNewLoca();
                    } else
                        Snackbar.make(view, "Địa chỉ không hợp lệ. Xin thử lại", Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.frg_addloction_btn_giomo:
                edtID = R.id.frg_addloction_btn_giomo;
                tpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.frg_addloction_btn_giodong:
                edtID = R.id.frg_addloction_btn_giodong;
                tpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        switch (edtID) {
            case R.id.frg_addloction_btn_giomo:
                hour = hourOfDay;
                min = minute;
                break;
            case R.id.frg_addloction_btn_giodong:
                hour = hourOfDay;
                min = minute;
                break;
        }
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
            case R.id.frg_addloction_btn_giomo:
                if (hour > -1) {
                    btn_timestart.setText(hour + "h" + min);
                }
                break;
            case R.id.frg_addloction_btn_giodong:
                if (hour > -1) {
                    btn_timeend.setText(hour + "h" + min);
                }
                break;
        }
    }
}
