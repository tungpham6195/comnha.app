package com.app.ptt.comnha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.Service.MyTool;

import java.io.StringWriter;
import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    TextView dot1, dot2, dot3, dot4, dot5, dot6;
    ImageView imgLogo;
    MyTool myTool;
    private static final String LOG = SplashActivity.class.getSimpleName();
    IntentFilter intentFilter;
    boolean isConnected;
    MyLocation myLocation;
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    NetworkChangeReceiver broadcast;
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onStart() {
        super.onStart();
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(mBroadcastSendAddress);
        intentFilter.addAction("android.location.PROVIDERS_CHANGED");
        broadcast = new NetworkChangeReceiver();
        registerReceiver(broadcast, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcast);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // myLocation=new MyLocation();
        setContentView(R.layout.activity_splash);
        dot1 = (TextView) findViewById(R.id.act_splash_dot1);
        dot2 = (TextView) findViewById(R.id.act_splash_dot2);
        dot3 = (TextView) findViewById(R.id.act_splash_dot3);
        dot4 = (TextView) findViewById(R.id.act_splash_dot4);
        dot5 = (TextView) findViewById(R.id.act_splash_dot5);
        dot6 = (TextView) findViewById(R.id.act_splash_dot6);
        imgLogo = (ImageView) findViewById(R.id.act_splash_imglogo);
        AnimationUtils.animateTransTrip(dot1, dot2, dot3, dot4, dot5, dot6);
        AnimationUtils.animateTransAlpha(imgLogo);
        myTool = new MyTool(getApplicationContext(), SplashActivity.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra("STT", 0) == 2) {
                Log.i(LOG + ".NetworkChangeReceiver", "Nhan vi tri cua ban:");
                try {
                    myLocation = myTool.getYourLocation();
                    Storage.deleteFile(getApplicationContext(), "myLocation");
                } catch (Exception e) {
                }
                if (myLocation != null) {
                    ArrayList<MyLocation> list = new ArrayList<>();
                    list.add(myLocation);
                    StringWriter out = new StringWriter();

                    if (Storage.parseMyLocationToJson(list).toString() != null) {
                        Storage.writeFile(getApplicationContext(), Storage.parseMyLocationToJson(list).toString(), "myLocation");
                    }
                }
                if (myTool.isGoogleApiConnected())
                    myTool.stopLocationUpdate();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        i.putExtra("isConnected", true);
                        startActivity(i);

                        finish();
                    }
                }, SPLASH_TIME_OUT);

            }

            if (isNetworkAvailable(context) && canGetLocation(context)) {
                if (myLocation == null) {
                    Log.i(LOG + ".NetworkChangeReceiver", "myLocation == null");
                    myTool.startGoogleApi();
                }
                isConnected = true;
            } else {
                new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        i.putExtra("isConnected", false);
                        startActivity(i);

                        // close this activity
                        finish();
                    }
                }, SPLASH_TIME_OUT);
                isConnected = false;
            }
            Log.i(LOG + ".NetworkChangeReceiver", "isConnected: " + isConnected);
        }

        private boolean canGetLocation(Context mContext) {
            try {
                LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!isGPSEnabled) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }

        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            if (!isConnected) {
                                Log.v(LOG, "Now you are connected to Internet!");
                                isConnected = true;
                            }
                            return true;
                        }
                    }
                }
            }
            Log.v(LOG, "You are not connected to Internet!");

            ;
            //networkStatus.setText("You are not connected to Internet!");
            isConnected = false;
            return false;
        }
    }
}
