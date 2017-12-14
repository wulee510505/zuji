package com.wulee.administrator.zuji.widget;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.wulee.administrator.zuji.R;


public class BaseProgressDialog {

    private Context mContext;
    private View mChild;
    private boolean isShowing = false;
    private boolean cancelable = true;
    private OnCancelListener mOnCancelListener;

    public BaseProgressDialog(Context context) {
        this.mContext = context;
        initChild();
    }

    public void show() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout content = ((Activity) mContext).getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        if (mChild == null && !initChild()) {
            return;
        }
        isShowing = true;
        content.addView(mChild, params);
    }

    private boolean initChild() {
        FrameLayout root = new FrameLayout(mContext);
        root.setClickable(true);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            View view = inflater.inflate(R.layout.base_progress_dialog, null);
            if (view != null) {
                root.addView(view, params);
                this.mChild = root;
                return true;
            }
        } catch (InflateException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void stop() {
        if (!isShowing) {
            return;
        }
        FrameLayout content = ((Activity) mContext).getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        content.removeView(mChild);
        isShowing = false;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public boolean cancelable() {
        return cancelable;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public boolean cancel() {
        if (!cancelable || !isShowing) {
            return false;
        }
        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel();
        }
        stop();
        return true;
    }

    public void setOnCancelListener(OnCancelListener cancelListener) {
        this.mOnCancelListener = cancelListener;
    }


    public interface OnCancelListener {
        void onCancel();
    }

}
