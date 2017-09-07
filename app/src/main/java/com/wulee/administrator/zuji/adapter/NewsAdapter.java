package com.wulee.administrator.zuji.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.entity.NewsInfo;
import com.wulee.administrator.zuji.utils.ImageUtil;

import java.util.ArrayList;


public class NewsAdapter extends BaseQuickAdapter<NewsInfo.NewsEntity> {

    private Context context;

    public NewsAdapter(int layoutResId, ArrayList<NewsInfo.NewsEntity> dataList,Context context) {
        super(layoutResId, dataList);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, NewsInfo.NewsEntity news) {

        baseViewHolder.setText(R.id.tv_news_title,news.getTitle());
        baseViewHolder.setText(R.id.tv_news_desc,news.getDescription());
        baseViewHolder.setText(R.id.tv_news_time , news.getCtime());

        ImageView ivPic = baseViewHolder.getView(R.id.iv_news_pic);
        ImageUtil.setDefaultImageView(ivPic,news.getPicUrl(),R.mipmap.bg_pic_def_rect,context);
    }
}
