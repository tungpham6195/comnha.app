package com.app.ptt.comnha;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.ptt.comnha.SingletonClasses.LoginSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button btn_signup, btn_signin, btn_posts, btn_postlist, btn_newloca;
    Toolbar toolbar;
    private Fragment fragment;
    private Bundle savedInstanceState;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public String userID;
    static final int PICK_LOCATION_REQUEST = 1;

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
                    LoginSession.getInstance().setUserID(userID);
                    Log.d("signed_in", "onAuthStateChanged:signed_in: " + userID);
                } else {
                    userID = null;
                    LoginSession.getInstance().setUserID(userID);
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
        btn_newloca = (Button) findViewById(R.id.btn_newlocation);
        toolbar = (Toolbar) findViewById(R.id.actmain_toolbar);
        setSupportActionBar(toolbar);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frg_signup_CODE));
                startActivity(intent);
            }
        });
        btn_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frag_addpost_CODE));
                startActivity(intent);
            }
        });
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frg_signin_CODE));
                startActivity(intent);
            }
        });
        btn_postlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocatlistFragment locatlistFragment = new LocatlistFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, locatlistFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        btn_newloca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdapterActivity.class);
                intent.putExtra(getString(R.string.fragment_CODE), getString(R.string.frag_addloca_CODE));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_more:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mAuth.signOut();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);

        }
    }

}
