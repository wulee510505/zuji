package com.wulee.administrator.zuji.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by wulee on 2017/6/15 16:51
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context).load(path).into(imageView);

        Uri uri = Uri.parse((String) path);
        imageView.setImageURI(uri);
    }
}
