package com.app.ptt.comnha.SingletonClasses;

/**
 * Created by PTT on 10/5/2016.
 */
public class DoRate {
    private static DoRate ourInstance;
    private long gia, vesinh, phucvu;

    public static DoRate getInstance() {
        if (ourInstance == null) {
            ourInstance = new DoRate();
        }
        return ourInstance;
    }

    private DoRate() {
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
}
