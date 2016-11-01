package com.app.ptt.comnha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcast);
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
//        Log.i(LOG, "openMap");
//        try{
//            routes = myService.returnRoute();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        if (routes== null) {
//            progressDialog = new ProgressDialog(this);
//            progressDialog.setCancelable(true);
//            progressDialog.setMessage("Loading data");
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressDialog.setProgressStyle(0);
//            progressDialog.setMax(100);
//            progressDialog.show();
//            progressBarStatus = 0;
//            temp = 0;
//            count = 0;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    myService.getDataInFireBase();
//                    while (progressBarStatus < 100) {
//                        progressBarStatus = loadProgress();
//                        try {
//
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        progressBarHandler.post(new Runnable() {
//                            public void run() {
//                                progressDialog.setProgress(progressBarStatus);
//                            }
//
//                        });
//                        if (progressBarStatus >= 100) {
//                            try {
//                                Thread.sleep(2000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            if (isComplete == 1) {
//                                routes = myService.returnRoute();
//                                MapFragment mapFragment = new MapFragment();
//                                mapFragment.getMethod(routes);
//                                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, mapFragment).commit();
//
//                            }
//                            progressDialog.dismiss();
//                        }
//
//                    }
//
//                    if (progressBarStatus == 101 && isComplete != 1)
//                        showToast("Lỗi rồi");
//                }
//            }).start();
//            if (progressBarStatus == 101 && isComplete != 1)
//                showToast("Lỗi rồi");
//
//        } else {
//            MapFragment mapFragment = new MapFragment();
//            mapFragment.getMethod(myService.returnRoute());
//            getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, mapFragment).commit();
        //   }
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
                    if (!ConnectionDetector.canGetLocation(this)) {
                        if (!ConnectionDetector.networkStatus(this)) {
                            Toast.makeText(getApplicationContext(), "Không có kết nối internet và gps", Toast.LENGTH_LONG).show();
                        } else {
                            ConnectionDetector.showSettingAlert(this);
                        }
                    } else {
                        if (!ConnectionDetector.networkStatus(this)) {
                            Toast.makeText(getApplicationContext(), "Không có kết nối internet", Toast.LENGTH_LONG).show();

                        } else {
                            openMap();
                        }
                    }
                }
            }

        }
    }

//    public int loadProgress() {
//        Log.i(LOG, "count= " + count);
//        Log.i(LOG, "temp= " + temp);
//        if (isComplete != 1) {
//            if (count < 60) {
//                count++;
//                if (isComplete == -1)
//                    return 101;
//                while (temp <= 1000000) {
//                    temp++;
//
//                    if (temp == 100000) {
//                        return 10;
//                    } else if (temp == 200000) {
//                        return 20;
//                    } else if (temp == 300000) {
//                        return 30;
//                    } else if (temp == 400000) {
//                        return 40;
//                    } else if (temp == 500000) {
//                        return 50;
//                    } else if (temp == 700000) {
//                        return 70;
//                    } else if (temp == 800000) {
//                        return 80;
//                    }
//                }
//                return 0;
//            } else {
//                return 101;
//            }
//        } else {
//            return 100;
//        }
//    }

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

