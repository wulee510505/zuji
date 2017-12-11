package com.wulee.administrator.zuji.comweb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * 处理通用的 scheme
 */

public class OriginUrlHandler extends UrlHandler {

    public OriginUrlHandler(Context context) {
        super(context);
    }

    @Override
    public boolean handlerUrl(@NonNull String url) {
        if (url.toLowerCase().startsWith("http")) {
            return super.handlerUrl(url);
        } else {
            // Otherwise allow the OS to handle things like tel, mailto, etc.
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        }
    }

}
