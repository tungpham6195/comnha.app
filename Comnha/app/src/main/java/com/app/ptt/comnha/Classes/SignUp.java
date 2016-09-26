package com.app.ptt.comnha.Classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.app.ptt.comnha.FireBase.Accounts;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by PTT on 9/16/2016.
 */
public class SignUp {
    private String ho, ten, tenlot, email, password, confirmPass, birth, username;

    private final Context suContext;
    private Firebase suRef;

    public SignUp(Context suContext) {
        this.suContext = suContext;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void doSignUp() {
        Firebase.setAndroidContext(suContext);
        suRef = new Firebase("https://com-nha.firebaseio.com/");


        if (!password.equals(confirmPass)) {
            Toast.makeText(suContext, "Mật khẩu xác nhận không đúng!!!", Toast.LENGTH_SHORT).show();
        } else {

            final FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String userID = task.getResult().getUser().getUid();
                        Accounts newAccount = new Accounts(ho, ten, tenlot, email, password, birth, username);
                        suRef.child("Users/" + userID).setValue(newAccount, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null) {
                                    Toast.makeText(suContext, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(suContext, "Đăng kí thành công!!!", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                    } else {
                        Toast.makeText(suContext, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    public void deleteUser(String userID){

    }
    public void updateProfile(String userID){

    }
}
