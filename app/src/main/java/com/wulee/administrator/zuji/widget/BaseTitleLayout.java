package com.wulee.administrator.zuji.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.DrawableRes;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.wulee.administrator.zuji.R;

/**
 * Created by wulee on 2017/8/25 14:41
 */

public class BaseTitleLayout extends View {
    private int layoutHeight;
    private int layoutWidth;

    private Canvas mCanvas;
    private Paint mPaint;
    private Rect rect;
    private int paddingLeft;
    private int paddingRight;

    private int iconSize = dp2px(25);
    private int leftImg = R.mipmap.icon_back;
    private int textSize = dp2px(17);
    private String leftText = "";
    private String centerText = "";
    private int rightImg1 = -1, rightImg2 = -1;
    private String rightText = "";
    private Bitmap leftBitmap;
    private LeftImgVisible mLeftImgVisible = LeftImgVisible.VISIBLE;

    private TitleLayoutClickListener listener;

    public BaseTitleLayout(Context context) {
        this(context, null);
    }

    public BaseTitleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseTitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int textColor = Color.parseColor("#020202");

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseTitleLayout);
        for (int i = 0; i < typedArray.length(); i++) {
            int index = typedArray.getIndex(i);
            if (index == R.styleable.BaseTitleLayout_titleTextColor) {
                textColor = typedArray.getColor(index, Color.parseColor("#ffffff"));

            } else if (index == R.styleable.BaseTitleLayout_titleTextSize) {
                textSize = typedArray.getDimensionPixelSize(index,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 17,
                                getResources().getDisplayMetrics()));

            } else if (index == R.styleable.BaseTitleLayout_titleIconSize) {
                iconSize = typedArray.getDimensionPixelSize(index,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 25,
                                getResources().getDisplayMetrics()));

            } else if (index == R.styleable.BaseTitleLayout_titleLeftImg) {
                leftImg = typedArray.getResourceId(index, R.mipmap.icon_back);

            } else if (index == R.styleable.BaseTitleLayout_titleLeftImgVisible) {
                int style = typedArray.getInt(index, LeftImgVisible.VISIBLE.ordinal());
                for (LeftImgVisible leftImgVisible : LeftImgVisible.values()) {
                    if(style == leftImgVisible.ordinal()) {
                        mLeftImgVisible = leftImgVisible;
                    }
                }


            } else if (index == R.styleable.BaseTitleLayout_titleLeftText) {
                leftText = typedArray.getString(index);

            } else if (index == R.styleable.BaseTitleLayout_titleCenterText) {
                centerText = typedArray.getString(index);

            } else if (index == R.styleable.BaseTitleLayout_titleRightImg1) {
                rightImg1 = typedArray.getResourceId(index, -1);

            } else if (index == R.styleable.BaseTitleLayout_titleRightImg2) {
                rightImg2 = typedArray.getResourceId(index, -1);

            } else if (index == R.styleable.BaseTitleLayout_titleRightText) {
                rightText = typedArray.getString(index);

            } else {
            }
        }

        typedArray.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(textColor);
        mPaint.setTextSize(dp2px(17));
        mPaint.setTextAlign(Paint.Align.CENTER);
        // 绘制图片范围
        rect = new Rect();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mCanvas = canvas;

        if(mLeftImgVisible == LeftImgVisible.VISIBLE) {
            leftBitmap = BitmapFactory.decodeResource(getResources(), leftImg);
            rect.top = layoutHeight / 2 - (iconSize + dp2px(5)) / 2;
            rect.bottom = layoutHeight / 2 + (iconSize + dp2px(5)) / 2;
            rect.left = paddingLeft;
            rect.right = paddingLeft + iconSize;
            mCanvas.drawBitmap(leftBitmap, null, rect, mPaint);
        }

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float textCenterHeight = getHeight() / 2 - fontMetrics.descent + (fontMetrics.descent - fontMetrics.ascent) / 2;

        if (!TextUtils.isEmpty(leftText)) {
            mCanvas.drawText(leftText, paddingLeft + (iconSize + dp2px(10)), textCenterHeight, mPaint);
        }

        if (!TextUtils.isEmpty(centerText)) {
            mPaint.setTextSize(textSize);
            if (mPaint.measureText(centerText) > layoutWidth / 3) {
                TextPaint textPaint = new TextPaint(mPaint);
                String str = TextUtils.ellipsize(centerText, textPaint, layoutWidth / 2, TextUtils.TruncateAt.END).toString();
                mCanvas.drawText(str, layoutWidth / 2, textCenterHeight, textPaint);
            } else {
                mCanvas.drawText(centerText, layoutWidth / 2, textCenterHeight, mPaint);
            }
        }

        if (rightImg1 != -1) {
            rect.top = layoutHeight / 2 - iconSize / 2;
            rect.bottom = layoutHeight / 2 + iconSize / 2;
            Bitmap rightBitmap1 = BitmapFactory.decodeResource(getResources(), rightImg1);
            rect.left = layoutWidth - paddingRight - getPaddingRight() - iconSize;
            rect.right = layoutWidth - paddingRight;
            mCanvas.drawBitmap(rightBitmap1, null, rect, mPaint);
            if (rightImg2 != -1) {
                Bitmap rightBitmap2 = BitmapFactory.decodeResource(getResources(), rightImg2);
                rect.left = layoutWidth - paddingRight - (iconSize * 2 + dp2px(5));
                rect.right = layoutWidth - paddingRight - (iconSize + dp2px(5));
                mCanvas.drawBitmap(rightBitmap2, null, rect, mPaint);
            }
        }

        if (!TextUtils.isEmpty(rightText)) {
            mCanvas.drawText(rightText, layoutWidth - paddingRight - mPaint.measureText(rightText),
                    textCenterHeight, mPaint);
        }

    }

    public void setLeftImg(@DrawableRes int res) {
        this.leftImg = res;
        invalidate();
    }

    public void setLeftText(String str) {
        this.leftText = str;
        invalidate();
    }

    public void setCenterText(String str) {
        if (TextUtils.isEmpty(str)) return;
        this.centerText = str;
        invalidate();
    }

    public void setRightImg1(@DrawableRes int res) {
        this.rightImg1 = res;
        invalidate();
    }

    public void setRightImg2(@DrawableRes int res) {
        this.rightImg2 = res;
        invalidate();
    }

    public void setRightText(String str) {
        this.rightText = str;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
        }
        layoutWidth = widthSize;
        layoutHeight = dp2px(45);
        paddingLeft = getPaddingLeft() == 0 ? dp2px(10) : getPaddingLeft();
        paddingRight = getPaddingRight() == 0 ? dp2px(10) : getPaddingRight();


        setMeasuredDimension(layoutWidth, layoutHeight);
    }

    public void setOnTitleClickListener(TitleLayoutClickListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();

        if (event.getAction() == MotionEvent.ACTION_UP && listener != null) {
            if (x <= iconSize + mPaint.measureText(leftText)) {
                listener.onLeftClickListener();
            } else if (x > layoutWidth - iconSize - paddingRight) {
                if (rightImg1 != -1) {
                    listener.onRightImg1ClickListener();
                } else if (!TextUtils.isEmpty(rightText)) {
                    listener.onRightTextClickListener();
                }
            } else if (x < layoutWidth - iconSize - paddingRight && x > layoutWidth - 2 * iconSize - paddingRight) {
                if (rightImg2 != -1) {
                    listener.onRightImg2ClickListener();
                }
            }
        }
        return true;
    }

    private int dp2px(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private enum LeftImgVisible{
        GONE,
        VISIBLE
    }
}
