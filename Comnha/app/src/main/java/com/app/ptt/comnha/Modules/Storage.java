package com.app.ptt.comnha.Modules;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by cuong on 11/7/2016.
 */

public class Storage {
    Context mContext;
    String filename = "myfile.json";
    String string = "{\n" +
            "  \"id\": 111 ,\n" +
            "  \"name\":\"Microsoft\",\n" +
            "  \"websites\": [\n" +
            "     \"http://microsoft.com\",\n" +
            "     \"http://msn.com\",\n" +
            "     \"http://hotmail.com\"\n" +
            "  ],\n" +
            "  \"address\":{\n" +
            "     \"street\":\"1 Microsoft Way\",\n" +
            "     \"city\":\"Redmond\"\n" +
            "  }\n" +
            "}";
    FileOutputStream outputStream;

    public Storage(Context context){
        this.mContext=context;
    }
    public void writeFile(){
        try {
            File file = new File(mContext.getCacheDir(), filename);
           // File file = new File(mContext.getCacheDir(), filename);
            outputStream=new FileOutputStream(file);
            //outputStream=mContext.openFileOutput(filename,Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
            Toast.makeText(mContext,"save ok",Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String readFile(){
        try {
            File file = new File(mContext.getCacheDir(), filename);
            //file.delete();
            FileInputStream inputStream=new FileInputStream(file);
                    //mContext.openFileInput(filename);
            BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb=new StringBuilder();
            String s=null;
            while ((s=br.readLine())!=null){
                sb.append(s);
                sb.append("\n");
            }
            return sb.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void readCompanyJSONFile() throws IOException,JSONException {

        // Đọc nội dung text của file company.json
        String jsonText = readFile();

        // Đối tượng JSONObject gốc mô tả toàn bộ tài liệu JSON.
        JSONObject jsonRoot = new JSONObject(jsonText);


        int id= jsonRoot.getInt("id");
        String name = jsonRoot.getString("name");

        JSONArray jsonArray = jsonRoot.getJSONArray("websites");
        String[] websites = new String[jsonArray.length()];

        for(int i=0;i < jsonArray.length();i++) {
            websites[i] = jsonArray.getString(i);
        }

        JSONObject jsonAddress = jsonRoot.getJSONObject("address");
        String street = jsonAddress.getString("street");
        String city = jsonAddress.getString("city");
        Toast.makeText(mContext,"Id:"+id+". Name:"+name+". Street"+street+". City:"+city+". Website"+websites[0].toString(),Toast.LENGTH_LONG).show();

    }
}
