package com.app.ptt.comnha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.ConnectionDetector;
import com.app.ptt.comnha.Modules.Route;

import java.util.ArrayList;

public class AdapterActivity extends AppCompatActivity {
    String locaKey;
    public static final String LOG = "AdapterActivity";
    public static final String mBroadcast = "mBroadcastComplete";
    private ProgressDialog progressDialog;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();

    int temp, count;
    Boolean isBound = false;
    private IntentFilter mIntentFilter;
    boolean isComplete;
    Bundle savedInstanceState;
    ArrayList<Route> routes;

    public AdapterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter);
        Log.i(LOG, "onCreate");
        //mIntentFilter = new IntentFilter();
        //mIntentFilter.addAction(mBroadcast);
        loadData();
    }


    @Override
    public void finish() {
        Log.i(LOG, "finish");
        super.finish();
    }


    public void showToast(final String a) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AdapterActivity.this, a, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openMap() {

        MapFragment mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, mapFragment).commit();
    }

    public void loadData() {
        Intent intent = getIntent();
        String FRAGMENT_CODE = intent.getExtras().getString(getResources().getString(R.string.fragment_CODE));
        if (FRAGMENT_CODE.equals(getString(R.string.frag_map_CODE))) {
            Log.i(LOG, "frag_map_CODE");
            if (findViewById(R.id.frame_adapter) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter) == null) {
                    try{
                        openMap();
                    } catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Xảy ra lỗi. Xin kiểm tra lại",Toast.LENGTH_LONG).show();
                    }


                }
            }

        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG, "onPause");

    }

    @Override
    protected void onStart() {
        Log.i(LOG, "onStart");
        super.onStart();


    }


    @Override
    protected void onStop() {
        Log.i(LOG, "onStop");
        super.onStop();
    }

}

