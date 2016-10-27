package com.app.ptt.comnha.Modules;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by cuong on 10/25/2016.
 */

public class PlaceAPI {
    Boolean isCorrectPlace=false;
    ArrayList<String> resultList=null;
    public ArrayList<String> autocomplete(String input, Context mContext) {
        resultList=new ArrayList<>();
        Geocoder gc=new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> list=gc.getFromLocationName(input,5);
            for (Address address:list) {
                String a = address.getAddressLine(0);
                String b = address.getSubLocality();
                String c = address.getSubAdminArea();
                String d = address.getAdminArea();
                String e="";
                if(a!=null){
                    e+=a;
                    if(b!=null ){
                        if(a!=null)
                         e+= ", "+b;
                        else
                            e+=b;
                    }
                    if(c!=null ){
                        if((a!=null||b!=null))
                        e+=", "+c;
                        else{
                            e+=c;
                        }
                        if(d!=null){
                            if((a!=null||b!=null||c!=null))
                            e+=", "+d;
                            else
                                e+=d;
                        }
                    }
                }
                resultList.add(e);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(resultList.size()>0)
            isCorrectPlace=true;
        return resultList;
    }
}
