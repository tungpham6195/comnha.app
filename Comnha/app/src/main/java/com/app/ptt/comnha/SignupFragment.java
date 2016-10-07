package com.app.ptt.comnha;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.Users;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment {
    EditText editText_ho, editText_ten, editText_tenlot, editText_username, editText_email,
            editText_password, editText_confirmPass, editText_birth;
    Button butt_signup, butt_loginFB, butt_loginGmail;
    TextView txt_forgotPass;
    Users createNewAccount;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        anhXa(view);
        Firebase.setAndroidContext(getContext());
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        butt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                doSignUp();
                createNewAccount = new Users(getActivity().getApplicationContext());
                createNewAccount.setHo(editText_ho.getText().toString());
                createNewAccount.setTen(editText_ten.getText().toString());
                createNewAccount.setTenlot(editText_tenlot.getText().toString());
                createNewAccount.setUsername(editText_username.getText().toString());
                createNewAccount.setEmail(editText_email.getText().toString());
                createNewAccount.setPassword(editText_password.getText().toString());
                createNewAccount.setConfirmPass(editText_confirmPass.getText().toString());
                createNewAccount.setBirth(editText_birth.getText().toString());
                createNewAccount.createNew();
//                Toast.makeText(getContext(), editText_email.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void anhXa(View view) {
        editText_ho = (EditText) view.findViewById(R.id.editText_ho);
        editText_ten = (EditText) view.findViewById(R.id.editText_ten);
        editText_tenlot = (EditText) view.findViewById(R.id.editText_tenLot);
        editText_username = (EditText) view.findViewById(R.id.editText_username);
        editText_email = (EditText) view.findViewById(R.id.editText_email);
        editText_password = (EditText) view.findViewById(R.id.editText_password);
        editText_confirmPass = (EditText) view.findViewById(R.id.editText_confirmPass);
        editText_birth = (EditText) view.findViewById(R.id.editText_birth);
        butt_signup = (Button) view.findViewById(R.id.butt_signup);

    }

    //        private void doSignUp(){
//        mAuth.createUserWithEmailAndPassword(editText_email.getText().toString(), editText_password.getText().toString())
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());
//
//                        if (!task.isSuccessful()) {
//                            Toast.makeText(getContext(),task.isSuccessful()+"Failed to sign up",Toast.LENGTH_SHORT).show();
//                        }else {
//                            Toast.makeText(getContext(),task.isSuccessful()+"success to sign up",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
