package com.app.ptt.comnha;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Classes.Signin_out;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SigninFragment extends Fragment {
    private EditText edt_email, edt_pass;
    private Button butt_loginFB, butt_loginGmail, btn_signin, btn_exit;
    private TextView txt_forgotPass;
    private Signin_out newSigninout;
    private FirebaseAuth mAuth;
    private FloatingActionButton fab_signup;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    public SigninFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        anhXa(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;
    }

    private void anhXa(final View view, final Bundle savedInstanceState) {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth = FirebaseAuth.getInstance();
        edt_email = (EditText) view.findViewById(R.id.edt_siFrg_username);
        edt_pass = (EditText) view.findViewById(R.id.edt_siFrg_password);
        btn_signin = (Button) view.findViewById(R.id.btn_siFrg_signin);
        btn_exit = (Button) view.findViewById(R.id.btn_siFrg_exit);
        butt_loginFB = (Button) view.findViewById(R.id.butt_siFrg_loginFB);
        butt_loginGmail = (Button) view.findViewById(R.id.butt_siFrg_loginGmail);
        txt_forgotPass = (TextView) view.findViewById(R.id.txt_siFrg_forgotPass);
        fab_signup = (FloatingActionButton) view.findViewById(R.id.fab_signup);
        fab_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), AdapterActivity.class);
                intent.putExtra(getActivity().getResources().getString(R.string.fragment_CODE),
                        getActivity().getResources().getString(R.string.frg_signup_CODE));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                newSigninout = new Signin_out();
//                newSigninout.setEmail(edt_email.getText().toString().trim());
//                newSigninout.setPassword(edt_pass.getText().toString().trim());
//                newSigninout.setSiContext(getActivity().getApplicationContext());
//                newSigninout.setmAuth(mAuth);
//                newSigninout.doSignIn();
//                Toast.makeText(getActivity(), "signin", Toast.LENGTH_SHORT).show();
                if (edt_email.getText().toString().equals("")) {
                    Snackbar.make(view, getResources().getString(R.string.txt_noemail), Snackbar.LENGTH_SHORT).show();
                } else if (edt_pass.getText().toString().equals("")) {
                    Snackbar.make(view, getResources().getString(R.string.txt_nopass), Snackbar.LENGTH_SHORT).show();
                } else if (!edt_email.getText().toString().equals("")
                        && !edt_pass.getText().toString().equals("")) {
                    if (!isValidEmailAddress(edt_email.getText().toString())) {
                        Snackbar.make(view, getResources().getString(R.string.txt_notemail), Snackbar.LENGTH_SHORT).show();
                    } else {
                        mAuth.signInWithEmailAndPassword(edt_email.getText().toString(), edt_pass.getText().toString())
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                        if (!task.isSuccessful()) {
                                            Log.w(TAG, "signInWithEmail:onComplete", task.getException());
//                                    Toast.makeText(getActivity(), "failed login with: " + edt_email.getText().toString(),
//                                            Toast.LENGTH_SHORT).show();
                                        } else {
                                            getActivity().finish();
                                        }
                                    }
                                });
                    }
                }

            }
        });
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mAuth.signOut();
//                Toast.makeText(getActivity().getApplicationContext(), "Exit", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });
        butt_loginGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "Gmail", Toast.LENGTH_SHORT).show();
            }
        });
        butt_loginFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "Facebook", Toast.LENGTH_SHORT).show();
            }
        });
        txt_forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
