package com.wulee.administrator.zuji.ui.pushmsg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.database.DBHandler;
import com.wulee.administrator.zuji.database.bean.PushMessage;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wulee on 2017/2/28 21:15
 */

public class PushMsgListActivity extends AppCompatActivity {

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
    }

    private void initData() {
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
