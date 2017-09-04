package com.wulee.administrator.zuji.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.huxq17.swipecardsview.BaseCardAdapter;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.entity.Constant;
import com.wulee.administrator.zuji.entity.FunPicInfo;
import com.wulee.administrator.zuji.utils.FileUtils;
import com.wulee.administrator.zuji.utils.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by wulee on 2017/9/1 16:23
 */

public class FunPicAdapter extends BaseCardAdapter {
    private List<FunPicInfo> datas;
    private Context context;

    public FunPicAdapter(List<FunPicInfo> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getCardLayoutId() {
        return R.layout.fun_pic_item;
    }

    @Override
    public void onBindData(final int position, View cardview) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        final ImageView imageView = (ImageView) cardview.findViewById(R.id.iv_fun_pic);
        TextView tvSave = (TextView) cardview.findViewById(R.id.tv_save);

        final FunPicInfo meizi = datas.get(position);
        String url = meizi.getUrl();
        //ImageUtil.setDefaultImageView(imageView,url,R.mipmap.bg_pic_def_rect,context);
        final Bitmap[] bmpSource = {null};
        Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                bmpSource[0] = resource;
                imageView.setImageBitmap(resource);
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bmpSource[0] != null){
                    File dir = new File(Constant.SAVE_PIC);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    try {
                        String filePath = Constant.SAVE_PIC + meizi.get_id()+".jpg";
                        if(!FileUtils.isFileExists(filePath)){
                            ImageUtil.saveBitmap(bmpSource[0],filePath);
                        }
                        Toast.makeText(context, "图片已保存至"+ Constant.SAVE_PIC , Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    /**
     * 如果可见的卡片数是3，则可以不用实现这个方法
     * @return
     */
    @Override
    public int getVisibleCardCount() {
        return super.getVisibleCardCount();
    }
}
