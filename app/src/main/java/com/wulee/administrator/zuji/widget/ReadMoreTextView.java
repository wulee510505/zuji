package com.wulee.administrator.zuji.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.wulee.administrator.zuji.R;

/**
 * Created by wulee on 2017/9/27 10:32
 */

public class ReadMoreTextView extends AppCompatTextView {
    private int mMaxLines;
    private BufferType mBufferType = BufferType.NORMAL;
    private CharSequence mText;
    private String mMoreText;
    private String mLessText;
    private int mMoreColor;
    private int mLessColor;

    public ReadMoreTextView(Context context) {
        super(context);
        setup();
    }

    public ReadMoreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        setup();
    }

    public ReadMoreTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
        setup();
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.ReadMoreTextView);
        mMoreText = a.getString(R.styleable.ReadMoreTextView_rmtMoreText);
        mLessText = a.getString(R.styleable.ReadMoreTextView_rmtLessText);
        mMoreColor = a.getInteger(R.styleable.ReadMoreTextView_rmtMoreColor, Color.BLUE);
        mLessColor = a.getInteger(R.styleable.ReadMoreTextView_rmtLessColor, Color.BLUE);
        a.recycle();
        setEllipsize(TextUtils.TruncateAt.END);
    }

    public void setMoreText(String more) {
        mMoreText = more;
    }

    public void setLessText(String less) {
        mLessText = less;
    }


    @Override
    public void setMaxLines(int maxlines) {
        mMaxLines = maxlines;
        setup();
        requestLayout();
    }

    @Override
    public void setText(final CharSequence text, BufferType type) {
        mText = text;
        mBufferType = type;
        setup();
        super.setText(text, type);
    }

    private void setup() {
        if (mListener == null || mMaxLines < 1 || mText == null) {
            return;
        }
        getViewTreeObserver().addOnGlobalLayoutListener(mListener);
    }

    private ViewTreeObserver.OnGlobalLayoutListener mListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);

            if (getLineCount() < mMaxLines) {
                return;
            }

            final CharSequence summary = createSummary();
            setTextInternal(summary);
            setOnClickListener(new OnClick(summary));
        }
    };

    private Spanned create(CharSequence content, String label, int color) {
        SpannableStringBuilder builder = new SpannableStringBuilder(label);
        builder.setSpan(new ForegroundColorSpan(color), 0, label.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return new SpannableStringBuilder(content).append(builder);
    }

    private CharSequence createContent() {
        if (mLessText == null || mLessText.length() == 0) {
            return mText;
        }
        return create(mText, mLessText, mLessColor);
    }

    private CharSequence createSummary() {
        if (mMoreText == null || mMoreText.length() == 0) {
            return mText;
        }
        Layout layout = getLayout();
        int start = layout.getLineStart(mMaxLines - 1);
        int end = layout.getLineEnd(mMaxLines - 1) - start;

        CharSequence content = mText.subSequence(start, mText.length());


        float moreWidth = getPaint().measureText(mMoreText, 0, mMoreText.length());
        float maxWidth = layout.getWidth() - moreWidth;
        int len = getPaint().breakText(content, 0, content.length(), true, maxWidth, null);
        if (content.charAt(end - 1) == '\n') {
            end = end - 1;
        }
        len = Math.min(len, end);
        return create(mText.subSequence(0, start + len), mMoreText, mMoreColor);
    }

    private void setTextInternal(CharSequence text) {
        super.setText(text, mBufferType);
    }

    private class OnClick implements View.OnClickListener {

        boolean expand = false;
        CharSequence summary;
        CharSequence content;

        OnClick(CharSequence s) {
            this.summary = s;
        }

        @Override
        public void onClick(View view) {
            if (expand) {
                if (content == null) {
                    content = createContent();
                }
                setTextInternal(content);
            } else {
                setTextInternal(summary);
            }
            expand = !expand;
        }
    }
}
