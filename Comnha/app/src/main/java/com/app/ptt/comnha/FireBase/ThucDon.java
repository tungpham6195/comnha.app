package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 10/29/2016.
 */

public class ThucDon {
    String tenmon, locaID, locaName, diachi;
    long gia;
    String monID;

    public void setMonID(String monID) {
        this.monID = monID;
    }

    public String getMonID() {

        return monID;
    }

    public String getTenmon() {
        return tenmon;
    }

    public long getGia() {
        return gia;
    }

    public void setTenmon(String tenmon) {
        this.tenmon = tenmon;
    }

    public void setGia(long gia) {
        this.gia = gia;
    }

    public String getLocaID() {
        return locaID;
    }

    public String getLocaName() {
        return locaName;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setLocaID(String locaID) {
        this.locaID = locaID;
    }

    public void setLocaName(String locaName) {
        this.locaName = locaName;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public ThucDon() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("tenmon", tenmon);
        result.put("gia", gia);
        result.put("locaID", locaID);
        result.put("locaName", locaName);
        result.put("diachi", diachi);
        return result;
    }
}
