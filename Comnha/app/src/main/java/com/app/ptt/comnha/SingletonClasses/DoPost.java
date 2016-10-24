package com.app.ptt.comnha.SingletonClasses;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by PTT on 10/24/2016.
 */
public class DoPost {
    private static DoPost ourInstance;
    private long gia, vesinh, phucvu;
    private ArrayList<File> files;
    private String locaID, name, address;

    public static DoPost getInstance() {
        if (ourInstance == null) {
            ourInstance = new DoPost();
        }
        return ourInstance;
    }

    private DoPost() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLocaID() {
        return locaID;
    }

    public void setLocaID(String locaID) {
        this.locaID = locaID;
    }

    public void setPhucvu(long phucvu) {
        this.phucvu = phucvu;
    }

    public void setVesinh(long vesinh) {
        this.vesinh = vesinh;
    }

    public void setGia(long gia) {
        this.gia = gia;
    }

    public long getGia() {
        return gia;
    }

    public long getVesinh() {
        return vesinh;
    }

    public long getPhucvu() {
        return phucvu;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    public ArrayList<File> getFiles() {
        return files;
    }
}
