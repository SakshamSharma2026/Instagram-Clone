package com.codewithshadow.instagram.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class Heart {

    public ImageView heartWhite,heartRed;

    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();


    public Heart(ImageView heartWhite, ImageView heartRed) {
        this.heartWhite = heartWhite;
        this.heartRed = heartRed;
    }

    public void toggleLike()
    {
        AnimatorSet animatorSet = new AnimatorSet();

        if (heartRed.getVisibility() == View.VISIBLE)
        {
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);
            ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(heartRed, "scaleY", 1f,0f);
            objectAnimatorY.setDuration(300);
            objectAnimatorY.setInterpolator(ACCELERATE_INTERPOLATOR);
            ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(heartRed, "scaleX", 1f,0f);
            objectAnimatorX.setDuration(300);
            objectAnimatorX.setInterpolator(ACCELERATE_INTERPOLATOR);
            heartRed.setVisibility(View.GONE);
            heartWhite.setVisibility(View.VISIBLE);
            animatorSet.playTogether(objectAnimatorY,objectAnimatorX);
        }
        else if (heartRed.getVisibility() == View.GONE)
        {
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);
            ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(heartRed, "scaleY", 0.1f,1f);
            objectAnimatorY.setDuration(300);
            objectAnimatorY.setInterpolator(DECELERATE_INTERPOLATOR);
            ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(heartRed, "scaleX", 0.1f,1f);
            objectAnimatorX.setDuration(300);
            objectAnimatorX.setInterpolator(DECELERATE_INTERPOLATOR);

            heartRed.setVisibility(View.VISIBLE);
            heartWhite.setVisibility(View.GONE);
            animatorSet.playTogether(objectAnimatorY,objectAnimatorX);
        }
        animatorSet.start();
    }
}
