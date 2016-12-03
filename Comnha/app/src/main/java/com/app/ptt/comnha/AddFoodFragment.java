package com.app.ptt.comnha;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.Food;
import com.app.ptt.comnha.FireBase.FoodCategory;
import com.app.ptt.comnha.Service.MyService;
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
public class AddFoodFragment extends Fragment implements View.OnClickListener, PickFoodCategoDialogFragment.PickFoodCategoryListener {
    private static final String LOG=AddFoodFragment.class.getSimpleName();
    FloatingActionButton fab_themMon;
    EditText edt_tenMon, edt_giamon;
    DatabaseReference dbRef;
    private ProgressDialog mProgressDialog;
    TextView txt_tenquan, txt_diachi;
    Button btn_chooseCatego;
    private String foodCategoID = null, locaID, tinh, huyen;
    PickFoodCategoDialogFragment pickFoodDialog;
    FragmentManager dialogFm;
    boolean isConnected=true;
    IntentFilter mIntentFilter;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(mBroadcastSendAddress)) {
                Log.i(LOG+".onReceive form Service","isConnected= "+ intent.getBooleanExtra("isConnected", false));
                if (intent.getBooleanExtra("isConnected", false)) {
                    isConnected = true;
                } else
                    isConnected = false;
            }
        }
    };
    public AddFoodFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        isConnected= MyService.returnIsConnected();
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(
                getResources().getString(R.string.firebase_path));
        tinh = ChooseLoca.getInstance().getTinh() + "/";
        huyen = ChooseLoca.getInstance().getHuyen() + "/";
        locaID = ChooseLoca.getInstance().getLocaID();
        View view = inflater.inflate(R.layout.fragment_addfood, container, false);
        anhxa(view);
        return view;
    }

    private void anhxa(View view) {
        dialogFm = getActivity().getSupportFragmentManager();
        pickFoodDialog = new PickFoodCategoDialogFragment();
        btn_chooseCatego = (Button) view.findViewById(R.id.frg_addfood_btnchoosecatego);
        fab_themMon = (FloatingActionButton) view.findViewById(R.id.frg_themMon_btn_save);
        edt_giamon = (EditText) view.findViewById(R.id.frg_themMon_edt_giaMon);
        edt_tenMon = (EditText) view.findViewById(R.id.frg_themMon_edt_tenMon);
        txt_diachi = (TextView) view.findViewById(R.id.frg_themMon_txt_diachi);
        txt_tenquan = (TextView) view.findViewById(R.id.frg_themMon_txt_tenquan);
        txt_tenquan.setText(ChooseLoca.getInstance().getName());
        txt_diachi.setText(ChooseLoca.getInstance().getAddress());
        fab_themMon.setOnClickListener(this);
        btn_chooseCatego.setOnClickListener(this);
        pickFoodDialog.setFoodCategoryChildEventListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_themMon_btn_save:
                if (edt_tenMon.getText().toString().trim().equals("")) {
                    Snackbar.make(v, getResources().getString(R.string.txt_noTenMon), Snackbar.LENGTH_SHORT).show();

                } else if (edt_giamon.getText().toString().trim().equals("")) {
                    Snackbar.make(v, getResources().getString(R.string.txt_nogiaMon), Snackbar.LENGTH_SHORT).show();
                } else if (foodCategoID == null) {
                    Snackbar.make(v, getResources().getString(R.string.txt_noChoseFoodCate), Snackbar.LENGTH_SHORT).show();
                } else {
                    if(isConnected)
                    DoSave();
                    else Toast.makeText(getContext(), "You are offline", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.frg_addfood_btnchoosecatego:
                pickFoodDialog.show(dialogFm, "fragment_pickFoodCategory");
                break;
        }
    }

    private void DoSave() {
        mProgressDialog = ProgressDialog.show(getActivity(),
                getResources().getString(R.string.txt_plzwait),
                getResources().getString(R.string.txt_addinmon), true, false);
        Food newFood = new Food();
        newFood.setGia(Long.valueOf(edt_giamon.getText().toString()));
        newFood.setTenmon(edt_tenMon.getText().toString());
        newFood.setLocaID(locaID);
        newFood.setFoodCategoID(foodCategoID);
        String key = dbRef.child(getResources().getString(R.string.thucdon_CODE)).push().getKey();
        Map<String, Object> thucdonValue = newFood.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(tinh + huyen +
                getResources().getString(R.string.thucdon_CODE)
                + key, thucdonValue);
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
    public void onStart() {
        super.onStart();
        isConnected= MyService.returnIsConnected();
        if(!isConnected){
            Toast.makeText(getContext(),"Offline mode",Toast.LENGTH_SHORT).show();
        }
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
        getContext().registerReceiver(broadcastReceiver,mIntentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(broadcastReceiver);
        ChooseLoca.getInstance().setLocaID(null);
        ChooseLoca.getInstance().setName(null);
        ChooseLoca.getInstance().setAddress(null);
        ChooseLoca.getInstance().setTinh(null);
        ChooseLoca.getInstance().setHuyen(null);
    }

    @Override
    public void onPickFoodCategory(FoodCategory foodCategory) {
        btn_chooseCatego.setHint(foodCategory.getName());
        foodCategoID = foodCategory.getFoodCategoryID();
    }
}
