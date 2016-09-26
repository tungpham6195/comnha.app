package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/19/2016.
 */
public class Posts {
    //không nên để là private
    String titlle, content, date, time;

    public Posts() {
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitlle(String titlle) {
        this.titlle = titlle;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> toMap(String userID) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("titlle", titlle);
        result.put("content", content);
        result.put("users/" + userID, true);
        return null;
    }
}
