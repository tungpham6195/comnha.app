package com.app.ptt.comnha;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.ConnectionDetector;
import com.app.ptt.comnha.Modules.Route;
import com.app.ptt.comnha.Service.MyService;

import java.io.IOException;
import java.util.ArrayList;

public class AdapterActivity extends AppCompatActivity implements ChooselocaFragment.onPassDatafromChooseLocaFrg {
    String locaKey;
    public static final String LOG = "AdapterActivity";
    public static final String mBroadcast = "mBroadcastComplete";
    private ProgressDialog progressDialog;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    MyService myService;
    int temp, count;
    private IntentFilter mIntentFilter;
    int isComplete = 0;
    Bundle savedInstanceState;
    ArrayList<Route> routes;

    public AdapterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        Log.i(LOG, "onCreate");
        doBindService();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcast);
        registerReceiver(mReceiver, mIntentFilter);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter);

    }

    public void doBindService() {
        Log.i(LOG, "doBindService");
        bindService(new Intent(this, MyService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void doUnbindService() {
        Log.i(LOG, "doUnbindService");
        unbindService(serviceConnection);
    }

    @Override
    public void finish() {
        Log.i(LOG, "finish");
        super.finish();
        doUnbindService();
        unregisterReceiver(mReceiver);

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcast)) {
                isComplete = intent.getIntExtra("LoadingComplete", 0);
                Log.i(LOG, "isComplete=" + isComplete);
            }
        }
    };
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = ((MyService.LocalBinder) service).getService();
            if (myService == null) {
                Log.i(LOG, "CCCCCCCCCCCCCCCCCCCCCCCCCCCc");
            } else {
                loadData(savedInstanceState);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService = null;
        }
    };

    public void showToast(final String a) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AdapterActivity.this, a, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openMap() {
        Log.i(LOG, "openMap");
        if (routes == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Loading data");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgressStyle(0);
            progressDialog.setMax(100);
            progressDialog.show();
            progressBarStatus = 0;
            temp = 0;
            count = 0;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    myService.getDataInFireBase();
                    while (progressBarStatus < 100) {
                        progressBarStatus = loadProgress();
                        try {

                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        progressBarHandler.post(new Runnable() {
                            public void run() {
                                progressDialog.setProgress(progressBarStatus);
                            }

                        });
                        if (progressBarStatus >= 100) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (isComplete == 1) {
                                routes = myService.returnRoute();
                                MapFragment mapFragment = new MapFragment();
                                mapFragment.getRoute(routes);
                                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, mapFragment).commit();

                            }
                            progressDialog.dismiss();
                        }

                    }
                    if (progressBarStatus == 101 && isComplete != 1)
                        showToast("Lỗi rồi");
                }
            }).start();


        } else {

            MapFragment mapFragment = new MapFragment();
            mapFragment.getRoute(routes);
            getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, mapFragment).commit();
        }
    }

    public void loadData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String FRAGMENT_CODE = intent.getExtras().getString(getResources().getString(R.string.fragment_CODE));
        Log.d("FRAGMENT_CODE", FRAGMENT_CODE);
        if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_localist_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                LocatlistFragment locatlistFragment = new LocatlistFragment();
                locatlistFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, locatlistFragment).commit();
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_addloca_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                AddlocaFragment addlocaFragment = new AddlocaFragment();
                addlocaFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, addlocaFragment).commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_locadetail_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                LocadetailFragment locadetailFragment = new LocadetailFragment();
                locadetailFragment.setLocaID(intent.getStringExtra(getResources().getString(R.string.key_CODE)));
                locadetailFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, locadetailFragment).commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_chooseloca_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {
                    ChooselocaFragment chooselocaFragment = new ChooselocaFragment();
                    chooselocaFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_adapter, chooselocaFragment)
                            .commit();
                } else {

                }
                ChooselocaFragment chooselocaFragment = new ChooselocaFragment();
                chooselocaFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, chooselocaFragment)
                        .commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_addpost_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                PostFragment postFragment = new PostFragment();
                postFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, postFragment)
                        .commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_viewpost_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                ViewpostFragment viewpostFragment = new ViewpostFragment();
                viewpostFragment.setPostID(intent.getExtras().getString(getString(R.string.key_CODE)));
                viewpostFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, viewpostFragment)
                        .commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_signin_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                SigninFragment signinFragment = new SigninFragment();
                signinFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, signinFragment)
                        .commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_signup_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                SignupFragment signupFragment = new SignupFragment();
                signupFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, signupFragment)
                        .commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_chooseimg_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                ChoosePhotoFragment choosePhotoFragment = new ChoosePhotoFragment();
                choosePhotoFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, choosePhotoFragment)
                        .commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_vote_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                VoteFragment voteFragment = new VoteFragment();
                voteFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, voteFragment)
                        .commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_map_CODE))) {
            Log.i(LOG, "frag_map_CODE");
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {
                }
                if (!ConnectionDetector.canGetLocation(this)) {
                    if (!ConnectionDetector.networkStatus(this)) {
                        Toast.makeText(getApplicationContext(), "Không có kết nối internet và gps", Toast.LENGTH_LONG).show();
                    } else {
                        ConnectionDetector.showSettingAlert(this);
                    }
                    routes = null;
                } else {
                    if (!ConnectionDetector.networkStatus(this)) {
                        Toast.makeText(getApplicationContext(), "Không có kết nối internet", Toast.LENGTH_LONG).show();
                        routes = null;
                    } else {
                        openMap();
                    }
                }


            }

        }
    }

    public int loadProgress() {
        Log.i(LOG, "count= " + count);
        Log.i(LOG, "temp= " + temp);
        if (isComplete != 1) {
            if (count < 60) {
                count++;

                if (isComplete == -1)
                    return 101;
                while (temp <= 1000000) {
                    temp++;

                    if (temp == 100000) {
                        return 10;
                    } else if (temp == 200000) {
                        return 20;
                    } else if (temp == 300000) {
                        return 30;
                    } else if (temp == 400000) {
                        return 40;
                    } else if (temp == 500000) {
                        return 50;
                    } else if (temp == 700000) {
                        return 70;
                    } else if (temp == 800000) {
                        return 80;
                    }
                }
                return 0;
            } else {
                return 101;
            }
        } else {
            return 100;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG,"onPause");
//        failed startActitityforResult
//        Intent intent1 = new Intent();
//        intent1.putExtra("result", locaKey);
//        setResult(Activity.RESULT_OK, intent1);
//        Log.d("intent",intent1.getStringExtra("result"));
//        Toast.makeText(getApplicationContext(), "from adapter: "+locaKey, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void passData(String data) {
        locaKey = data;
    }
}
