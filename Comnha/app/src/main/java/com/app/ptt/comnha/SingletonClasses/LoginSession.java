package com.app.ptt.comnha.SingletonClasses;

/**
 * Created by PTT on 9/27/2016.
 */
public class LoginSession {
    private static LoginSession ourInstance;
    private String userID, username, email;

    public static LoginSession getInstance() {
        if (ourInstance == null) {
            ourInstance = new LoginSession();
        }
        return ourInstance;
    }

    private LoginSession() {
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
