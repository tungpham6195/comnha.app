package com.app.ptt.comnha;

import android.app.ActivityManager;
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
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Modules.Route;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String mBroadcast = "mBroadcastComplete";
    private ProgressDialog progressDialog;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();

    private MyService myService;
    private static final String LOG = "MainActivity2";
    private Boolean isBound = false;
    private Button btn_posts, btn_postlist, btn_newloca, btn_map, btn_search, btn_load;
    private Bundle savedInstanceState;
    private IntentFilter mIntentFilter;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    int fileSize;
    boolean isComplete = false;
    public String userID;
    ArrayList<Route> routes;

    private Toolbar mtoolbar;
    private DrawerLayout mdrawer;
    private ActionBarDrawerToggle mtoggle;
    private NavigationView mnavigationView;
    TextView txt_email, txt_un;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);

        mdrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mtoggle = new ActionBarDrawerToggle(
                this, mdrawer, mtoolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mdrawer.setDrawerListener(mtoggle);
        mtoggle.syncState();

        mnavigationView = (NavigationView) findViewById(R.id.nav_view);
        mnavigationView.setNavigationItemSelectedListener(this);

        this.savedInstanceState = savedInstanceState;
        mIntentFilter = new IntentFilter();
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
//                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                    if (drawer.isDrawerOpen(GravityCompat.START)) {
//                        Log.i("drawopend", "opened");
//                    }
                } else {
                    userID = "";
                    LoginSession.getInstance().setUserID(null);
                    Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_SHORT).show();
                    Log.d("signed_out", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    void anhXa() {
        txt_email = (TextView) findViewById(R.id.nav_head_email);
//        btn_posts = (Button) findViewById(R.id.btn_post);
//        btn_postlist = (Button) findViewById(R.id.btn_postlst);
//        btn_newloca = (Button) findViewById(R.id.btn_newlocation);
        btn_map = (Button) findViewById(R.id.btn_map);
//        btn_search = (Button) findViewById(R.id.btn_search);
//        btn_load = (Button) findViewById(R.id.btn_load);

//        btn_posts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, AdapterActivity.class);
//                intent.putExtra(getString(R.string.fragment_CODE),
//                        getString(R.string.frag_addpost_CODE));
//                startActivity(intent);
//            }
//        });
//        btn_postlist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LocatlistFragment locatlistFragment = new LocatlistFragment();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.frame, locatlistFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });
//        btn_newloca.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, AdapterActivity.class);
//                intent.putExtra(getString(R.string.fragment_CODE),
//                        getString(R.string.frag_addloca_CODE));
//                startActivity(intent);
//
//            }
//        });
//        btn_load.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setCancelable(true);
                progressDialog.setMessage("Loading data");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setProgressStyle(0);
                progressDialog.setMax(100);
                progressDialog.show();
                progressBarStatus = 0;
                fileSize = 0;
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
                                if (isComplete == true) {
                                    routes = myService.returnRoute();
                                    if (routes == null) {
                                        Toast.makeText(Main2Activity.this, "Khong load dc dia diem", Toast.LENGTH_LONG).show();
                                    } else {
                                        MapFragment mapFragment = new MapFragment();
                                        mapFragment.getMethod(routes);
                                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.frame, mapFragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    }
                                }
                                progressDialog.dismiss();
                            }


                        }
                    }
                }).start();


            }
        });
//
//        btn_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SearchFragment searchFragment = new SearchFragment();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.frame, searchFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });
    }

    public int loadProgress() {
        if (!isComplete) {
            while (fileSize <= 1000000) {
                fileSize++;
                if (fileSize == 100000) {
                    return 10;
                } else if (fileSize == 200000) {
                    return 20;
                } else if (fileSize == 300000) {
                    return 30;
                } else if (fileSize == 400000) {
                    return 40;
                } else if (fileSize == 500000) {
                    return 50;
                } else if (fileSize == 700000) {
                    return 70;
                } else if (fileSize == 800000) {
                    return 80;
                }
            }
            return 0;
        } else {
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
        registerReceiver(mReceiver, mIntentFilter);
        Log.i(LOG, "onResume");
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcast)) {
                isComplete = intent.getBooleanExtra("LoadingComplete", false);
                Log.i(LOG, "isComplete=" + isComplete);
            }
        }
    };

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_profile:
                break;
            case R.id.nav_homepage:
                LocatlistFragment locatlistFragment = new LocatlistFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, locatlistFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.nav_uploadedimg:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
            case R.id.nav_signin:
                Intent intent = new Intent(Main2Activity.this, AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frg_signin_CODE));
                startActivity(intent);
                break;
            case R.id.nav_signout:
                mAuth.signOut();
                LoginSession.getInstance().setUserID("");
                break;
        }
        mdrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mdrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
