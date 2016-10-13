package com.app.ptt.comnha.Modules;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class getDistrict {
    private static final String LOG = "getDistrict";
    private Geocoder geocoder;
    private double latitude,longitude;
    private List<Address> addresses;
    public getDistrict(Double latitude, Double longitude, Geocoder geocoder) throws IOException {
        this.latitude=latitude;
        this.longitude=longitude;
        this.geocoder=geocoder;
        addresses=new ArrayList<>();
    }
    public String execute() throws NullPointerException, IOException {
         addresses= geocoder.getFromLocation(latitude, longitude, 1);
        if(addresses.size()>0) {
            Log.i(LOG,addresses.get(0).getSubAdminArea()+"");
            return addresses.get(0).getSubAdminArea();

        }else
            return "";
    }

}
