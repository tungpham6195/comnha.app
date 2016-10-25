package com.app.ptt.comnha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class Adapter2Activity extends AppCompatActivity {
    static final String STATE_ADDPOST_FRAGMENT = "addpostFragment";
    static final int CHECK_ADDPOST_FRAGMENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter2);
        Intent intent = getIntent();
        String FRAGMENT_CODE = intent.getExtras().getString(getResources().getString(R.string.fragment_CODE));
        if (FRAGMENT_CODE.equals(getString(R.string.frag_addpost_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (savedInstanceState == null) {
                    AddPostFragment addpostFragment = new AddPostFragment();
                    addpostFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, addpostFragment)
                            .commit();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ADDPOST_FRAGMENT, CHECK_ADDPOST_FRAGMENT);
        Log.i("saveState", "saved");
    }
}