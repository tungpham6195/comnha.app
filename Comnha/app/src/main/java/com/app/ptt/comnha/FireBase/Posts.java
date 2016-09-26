package com.app.ptt.comnha.FireBase;

import java.util.Map;

/**
 * Created by PTT on 9/19/2016.
 */
public class Posts {
    //không nên để là private
    String titlle, content;
    Map<String, Object> user;

    public Posts() {
    }

    public void setUser(Map<String, Object> user) {
        this.user = user;
    }

    public void setTitlle(String titlle) {
        this.titlle = titlle;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
