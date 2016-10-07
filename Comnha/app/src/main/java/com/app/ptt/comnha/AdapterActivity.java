package com.app.ptt.comnha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AdapterActivity extends AppCompatActivity implements ChooselocaFragment.onPassDatafromChooseLocaFrg {
    String locaKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter);
        Intent intent = getIntent();
        String FRAGMENT_CODE = intent.getExtras().getString(getResources().getString(R.string.fragment_CODE));
        Log.d("FRAGMENT_CODE", FRAGMENT_CODE);
        if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_localist_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                LocatlistFragment locatlistFragment = new LocatlistFragment();
                locatlistFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, locatlistFragment).commit();
            }
        } else if (FRAGMENT_CODE.equals(getResources().getString(R.string.frag_addloca_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                AddlocaFragment addlocaFragment = new AddlocaFragment();
                addlocaFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, addlocaFragment).commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_locadetail_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                LocadetailFragment locadetailFragment = new LocadetailFragment();
                locadetailFragment.setLocaID(intent.getStringExtra(getResources().getString(R.string.key_CODE)));
                locadetailFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, locadetailFragment).commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_chooseloca_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {
                    ChooselocaFragment chooselocaFragment = new ChooselocaFragment();
                    chooselocaFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_adapter, chooselocaFragment)
                            .commit();
                } else {

                }
                ChooselocaFragment chooselocaFragment = new ChooselocaFragment();
                chooselocaFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, chooselocaFragment)
                        .commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frag_addpost_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                PostFragment postFragment = new PostFragment();
                postFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, postFragment)
                        .commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_viewpost_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                ViewpostFragment viewpostFragment = new ViewpostFragment();
                viewpostFragment.setPostID(intent.getExtras().getString(getString(R.string.key_CODE)));
                viewpostFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, viewpostFragment)
                        .commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_signin_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                SigninFragment signinFragment = new SigninFragment();
                signinFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, signinFragment)
                        .commit();
            }
        } else if (FRAGMENT_CODE.equals(getString(R.string.frg_signup_CODE))) {
            if (findViewById(R.id.frame_adapter) != null) {
                if (savedInstanceState != null) {

                } else {

                }
                SignupFragment signupFragment = new SignupFragment();
                signupFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.frame_adapter, signupFragment)
                        .commit();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();

    }

    @Override
    protected void onPause() {
        super.onPause();
//        failed startActitityforResult
//        Intent intent1 = new Intent();
//        intent1.putExtra("result", locaKey);
//        setResult(Activity.RESULT_OK, intent1);
//        Log.d("intent",intent1.getStringExtra("result"));
//        Toast.makeText(getApplicationContext(), "from adapter: "+locaKey, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void passData(String data) {
        locaKey = data;
    }
}
