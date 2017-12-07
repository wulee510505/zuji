package com.wulee.administrator.zuji.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.utils.UIUtils;

/**
 * Created by wulee on 2017/10/17 16:56
 */

public class DotIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {
    private static final int DEFAULT_DISTANCE = 6;
    private int oldPosition = -1;
    private int distance = 0;

    private ViewPager viewPager;

    public DotIndicator(Context context) {
        this(context, null);
    }

    public DotIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DotIndicator);
        distance = array.getDimensionPixelSize(R.styleable.DotIndicator_distance,
                UIUtils.dip2px(DEFAULT_DISTANCE));
        array.recycle();
        init();
    }

    private void init() {
//        设置横向
        setOrientation(LinearLayout.HORIZONTAL);
        if (!(getLayoutParams() instanceof FrameLayout.LayoutParams)) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.BOTTOM | Gravity.START;
            setLayoutParams(params);
        }
    }

    /**
     * 关联viewPager 并注册监听
     * @param viewPager
     */
    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        if (viewPager != null) {
            setDotIndicator(viewPager.getAdapter().getCount(),getContext());
        }
    }

    /**
     * 设置圆点指示器
     * @param count
     * @param context
     */
    private void setDotIndicator(int count, Context context) {
        removeIndicator();
        if (count <= 0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            ImageView iView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = distance;
            params.rightMargin = distance;
            iView.setImageResource(R.drawable.dot_stroke);
            addView(iView, params);
        }
        notifyIndicator(viewPager.getCurrentItem());

    }

    /**
     * 移除指示器
     */
    private void removeIndicator() {
        removeAllViews();
    }

    /**
     * 通知更新圆点指示器
     * @param currentPosition
     */
    private void notifyIndicator(int currentPosition) {
        if (oldPosition != currentPosition){
            if (oldPosition == -1){
                ((ImageView)getChildAt(currentPosition)).setImageResource(R.drawable.dot_solid);
                oldPosition = currentPosition;
                return;
            }
            ((ImageView)getChildAt(oldPosition)).setImageResource(R.drawable.dot_stroke);
            ((ImageView)getChildAt(currentPosition)).setImageResource(R.drawable.dot_solid);
            oldPosition = currentPosition;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        notifyIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
