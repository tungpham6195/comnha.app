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

/**
 * Created by cuong on 10/25/2016.
 */

public class PlaceAPI {

    private static final String LOG = PlaceAPI.class.getSimpleName();
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static final String API_KEY = "AIzaSyDU6VmWjBJP-k6yWgP4v4X5EVFhyCljulo";
    String fullname;
    PlaceAttribute mPlaceAttributes;
    ArrayList<PlaceAttribute> list=new ArrayList<>();
    public ArrayList<PlaceAttribute> autocomplete (String input) {
        if(input!="") {
            new LongProgress().execute(input);
            Log.i("CCCCCCCCCCCCCCCCCcc", list.size() + "");
            if (list.size() > 0)
                return list;
        }
        return null;
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
                    list=new ArrayList<>();
                    for(int k=0;k<resultsJsonResult.length();k++) {
                        mPlaceAttributes=new PlaceAttribute();
                        JSONObject temp = resultsJsonResult.getJSONObject(k);
                        fullname = temp.getString("formatted_address");
                        // Extract the Place descriptions from the results
                        JSONArray jsonAddresses = temp.getJSONArray("address_components");
                        for (int i = 0; i < jsonAddresses.length(); i++) {
                            JSONObject jsonObject = jsonAddresses.getJSONObject(i);
                            name = jsonObject.getString("long_name");
                            JSONArray jsonType = jsonObject.getJSONArray("types");
                            List<String> list = new ArrayList<>();
                            for (int j = 0; j < jsonType.length(); j++) {
                                list.add(jsonType.getString(j));
                                addtoPlaceAttribute(list, name);
                            }
                        }
                        mPlaceAttributes.setFullname(fullname);
                        list.add(mPlaceAttributes);
                    }
                }


            } catch (JSONException e) {
                Log.e(LOG, "Cannot process JSON results", e);
            }
        }
    }
    public PlaceAttribute addtoPlaceAttribute(List<String> list,String name){
        if(name==null)
            name="";
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
        Log.i(LOG+".addtoPlaceAttribute","Street number: "+mPlaceAttributes.getStreet_number());
        Log.i(LOG+".addtoPlaceAttribute","Route: "+mPlaceAttributes.getRoute());
        Log.i(LOG+".addtoPlaceAttribute","Locality: "+mPlaceAttributes.getLocality());
        Log.i(LOG+".addtoPlaceAttribute","District: "+mPlaceAttributes.getDistrict());
        Log.i(LOG+".addtoPlaceAttribute","State: "+mPlaceAttributes.getState());
        return mPlaceAttributes;
    }

}