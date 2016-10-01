package com.app.ptt.comnha;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ptt.comnha.Classes.Signin_out;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class SigninFragment extends Fragment {
    private EditText edt_email, edt_pass;
    private Button butt_loginFB, butt_loginGmail, btn_signin, btn_exit;
    private TextView txt_forgotPass;
    private Signin_out newSigninout;
    private FirebaseAuth mAuth;

    public SigninFragment() {
        // Required empty public constructor
    }

    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        anhXa(view);
        return view;
    }

    private void anhXa(View view) {
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
                newSigninout = new Signin_out();
                newSigninout.setEmail(edt_email.getText().toString().trim());
                newSigninout.setPassword(edt_pass.getText().toString().trim());
                newSigninout.setSiContext(getActivity().getApplicationContext());
                newSigninout.setmAuth(mAuth);
                newSigninout.doSignIn();
            }
        });
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mAuth.signOut();
                Toast.makeText(getActivity().getApplicationContext(), "Exit", Toast.LENGTH_SHORT).show();
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
}
