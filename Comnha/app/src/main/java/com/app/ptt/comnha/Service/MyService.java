package com.app.ptt.comnha.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.app.ptt.comnha.AddlocaFragment;
import com.app.ptt.comnha.FireBase.MyLocation;
import com.app.ptt.comnha.MainActivity;
import com.app.ptt.comnha.Modules.Storage;
import com.app.ptt.comnha.SplashActivity;

import java.util.ArrayList;

public class MyService extends Service {
    private static String LOG_TAG = MyService.class.getSimpleName();
    private final IBinder binder = new MyServiceBinder();
    NetworkChangeReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;
     String className1=null;
    int a=0;
    static int posOfReviewType=1;
    Intent broadcastIntent;
    boolean isSaved1=false;
    public boolean isConnected=false;
    static boolean isConnected1=false;
    public boolean isSaved=false;
    MyTool myTool;
    static String changeContent="";
    static ArrayList<String> listSaved=new ArrayList<>();
    public static final String mBroadcastSendAddress = "mBroadcastSendAddress";
    public static final String mBroadcastSendAddress1 = "mBroadcastSendAddress1";
    public class MyServiceBinder extends Binder {

        public MyService getService()  {
            Log.i(LOG_TAG, "isSaved="+ isSaved +"-"+a);
            return MyService.this;
        }
    }
    public MyService() {
    }
    public static void setPosOfReviewType(int pos){
        posOfReviewType=pos;
    }
    public static int getPosOfReviewType(){
       return posOfReviewType;
    }
    public static String getChangeContent(){
        return changeContent;
    }
    public static void setChangeContent(String isChanged){
        changeContent=isChanged;
        if(changeContent.equals("justCommend")||changeContent.equals("justDelete")){
            Log.i(LOG_TAG+".setChangeContent","before remove="+listSaved.size());
            for(int a=0;a<listSaved.size();a++){
                Log.i(LOG_TAG+".setChangeContent- "+listSaved.get(a).toString(),"");
                if(listSaved.get(a).toString().toLowerCase().contains("postlist".toLowerCase())){
                    listSaved.remove(a);
                    Log.i(LOG_TAG+".setChangeContent- ","after remove remove="+listSaved.size());
                }
            }

        }
    }
    public static int saveToListSaved(String file){
        for (String myFile:listSaved)
            if(myFile.equals(file)){
                Log.i(LOG_TAG+".saveToListSaved=1",file);
                return 1;
            }
        listSaved.add(file);
        Log.i(LOG_TAG+".saveToListSaved=2",file);
        return 2;
    }
    @Override
    public IBinder onBind(Intent intent) {
        mIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mIntentFilter.addAction("android.location.PROVIDERS_CHANGED");
        mIntentFilter.addAction(mBroadcastSendAddress1);
        mBroadcastReceiver = new NetworkChangeReceiver();
        broadcastIntent = new Intent();
        registerReceiver(mBroadcastReceiver,mIntentFilter);
        // TODO: Return the communication channel to the service.
        return this.binder;
    }
    @Override
    public void onRebind(Intent intent) {
        Log.i(LOG_TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(mBroadcastReceiver);
        Log.i(LOG_TAG, "onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }
    public static boolean returnIsConnected(){
        return isConnected1;
    }
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(mBroadcastSendAddress1) &&intent.getIntExtra("STT",0)==2){
                Log.i(LOG_TAG+".onReceive form MyTool","Save location");
                Log.i(LOG_TAG, "save location="+ isSaved +"-"+a);
                if(myTool.getYourLocation()!=null){
                    Storage.deleteFile(getApplicationContext(), "myLocation");
                    ArrayList<MyLocation> list = new ArrayList<>();
                    list.add(myTool.getYourLocation());
                    Storage.writeFile(getApplicationContext(), Storage.parseMyLocationToJson(list).toString(), "myLocation");
                    isSaved=true;
                    myTool.stopLocationUpdate();
                    broadcastIntent.setAction(mBroadcastSendAddress);
                    broadcastIntent.putExtra("isConnected", true);
                    context.sendBroadcast(broadcastIntent);
                }
            }

            Log.i(LOG_TAG, "isSaved="+ isSaved +"-"+a);
            if(isNetworkAvailable(context)&&canGetLocation(context)){
                isSaved=MainActivity.temp;
                if(!isSaved && a==0){
                    myTool=new MyTool(context,MyService.class.getSimpleName());
                    myTool.startGoogleApi();
                    a++;
                } else {
                    broadcastIntent.setAction(mBroadcastSendAddress);
                    broadcastIntent.putExtra("isConnected", true);
                    context.sendBroadcast(broadcastIntent);
                }
                isConnected = true;
                isConnected1=true;
                Log.i(LOG_TAG+".sendStatus","isConnected="+isConnected);

            }else {
                broadcastIntent.setAction(mBroadcastSendAddress);
                broadcastIntent.putExtra("isConnected", false);
                context.sendBroadcast(broadcastIntent);
                isConnected=false;
                isConnected1=false;
                Log.i(LOG_TAG+".sendStatus","isConnected="+isConnected);
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
                        //Toast.makeText(getApplicationContext(),"Ten:"+info[i].getTypeName()+"--TrangThai:"+info[i].getState().toString(),Toast.LENGTH_LONG).show();
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            Log.v(LOG_TAG, "Now you are connected to Internet!");
                            return true;
                        }
                    }
                }
            }
            Log.v(LOG_TAG, "You are not connected to Internet!");
            return false;
        }
    }

}
