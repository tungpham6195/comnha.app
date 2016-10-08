package com.app.ptt.comnha.Classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by PTT on 9/23/2016.
 */

public class Signin_out {
    private String email, password;
    private FirebaseAuth mAuth;
    private Context siContext;

    public Signin_out() {
    }

    public void setSiContext(Context siContext) {
        this.siContext = siContext;
    }

    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void doSignIn() {
        mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(siContext, "Signed in successful", Toast.LENGTH_SHORT).show();
                    LoginSession.getInstance().setUserID(task.getResult().getUser().getUid());
                } else {
                    Log.e("errorsignin",task.getException().toString());
//                    Toast.makeText(siContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
