package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/26/2016.
 */

public class Location {
    String name, diachi, sdt, timestart, timeend,latitude,longitude;
    long giamin, giamax;

    long checkinNumb;

    public Location() {
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setTimestart(String timestart) {
        this.timestart = timestart;
    }

    public void setTimeend(String timeend) {
        this.timeend = timeend;
    }

    public void setGiamin(long giamin) {
        this.giamin = giamin;
    }

    public void setGiamax(long giamax) {
        this.giamax = giamax;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public void setCheckinNumb(long checkinNumb) {
        this.checkinNumb = checkinNumb;
    }

    public Map<String, Object> toMap(String postID) {
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
        result.put("checkinNumb", checkinNumb);
        result.put("posts/" + postID, true);
        return result;
    }
}
