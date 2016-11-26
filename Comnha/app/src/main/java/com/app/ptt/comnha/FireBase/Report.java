package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 11/16/2016.
 */

public class Report {
    String name, address, province, district, localID, timestart, timeend, sdt;
    long giamin, giamax;
    double lat, lng;

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {

        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getSdt() {

        return sdt;
    }

    public void setTimestart(String timestart) {
        this.timestart = timestart;
    }

    public void setTimeend(String timeend) {
        this.timeend = timeend;
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

    public void setGiamin(long giamin) {

        this.giamin = giamin;
    }

    public void setGiamax(long giamax) {
        this.giamax = giamax;
    }

    public void setLocalID(String localID) {
        this.localID = localID;
    }

    public String getLocalID() {

        return localID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getProvince() {
        return province;
    }

    public String getDistrict() {
        return district;
    }

    public Report() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("address", address);
        result.put("sdt", sdt);
        result.put("giamax", giamax);
        result.put("giamin", giamin);
        result.put("timestart", timestart);
        result.put("timeend", timeend);
        result.put("localID", localID);
        result.put("lat", lat);
        result.put("lng", lng);
        return result;
    }
}
