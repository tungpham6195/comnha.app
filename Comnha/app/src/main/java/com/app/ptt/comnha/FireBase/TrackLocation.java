package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/26/2016.
 */

public class TrackLocation {
    String name, diachi;
    int countTrack;

    public void setCountTrack(int countTrack) {
        this.countTrack = countTrack;
    }

    public int getCountTrack() {
        return countTrack;
    }

    String locaID;
    long tongAVG;



    public void setTongAVG(long tongAVG) {
        this.tongAVG = tongAVG;
    }


    public long getTongAVG() {
        return tongAVG;
    }

    public TrackLocation() {
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


    public String getName() {
        return name;
    }

    public String getDiachi() {
        return diachi;
    }


    public String getLocaID() {
        return locaID;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("diachi", diachi);
        result.put("countTrack", countTrack);
        return result;
    }

}
