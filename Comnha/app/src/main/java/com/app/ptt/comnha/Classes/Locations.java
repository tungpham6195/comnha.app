package com.app.ptt.comnha.Classes;

import android.content.Context;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.Location;
import com.app.ptt.comnha.Interfaces.Transactions;
import com.app.ptt.comnha.R;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by PTT on 9/26/2016.
 */

public class Locations implements Transactions {
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

    @Override
    public void setupFirebase() {
        Firebase.setAndroidContext(lcaContext);
        lcaRef = new Firebase("https://com-nha.firebaseio.com/");
    }

    @Override
    public void createNew() {
        setupFirebase();
        final Location newLocation = new Location();
        newLocation.setName(name);
        newLocation.setDiachi(diachi);
        newLocation.setSdt(sdt);
        newLocation.setTimestart(timestart);
        newLocation.setTimeend(timeend);
        newLocation.setGiamin(giamin);
        newLocation.setGiamax(giamax);
        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);
        lcaRef.child("Locations").push().setValue(newLocation, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    Toast.makeText(lcaContext,R.string.text_noti_addloca_succes, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(lcaContext, "Lỗi: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }
}
