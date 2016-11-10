package com.app.ptt.comnha.Modules;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.JsonWriter;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.MyLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONAware;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cuong on 11/7/2016.
 */

public class Storage {
    Context mContext;
    String filename = "myfile.json";



    //public Storage(Context context){
       // this.mContext=context;
   // }
    public static void writeFile(Context mContext,String input){
        FileOutputStream outputStream;
        try {
            File file = new File(mContext.getCacheDir(), "myfile.json");
            outputStream=new FileOutputStream(file);
            outputStream.write(input.getBytes());
            outputStream.close();
            Toast.makeText(mContext,"save ok",Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(Context mContext){

        try {
            File file = new File(mContext.getCacheDir(), "myfile.json");
            //file.delete();
            FileInputStream inputStream=new FileInputStream(file);
            BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb=new StringBuilder();
            String s=null;
            while ((s=br.readLine())!=null){
                sb.append(s);
                sb.append("\n");
            }
            Toast.makeText(mContext,sb.toString(),Toast.LENGTH_LONG).show();
            return sb.toString();


        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();


        }
        return null;

    }
    public static ArrayList< MyLocation > readJSONFile(String json) throws JSONException, ParseException {
        ArrayList< MyLocation > locations=new ArrayList<>();
        MyLocation myLocation;
        JSONArray jsonArray = new JSONArray(json);
        for(int i=0;i<jsonArray.length();i++){
            myLocation=new MyLocation();
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            myLocation.setLocaID(jsonObject.getString("locaID"));
            myLocation.setKhoangcach(jsonObject.getString("khoangcach"));
            myLocation.setName(jsonObject.getString("name"));
            myLocation.setDiachi(jsonObject.getString("diachi"));
            myLocation.setSdt(jsonObject.getString("sdt"));
            myLocation.setTimestart(jsonObject.getString("timestart"));
            myLocation.setTimeend(jsonObject.getString("timeend"));
            myLocation.setTinhtp(jsonObject.getString("tinhtp"));
            myLocation.setQuanhuyen(jsonObject.getString("quanhuyen"));
            myLocation.setLat(jsonObject.getDouble("lat"));
            myLocation.setLng(jsonObject.getDouble("lng"));
            myLocation.setGiamax(jsonObject.getLong("giamax"));
            myLocation.setGiamin(jsonObject.getLong("giamin"));
            myLocation.setGiaTong(jsonObject.getLong("giaTong"));
            myLocation.setVsTong(jsonObject.getLong("vsTong"));
            myLocation.setPvTong(jsonObject.getLong("pvTong"));
            myLocation.setSize(jsonObject.getLong("size"));
            myLocation.setGiaAVG(jsonObject.getLong("giaAVG"));
            myLocation.setPvAVG(jsonObject.getLong("pvAVG"));
            myLocation.setVsAVG(jsonObject.getLong("vsAVG"));
            myLocation.setTongAVG(jsonObject.getLong("tongAVG"));
            locations.add(myLocation);
        }
        if(locations.size()>0)
            return locations;
        return null;




    }
    public static void  parseToJson(Writer out,ArrayList< MyLocation >locations) throws IOException {
        JsonWriter jsonWriter = new JsonWriter(out);
        jsonWriter.beginArray();
            for (MyLocation location:locations){
            jsonWriter.beginObject();
            jsonWriter.name("locaID").value(location.getLocaID());
            jsonWriter.name("name").value(location.getName());
            jsonWriter.name("diachi").value(location.getDiachi());
            jsonWriter.name("sdt").value(location.getSdt());
            jsonWriter.name("timestart").value(location.getTimestart());
            jsonWriter.name("timeend").value(location.getTimeend());
            jsonWriter.name("tinhtp").value(location.getTinhtp());
            jsonWriter.name("quanhuyen").value(location.getQuanhuyen());
            jsonWriter.name("khoangcach").value(location.getKhoangcach());
            jsonWriter.name("lat").value(location.getLat());
            jsonWriter.name("lng").value(location.getLng());
            jsonWriter.name("giamin").value(location.getGiamin());
            jsonWriter.name("giamax").value(location.getGiamax());
            jsonWriter.name("giaTong").value(location.getGiaTong());
            jsonWriter.name("vsTong").value(location.getVsTong());
            jsonWriter.name("pvTong").value(location.getPvTong());
            jsonWriter.name("size").value(location.getSize());
            jsonWriter.name("giaAVG").value(location.getGiaAVG());
            jsonWriter.name("vsAVG").value(location.getVsAVG());
            jsonWriter.name("pvAVG").value(location.getPvAVG());
            jsonWriter.name("tongAVG").value(location.getTongAVG());
            jsonWriter.endObject();
            }
        jsonWriter.endArray();
    }

}
