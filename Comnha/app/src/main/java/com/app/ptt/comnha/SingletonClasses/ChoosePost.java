package com.app.ptt.comnha.SingletonClasses;

/**
 * Created by PTT on 10/5/2016.
 */
public class ChoosePost {
    private static ChoosePost ourInstance;
    private String postID, tinh, huyen;

    public static ChoosePost getInstance() {
        if (ourInstance == null) {
            ourInstance = new ChoosePost();
        }
        return ourInstance;
    }

    private ChoosePost() {
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public String getHuyen() {
        return huyen;
    }

    public String getTinh() {
        return tinh;
    }

    public String getPostID() {
        return postID;
    }

    public void setHuyen(String huyen) {
        this.huyen = huyen;

    }
}
