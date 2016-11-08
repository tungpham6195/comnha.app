package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 10/29/2016.
 */

public class Food {
    String tenmon, monID, locaID, foodCategoID;

    long gia;

    public void setLocaID(String locaID) {
        this.locaID = locaID;
    }

    public void setFoodCategoID(String foodCategoID) {
        this.foodCategoID = foodCategoID;
    }

    public String getLocaID() {

        return locaID;
    }

    public String getFoodCategoID() {
        return foodCategoID;
    }

    public void setMonID(String monID) {
        this.monID = monID;
    }

    public String getMonID() {

        return monID;
    }

    public String getTenmon() {
        return tenmon;
    }

    public long getGia() {
        return gia;
    }

    public void setTenmon(String tenmon) {
        this.tenmon = tenmon;
    }

    public void setGia(long gia) {
        this.gia = gia;
    }

    public Food() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("tenmon", tenmon);
        result.put("gia", gia);
        result.put("locaID", locaID);
        result.put("foodCategoID", foodCategoID);
        return result;
    }
}
