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
import com.app.ptt.comnha.Modules.Route;
import com.app.ptt.comnha.Service.GPSService;
import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements DirectionFinderListener {
    private Geocoder geocoder;
    private GPSService gpsService;
    private static final String LOG = "___MY LOG___";
    private Boolean isBound=false;
    private Button btn_signup, btn_signin, btn_posts, btn_postlist, btn_newloca,btn_map,btn_search,btn_load;
    private Fragment fragment;
    private Bundle savedInstanceState;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public String userID;
    ArrayList<Route> routes;
    ArrayList<String> listPlace;
    String yourLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;
        if(!isMyServiceRunning(GPSService.class)){
            final Intent intent=new Intent(this,GPSService.class);
            startService(intent);
        }
        Log.i(LOG,"onCreate");
        listPlace = new ArrayList<String>();
        listPlace.add("89 Ngô Quyền Quận 9");
        listPlace.add("1 Võ Văn Ngân Thủ Đức");
        listPlace.add("250 Lê Văn Việt Quận 9");
        listPlace.add("89 Lê Văn Chí Thủ Đức");
        listPlace.add("60 Dân Chủ Thủ Đức");
        geocoder=new Geocoder(this, Locale.getDefault());
        routes=new ArrayList<Route>();

        anhXa();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    userID = user.getUid();
                    Toast.makeText(getApplicationContext(), "Signed in successfull with " + user.getEmail().toString(), Toast.LENGTH_SHORT).show();
                    LoginSession.getInstance().setUserID(userID);
                    Log.d("signed_in", "onAuthStateChanged:signed_in: " + userID);
                } else {
                    userID = "";
                    Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_SHORT).show();
                    Log.d("signed_out", "onAuthStateChanged:signed_out");
                }
            }
        };


    }
    public void findDirection(String orgin,String destination){
        try{
            new DirectionFinder(this,orgin,destination,routes,geocoder).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void anhXa() {
        btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_signin = (Button) findViewById(R.id.btn_signin);
        btn_posts = (Button) findViewById(R.id.btn_post);
        btn_postlist = (Button) findViewById(R.id.btn_postlst);
        btn_newloca = (Button) findViewById(R.id.btn_newlocation);
        btn_map =(Button) findViewById(R.id.btn_map);
        btn_search=(Button) findViewById(R.id.btn_search);
        btn_load=(Button) findViewById(R.id.btn_load);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.frame) != null) {
                    if (savedInstanceState != null) {

                    } else {

                    }
                    SignupFragment signupFragment = new SignupFragment();
                    signupFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame, signupFragment).addToBackStack(null).commit();

                }
            }
        });
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
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, signinFragment);
                transaction.addToBackStack(null);
                transaction.commit();
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
                AddlocaFragment addlocaFragment = new AddlocaFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, addlocaFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadListPlace();
            }
        });
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MapFragment mapFragment=new MapFragment();
                mapFragment.getMethod(routes);
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame,mapFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment searchFragment=new SearchFragment();
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame,searchFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
    public void loadListPlace(){
        String origin="";
        try {
            origin=gpsService.returnLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String destination: listPlace){
            findDirection(destination,origin);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        doBinService();
        Log.i(LOG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isMyServiceRunning(GPSService.class)){
            final Intent intent=new Intent(this,GPSService.class);
            startService(intent);
            Log.i("Resume","Resume");
        }
        Log.i(LOG,"onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mAuth.signOut();
        LoginSession.getInstance().setUserID("");
        doUnbinService();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);

        }
        Log.i(LOG,"onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isMyServiceRunning(GPSService.class)){
            final Intent intent=new Intent(this,GPSService.class);
            stopService(intent);

        }
        Log.i(LOG,"Pause");
    }

    public void doBinService(){
        if(!isBound){
            Intent intent=new Intent(this,GPSService.class);
            bindService(intent,serviceConnection,BIND_AUTO_CREATE);
            isBound=true;
        }
    }
    public void doUnbinService(){
        if(isBound){
            unbindService(serviceConnection);
            isBound=false;
        }
    }
    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GPSService.LocalBinder binder=(GPSService.LocalBinder) service;
            gpsService=binder.getService();
            isBound=true;
            Log.i(LOG,"ServiceConnection");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound=false;
        }
    };
    public boolean isMyServiceRunning(Class<?> serviceClass){
        ActivityManager manager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(ArrayList<Route> routes) {
        this.routes=routes;

    }
}
