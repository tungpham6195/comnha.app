package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/26/2016.
 */

public class MyLocation {
    String name, diachi, sdt, timestart, timeend, latitude, longitude;
    long giamin, giamax;
    String locaID;

    public MyLocation() {
    }

    public void setLocaID(String locaID) {
        this.locaID = locaID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public void setTimestart(String timestart) {
        this.timestart = timestart;
    }

    public void setTimeend(String timeend) {
        this.timeend = timeend;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setGiamin(long giamin) {
        this.giamin = giamin;
    }

    public void setGiamax(long giamax) {
        this.giamax = giamax;
    }

    public String getName() {
        return name;
    }

    public String getDiachi() {
        return diachi;
    }

    public String getSdt() {
        return sdt;
    }

    public String getTimestart() {
        return timestart;
    }

    public String getTimeend() {
        return timeend;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public long getGiamin() {
        return giamin;
    }

    public long getGiamax() {
        return giamax;
    }

    public String getLocaID() {
        return locaID;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("diachi", diachi);
        result.put("sdt", sdt);
        result.put("timeend", timeend);
        result.put("timestart", timestart);
        result.put("giamax", giamax);
        result.put("giamin", giamin);
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        return result;
    }
}
