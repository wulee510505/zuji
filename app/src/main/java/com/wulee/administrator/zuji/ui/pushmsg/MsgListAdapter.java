package com.wulee.administrator.zuji.ui.pushmsg;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.database.bean.PushMessage;
import com.wulee.administrator.zuji.utils.DateTimeUtils;

import java.util.ArrayList;


public class MsgListAdapter extends BaseQuickAdapter<PushMessage> {

    public MsgListAdapter(int layoutResId, ArrayList<PushMessage> dataList) {
        super(layoutResId, dataList);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final PushMessage msg) {

        baseViewHolder.setText(R.id.tv_content,msg.getContent());
        baseViewHolder.setText(R.id.tv_time, DateTimeUtils.getStringDateTime(msg.getTime()));

    }
}
