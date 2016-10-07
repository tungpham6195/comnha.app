package com.app.ptt.comnha.SingletonClasses;

/**
 * Created by PTT on 10/5/2016.
 */
public class ChooseLoca {
    private static ChooseLoca ourInstance;
    private String locaID;

    public static ChooseLoca getInstance() {
        if (ourInstance == null) {
            ourInstance = new ChooseLoca();
        }
        return ourInstance;
    }

    private ChooseLoca() {
    }

    public String getLocaID() {
        return locaID;
    }

    public void setLocaID(String locaID) {
        this.locaID = locaID;
    }
}
