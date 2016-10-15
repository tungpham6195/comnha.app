package com.app.ptt.comnha;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.ptt.comnha.Modules.DirectionFinder;
import com.app.ptt.comnha.Modules.DirectionFinderListener;
import com.app.ptt.comnha.Modules.Route;

import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.api.model.StringList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends FragmentActivity  {
    public static final String mBroadcast="mBroadcastComplete";
    private ProgressDialog progressDialog;
    private int progressBarStatus=0;
    private Handler progressBarHandler=new Handler();

    private MyService myService;
    private static final String LOG = "MainActivity";
    private Boolean isBound = false;
    private Button btn_signup, btn_signin, btn_posts, btn_postlist, btn_newloca, btn_map, btn_search, btn_load;
    private Bundle savedInstanceState;
    private IntentFilter mIntentFilter;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    int temp,count;
    int isComplete=0;
    public String userID;
    ArrayList<Route> routes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;
        mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(mBroadcast);

        Log.i(LOG, "onCreate");
        routes = new ArrayList<>();
        anhXa();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    userID = user.getUid();
                    Toast.makeText(getApplicationContext(), "Signed in successfull with " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    LoginSession.getInstance().setUserID(userID);
                    Log.d("signed_in", "onAuthStateChanged:signed_in: " + user.getUid());
                } else {
                    userID = "";
                    LoginSession.getInstance().setUserID(null);
                    Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_SHORT).show();
                    Log.d("signed_out", "onAuthStateChanged:signed_out");
                }
            }
        };


    }
    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(mBroadcast)){
                isComplete=intent.getIntExtra("LoadingComplete",0);
                Log.i(LOG,"isComplete="+isComplete);
            }
        }
    };




    void anhXa() {
        btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_signin = (Button) findViewById(R.id.btn_signin);
        btn_posts = (Button) findViewById(R.id.btn_post);
        btn_postlist = (Button) findViewById(R.id.btn_postlst);
        btn_newloca = (Button) findViewById(R.id.btn_newlocation);
        btn_map = (Button) findViewById(R.id.btn_map);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_load = (Button) findViewById(R.id.btn_load);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frg_signup_CODE));
                startActivity(intent);
            }
        });
        btn_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frag_addpost_CODE));
                startActivity(intent);
            }
        });
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frg_signin_CODE));
                startActivity(intent);
            }
        });
        btn_postlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocatlistFragment locatlistFragment = new LocatlistFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, locatlistFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        btn_newloca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frag_addloca_CODE));
                startActivity(intent);

            }
        });
        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog=new ProgressDialog(v.getContext());
                progressDialog.setCancelable(true);
                progressDialog.setMessage("Loading data");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setProgressStyle(0);
                progressDialog.setMax(100);
                progressDialog.show();
                progressBarStatus=0;
                temp=0;
                count=0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myService.getDataInFireBase();
                        while(progressBarStatus<100) {
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
                                    mapFragment.getMethod(routes);
                                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame, mapFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                                progressDialog.dismiss();

                            }
                        }
                        if(progressBarStatus==101 &&isComplete!=1){
                            showToast("Lỗi! Xin kiểm tra lại");
                        }

                    }

                }).start();

            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment searchFragment = new SearchFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, searchFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public int loadProgress(){
        Log.i(LOG,"Count= "+count);
        Log.i(LOG,"temp= "+temp);
        if(isComplete !=1) {
            if(isComplete==-1)
                return 101;
            if(count<15) {
                count++;
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
            }
            else{
                return 101;
            }

        }
        else {
            return 100;
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        doBinService();
//        gpsService.init();
        Log.i(LOG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isMyServiceRunning(MyService.class)) {
            final Intent intent = new Intent(this, MyService.class);
            startService(intent);
            Log.i("Resume", "Resume");
        }
        registerReceiver(mReceiver,mIntentFilter);
        Log.i(LOG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        doUnbinService();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        Log.i(LOG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isMyServiceRunning(MyService.class)) {
            final Intent intent = new Intent(this, MyService.class);
            stopService(intent);

        }
        unregisterReceiver(mReceiver);
        Log.i(LOG, "Pause");
    }

    public void doBinService() {
        if (!isBound) {
            Intent intent = new Intent(this, MyService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    public void doUnbinService() {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();

            isBound = true;
            Log.i(LOG, "ServiceConnection");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
