package com.app.ptt.comnha;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends FragmentActivity {
    private Button btn_signup, btn_signin, btn_posts, btn_postlist;
    private Fragment fragment;
    private Bundle savedInstanceState;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;
        anhXa();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    userID = user.getUid();
                    Toast.makeText(getApplicationContext(), "Signed in successfull with " + user.getEmail().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("signed_in", "onAuthStateChanged:signed_in: " + userID);
                } else {
                    userID = null;
                    Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_SHORT).show();
                    Log.d("signed_out", "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    void anhXa() {
        btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_signin = (Button) findViewById(R.id.btn_signin);
        btn_posts = (Button) findViewById(R.id.btn_post);
        btn_postlist = (Button) findViewById(R.id.btn_postlst);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.frame) != null) {
                    if (savedInstanceState != null) {
//
                    } else {

                    }
                    SignupFragment signupFragment = new SignupFragment();
                    signupFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame, signupFragment).addToBackStack(null).commit();

                }
            }
        });
        btn_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostFragment postFragment = new PostFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, postFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SigninFragment signinFragment = new SigninFragment();
                signinFragment.setmAuth(mAuth);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, signinFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        btn_postlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostlistFragment postlistFragment = new PostlistFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, postlistFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.signOut();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
