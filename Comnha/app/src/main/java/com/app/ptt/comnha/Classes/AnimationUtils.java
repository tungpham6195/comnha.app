package com.app.ptt.comnha.Classes;

import android.animation.ObjectAnimator;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by PTT on 11/1/2016.
 */

public class AnimationUtils {
    public static void animateItemRcylerV(RecyclerView.ViewHolder holder, boolean goesDown) {
        ObjectAnimator animatorTraslateY = ObjectAnimator.ofFloat(holder.itemView,
                "translationY", goesDown == true ? -500 : 500, 0);
        animatorTraslateY.setDuration(500);
        animatorTraslateY.start();
    }

    public static void animatbtnRefreshIfChange(View view) {
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(view,
                "translationY", -1000, 0);
        animatorTranslateY.setDuration(700);
        animatorTranslateY.start();
    }

    public static void animatbtnRefreshIfClick(View view) {
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(view,
                "translationY", 0, -1000);
        animatorTranslateY.setDuration(1000);
        animatorTranslateY.start();
    }

    public static void animatfabMenuIn(View view) {
        ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view,
                "translationX", 600, 0);
        animatorTranslateX.setDuration(1000);
        animatorTranslateX.start();
    }

    public static void animatfabMenuOut(View view) {
        ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view,
                "translationX", 0, 600);
        animatorTranslateX.setDuration(1000);
        animatorTranslateX.start();
    }

    public static void animatShowTagfromLeft(View view) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view,
                "translationX", -1000, 0);
        animatorTranslateX.setDuration(700);
        animatorTranslateX.start();
    }

    public static void animatHideTagfromRight(final View view) {
        ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view,
                "translationX", 0, -1000);
        animatorTranslateX.setDuration(700);
        animatorTranslateX.start();
        view.setVisibility(View.VISIBLE);
        new CountDownTimer(700, 1000) {
            public void onFinish() {
                view.setVisibility(View.INVISIBLE);

            }

            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }

    public static void animatShowTagfromBottom(View view) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(view,
                "translationY", 100, 0);
        animatorTranslateY.setDuration(250);
        animatorTranslateY.start();

    }

    public static void animatHideTagfromTop(final View view) {
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(view,
                "translationY", 0, 120);
        animatorTranslateY.setDuration(250);
        animatorTranslateY.start();
        new CountDownTimer(250, 1000) {
            public void onFinish() {
                view.setVisibility(View.INVISIBLE);

            }

            public void onTick(long millisUntilFinished) {
            }
        }.start();

    }
}
