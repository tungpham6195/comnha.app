package com.app.ptt.comnha.SingletonClasses;

import com.app.ptt.comnha.FireBase.MyLocation;

/**
 * Created by PTT on 10/5/2016.
 */
public class ReportLocal {
    private static ReportLocal ourInstance;
    private MyLocation myLocation;

    public static ReportLocal getInstance() {
        if (ourInstance == null) {
            ourInstance = new ReportLocal();
        }
        return ourInstance;
    }

    private ReportLocal() {
    }

    public void setMyLocation(MyLocation myLocation) {
        this.myLocation = myLocation;
    }

    public MyLocation getMyLocation() {

        return myLocation;
    }
}
