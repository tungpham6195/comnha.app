package com.app.ptt.comnha.FireBase;

/**
 * Created by PTT on 10/5/2016.
 */

public class Comment {
    String content, time, date, userID;


    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getUserID() {
        return userID;
    }

    public Comment() {
    }
}
