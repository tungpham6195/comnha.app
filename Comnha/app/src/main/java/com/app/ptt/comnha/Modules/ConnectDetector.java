package com.app.ptt.comnha.Modules;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by CIQAZ on 9/26/2016.
 */

public class ConnectDetector {
    private Context context;
    public ConnectDetector(Context context){
        this.context=context;
    }
    public boolean isConnectInternet(){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            NetworkInfo[] info=connectivityManager.getAllNetworkInfo();
            if(info!=null){
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
            }
        }
        return false;
    }
}
