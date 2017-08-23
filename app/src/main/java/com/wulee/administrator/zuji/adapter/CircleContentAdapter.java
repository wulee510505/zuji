package com.wulee.administrator.zuji.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.entity.CircleContent;
import com.wulee.administrator.zuji.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;


public class CircleContentAdapter extends BaseQuickAdapter<CircleContent> {

    private Context mcontext;

    public CircleContentAdapter(int layoutResId, ArrayList<CircleContent> dataList,Context context) {
        super(layoutResId, dataList);
        this.mcontext = context;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, CircleContent content) {

        ImageView ivAvatar = baseViewHolder.getView(R.id.userAvatar);
        ImageUtil.setDefaultImageView(ivAvatar,content.getUserAvatar(),R.mipmap.icon_user_def,mcontext);

        baseViewHolder.setText(R.id.userNick,content.getUserNick());
        baseViewHolder.setText(R.id.content , content.getContent());
        baseViewHolder.setText(R.id.time , content.getCreatedAt());



        NineGridImageViewAdapter<CircleContent.CircleImageBean> mAdapter = new NineGridImageViewAdapter<CircleContent.CircleImageBean>() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, CircleContent.CircleImageBean img) {
                Glide.with(context)
                        .load(img.getUrl())
                        .placeholder(R.mipmap.ic_launcher)
                        .into(imageView);
            }
            @Override
            protected ImageView generateImageView(Context context) {
                return super.generateImageView(context);
            }
            @Override
            protected void onItemImageClick(Context context, ImageView imageView, int index, List<CircleContent.CircleImageBean> photoList) {

            }
        };

        NineGridImageView nineGridImageView = baseViewHolder.getView(R.id.nine_grid_view);
        nineGridImageView.setAdapter(mAdapter);
        nineGridImageView.setImagesData(content.getImageList());

    }

}
