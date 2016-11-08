package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 11/8/2016.
 */

public class FoodCategory {
    String name, foodCategoryID;

    public void setName(String name) {
        this.name = name;
    }

    public void setFoodCategoryID(String foodCategoryID) {
        this.foodCategoryID = foodCategoryID;
    }

    public String getName() {
        return name;
    }

    public String getFoodCategoryID() {
        return foodCategoryID;
    }

    public FoodCategory() {
    }

    public Map<String, Object> topMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        return result;
    }
}
