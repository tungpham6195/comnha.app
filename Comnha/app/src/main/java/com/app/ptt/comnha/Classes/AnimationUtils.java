package com.app.ptt.comnha.Classes;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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

    public static void animatShowTagMap(View view1, View view2) {
        final ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view1, "translationX", -1050, 0),
                animatorTranslateX1 = ObjectAnimator.ofFloat(view2, "translationX", -1050, 0),
                animatorTranslateY = ObjectAnimator.ofFloat(view2, "translationY", 120, 0);
        animatorTranslateX.setDuration(500);
        animatorTranslateX1.setDuration(500);
        animatorTranslateY.setDuration(180);
        animatorTranslateX.start();
        animatorTranslateX1.start();
        animatorTranslateX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animatorTranslateY.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public static void animatHideTagMap(View view1, View view2) {
        final ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view1, "translationX", 0, -1050),
                animatorTranslateY = ObjectAnimator.ofFloat(view2, "translationY", 0, 120),
                animatorTranslateX1 = ObjectAnimator.ofFloat(view2, "translationX", 0, -1050);
        animatorTranslateX.setDuration(500);
        animatorTranslateX1.setDuration(500);
        animatorTranslateY.setDuration(180);
        animatorTranslateY.start();
        animatorTranslateY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animatorTranslateX.start();
                animatorTranslateX1.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public static void animatShowTagMap2(View view1) {
        final ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view1, "translationX", -1050, 0);
        animatorTranslateX.setDuration(500);
        animatorTranslateX.start();
    }

    public static void animatHideTagMap2(View view1) {
        final ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(view1, "translationX", 0, -1050);
        animatorTranslateX.setDuration(500);
        animatorTranslateX.start();
    }

}
