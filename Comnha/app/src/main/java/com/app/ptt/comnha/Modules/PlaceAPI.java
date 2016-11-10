package com.app.ptt.comnha.Modules;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by cuong on 10/25/2016.
 */

public class PlaceAPI {

    private static final String LOG = PlaceAPI.class.getSimpleName();
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static final String API_KEY = "AIzaSyDU6VmWjBJP-k6yWgP4v4X5EVFhyCljulo";
    String fullname;
    String addressNum;
    LocationFinderListener location;
    PlaceAttribute mPlaceAttributes;
    public PlaceAPI(String input, LocationFinderListener location){
        this.location=location;
        if(input!="") {
            Log.i(LOG+".PlaceAPI",input);
            mPlaceAttributes=new PlaceAttribute();
            location.onLocationFinderStart();
            new LongProgress().execute(input);

        }
    }
    private class LongProgress extends AsyncTask<String,Void,String>{
        String name;
        @Override
        protected String doInBackground(String... params) {
            StringBuffer stringBuffer=new StringBuffer();
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            try {
                sb.append(URLEncoder.encode(params[0], "utf8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sb.append("&key=" + API_KEY);
            sb.append("&components=country:VN");
            String link=sb.toString();
            Log.i("LINK:",link);
            try{
                URL url=new URL(link);

                InputStream is=url.openConnection().getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                String line;
                while((line=reader.readLine())!=null){
                    stringBuffer.append(line+"\n");
                }
                is.close();
                return stringBuffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObj = new JSONObject(s.toString());
                JSONArray resultsJsonResult = jsonObj.getJSONArray("results");
                if (resultsJsonResult.length()>0) {
                    //for(int k=0;k<resultsJsonResult.length();k++) {

                        JSONObject temp = resultsJsonResult.getJSONObject(0);
                        fullname = temp.getString("formatted_address");
                        // Extract the Place descriptions from the results
                        JSONArray jsonAddresses = temp.getJSONArray("address_components");
                        for (int i = 0; i < jsonAddresses.length(); i++) {
                            JSONObject jsonObject = jsonAddresses.getJSONObject(i);
                            name = jsonObject.getString("long_name");
                            JSONArray jsonType = jsonObject.getJSONArray("types");
                            List<String> list = new ArrayList<>();
                            addressNum="";
                            for (int j = 0; j < jsonType.length(); j++) {
                                list.add(jsonType.getString(j));
                                addtoPlaceAttribute(list, name);
                            }
                        }
                        mPlaceAttributes.setFullname(getFullName());
                        Log.i(LOG+".addtoPlaceAttribute","Full name: "+mPlaceAttributes.getFullname());
                        Log.i(LOG+".addtoPlaceAttribute","Address Number:"+mPlaceAttributes.getAddressNum());
                        Log.i(LOG+".addtoPlaceAttribute","Locality: "+mPlaceAttributes.getLocality());
                        Log.i(LOG+".addtoPlaceAttribute","District: "+mPlaceAttributes.getDistrict());
                        Log.i(LOG+".addtoPlaceAttribute","State: "+mPlaceAttributes.getState());
                        if (mPlaceAttributes!=null){
                            location.onLocationFinderSuccess(mPlaceAttributes);
                        }else{
                            location.onLocationFinderSuccess(null);
                        }

                   // }
                }


            } catch (JSONException e) {
                Log.e(LOG, "Cannot process JSON results", e);
            }
        }
    }
    public String  getFullName(){
        String a = mPlaceAttributes.getAddressNum();
        String b = mPlaceAttributes.getLocality();
        String c = mPlaceAttributes.getDistrict();
        String d = mPlaceAttributes.getState();
        String e = "";
        if (a != null) {
            e += a;

        }
        if (b != null) {
            if (a == null)
                e += b;
            else
                e += ", " + b;

        }
        if (c != null) {

            if (a == null && b == null)
                e += c;
            else
                e += ", " + c;

        }
        if (d != null) {
            if (a == null && b == null && c == null)
                e += d;
            else
                e += ", " + d;

        }
        return e;
    }
    public PlaceAttribute addtoPlaceAttribute(List<String> list,String name){
        if(name==null)
            name="";
        for(int i=0;i<list.size();i++){
            switch (list.get(i).toString()){
                case "street_number":
                    addressNum=name;
                    break;
                case "route":
                    addressNum+=" "+name;
                    break;
                case "sublocality_level_1":
                    mPlaceAttributes.setLocality(name);
                    break;
                case "administrative_area_level_2":

                    mPlaceAttributes.setDistrict(name);
                    break;
                case "administrative_area_level_1":
                    mPlaceAttributes.setState(name);
                    break;
                default:
                    break;
            }
        }
        mPlaceAttributes.setAddressNum(addressNum);

        return mPlaceAttributes;
    }

}