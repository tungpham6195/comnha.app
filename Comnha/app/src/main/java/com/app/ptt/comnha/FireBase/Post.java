package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/19/2016.
 */
public class Post {
    //không nên để là private
    String title, content, date, time, postID;
    long vesinh, phucvu, gia;

    public Post() {
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostID() {
        return postID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public long getVesinh() {
        return vesinh;
    }

    public long getPhucvu() {
        return phucvu;
    }

    public long getGia() {
        return gia;
    }

    public Map<String, Object> toMap(String userID, String locID) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("content", content);
        result.put("date", date);
        result.put("time", time);
        result.put("vesinh", vesinh);
        result.put("phucvu", phucvu);
        result.put("gia", gia);
        return result;
    }
}
