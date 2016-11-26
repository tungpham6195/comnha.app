package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 11/16/2016.
 */

public class Report {
    String reportID, oldname, oldaddress, oldprovince, olddistrict, oldtimestart,
            oldtimeend, oldsdt, name, address, province, district, localID, timestart, timeend, sdt;
    long oldgiamin, oldgiamax, giamin, giamax;
    double lat, lng;

    public void setReportID(String reportID) {
        this.reportID = reportID;
    }

    public String getReportID() {
        return reportID;
    }

    public void setOld_name(String old_name) {
        this.oldname = old_name;
    }

    public void setOld_address(String old_address) {
        this.oldaddress = old_address;
    }

    public void setOld_province(String old_province) {
        this.oldprovince = old_province;
    }

    public void setOld_district(String old_district) {
        this.olddistrict = old_district;
    }

    public void setOld_timestart(String old_timestart) {
        this.oldtimestart = old_timestart;
    }

    public void setOld_timeend(String old_timeend) {
        this.oldtimeend = old_timeend;
    }

    public void setOld_sdt(String old_sdt) {
        this.oldsdt = old_sdt;
    }

    public void setOld_giamin(long old_giamin) {
        this.oldgiamin = old_giamin;
    }

    public void setOld_giamax(long old_giamax) {
        this.oldgiamax = old_giamax;
    }

    public String getOld_name() {
        return oldname;
    }

    public String getOld_address() {
        return oldaddress;
    }

    public String getOld_province() {
        return oldprovince;
    }

    public String getOld_district() {
        return olddistrict;
    }

    public String getOld_timestart() {
        return oldtimestart;
    }

    public String getOld_timeend() {
        return oldtimeend;
    }

    public String getOld_sdt() {
        return oldsdt;
    }

    public long getOld_giamin() {
        return oldgiamin;
    }

    public long getOld_giamax() {
        return oldgiamax;
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
        result.put("oldname", oldname);
        result.put("oldaddress", oldaddress);
        result.put("oldsdt", oldsdt);
        result.put("oldgiamax", oldgiamax);
        result.put("oldgiamin", oldgiamin);
        result.put("oldtimestart", oldtimestart);
        result.put("oldtimeend", oldtimeend);

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
