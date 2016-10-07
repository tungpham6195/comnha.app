package com.app.ptt.comnha.Modules;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by cuong on 10/5/2016.
 */

public class Route {
    public MyDistance distance;
    public Duration duration;
    public String district;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public List<LatLng> points;
}
