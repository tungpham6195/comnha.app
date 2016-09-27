package com.app.ptt.comnha.FireBase;

/**
 * Created by PTT on 9/16/2016.
 */
public class Account {
    private String ho, ten, tenlot, email, password, birth, username;

    public Account() {
    }

    public Account(String ho, String ten, String tenlot, String email, String password, String birth, String username) {
        this.ho = ho;
        this.ten = ten;
        this.tenlot = tenlot;
        this.email = email;
        this.password = password;
        this.birth = birth;
        this.username = username;
    }

    public String getHo() {
        return ho;
    }

    public String getTen() {
        return ten;
    }

    public String getTenlot() {
        return tenlot;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getBirth() {
        return birth;
    }

    public String getUsername() {
        return username;
    }
}
