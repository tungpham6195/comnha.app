package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/19/2016.
 */
public class Posts {
    //không nên để là private
    String titlle, content, date, time;
    long vesinh, phucvu, gia;

    public Posts() {
    }

    public void setVesinh(long vesinh) {
        this.vesinh = vesinh;
    }

    public void setPhucvu(long phucvu) {
        this.phucvu = phucvu;
    }

    public void setGia(long gia) {
        this.gia = gia;
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

    public Map<String, Object> toMap(String userID, String locID) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("titlle", titlle);
        result.put("content", content);
        result.put("date", date);
        result.put("time", time);
        result.put("vesinh", vesinh);
        result.put("phucvu", phucvu);
        result.put("gia", gia);
        result.put("users/" + userID, true);
        result.put("locations/" + locID, true);
        return result;
    }
}
