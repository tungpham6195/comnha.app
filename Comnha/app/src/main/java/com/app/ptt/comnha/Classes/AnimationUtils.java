package com.app.ptt.comnha.Classes;

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
}