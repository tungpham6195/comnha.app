package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 11/16/2016.
 */

public class Report {
    String reportID, old_name, old_address, old_province, old_district, old_timestart, old_timeend, old_sdt;
    long old_giamin, old_giamax;
    double old_lat, old_lng;
    String name, address, province, district, localID, timestart, timeend, sdt;
    long giamin, giamax;
    double lat, lng;

    public void setReportID(String reportID) {
        this.reportID = reportID;
    }

    public String getReportID() {
        return reportID;
    }

    public void setOld_name(String old_name) {
        this.old_name = old_name;
    }

    public void setOld_address(String old_address) {
        this.old_address = old_address;
    }

    public void setOld_province(String old_province) {
        this.old_province = old_province;
    }

    public void setOld_district(String old_district) {
        this.old_district = old_district;
    }

    public void setOld_timestart(String old_timestart) {
        this.old_timestart = old_timestart;
    }

    public void setOld_timeend(String old_timeend) {
        this.old_timeend = old_timeend;
    }

    public void setOld_sdt(String old_sdt) {
        this.old_sdt = old_sdt;
    }

    public void setOld_giamin(long old_giamin) {
        this.old_giamin = old_giamin;
    }

    public void setOld_giamax(long old_giamax) {
        this.old_giamax = old_giamax;
    }

    public void setOld_lat(double old_lat) {
        this.old_lat = old_lat;
    }

    public void setOld_lng(double old_lng) {
        this.old_lng = old_lng;
    }

    public String getOld_name() {
        return old_name;
    }

    public String getOld_address() {
        return old_address;
    }

    public String getOld_province() {
        return old_province;
    }

    public String getOld_district() {
        return old_district;
    }

    public String getOld_timestart() {
        return old_timestart;
    }

    public String getOld_timeend() {
        return old_timeend;
    }

    public String getOld_sdt() {
        return old_sdt;
    }

    public long getOld_giamin() {
        return old_giamin;
    }

    public long getOld_giamax() {
        return old_giamax;
    }

    public double getOld_lat() {
        return old_lat;
    }

    public double getOld_lng() {
        return old_lng;
    }

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
        result.put("oldname", old_name);
        result.put("oldaddress", old_address);
        result.put("oldsdt", old_sdt);
        result.put("oldgiamax", old_giamax);
        result.put("oldgiamin", old_giamin);
        result.put("oldtimestart", old_timestart);
        result.put("oldtimeend", old_timeend);

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
