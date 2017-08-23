package com.wulee.administrator.zuji.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.wulee.administrator.zuji.R;

/**
 * Created by wulee on 2017/8/18 14:44
 */

public class AnimArcButtons extends RelativeLayout {

    private Context context;
    private int leftMargin = 0, bottomMargin = 0;
    private final int buttonWidth = 80;// 图片宽高
    private final int r = 240;// 半径
    private final int maxTimeSpent = 200;// 最长动画耗时
    private final int minTimeSpent = 80;// 最短动画耗时
    private int intervalTimeSpent;// 每相邻2个的时间间隔

    private Button[] btns;
    private Button btn_menu;
    private RelativeLayout.LayoutParams params;
    private boolean isOpen = false;// 是否菜单打开状态
    private float angle;// 每个按钮之间的夹角

    public int bottomMargins = this.getMeasuredHeight() - buttonWidth
            - bottomMargin;

    public AnimArcButtons(Context context) {
        super(context);
        this.context = context;
    }

    public AnimArcButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = LayoutInflater.from(context).inflate(R.layout.anim_arc_button,
                this);
        initButtons(view);
    }

    private void initButtons(View view) {
        // 可以根据按钮的个数自己增减
        btns = new Button[3];
        btns[0] = (Button) view.findViewById(R.id.btn_weather);
        btns[1] = (Button) view.findViewById(R.id.btn_nav);
        btns[2] = (Button) view.findViewById(R.id.btn_circle);
        btn_menu = (Button) view.findViewById(R.id.btn_menu);

        leftMargin = ((RelativeLayout.LayoutParams) (btn_menu.getLayoutParams())).leftMargin;
        bottomMargin = ((RelativeLayout.LayoutParams) (btn_menu
                .getLayoutParams())).bottomMargin;

        for (int i = 0; i < btns.length; i++) {
            // 初始化的时候按钮重合
            btns[i].setLayoutParams(btn_menu.getLayoutParams());
            btns[i].setTag(String.valueOf(i));
            btns[i].setOnClickListener(clickListener);
        }

        intervalTimeSpent = (maxTimeSpent - minTimeSpent) / btns.length;
        angle = (float) Math.PI / (2 * (btns.length - 1));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bottomMargins = this.getMeasuredHeight() - buttonWidth - bottomMargin;
        btn_menu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isOpen) {
                    openMenu();
                } else {
                    closeMenu();
                }
            }
        });

    }

    public void closeMenu() {
        if (isOpen == true) {
            isOpen = false;
            for (int i = 0; i < btns.length; i++) {
                float xLenth = (float) (r * Math.sin(i * angle));
                float yLenth = (float) (r * Math.cos(i * angle));
                btns[i].startAnimation(animTranslate(-xLenth, yLenth,
                        leftMargin, bottomMargins, btns[i], maxTimeSpent - i
                                * intervalTimeSpent));
                btns[i].setVisibility(View.INVISIBLE);
            }
        }
        btn_menu.setBackgroundResource(R.mipmap.menu_open);
    }

    public void openMenu() {
        isOpen = true;
        for (int i = 0; i < btns.length; i++) {
            float xLenth = (float) (r * Math.sin(i * angle));
            float yLenth = (float) (r * Math.cos(i * angle));
            btns[i].startAnimation(animTranslate(xLenth, -yLenth, leftMargin
                            + (int) xLenth, bottomMargins - (int) yLenth, btns[i],
                    minTimeSpent + i * intervalTimeSpent));
            btns[i].setVisibility(View.VISIBLE);
        }
        btn_menu.setBackgroundResource(R.mipmap.menu_close);
    }

    private Animation animScale(float toX, float toY) {
        Animation animation = new ScaleAnimation(1.0f, toX, 1.0f, toY,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        //animation.setInterpolator(context, anim.accelerate_decelerate_interpolator);
        animation.setDuration(400);
        animation.setFillAfter(false);
        return animation;

    }

    private Animation animTranslate(float toX, float toY, final int lastX,
                                    final int lastY, final Button button, long durationMillis) {
        Animation animation = new TranslateAnimation(0, toX, 0, toY);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                params = new RelativeLayout.LayoutParams(0, 0);
                params.height = buttonWidth;
                params.width = buttonWidth;
                params.setMargins(lastX, lastY, 0, 0);
                button.setLayoutParams(params);
                button.clearAnimation();

            }
        });
        animation.setDuration(durationMillis);
        return animation;
    }

    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int selectedItem = Integer.parseInt((String) v.getTag());
            for (int i = 0; i < btns.length; i++) {
                if (i == selectedItem) {
                    btns[i].startAnimation(animScale(2.0f, 2.0f));
                } else {
                    btns[i].startAnimation(animScale(0.0f, 0.0f));
                }
            }
            if (onButtonClickListener != null) {
                onButtonClickListener.onButtonClick(v, selectedItem);
            }
        }

    };

    public boolean isOpen() {
        return isOpen;
    }

    private OnButtonClickListener onButtonClickListener;

    public interface OnButtonClickListener {
        void onButtonClick(View v, int id);
    }

    public void setOnButtonClickListener(
            OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }
}
