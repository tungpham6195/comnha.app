package com.app.ptt.comnha.Modules;

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

/**
 * Created by cuong on 10/25/2016.
 */

public class PlaceAPI {
    private static final String LOG=PlaceAPI.class.getSimpleName();
    private static final String YOUR_API_KEY="AIzaSyCTvqqhbnL3huvHIL8ggBEVCPp_Pz8UQ6I";
    private static final String PLACE_API_LINK="https://maps.googleapis.com/maps/api/place/autocomplete/json?key="+YOUR_API_KEY+"&type(geocode)" + "&input=";
    public ArrayList<String> autocomplete(String input){
        ArrayList<String> resultList=null;
        HttpURLConnection conn=null;
        StringBuilder jsonResults=new StringBuilder();
        try{
            StringBuilder sb=new StringBuilder(PLACE_API_LINK);
            sb.append(URLEncoder.encode(input,"utf8"));
            URL url=new URL(sb.toString());
            conn=(HttpURLConnection) url.openConnection();
            InputStreamReader in=new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff=new char[1024];
            while((read=in.read(buff))!=-1){
                jsonResults.append(buff,0,read);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return resultList;
        } catch (IOException e) {
            e.printStackTrace();
            return resultList;
        } finally {
            if(conn!=null){
                conn.disconnect();
            }
        }
        try{
            JSONObject jsonObject=new JSONObject(jsonResults.toString());
            JSONArray jsonArray=jsonObject.getJSONArray("predictions");
            resultList=new ArrayList<>(jsonArray.length());
            for(int i=0;i<jsonArray.length();i++){
                resultList.add(jsonArray.getJSONObject(i).getString("description"));
                //JSONObject jsonStruct_Format=jsonArray.getJSONObject(i).getJSONObject("structured_formatting");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
