package com.wulee.administrator.zuji.comweb;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

/**
 * URL 拦截基类
 */

public abstract class UrlHandler {

    protected Context mContext;
    private UrlHandler nextUrlHandler = null;

    public UrlHandler(Context context) {
        mContext = context;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public UrlHandler getNextUrlHandler() {
        return nextUrlHandler;
    }

    public void setNextUrlHandler(UrlHandler nextUrlHandler) {
        this.nextUrlHandler = nextUrlHandler;
    }

    @CallSuper
    public boolean handlerUrl(@NonNull String url) {
        if (getNextUrlHandler() != null) {
            return getNextUrlHandler().handlerUrl(url);
        }
        return false;
    }
}
