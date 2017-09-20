package com.wulee.administrator.zuji.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.entity.MessageInfo;

import java.util.ArrayList;


public class MessageAdapter extends BaseQuickAdapter<MessageInfo> {

    public MessageAdapter(int layoutResId, ArrayList<MessageInfo> dataList) {
        super(layoutResId, dataList);
    }
    @Override
    protected void convert(BaseViewHolder baseViewHolder, MessageInfo message) {

        baseViewHolder.setText(R.id.tv_content,message.getContent());
        if(message.piInfo != null){
            if(!TextUtils.isEmpty(message.piInfo.getName())){
                baseViewHolder.setText(R.id.tv_name,message.piInfo.getName());
            }else{
                baseViewHolder.setText(R.id.tv_name,message.piInfo.getUsername());
            }
        }
        baseViewHolder.setText(R.id.tv_time , message.getCreatedAt().substring(0,16));
    }
}
