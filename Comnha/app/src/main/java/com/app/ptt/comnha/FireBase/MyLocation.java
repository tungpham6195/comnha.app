package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/26/2016.
 */

public class MyLocation {
    String name, diachi, sdt, timestart, timeend;
    long giamin, giamax;
    String locaID;
    long giaTong = 0, vsTong = 0, pvTong = 0;
    long size=0;

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

    public long getGiamin() {
        return giamin;
    }

    public long getGiamax() {
        return giamax;
    }

    public String getLocaID() {
        return locaID;
    }

    public void setGiaTong(long giaTong) {
        this.giaTong = giaTong;
    }

    public void setVsTong(long vsTong) {
        this.vsTong = vsTong;
    }

    public void setPvTong(long pvTong) {
        this.pvTong = pvTong;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public long getPvTong() {
        return pvTong;
    }

    public long getVsTong() {
        return vsTong;
    }

    public long getGiaTong() {
        return giaTong;
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
        result.put("giaTong", giaTong);
        result.put("vsTong", vsTong);
        result.put("pvTong", pvTong);
        result.put("size", size);
        return result;
    }
}
