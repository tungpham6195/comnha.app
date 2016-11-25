package com.app.ptt.comnha;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Interfaces.Transactions;
import com.app.ptt.comnha.Modules.ConnectionDetector;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.Service.MyTool;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.firebase.client.Firebase;
import com.firebase.client.realtime.Connection;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , FloatingActionButton.OnClickListener {
    private LocationManager locationManager;
    private LocationListener listener;
    private static final String LOG = MainActivity.class.getSimpleName();
    private Bundle savedInstanceState;
    private ProgressDialog progressDialog;
    private IntentFilter mIntentFilter;
    private MyLocation myLocation;
    private ProgressDialog logoutDialog;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean requestLocation = false;
    boolean temp=true;
    public String userID, username, email;

    private Toolbar mtoolbar;
    private DrawerLayout mdrawer;
    private ActionBarDrawerToggle mtoggle;
    private NavigationView mnavigationView;
    private TextView txt_email, txt_un;
    private FloatingActionMenu fabmenu;
    //private boolean checkConnection = true;
    private FloatingActionButton fab_review, fab_addloca, fab_changloca;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    private Firebase ref;
    boolean isConnected=false;
    private BottomBar bottomBar;
    private PopupMenu popupMenu;
    private MyTool myTool;
    private ChangeLocationBottomSheetDialogFragment changeLccaBtmSheet;
    NetworkChangeReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG, "onCreate");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.txt_plzwait));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");



        myTool = new MyTool(getApplicationContext(), MainActivity.class.getSimpleName());
        setContentView(R.layout.activity_main2);

        Firebase.setAndroidContext(this);
        ref = new Firebase(getResources().getString(R.string.firebase_path));
        anhXa();
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
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
                Menu menu = mnavigationView.getMenu();
                if (user != null) {
                    try {
                        if (user.getEmail() != null) {
                            userID = user.getUid();
                            MenuItem menuItem = menu.findItem(R.id.nav_profile);
                            menuItem.setEnabled(true);
                            MenuItem menuItem1 = menu.findItem(R.id.nav_signin);
                            menuItem1.setEnabled(false);
                            MenuItem menuItem2 = menu.findItem(R.id.nav_signout);
                            menuItem2.setEnabled(true);
                            Log.d("signed_in", "onAuthStateChanged:signed_in: " + user.getEmail());
                            LoginSession.getInstance().setUserID(userID);
                            LoginSession.getInstance().setEmail(user.getEmail());
                            LoginSession.getInstance().setUsername(user.getDisplayName());
                            txt_email.setText(user.getEmail());
                            txt_un.setText(user.getDisplayName());
                        } else {
                            MenuItem menuItem = menu.findItem(R.id.nav_profile);
                            menuItem.setEnabled(false);
                            MenuItem menuItem1 = menu.findItem(R.id.nav_signout);
                            menuItem1.setEnabled(false);
                            MenuItem menuItem2 = menu.findItem(R.id.nav_signin);
                            menuItem2.setEnabled(true);

                            txt_email.setText(getResources().getString(R.string.text_hello));
                            txt_un.setText(getResources().getString(R.string.text_user));
                            userID = "";
                            LoginSession.getInstance().setUserID(null);
                            LoginSession.getInstance().setUsername(null);
                            LoginSession.getInstance().setEmail(null);
                        }
                    } catch (NullPointerException mess) {
                        txt_email.setText(getResources().getString(R.string.text_hello));
                        txt_un.setText(getResources().getString(R.string.text_user));
                        userID = "";
                        LoginSession.getInstance().setUserID(null);
                        LoginSession.getInstance().setUsername(null);
                        LoginSession.getInstance().setEmail(null);
                    }
                } else {
//                    txt_email.setText(getResources().getString(R.string.text_hello));
//                    txt_un.setText(getResources().getString(R.string.text_user));
//                    userID = "";
//                    LoginSession.getInstance().setUserID(null);
//                    LoginSession.getInstance().setUsername(null);
//                    LoginSession.getInstance().setEmail(null);
////                    Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_SHORT).show();
//                    Log.d("signed_out", "onAuthStateChanged:signed_out");
                }
            }
        };
    }


    void anhXa() {
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        fabmenu = (FloatingActionMenu) findViewById(R.id.main_fabMenu);
        fab_review = (FloatingActionButton) findViewById(R.id.main_fabitem3);
        fab_addloca = (FloatingActionButton) findViewById(R.id.main_fabitem2);
        fab_changloca = (FloatingActionButton) findViewById(R.id.main_fabitem1);
        //myTool.startGoogleApi();
       // progressDialog.show();
        //request();
    }
    public void bottomBarEvent(){
        fab_review.setOnClickListener(this);
        fab_addloca.setOnClickListener(this);
        fab_changloca.setOnClickListener(this);
        fabmenu.setClosedOnTouchOutside(true);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentTransaction transaction;
                switch (tabId) {
                    case R.id.tab_reviews:
                        fabmenu.close(true);

                            ReviewFragment reviewFragment = new ReviewFragment();
                            reviewFragment.setContext(getApplicationContext());
                            reviewFragment.setTinh(LoginSession.getInstance().getTinh());
                            reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
                            reviewFragment.setSortType(1);
                            reviewFragment.setIsConnected(isConnected);
                            transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame, reviewFragment);
                            transaction.commit();
                            AnimationUtils.animatfabMenuIn(fabmenu);
                    //    }
                        break;
                    case R.id.tab_stores:
                        fabmenu.close(true);
                       // if (isConnected) {
                            StoreFragment storeFragment = new StoreFragment();
                            storeFragment.setFilter(1);
                            storeFragment.setIsConnected(isConnected);
                            storeFragment.setContext(getApplicationContext());
                            storeFragment.setTinh(LoginSession.getInstance().getTinh());
                            storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                            storeFragment.setYourLocation(myLocation);
                            transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame, storeFragment);
                            transaction.commit();
                            AnimationUtils.animatfabMenuIn(fabmenu);
                     //   }
                        break;
                    case R.id.tab_locations:
                        FilterFragment filterFragment = new FilterFragment();
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame, filterFragment);
                        transaction.commit();
                        AnimationUtils.animatfabMenuOut(fabmenu);
                        fabmenu.close(true);
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
                                      //  if (isConnected) {
                                            ReviewFragment reviewFragment = new ReviewFragment();
                                            reviewFragment.setSortType(1);
                                        reviewFragment.setIsConnected(isConnected);
                                            reviewFragment.setContext(getApplicationContext());
                                            reviewFragment.setTinh(LoginSession.getInstance().getTinh());
                                            reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            transaction.replace(R.id.frame, reviewFragment);
                                            transaction.commit();
                                     //   }// else startGetLocation();
                                        break;
                                    case R.id.popup_viewpost_mostcomment:
                                     //   if (isConnected) {
                                            ReviewFragment reviewFragment1 = new ReviewFragment();
                                        reviewFragment1.setIsConnected(isConnected);
                                            reviewFragment1.setSortType(2);
                                            reviewFragment1.setContext(getApplicationContext());
                                            reviewFragment1.setTinh(LoginSession.getInstance().getTinh());
                                            reviewFragment1.setHuyen(LoginSession.getInstance().getHuyen());
                                            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                                            transaction1.replace(R.id.frame, reviewFragment1);
                                            transaction1.commit();
                                     //   } //else startGetLocation();
                                        break;
                                    case R.id.popup_viewpost_mostlike:
                                  //      if (isConnected) {
                                            ReviewFragment reviewFragment2 = new ReviewFragment();
                                            reviewFragment2.setSortType(3);
                                        reviewFragment2.setIsConnected(isConnected);
                                            reviewFragment2.setContext(getApplicationContext());
                                            reviewFragment2.setTinh(LoginSession.getInstance().getTinh());
                                            reviewFragment2.setHuyen(LoginSession.getInstance().getHuyen());
                                            FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                                            transaction2.replace(R.id.frame, reviewFragment2);
                                            transaction2.commit();
                                  //      } //else startGetLocation();
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
                                FragmentTransaction transaction;
                                StoreFragment storeFragment;
                                switch (item.getItemId()) {
                                    case R.id.popup_viewquan_none:
                                     //   if (isConnected) {
                                            storeFragment = new StoreFragment();
                                            storeFragment.setFilter(1);
                                            storeFragment.setIsConnected(isConnected);
                                            storeFragment.setContext(getApplicationContext());
                                            storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                            storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                            storeFragment.setYourLocation(myLocation);
                                            transaction = getSupportFragmentManager().beginTransaction();
                                            transaction.replace(R.id.frame, storeFragment);
                                            transaction.commit();
                                    //    } //else startGetLocation();
                                        break;
                                    case R.id.popup_viewquan_gia:
                                       // if (isConnected) {
                                            storeFragment = new StoreFragment();
                                            storeFragment.setIsConnected(isConnected);
                                            storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                            storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                            storeFragment.setContext(getApplicationContext());
                                            storeFragment.setYourLocation(myLocation);
                                            storeFragment.setFilter(2);
                                            transaction = getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.frame, storeFragment);
                                            transaction.commit();
                                      //  } //else startGetLocation();
                                        break;
                                    case R.id.popup_viewquan_pv:
                                     //   if (isConnected) {
                                            storeFragment = new StoreFragment();
                                            storeFragment.setIsConnected(isConnected);
                                            storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                            storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                            storeFragment.setYourLocation(myLocation);
                                            storeFragment.setContext(getApplicationContext());
                                            storeFragment.setFilter(3);
                                            transaction = getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.frame, storeFragment);
                                            transaction.commit();
                                    //    } //else startGetLocation();
                                        break;
                                    case R.id.popup_viewquan_vs:
                                       // if (isConnected) {
                                            storeFragment = new StoreFragment();
                                            storeFragment.setIsConnected(isConnected);
                                            storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                            storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                            storeFragment.setYourLocation(myLocation);
                                            storeFragment.setContext(getApplicationContext());
                                            storeFragment.setFilter(4);
                                            transaction = getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.frame, storeFragment);
                                            transaction.commit();
                                    //    } //else startGetLocation();
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                        break;
                    case R.id.tab_locations:
//                        popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.tab_locations), Gravity.START);
//                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_locafilter, popupMenu.getMenu());
//                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//                                switch (item.getItemId()) {
//                                    case R.id.popup_locafilter_myloca:
//                                        break;
//                                    case R.id.popup_locafilter_choseloca:
//                                        break;
//                                }
//                                return true;
//                            }
//                        });
//                        popupMenu.show();
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG, "onStart");
        mIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mIntentFilter.addAction("android.location.PROVIDERS_CHANGED");
        mIntentFilter.addAction(mBroadcastSendAddress);
        mBroadcastReceiver=new NetworkChangeReceiver();
        registerReceiver(mBroadcastReceiver, mIntentFilter);

        mAuth.addAuthStateListener(mAuthListener);
        try {
            if (mAuth.getCurrentUser() == null) {
                mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("signInAnonymously", "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("signInAnonymouslyError", "signInAnonymously", task.getException());
//                            Toast.makeText(MainActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            if (mAuth.getCurrentUser().getEmail() == null) {
                mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("signInAnonymously", "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("signInAnonymouslyError", "signInAnonymously", task.getException());
                        }
                    }
                });
            }
        } catch (NullPointerException mess) {

        }

        Log.i(LOG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG, "onResume");
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        Log.i(LOG, "onStop");

    }

    @Override
    protected void onDestroy() {
        Log.i(LOG, "onDestroy");
        super.onDestroy();
       // Storage.deleteFile(getApplicationContext(),"myLocation");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG, "Pause");
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
                Intent intent = new Intent(MainActivity.this, Adapter2Activity.class);
                intent.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frg_prodetail_CODE));
                startActivity(intent);
                break;
            case R.id.nav_homepage:
                if (bottomBar.getCurrentTabPosition() != 0) {
                    bottomBar.selectTabAtPosition(0);
                }
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
            case R.id.nav_about:
                AboutBottomSheetDialogFragment aboutBottomSheetDialogFragment
                        = new AboutBottomSheetDialogFragment();
                aboutBottomSheetDialogFragment.show(getSupportFragmentManager(),
                        getString(R.string.frag_about_CODE));
                break;
            case R.id.nav_admin:

                break;
            case R.id.nav_signin:
                Intent intent1 = new Intent(MainActivity.this, Adapter2Activity.class);
                intent1.putExtra(getString(R.string.fragment_CODE),
                        getString(R.string.frg_signin_CODE));
                startActivity(intent1);
                break;
            case R.id.nav_signout:
                logoutDialog = ProgressDialog.show(this,
                        getResources().getString(R.string.txt_plzwait),
                        getResources().getString(R.string.txt_logginout), true, false);
                mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("signInAnonymously", "signInAnonymously:onComplete:" + task.isSuccessful());
                        logoutDialog.dismiss();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("signInAnonymouslyError", "signInAnonymously", task.getException());
//                            Toast.makeText(MainActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                        } else {
                            LoginSession.getInstance().setTen(null);
                            LoginSession.getInstance().setHo(null);
                            LoginSession.getInstance().setTenlot(null);
                            LoginSession.getInstance().setNgaysinh(null);
                            LoginSession.getInstance().setPassword(null);
                        }
                    }
                });
                break;
            case R.id.nav_map:
               // if (isConnected) {
                    Intent intent2 = new Intent(MainActivity.this, AdapterActivity.class);
                    intent2.putExtra(getString(R.string.fragment_CODE),
                            getString(R.string.frag_map_CODE));

                    startActivity(intent2);
             //   }
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
                changeLccaBtmSheet = new ChangeLocationBottomSheetDialogFragment();
                changeLccaBtmSheet.show(getSupportFragmentManager(), "fragment_changeLocal");
                changeLccaBtmSheet.setOnChangeLocationListenner(new ChangeLocationBottomSheetDialogFragment.OnChangeLocationListenner() {
                    @Override
                    public void onChangeLocation(String Province, String District) {
                        LoginSession.getInstance().setTinh(Province);
                        fab_changloca.setLabelText(Province + ", " + District);
                        LoginSession.getInstance().setHuyen(District);
                        fabmenu.close(true);
                        FragmentTransaction transaction;
                        switch (bottomBar.getCurrentTabPosition()) {
                            case 0:
                                //if (isConnected) {
                                    ReviewFragment reviewFragment = new ReviewFragment();
                                    reviewFragment.setTinh(LoginSession.getInstance().getTinh());
                                    reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                    reviewFragment.setSortType(1);
                                    reviewFragment.setIsConnected(isConnected);
                                    reviewFragment.setContext(getApplicationContext());
                                    transaction = getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame, reviewFragment);
                                    transaction.commit();
                              //  }// else startGetLocation();
                                break;
                            case 1:
                           //     if (isConnected) {
                                    StoreFragment storeFragment = new StoreFragment();
                                    storeFragment.setFilter(1);
                                    storeFragment.setIsConnected(isConnected);
                                    storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                    storeFragment.setContext(getApplicationContext());
                                    storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                    storeFragment.setYourLocation(myLocation);
                                    transaction = getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame, storeFragment);
                                    transaction.commit();
                               // }// else startGetLocation();
                                break;
                            case 2:
                                break;
                        }
                    }

                    @Override
                    public void onChangetoMylocation(boolean isMylocation) {
                        if (isMylocation) {
                            if (isConnected) {
                                LoginSession.getInstance().setHuyen(myLocation.getQuanhuyen());
                                LoginSession.getInstance().setTinh(myLocation.getTinhtp());
                                fab_changloca.setLabelText(LoginSession.getInstance().getHuyen() + ", " +
                                        LoginSession.getInstance().getTinh());
                            } //else startGetLocation();
                            fabmenu.close(true);
                            FragmentTransaction transaction;
                            switch (bottomBar.getCurrentTabPosition()) {
                                case 0:
                                   // if (isConnected) {
                                        ReviewFragment reviewFragment = new ReviewFragment();
                                        reviewFragment.setTinh(LoginSession.getInstance().getTinh());
                                        reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                        reviewFragment.setSortType(1);
                                    reviewFragment.setIsConnected(isConnected);
                                        reviewFragment.setContext(getApplicationContext());
                                        transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.frame, reviewFragment);
                                        transaction.commit();
                                 //   }// else startGetLocation();
                                    break;
                                case 1:
                                   // if (isConnected) {
                                        StoreFragment storeFragment = new StoreFragment();
                                        storeFragment.setFilter(1);
                                        storeFragment.setIsConnected(isConnected);
                                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                        storeFragment.setContext(getApplicationContext());
                                        storeFragment.setYourLocation(myLocation);
                                        transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.frame, storeFragment);
                                        transaction.commit();
                                  //  } //else startGetLocation();
                                    break;
                                case 2:
                                    break;
                            }
                        }
                    }
                });
                break;
            case R.id.main_fabitem2:
                if (LoginSession.getInstance().getUserID() == null) {
                    Toast.makeText(this, getString(R.string.txt_needlogin),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, Adapter2Activity.class);
                    intent.putExtra(getString(R.string.fragment_CODE),
                            getString(R.string.frag_addloca_CODE));
                    startActivity(intent);
                }
                break;
            case R.id.main_fabitem3:
                if (LoginSession.getInstance().getUserID() == null) {
                    Toast.makeText(this, getString(R.string.txt_needlogin),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent1 = new Intent(MainActivity.this, Adapter2Activity.class);
                    intent1.putExtra(getString(R.string.fragment_CODE),
                            getString(R.string.frag_addpost_CODE));
                    startActivity(intent1);
                }
                break;

        }
    }

//    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getIntExtra("STT", 0) == 2) {
//                Log.i(LOG + ".MainActivity", "Nhan vi tri cua ban:");
//                try {
//                    myLocation = myTool.getYourLocation();
//                    progressDialog.dismiss();
//                } catch (Exception e) {
//                }
//                if (myLocation != null) {
//                    LoginSession.getInstance().setTinh(myLocation.getTinhtp());
//                    LoginSession.getInstance().setHuyen(myLocation.getQuanhuyen());
//                    fab_changloca.setLabelText(LoginSession.getInstance().getHuyen() + ", "
//                            + LoginSession.getInstance().getTinh());
//                    Log.i(LOG + ".MainActivity", "myLocation != null");
//                    bottomBarEvent();
//                    myTool.stopLocationUpdate();
//                }
//            }
//            if (intent.getIntExtra("STT", 0) == 3) {
//                Log.i(LOG + ".BroadcastReceiver", "Nhan su thay doi vi tri cua ban:");
//                try {
//                    myLocation = myTool.getYourLocation();
//                } catch (Exception e) {
//                }
//                if (myLocation != null) {
//                    LoginSession.getInstance().setTinh(myLocation.getTinhtp());
//                    LoginSession.getInstance().setHuyen(myLocation.getQuanhuyen());
//                    fab_changloca.setLabelText(LoginSession.getInstance().getHuyen() + ", "
//                            + LoginSession.getInstance().getTinh());
//                }
//            }
//            if (intent.getIntExtra("STT", 0) == -1) {
//                Log.i(LOG + ".BroadcastReceiver", "No connecttion");
//                progressDialog.dismiss();
//                ConnectionDetector.showNoConnectAlert(MainActivity.this);
//            }
//            if (intent.getIntExtra("STT", 0) == -2) {
//                Log.i(LOG + ".BroadcastReceiver", "No internet");
//                progressDialog.dismiss();
//                ConnectionDetector.showNetworkAlert(MainActivity.this);
//            }
//            if (intent.getIntExtra("STT", 0) == -3) {
//                Log.i(LOG + ".BroadcastReceiver", "No gps");
//                progressDialog.dismiss();
//                ConnectionDetector.showSettingAlert(MainActivity.this);
//            }
//            if (intent.getIntExtra("STT", 0) == -4) {
//                Log.i(LOG + ".BroadcastReceiver", "No gps");
//                ConnectionDetector.showGetLocationError(MainActivity.this);
//                progressDialog.dismiss();
//            }
//        }
//    };
    public boolean checkConnection() {
        if (!ConnectionDetector.isWifiAvailable(this)) {
            if (!ConnectionDetector.canGetLocation(this)) {
                Log.i(LOG + ".startGoogleApi", "NoInternetAndGps");
                ConnectionDetector.showNetworkAlert(MainActivity.this);

            } else {
                Log.i(LOG + ".startGoogleApi", "NoInternet");
                ConnectionDetector.showNetworkAlert(MainActivity.this);
            }
        } else {
            if (!ConnectionDetector.canGetLocation(this)) {
                Log.i(LOG + ".startGoogleApi", "NoGps");

            } else {
                return true;
            }
        }
        return false;
    }
    public void startGetLocation() {
        if (!myTool.isGoogleApiConnected()) {
            Log.i(LOG + ".startGetLocation", "Google Api not connect");
            myTool.startGoogleApi();
            requestLocation = true;
        } else {
            if (myLocation == null) {
                Log.i(LOG + ".startGetLocation", "myLocation==null");
                myTool.startGoogleApi();
            }
        }
        progressDialog.show();
    }


    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Intent myIntent = getIntent();
            boolean temp = myIntent.getBooleanExtra("isConnected", false);
            Log.i(LOG + ".NetworkChangeReceiver", "isConnected splash " + temp);
            if (intent.getIntExtra("STT", 0) == 2) {
                Log.i(LOG + ".NetworkChangeReceiver", "Nhan vi tri cua ban:");
                try {
                    myLocation = myTool.getYourLocation();
                    Storage.deleteFile(getApplicationContext(), "myLocation");
                    progressDialog.dismiss();
                } catch (Exception e) {
                }
                if (myLocation != null) {
                    ArrayList<MyLocation> list = new ArrayList<>();
                    list.add(myLocation);
                    Storage.writeFile(getApplicationContext(), Storage.parseMyLocationToJson(list).toString(), "myLocation");
                    LoginSession.getInstance().setTinh(myLocation.getTinhtp());
                    LoginSession.getInstance().setHuyen(myLocation.getQuanhuyen());
                    fab_changloca.setLabelText(LoginSession.getInstance().getHuyen() + ", "
                            + LoginSession.getInstance().getTinh());
                    Log.i(LOG + ".NetworkChangeReceiver", "myLocation != null");
                    bottomBarEvent();
                    if (myTool.isGoogleApiConnected())
                        myTool.stopLocationUpdate();
                }
            }
            if (isNetworkAvailable(context) && canGetLocation(context)) {

                if (temp) {
                    ArrayList<MyLocation> locations = new ArrayList<>();
                    String a = Storage.readFile(getApplicationContext(), "myLocation");
                    if (a != null) {
                        locations = Storage.readJSONMyLocation(a);
                        if (locations.size() > 0)
                            myLocation = locations.get(0);
                        else {
                            myLocation = null;
                        }
                    }

            }

                if (myLocation == null) {
                    Log.i(LOG + ".NetworkChangeReceiver", "myLocation == null");
                    myTool.startGoogleApi();
                    progressDialog.show();
                } else {
                    LoginSession.getInstance().setTinh(myLocation.getTinhtp());
                    LoginSession.getInstance().setHuyen(myLocation.getQuanhuyen());
                    fab_changloca.setLabelText(LoginSession.getInstance().getHuyen() + ", "
                            + LoginSession.getInstance().getTinh());
                    Log.i(LOG + ".NetworkChangeReceiver", "myLocation != null");
                    bottomBarEvent();
                }
                isConnected = true;
            } else {
                Toast.makeText(getApplicationContext(),"Offline mode",Toast.LENGTH_LONG).show();
                ArrayList<MyLocation> locations = new ArrayList<>();
                String a = Storage.readFile(getApplicationContext(), "myLocation");
                if (a != null) {
                    locations = Storage.readJSONMyLocation(a);
                    if (locations.size() > 0)
                        myLocation = locations.get(0);
                    else {
                        myTool.startGoogleApi();
                    }
                    if (myLocation != null) {
                        LoginSession.getInstance().setTinh(myLocation.getTinhtp());
                        LoginSession.getInstance().setHuyen(myLocation.getQuanhuyen());
                        fab_changloca.setLabelText(LoginSession.getInstance().getHuyen() + ", "
                                + LoginSession.getInstance().getTinh());
                        Log.i(LOG + ".NetworkChangeReceiver", "myLocation != null");
                        bottomBarEvent();
                        isConnected = false;
                    }
                if (!canGetLocation(context) && !isNetworkAvailable(context)) {
                   //ConnectionDetector.showNoConnectAlert(MainActivity.this);

                } else {
                    if (!isNetworkAvailable(context)) {
                       // ConnectionDetector.showNetworkAlert(MainActivity.this);
                    } else {
                       // ConnectionDetector.showSettingAlert(MainActivity.this);
                    }
                }
                }
                Log.i(LOG + ".NetworkChangeReceiver", "isConnected: " + isConnected);

            }
        }

        private boolean canGetLocation(Context mContext) {
            try {
                LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!isGPSEnabled) {
                    isConnected = false;
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                isConnected = false;
                return false;
            }

        }

        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info =connectivity.getAllNetworkInfo();

                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                       Toast.makeText(getApplicationContext(),"Ten:"+info[i].getTypeName()+"--TrangThai:"+info[i].getState().toString(),Toast.LENGTH_LONG).show();
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            if (!isConnected &&canGetLocation(getApplicationContext())) {
                                Log.v(LOG, "Now you are connected to Internet!");
                                //Toast.makeText(getApplicationContext(), "Now you are connected to Internet!", Toast.LENGTH_SHORT).show();
                                isConnected = true;
                                //do your processing here ---
                                //if you need to post any data to the server or get status
                                //update from the server
                            }
                            return true;
                        }
                    }
                }
            }
            Log.v(LOG, "You are not connected to Internet!");
           // Toast.makeText(getApplicationContext(), "You are offline", Toast.LENGTH_SHORT).show();
            ;
            //networkStatus.setText("You are not connected to Internet!");
            isConnected = false;
            return false;
        }
        public  boolean isMobileNetworkAvailable(Context mContext) {
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mNetworkInfo.isConnected()) {
                isConnected = true;
                return true;
            }
            isConnected = false;
            return false;
        }
    }
}
