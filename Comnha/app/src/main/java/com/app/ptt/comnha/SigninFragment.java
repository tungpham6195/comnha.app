package com.app.ptt.comnha;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
    FirebaseAuth.AuthStateListener mAuthStateListener;

    public SigninFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        anhXa(view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;
    }

    private void anhXa(View view) {
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
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                newSigninout = new Signin_out();
//                newSigninout.setEmail(edt_email.getText().toString().trim());
//                newSigninout.setPassword(edt_pass.getText().toString().trim());
//                newSigninout.setSiContext(getActivity().getApplicationContext());
//                newSigninout.setmAuth(mAuth);
//                newSigninout.doSignIn();
//                Toast.makeText(getActivity(), "signin", Toast.LENGTH_SHORT).show();
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
