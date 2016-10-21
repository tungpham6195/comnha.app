package com.app.ptt.comnha.Modules;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by cuong on 10/5/2016.
 */

public class Route {
    private MyDistance distance;
    private Duration duration;
    private String district;
    private String endAddress;
    private LatLng endLocation;
    private String startAddress;
    private LatLng startLocation;
    private List<LatLng> points;
    private String tenQuan,sdt,gioMo,gioDong,LocalID;
    private Long giaMin,giaMax;

    public MyDistance getDistance() {
        return distance;
    }

    public void setDistance(MyDistance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }

    public String getTenQuan() {
        return tenQuan;
    }

    public void setTenQuan(String tenQuan) {
        this.tenQuan = tenQuan;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getGioMo() {
        return gioMo;
    }

    public void setGioMo(String gioMo) {
        this.gioMo = gioMo;
    }

    public String getGioDong() {
        return gioDong;
    }

    public void setGioDong(String gioDong) {
        this.gioDong = gioDong;
    }

    public String getLocalID() {
        return LocalID;
    }

    public void setLocalID(String localID) {
        LocalID = localID;
    }

    public Long getGiaMax() {
        return giaMax;
    }

    public void setGiaMax(Long giaMax) {
        this.giaMax = giaMax;
    }

    public Long getGiaMin() {
        return giaMin;
    }

    public void setGiaMin(Long giaMin) {
        this.giaMin = giaMin;
    }
}
