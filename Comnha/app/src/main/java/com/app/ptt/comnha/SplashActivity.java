package com.app.ptt.comnha;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ptt.comnha.Classes.AnimationUtils;

public class SplashActivity extends AppCompatActivity {
    TextView dot1, dot2, dot3, dot4, dot5, dot6;
    ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        dot1 = (TextView) findViewById(R.id.act_splash_dot1);
        dot2 = (TextView) findViewById(R.id.act_splash_dot2);
        dot3 = (TextView) findViewById(R.id.act_splash_dot3);
        dot4 = (TextView) findViewById(R.id.act_splash_dot4);
        dot5 = (TextView) findViewById(R.id.act_splash_dot5);
        dot6 = (TextView) findViewById(R.id.act_splash_dot6);
        imgLogo = (ImageView) findViewById(R.id.act_splash_imglogo);
        AnimationUtils.animateTransTrip(dot1, dot2, dot3, dot4, dot5, dot6);
        AnimationUtils.animateTransAlpha(imgLogo);
    }

    @Override
    public void onBackPressed() {
    }
}
