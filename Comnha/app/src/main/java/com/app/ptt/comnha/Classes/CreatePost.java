package com.app.ptt.comnha.Classes;

import android.content.Context;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.Posts;
import com.app.ptt.comnha.Interfaces.Transactions;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by PTT on 9/19/2016.
 */
public class CreatePost implements Transactions {
    private Posts newPost;
    private FirebaseStorage storage;
    private StorageReference stReference;
    private Firebase database;
    DatabaseReference dbReference;

    private String tittle, content, userID;
    private int vesinh, phucvu, gia;
    private Context context;

    public void setGia(int gia) {
        this.gia = gia;
    }

    public void setPhucvu(int phucvu) {
        this.phucvu = phucvu;
    }

    public void setVesinh(int vesinh) {
        this.vesinh = vesinh;
    }

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


    @Override
    public void setupFirebase() {
        Firebase.setAndroidContext(context);
        database = new Firebase("https://com-nha.firebaseio.com/");
    }

    private String getDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return df.format(c.getTime());
    }

    private String getTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        return df.format(c.getTime());
    }

    @Override
    public void createNew() {
        setupFirebase();

        newPost = new Posts();
        newPost.setTitlle(tittle);
        newPost.setContent(content);
        newPost.setDate(getDate());
        newPost.setTime(getTime());
        newPost.setGia(gia);
        newPost.setVesinh(vesinh);
        newPost.setPhucvu(phucvu);

        database.child("Posts").push().setValue(newPost, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(context, "Đăng bài bị lỗi", Toast.LENGTH_SHORT).show();
                } else {
                    database.child("Posts/" + firebase.getKey()).updateChildren(newPost.toMap(userID, ""));
//                            .child("user/"+userID).setValue(true);
//                    ).updateChildren(users);
                    Toast.makeText(context, "Đã đăng thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }
}
