package com.wulee.administrator.zuji.widget;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by zhangl on 16/6/3
 */
public class ScalePageTransformer implements ViewPager.PageTransformer {

    public static final float MAX_SCALE = 1.0f;
    public static final float MIN_SCALE = 0.75f;
    public static final float MAX_ALPHA = 1.0f;
    public static final float MIN_ALPHA = 1.0f;


    @Override
    public void transformPage(View page, float position) {

        if (position < -1) {
            position = -1;

        } else if (position > 1) {
            position = 1;
        }

        float tempScale = 0f;
        float tempAlpha = 0f;

        if (position < 0) {
            tempScale = 1 + position;
            tempAlpha = 1 + position;
        } else if (position >= 0) {
            tempScale = 1 - position;
            tempAlpha = 1 - position;
        }

        float slope = (MAX_SCALE - MIN_SCALE) / 1;
        float scaleValue = MIN_SCALE + tempScale * slope;

        float slopeAlpha = (MAX_ALPHA - MIN_ALPHA) / 1;
        float scaleAlphaValue = MIN_ALPHA + tempAlpha * slopeAlpha;

        page.setScaleY(scaleValue);
        page.setAlpha(scaleAlphaValue);
    }
}
