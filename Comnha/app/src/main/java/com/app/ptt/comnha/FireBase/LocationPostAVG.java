package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 10/31/2016.
 */

public class LocationPostAVG {
    String locaID;
    long giaAVG, vsAVG, pvAVG, tongAVG;

    public void setLocaID(String locaID) {
        this.locaID = locaID;
    }

    public void setGiaAVG(long giaAVG) {
        this.giaAVG = giaAVG;
    }

    public void setVsAVG(long vsAVG) {
        this.vsAVG = vsAVG;
    }

    public void setPvAVG(long pvAVG) {
        this.pvAVG = pvAVG;
    }

    public void setTongAVG(long tongAVG) {
        this.tongAVG = tongAVG;
    }

    public String getLocaID() {
        return locaID;
    }

    public long getGiaAVG() {
        return giaAVG;
    }

    public long getVsAVG() {
        return vsAVG;
    }

    public long getPvAVG() {
        return pvAVG;
    }

    public long getTongAVG() {
        return tongAVG;
    }

    public LocationPostAVG() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("giaAVG", giaAVG);
        result.put("vsAVG", vsAVG);
        result.put("pvAVG", pvAVG);
        result.put("tongAVG", tongAVG);
        return result;
    }
}
