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
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    AddpostFragment addpostFragment = new AddpostFragment();
                    addpostFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, addpostFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_localist_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    LocatlistFragment locatlistFragment = new LocatlistFragment();
                    locatlistFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, locatlistFragment).commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_addloca_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    AddlocaFragment addlocaFragment = new AddlocaFragment();
                    addlocaFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, addlocaFragment).commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_locadetail_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    LocadetailFragment locadetailFragment = new LocadetailFragment();
                    locadetailFragment.setLocaID(intent.getStringExtra(getResources().getString(R.string.key_CODE)));
                    locadetailFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, locadetailFragment).commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_chooseloca_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    ChooselocaFragment chooselocaFragment = new ChooselocaFragment();
                    chooselocaFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, chooselocaFragment).commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_chooseimg_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    ChoosePhotoFragment choosePhotoFragment = new ChoosePhotoFragment();
                    choosePhotoFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, choosePhotoFragment).commit();
                }
            }

        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_viewpost_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    ViewpostFragment viewpostFragment = new ViewpostFragment();
                    viewpostFragment.setPostID(intent.getExtras().getString(getString(R.string.key_CODE)));
                    viewpostFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, viewpostFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_signin_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    SigninFragment signinFragment = new SigninFragment();
                    signinFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, signinFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_signup_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    SignupFragment signupFragment = new SignupFragment();
                    signupFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, signupFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_vote_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    DoVoteFragment dovoteFragment = new DoVoteFragment();
                    dovoteFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, dovoteFragment)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frg_prodetail_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    ProfiledetailFragment proDetailFrag = new ProfiledetailFragment();
                    proDetailFrag.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, proDetailFrag)
                            .commit();
                }
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frg_themmon_CODE))) {
            if (findViewById(R.id.frame_adapter2) != null) {
                if (getSupportFragmentManager().findFragmentById(R.id.frame_adapter2) == null) {
                    ThemMonFragment themMonFragment = new ThemMonFragment();
                    themMonFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter2, themMonFragment)
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