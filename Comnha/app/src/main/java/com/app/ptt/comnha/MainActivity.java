package com.app.ptt.comnha;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Geocoder;
import android.os.Bundle;
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
import com.app.ptt.comnha.Modules.GPSService;
import com.app.ptt.comnha.Modules.Route;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements DirectionFinderListener {
    private Button btn_signup, btn_signin, btn_posts, btn_postlist,btn_map;
    private Fragment fragment;
    private Bundle savedInstanceState;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public String userID;
    private GPSService gpsService;
    private Boolean isBound=false;
    ArrayList<Route> listRoute;
    Geocoder geocoder;
    int i=4;
    List<String> listPlace;
    boolean flag=false;
    ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isMyServiceRunning(GPSService.class)){
            final Intent intent=new Intent(this,GPSService.class);
            startService(intent);
            Log.i("Start Service: ","Success");
        }
        this.savedInstanceState = savedInstanceState;
        list = new ArrayList<String>();
        list.add("89 Ngô Quyền Quận 9");
        list.add("1 Võ Văn Ngân Thủ Đức");
        list.add("250 Lê Văn Việt Quận 9");
        list.add("89 Lê Văn Chí Thủ Đức");
        list.add("60 Dân Chủ Thủ Đức");

        geocoder=new Geocoder(this, Locale.getDefault());
        listRoute=new ArrayList<Route>();
        listPlace=new ArrayList<String>();
        anhXa();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    userID = user.getUid();
                    Toast.makeText(getApplicationContext(), "Signed in successfull with " + user.getEmail().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("signed_in", "onAuthStateChanged:signed_in: " + userID);
                } else {
                    userID = null;
                    Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_SHORT).show();
                    Log.d("signed_out", "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    void anhXa() {
        btn_signup = (Button) findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i == 0) {
                    i++;
                    btn_signup.setText("Sign Up");
                    if (findViewById(R.id.frame) != null) {
                        if (savedInstanceState != null) {
    //
                        } else {

                        }
                        SignupFragment signupFragment = new SignupFragment();
                        signupFragment.setArguments(getIntent().getExtras());
                        getSupportFragmentManager().beginTransaction().add(R.id.frame, signupFragment).addToBackStack(null).commit();

                    }
                }
                if(i==1){
                    i++;
                    btn_signup.setText("Post");
                    PostFragment postFragment = new PostFragment();
                    postFragment.setUserID(mAuth.getCurrentUser().getUid().toString());
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, postFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                if(i==2){
                    i++;
                    btn_signup.setText("Sign In");
                    SigninFragment signinFragment = new SigninFragment();
                    signinFragment.setmAuth(mAuth);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, signinFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                if(i==3){
                    i++;
                    btn_signup.setText("Sign Post1");
                    PostlistFragment postlistFragment=new PostlistFragment();
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame,postlistFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                if(i==4 ){
                    //i=0;
                    for(String a: list){
                        findDirection(gpsService.getYourLocation(),a);
                        flag=true;
                    }
                    Log.i("#####",listRoute.size()+"");
                    if(listRoute.size()>0) {
                        btn_signup.setText("Map");
                        MapFragment mapFragment = new MapFragment(gpsService.getLatitude(), gpsService.getLongtitude(), listRoute);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame, mapFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            }
        });
<<<<<<< HEAD

=======
        btn_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostFragment postFragment = new PostFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, postFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SigninFragment signinFragment = new SigninFragment();
                signinFragment.setmAuth(mAuth);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, signinFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        btn_postlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostlistFragment postlistFragment = new PostlistFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, postlistFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
>>>>>>> origin/master
    }


    public void findDirection(String orgin,String destination){
        try{
            new DirectionFinder(this,orgin,destination,listRoute,geocoder).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    protected void onStart() {
        super.onStart();
        doBindService();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            gpsService.startGoogleApi();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(isMyServiceRunning(GPSService.class)){
//            final Intent intent=new Intent(this,GPSService.class);
//            stopService(intent);
//        }
        try{
            gpsService.stopGoogleApi();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        doUnbindService();
        mAuth.signOut();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(ArrayList<Route> route) {
        this.listRoute=route;
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GPSService.LocalBinder binder = (GPSService.LocalBinder) service;
            gpsService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
    public void doBindService() {
        if (!isBound) {
            Intent intent = new Intent(this, GPSService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            isBound = true;
        }
    }
    public void doUnbindService() {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }

    }

}
