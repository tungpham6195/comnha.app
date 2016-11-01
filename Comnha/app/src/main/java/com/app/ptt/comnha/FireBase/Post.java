package com.app.ptt.comnha.FireBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/19/2016.
 */
public class Post {
    //không nên để là private
    String title, content, date, time, postID, username, uid, locaID, locaName, diachi;
    long vesinh, phucvu, gia;
    int likeCount, commentCount;
    Map<String, Boolean> likes;

    public Post() {
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getDiachi() {

        return diachi;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getCommentCount() {

        return commentCount;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public Map<String, Boolean> getLikes() {

        return likes;
    }

    public void setLocaID(String locaID) {
        this.locaID = locaID;
    }

    public void setLocaName(String locaName) {
        this.locaName = locaName;
    }

    public String getLocaID() {
        return locaID;
    }

    public String getLocaName() {
        return locaName;
    }

    public int getLikeCount() {
        return likeCount;
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

    public String getUid() {
        return uid;
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

    public String getPostID() {
        return postID;
    }

    public String getUsername() {
        return username;
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

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("content", content);
        result.put("date", date);
        result.put("time", time);
        result.put("uid", uid);
        result.put("username", username);
        result.put("vesinh", vesinh);
        result.put("phucvu", phucvu);
        result.put("gia", gia);
        result.put("commentCount", commentCount);
        result.put("likeCount", likeCount);
        result.put("likes", likes);
        result.put("locaID", locaID);
        result.put("locaName", locaName);
        result.put("diachi", diachi);
        return result;
    }
}
