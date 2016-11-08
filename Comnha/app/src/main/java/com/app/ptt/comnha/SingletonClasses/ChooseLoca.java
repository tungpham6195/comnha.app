package com.app.ptt.comnha.SingletonClasses;

/**
 * Created by PTT on 10/5/2016.
 */
public class ChooseLoca {
    private static ChooseLoca ourInstance;
    private String locaID, name, address, tinh, huyen;

    public static ChooseLoca getInstance() {
        if (ourInstance == null) {
            ourInstance = new ChooseLoca();
        }
        return ourInstance;
    }

    private ChooseLoca() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLocaID() {
        return locaID;
    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public void setHuyen(String huyen) {
        this.huyen = huyen;
    }

    public String getTinh() {

        return tinh;
    }

    public String getHuyen() {
        return huyen;
    }

    public void setLocaID(String locaID) {
        this.locaID = locaID;
    }
}
