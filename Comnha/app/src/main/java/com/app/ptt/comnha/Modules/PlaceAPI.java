package com.app.ptt.comnha.Modules;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
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

/**
 * Created by cuong on 10/25/2016.
 */

public class PlaceAPI {
//    Boolean isCorrectPlace=false;
//    ArrayList<String> resultList=null;
//    public ArrayList<String> autocomplete(String input, Context mContext) {
//        resultList=new ArrayList<>();
//        Geocoder gc=new Geocoder(mContext, Locale.getDefault());
//        try {
//            List<Address> list=gc.getFromLocationName(input,5);
//            for (Address address:list) {
//                String a = address.getAddressLine(0);
//                String b = address.getSubLocality();
//                String c = address.getSubAdminArea();
//
//                String d = address.getAdminArea();
//                String e="";
//                if(a!=null){
//                    e+=a;
//                    if(b!=null ){
//                        if(a!=null)
//                         e+= ", "+b;
//                        else
//                            e+=b;
//                    }
//                    if(c!=null ){
//                        if((a!=null||b!=null))
//                        e+=", "+c;
//                        else{
//                            e+=c;
//                        }
//                        if(d!=null){
//                            if((a!=null||b!=null||c!=null))
//                            e+=", "+d;
//                            else
//                                e+=d;
//                        }
//                    }
//                }
//                resultList.add(e);
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(resultList.size()>0)
//            isCorrectPlace=true;
//        return resultList;
//    }
    private static final String LOG = PlaceAPI.class.getSimpleName();
    PlaceAttribute mPlaceAttributes=new PlaceAttribute();
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static final String API_KEY = "AIzaSyDU6VmWjBJP-k6yWgP4v4X5EVFhyCljulo";
    String fullname;
    public PlaceAttribute autocomplete (String input) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append( URLEncoder.encode(input, "utf8"));
            sb.append("&key=" + API_KEY);
            Log.i("LOG NE",sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG, "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.e(LOG, "Error connecting to Places API", e);
            return null;
        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
        }
        try {
            String name;
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray resultsJsonResult = jsonObj.getJSONArray("results");
            if (resultsJsonResult.length()>0) {
                JSONObject a = resultsJsonResult.getJSONObject(0);
                fullname = a.getString("formatted_address");
                // Extract the Place descriptions from the results
                JSONArray jsonAddresses=a.getJSONArray("address_components");
                for(int i=0;i<jsonAddresses.length();i++){
                    JSONObject jsonObject=jsonAddresses.getJSONObject(i);
                    name=jsonObject.getString("long_name");
                    JSONArray jsonType=jsonObject.getJSONArray("types");
                    List<String> temp = new ArrayList<>();
                    for(int j=0;j<jsonType.length();j++) {
                        temp.add(jsonType.getString(j));
                        addtoPlaceAttribute(temp,name);
                    }



                }
                mPlaceAttributes.setFullname(mPlaceAttributes.getStreet_number()+" "
                        +mPlaceAttributes.getRoute()+", "
                        +mPlaceAttributes.getLocality()+", "
                        +mPlaceAttributes.getDistrict()+", "
                        +mPlaceAttributes.getState());
                return mPlaceAttributes;
            }


        } catch (JSONException e) {
            Log.e(LOG, "Cannot process JSON results", e);
        }
        return null;
    }
    public PlaceAttribute addtoPlaceAttribute(List<String> list,String name){
        for(int i=0;i<list.size();i++){
            switch (list.get(i).toString()){
                case "street_number":
                    mPlaceAttributes.setStreet_number(name);
                    break;
                case "route":
                    mPlaceAttributes.setRoute(name);
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
        Log.i(LOG+".addtoPlaceAttribute","Full name: "+mPlaceAttributes.getFullname());
        Log.i(LOG+".addtoPlaceAttribute","Route: "+mPlaceAttributes.getRoute());
        Log.i(LOG+".addtoPlaceAttribute","Locality: "+mPlaceAttributes.getLocality());
        Log.i(LOG+".addtoPlaceAttribute","District: "+mPlaceAttributes.getDistrict());
        Log.i(LOG+".addtoPlaceAttribute","State: "+mPlaceAttributes.getState());
        return mPlaceAttributes;
    }

}