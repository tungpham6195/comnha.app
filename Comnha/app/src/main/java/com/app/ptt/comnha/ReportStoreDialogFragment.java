package com.app.ptt.comnha;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.FireBase.Report;
import com.app.ptt.comnha.SingletonClasses.ReportLocal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportStoreDialogFragment extends DialogFragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    TextView txt_tenquan, txt_sdt, txt_diachi, txt_gia, txt_giomocau;
    EditText edt_tenquan, edt_sdt,
            edt_giamin, edt_giamax;
    AutoCompleteTextView edt_diachi;
    Button btn_timestart, btn_timeend, btn_cancel, btn_ok;
    private TimePickerDialog tpd;
    private Calendar now;
    int edtID, pos;
    int hour, min;
    private DatabaseReference dbRef;
    String prov, dist;
    MyLocation location;
    ProgressDialog mProgressDialog;

    public ReportStoreDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_store_dialog, container, false);
        location = ReportLocal.getInstance().getMyLocation();
        now = Calendar.getInstance();
        prov = ReportLocal.getInstance().getMyLocation().getTinhtp() + "/";
        dist = ReportLocal.getInstance().getMyLocation().getQuanhuyen() + "/";
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://com-nha.firebaseio.com/");
        anhxa(view);
        return view;
    }

    private void anhxa(View view) {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.txt_plzwait));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        txt_tenquan = (TextView) view.findViewById(R.id.frg_reportstore_txttenquan);
        txt_diachi = (TextView) view.findViewById(R.id.frg_reportstore_txtdiachi);
        txt_sdt = (TextView) view.findViewById(R.id.frg_reportstore_txtsdt);
        txt_gia = (TextView) view.findViewById(R.id.frg_reportstore_txtgia);
        txt_giomocau = (TextView) view.findViewById(R.id.frg_reportstore_txtgiomocua);
        edt_diachi = (AutoCompleteTextView) view.findViewById(R.id.frg_reportstore_autocomplete);
        edt_tenquan = (EditText) view.findViewById(R.id.frg_reportstore_edt_tenquan);
        edt_sdt = (EditText) view.findViewById(R.id.frg_reportstore_edt_sdt);
        edt_giamin = (EditText) view.findViewById(R.id.frg_reportstore_edt_giamin);
        edt_giamax = (EditText) view.findViewById(R.id.frg_reportstore_edt_giamax);
        btn_cancel = (Button) view.findViewById(R.id.frg_reportstore_btncancel);
        btn_ok = (Button) view.findViewById(R.id.frg_reportstore_btnok);
        btn_timestart = (Button) view.findViewById(R.id.frg_reportstore_btn_giomo);
        btn_timeend = (Button) view.findViewById(R.id.frg_reportstore_btn_giodong);
        tpd = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), true);
        tpd.setOnDismissListener(this);
        tpd.setOnCancelListener(this);
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_timestart.setOnClickListener(this);
        btn_timeend.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(getResources().getString(R.string.txt_reportStore));
    }

    @Override
    public void onStart() {
        super.onStart();
        String tenquan = "Tên quán: " + ReportLocal.getInstance().getMyLocation().getName(),
                diachi = "Địa chỉ: " + ReportLocal.getInstance().getMyLocation().getDiachi(),
                sdt = "SĐT: " + ReportLocal.getInstance().getMyLocation().getSdt(),
                gia = "Giá: " + ReportLocal.getInstance().getMyLocation().getGiamin() + " - " +
                        ReportLocal.getInstance().getMyLocation().getGiamax(),
                giomocua = "Giờ mở cửa: " +
                        ReportLocal.getInstance().getMyLocation().getTimestart() + " - " +
                        ReportLocal.getInstance().getMyLocation().getTimeend();
        txt_tenquan.setHint(tenquan);
        txt_diachi.setHint(diachi);
        txt_sdt.setHint(sdt);
        txt_gia.setHint(gia);
        txt_giomocau.setHint(giomocua);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_reportstore_btnok:
                if (edt_tenquan.getText().toString().equals("")
                        && edt_diachi.getText().toString().equals("")
                        && edt_sdt.getText().toString().equals("")
                        && edt_giamax.getText().toString().equals("")
                        && edt_giamin.getText().toString().equals("")
                        && btn_timeend.getText().toString().equals("9h")
                        && btn_timestart.getText().toString().equals("9h")) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Bạn không có thay đổi nào cả!!!")
                            .setPositiveButton("Trở lại", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    mProgressDialog.show();
                    Report newReport = new Report();
                    newReport.setLocalID(location.getLocaID());
                    if (edt_tenquan.getText().toString().equals("")) {
                        newReport.setName(location.getName());

                    } else {
                        newReport.setName(edt_tenquan.getText().toString().trim());
                    }
                    if (edt_diachi.getText().toString().equals("")) {
                        newReport.setAddress(location.getDiachi());

                    } else {
                        newReport.setAddress(edt_diachi.getText().toString().trim());
                    }
                    if (edt_sdt.getText().toString().equals("")) {
                        newReport.setSdt(location.getSdt());
                    } else {
                        newReport.setSdt(edt_sdt.getText().toString().trim());
                    }
                    if (edt_giamax.getText().toString().equals("")) {
                        newReport.setGiamax(location.getGiamax());

                    } else {
                        newReport.setGiamax(Long.parseLong(edt_giamax.getText().toString().trim()));
                    }
                    if (edt_giamin.getText().toString().equals("")) {
                        newReport.setGiamin(location.getGiamin());
                    } else {
                        newReport.setGiamin(Long.parseLong(edt_giamin.getText().toString().trim()));
                    }
                    if (btn_timeend.getText().toString().equals(location.getTimeend())) {
                        newReport.setTimeend(location.getTimeend());
                    } else {
                        newReport.setTimeend(btn_timeend.getText().toString());
                    }
                    if (btn_timestart.getText().toString().equals(location.getTimestart())) {
                        newReport.setTimestart(location.getTimestart());
                    } else {
                        newReport.setTimestart(btn_timestart.getText().toString());
                    }
                    Map<String, Object> reportValue = newReport.toMap();
                    final Map<String, Object> updateChild = new HashMap<>();
                    String key = dbRef.child(prov + dist + getString(R.string.reports_CODE))
                            .push().getKey();
                    updateChild.put(prov + dist +
                            getString(R.string.reports_CODE) + key, reportValue);
                    new AlertDialog.Builder(getContext())
                            .setMessage("Bạn có muốn gửi???")
                            .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dbRef.updateChildren(updateChild).addOnCompleteListener(
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isComplete()) {
                                                        mProgressDialog.dismiss();
                                                        Toast.makeText(getContext(), task.getException()
                                                                        .getMessage(),
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        mProgressDialog.dismiss();
                                                        new AlertDialog.Builder(getContext())
                                                                .setMessage("Chúng tôi sẽ xem qua " +
                                                                        "report của bạn")
                                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                        dismiss();
                                                                    }
                                                                })
                                                                .show();
                                                    }
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("Trở lại", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
                break;
            case R.id.frg_reportstore_btncancel:
                new AlertDialog.Builder(getContext())
                        .setMessage("Bạn có muốn hủy???")
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismiss();
                            }
                        })
                        .setNegativeButton("Trở lại", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.frg_reportstore_btn_giomo:
                edtID = R.id.frg_addloction_btn_giomo;
                tpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.frg_reportstore_btn_giodong:
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

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }
}
