package com.app.ptt.comnha.FireBase;

/**
 * Created by PTT on 9/16/2016.
 */
public class Account {
    String ho, ten, tenlot, password, birth;

    public Account() {
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public void setTenlot(String tenlot) {
        this.tenlot = tenlot;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBirth(String birth) {
        this.birth = birth;
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


    public String getPassword() {
        return password;
    }

    public String getBirth() {
        return birth;
    }

}
