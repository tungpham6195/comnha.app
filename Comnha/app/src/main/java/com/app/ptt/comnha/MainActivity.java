package com.app.ptt.comnha;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Service.MyTool;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.firebase.client.Firebase;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , FloatingActionButton.OnClickListener {

    //    private MyService myService;
    //private MyService myService;
    private static final String LOG = MainActivity.class.getSimpleName();
    //private Boolean isBound = false;
    private Bundle savedInstanceState;
    private ProgressDialog progressDialog;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private IntentFilter mIntentFilter;
    private MyLocation myLocation;
    private ProgressDialog logoutDialog;
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
    //private boolean checkConnection = true;
    private FloatingActionButton fab_review, fab_addloca, fab_uploadpho;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    private Firebase ref;
    private BottomBar bottomBar;
    private PopupMenu popupMenu;
    private MyTool myTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG, "onCreate");
        setContentView(R.layout.activity_main2);
        Firebase.setAndroidContext(this);
        ref = new Firebase(getResources().getString(R.string.firebase_path));
        //anhXa();
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
        Log.i(LOG + ",anhxa", "quan:" + myLocation.getQuanhuyen() + ". tp:" + myLocation.getTinhtp());
//        myLocation.getQuanhuyen()
//                myLocation.getTinhtp()
        LoginSession.getInstance().setHuyen("Quận 9");
        LoginSession.getInstance().setTinh("Hồ Chí Minh");
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
                        fabmenu.close(true);
                        ReviewFragment reviewFragment = new ReviewFragment();
                        reviewFragment.setTinh(LoginSession.getInstance().getTinh());
                        reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
                        reviewFragment.setSortType(1);
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame, reviewFragment);
                        transaction.commit();
                        AnimationUtils.animatfabMenuIn(fabmenu);
                        break;
                    case R.id.tab_stores:
                        fabmenu.close(true);
                        StoreFragment storeFragment = new StoreFragment();
                        storeFragment.setFilter(1);
                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                        storeFragment.setYourLocation(myLocation);
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame, storeFragment);
                        transaction.commit();
                        AnimationUtils.animatfabMenuIn(fabmenu);
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
                                        ReviewFragment reviewFragment = new ReviewFragment();
                                        reviewFragment.setSortType(1);
                                        reviewFragment.setTinh(LoginSession.getInstance().getTinh());
                                        reviewFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.frame, reviewFragment);
                                        transaction.commit();
                                        break;
                                    case R.id.popup_viewpost_mostcomment:
                                        ReviewFragment reviewFragment1 = new ReviewFragment();
                                        reviewFragment1.setSortType(2);
                                        reviewFragment1.setTinh(LoginSession.getInstance().getTinh());
                                        reviewFragment1.setHuyen(LoginSession.getInstance().getHuyen());
                                        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                                        transaction1.replace(R.id.frame, reviewFragment1);
                                        transaction1.commit();
                                        break;
                                    case R.id.popup_viewpost_mostlike:
                                        ReviewFragment reviewFragment2 = new ReviewFragment();
                                        reviewFragment2.setSortType(3);
                                        reviewFragment2.setTinh(LoginSession.getInstance().getTinh());
                                        reviewFragment2.setHuyen(LoginSession.getInstance().getHuyen());
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
                                FragmentTransaction transaction;
                                StoreFragment storeFragment;
                                switch (item.getItemId()) {
                                    case R.id.popup_viewquan_none:
                                        storeFragment = new StoreFragment();
                                        storeFragment.setFilter(1);
                                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                        storeFragment.setYourLocation(myLocation);
                                        transaction = getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.frame, storeFragment);
                                        transaction.commit();
                                        break;
                                    case R.id.popup_viewquan_gia:
                                        storeFragment = new StoreFragment();
                                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                        storeFragment.setYourLocation(myLocation);
                                        storeFragment.setFilter(2);
                                        transaction = getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.frame, storeFragment);
                                        transaction.commit();
                                        break;
                                    case R.id.popup_viewquan_pv:
                                        storeFragment = new StoreFragment();
                                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                        storeFragment.setYourLocation(myLocation);
                                        storeFragment.setFilter(3);
                                        transaction = getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.frame, storeFragment);
                                        transaction.commit();
                                        break;
                                    case R.id.popup_viewquan_vs:
                                        storeFragment = new StoreFragment();
                                        storeFragment.setTinh(LoginSession.getInstance().getTinh());
                                        storeFragment.setHuyen(LoginSession.getInstance().getHuyen());
                                        storeFragment.setYourLocation(myLocation);
                                        storeFragment.setFilter(4);
                                        transaction = getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.frame, storeFragment);
                                        transaction.commit();
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
        if (myLocation == null) {
            myTool = new MyTool(getApplicationContext(), MainActivity.class.getSimpleName());
            myTool.startGoogleApi();
        }
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastSendAddress);
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
//                            Toast.makeText(MainActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (NullPointerException mess) {

        }
//        doBinService();

        //doBinService();

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

//        registerReceiver(mReceiver, mIntentFilter);
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
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG, "onDestroy");
        super.onDestroy();
        myTool.stopGoogleApi();
//        doUnbinService();

        //doUnbinService();

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
                        }
                    }
                });
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
                Intent intent = new Intent(MainActivity.this, Adapter2Activity.class);
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

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra("STT", 0) == 2 && intent.getBooleanExtra("Location", false)) {
                Log.i(LOG + ".MainActivity", "Nhan vi tri cua ban:");
                myLocation = myTool.getYourLocation();
                anhXa();
                //  myTool.stopGoogleApi();
            }
        }
    };

}
