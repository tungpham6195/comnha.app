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
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Modules.ConnectionDetector;
import com.app.ptt.comnha.Service.MyService;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.firebase.client.Firebase;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , FloatingActionButton.OnClickListener {


    private MyService myService;
    private static final String LOG = MainActivity.class.getSimpleName();
    private Boolean isBound = false;
    private Bundle savedInstanceState;
    public static final String mBroadcast = "mBroadcastComplete";
    private ProgressDialog progressDialog;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private IntentFilter mIntentFilter;

    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private int fileSize;
    public String userID, username, email;

    private Toolbar mtoolbar;
    private DrawerLayout mdrawer;
    private ActionBarDrawerToggle mtoggle;
    private NavigationView mnavigationView;
    private TextView txt_email, txt_un;
    private FloatingActionMenu fabmenu;
    private boolean checkConnection = true;
    private FloatingActionButton fab_review, fab_addloca, fab_uploadpho;
    private Firebase ref;
    private BottomBar bottomBar;
    private PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Firebase.setAndroidContext(this);
        ref = new Firebase(getResources().getString(R.string.firebase_path));
        anhXa();

        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcast);
        mdrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mtoggle = new ActionBarDrawerToggle(
                this, mdrawer, mtoolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mdrawer.setDrawerListener(mtoggle);
        mtoggle.syncState();
        mnavigationView = (NavigationView) findViewById(R.id.nav_view);
        mnavigationView.setNavigationItemSelectedListener(this);
        View header = mnavigationView.getHeaderView(0);
        txt_email = (TextView) header.findViewById(R.id.nav_head_email);
        txt_un = (TextView) header.findViewById(R.id.nav_head_username);
        this.savedInstanceState = savedInstanceState;

        Log.i(LOG, "onCreate");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userID = user.getUid();
//                    Toast.makeText(getApplicationContext(), "Signed in successfull with " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    LoginSession.getInstance().setUserID(userID);
                    LoginSession.getInstance().setEmail(user.getEmail());
                    try {
                        LoginSession.getInstance().setUsername(user.getDisplayName());
                        txt_email.setText(user.getEmail());
                        txt_un.setText(user.getDisplayName());
                    } catch (NullPointerException mess) {

                    }
                    Log.d("signed_in", "onAuthStateChanged:signed_in: " + user.getUid());
                    Log.i("email", LoginSession.getInstance().getEmail());
                } else {
                    txt_email.setText(getResources().getString(R.string.text_hello));
                    txt_un.setText(getResources().getString(R.string.text_user));
                    userID = "";
                    LoginSession.getInstance().setUserID(null);
                    LoginSession.getInstance().setUsername(null);
                    LoginSession.getInstance().setEmail(null);
                    Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_SHORT).show();
                    Log.d("signed_out", "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcast)) {
                checkConnection = intent.getBooleanExtra("LocationError", true);
                Log.i(LOG, "checkConnection=" + checkConnection);
                if (!checkConnection) {
                    if (!ConnectionDetector.canGetLocation(MainActivity.this)) {
                        ConnectionDetector.showSettingAlert(MainActivity.this);
                    }
                }
            }
        }
    };

    void anhXa() {
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        fabmenu = (FloatingActionMenu) findViewById(R.id.main_fabMenu);
        fab_review = (FloatingActionButton) findViewById(R.id.main_fabitem3);
        fab_addloca = (FloatingActionButton) findViewById(R.id.main_fabitem2);
        fab_uploadpho = (FloatingActionButton) findViewById(R.id.main_fabitem1);
        fab_review.setOnClickListener(this);
        fab_addloca.setOnClickListener(this);
        fab_uploadpho.setOnClickListener(this);
        fabmenu.setClosedOnTouchOutside(true);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentTransaction transaction;
                switch (tabId) {
                    case R.id.tab_reviews:
                        ReviewFragment reviewFragment = new ReviewFragment();
                        reviewFragment.setSortType(1);
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame, reviewFragment);
                        transaction.commit();
                        break;
                    case R.id.tab_stores:
                        LocatlistFragment locatlistFragment = new LocatlistFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame, locatlistFragment);
                        transaction.commit();
                        break;
                    case R.id.tab_locations:
                        break;
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_reviews:
                        popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.tab_reviews), Gravity.END);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_viewpost, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.popup_viewpost_lastnews:
                                        ReviewFragment reviewFragment = new ReviewFragment();
                                        reviewFragment.setSortType(1);
                                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.frame, reviewFragment);
                                        transaction.commit();
                                        break;
                                    case R.id.popup_viewpost_mostcomment:
                                        ReviewFragment reviewFragment1 = new ReviewFragment();
                                        reviewFragment1.setSortType(2);
                                        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                                        transaction1.replace(R.id.frame, reviewFragment1);
                                        transaction1.commit();
                                        break;
                                    case R.id.popup_viewpost_mostlike:
                                        ReviewFragment reviewFragment2 = new ReviewFragment();
                                        reviewFragment2.setSortType(3);
                                        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                                        transaction2.replace(R.id.frame, reviewFragment2);
                                        transaction2.commit();
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();

                        break;
                    case R.id.tab_stores:
                        popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.tab_stores), Gravity.CENTER);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_viewquan, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.popup_viewquan_gia:
                                        break;
                                    case R.id.popup_viewquan_pv:
                                        break;
                                    case R.id.popup_viewquan_vs:
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                        break;
                    case R.id.tab_locations:
                        break;
                }
            }
        });
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
//        if (!isMyServiceRunning(MyService.class)) {
//            final Intent intent = new Intent(this, MyService.class);
//            startService(intent);
//        }
        registerReceiver(mReceiver, mIntentFilter);
        Log.i(LOG, "onResume");
    }


    @Override
    protected void onStop() {
        super.onStop();
        //doUnbinService();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        Log.i(LOG, "onStop");
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG, "onDestroy");
        super.onDestroy();
        doUnbinService();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_profile:
                Intent intent = new Intent(MainActivity.this, AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frg_prodetail_CODE));
                startActivity(intent);
                break;
            case R.id.nav_homepage:
                LocatlistFragment locatlistFragment = new LocatlistFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, locatlistFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
            case R.id.nav_signin:
                Intent intent1 = new Intent(MainActivity.this, AdapterActivity.class);
                intent1.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frg_signin_CODE));
                startActivity(intent1);
                break;
            case R.id.nav_signout:
                mAuth.signOut();
                break;
            case R.id.nav_map:
                Intent intent2 = new Intent(MainActivity.this, AdapterActivity.class);
                intent2.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frag_map_CODE));
                startActivity(intent2);
                break;
        }
        mdrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mdrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_fabitem1:
                break;
            case R.id.main_fabitem2:
                Intent intent = new Intent(MainActivity.this, AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frag_addloca_CODE));
                startActivity(intent);
                break;
            case R.id.main_fabitem3:
                Intent intent1 = new Intent(MainActivity.this, Adapter2Activity.class);
                intent1.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frag_addpost_CODE));
                startActivity(intent1);
                break;

        }
    }
}
