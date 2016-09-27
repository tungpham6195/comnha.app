package com.app.ptt.comnha.SingletonClasses;

/**
 * Created by PTT on 9/27/2016.
 */
public class LoginSession {
    private static LoginSession ourInstance = new LoginSession();
    private String userID;

    public static LoginSession getInstance() {
        if(ourInstance==null){
            ourInstance=new LoginSession();
        }
        return ourInstance;
    }

    private LoginSession() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
