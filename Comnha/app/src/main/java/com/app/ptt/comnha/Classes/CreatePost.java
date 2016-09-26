package com.app.ptt.comnha.Classes;

import android.content.Context;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.Posts;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PTT on 9/19/2016.
 */
public class CreatePost {
    private Posts newPost;
    private FirebaseStorage storage;
    private StorageReference stReference;
    private Firebase database;
    DatabaseReference dbReference;

    private String tittle, content, userID;
    private Context context;

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public CreatePost() {

        //Create reference of db


        //Create reference of storage
        storage = FirebaseStorage.getInstance();
        stReference = storage.getReferenceFromUrl("gs://com-nha.appspot.com");
    }

    private void setupFireBase() {
        Firebase.setAndroidContext(context);
        database = new Firebase("https://com-nha.firebaseio.com/");
    }

    public void doPost() {
        setupFireBase();
        newPost = new Posts();
        newPost.setTitlle(tittle);
        newPost.setContent(content);
        database.child("Posts").push().setValue(newPost, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(context, "Đăng bài bị lỗi", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> users = new HashMap<>();
                    users.put("users/" + userID, true);
                    database.child("Posts/"+firebase.getKey()).updateChildren(users);
//                            .child("user/"+userID).setValue(true);
//                    ).updateChildren(users);
                    Toast.makeText(context, "Đã đăng thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
