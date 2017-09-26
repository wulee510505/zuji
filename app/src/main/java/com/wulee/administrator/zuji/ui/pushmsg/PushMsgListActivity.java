package com.wulee.administrator.zuji.ui.pushmsg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.DBHandler;
import com.wulee.administrator.zuji.database.bean.PushMessage;

import java.util.ArrayList;
import java.util.List;

import static com.wulee.administrator.zuji.PushMsgReceiver.ACTION_HIDE_PUSH_MSG_NOTIFICATION;


/**
 * Created by wulee on 2017/2/28 21:15
 */

public class PushMsgListActivity extends BaseActivity {

    private ProgressBar mPb;
    private TextView tvNodata;
    private RecyclerView mRecyclerView;
    private MsgListAdapter mAdapter;

    ArrayList<PushMessage> msgList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.push_msg_list_main);

        initView();
        initData();
    }

    private void initView() {
        ((TextView)findViewById(R.id.title)).setText("消息");
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        mPb =  (ProgressBar)findViewById(R.id.progress_bar);
        tvNodata =  (TextView) findViewById(R.id.tv_nodata);

        mAdapter = new MsgListAdapter(R.layout.push_msg_list_item, msgList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                List<PushMessage> dataList = mAdapter.getData();
                if(null != dataList && dataList.size()>0){
                    PushMessage msg = dataList.get(pos);
                    if(null != msg){
                        Intent intent = new Intent(PushMsgListActivity.this,MsgDetailActivity.class);
                        intent.putExtra("msg",msg);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void initData() {
        sendBroadcast(new Intent(ACTION_HIDE_PUSH_MSG_NOTIFICATION));

        mPb.setVisibility(View.VISIBLE);
        List<PushMessage> list = DBHandler.getAllPushMessage();
        if(null != list && list.size()>0){
            mPb.setVisibility(View.GONE);
            tvNodata.setVisibility(View.GONE);
            msgList.clear();
            msgList.addAll(list);
            mAdapter.setNewData(msgList);
        }else{
            mPb.setVisibility(View.GONE);
            tvNodata.setVisibility(View.VISIBLE);
        }
    }
}
