package com.app.ptt.comnha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.AnimationUtils;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.Modules.ConnectionDetector;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.Service.MyTool;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    TextView dot1, dot2, dot3, dot4, dot5, dot6;
    ImageView imgLogo;
    MyTool myTool;
    private static final String LOG = SplashActivity.class.getSimpleName();
    IntentFilter intentFilter;
    boolean isConnected;
    MyLocation myLocation;
    public static final String mBroadcastSendAddress1 = "mBroadcastSendAddress1";
    NetworkChangeReceiver broadcast;
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onStart() {
        super.onStart();
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(mBroadcastSendAddress1);
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
                Log.i(LOG + ".NetworkChangeReceiver", "isNetworkAvailable(context) && canGetLocation(context) ");
                if (myLocation == null) {
                    Log.i(LOG + ".NetworkChangeReceiver", "myLocation == null");
                    myTool.startGoogleApi();
                }
                isConnected = true;
            } else {
                Log.i(LOG + ".NetworkChangeReceiver","!(isNetworkAvailable(context) && canGetLocation(context)) ");
                String a= Storage.readFile(getApplicationContext(), "myLocation");
                if(a!=null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(SplashActivity.this, MainActivity.class);
                            i.putExtra("isConnected", false);
                            startActivity(i);
                            finish();
                        }
                    }, SPLASH_TIME_OUT);
                    isConnected = false;
                }
                else{
                    ConnectionDetector.showSettingAlertFirstTime(SplashActivity.this);
                }

            }

        }

        private boolean canGetLocation(Context mContext) {
            int a = 0;
            try {
                a = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE);
                //Log.i(LOG_TAG + ".canGetLocation", a + "");
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            if (a >= 2) return true;
            return false;
        }

        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {

                NetworkInfo[] info = connectivity.getAllNetworkInfo();

                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        Log.i(LOG+".isNetworkAvailable",info[i].getState().toString());
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
