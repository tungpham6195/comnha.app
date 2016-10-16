package com.app.ptt.comnha.Classes;

import android.content.Context;

import com.firebase.client.Firebase;

/**
 * Created by PTT on 9/26/2016.
 */

public class Locations {
    private String name, diachi, sdt, timestart, timeend, latitude, longitude;
    private long giamin, giamax;
    private long checkinNumb;

    private Firebase lcaRef;

    private Context lcaContext;

    public Locations(Context lcaContext) {
        this.lcaContext = lcaContext;
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

    private void setupFirebase() {

    }

    public boolean createNew() {
        final boolean[] check = new boolean[1];
        setupFirebase();

        return check[0];
    }

}
