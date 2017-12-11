package com.wulee.administrator.zuji.comweb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

/**
 * 拦截需要处理的url
 */

public class FirstUrlHandler extends UrlHandler {

    public FirstUrlHandler(Context context) {
        super(context);
    }

    @Override
    public boolean handlerUrl(@NonNull String url) {
        if (url.contains("http://ihongqiqu.com/archives/")) {
            Toast.makeText(mContext, url, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.handlerUrl(url);
    }
}
