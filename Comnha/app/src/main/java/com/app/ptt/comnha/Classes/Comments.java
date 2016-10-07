package com.app.ptt.comnha.Classes;

import android.content.Context;

/**
 * Created by PTT on 10/6/2016.
 */

public class Comments {
    String content, time, userID, postID,username;
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public Comments() {
    }

}
