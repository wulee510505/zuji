package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.MessageAdapter;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.MessageInfo;
import com.wulee.administrator.zuji.widget.BaseTitleLayout;
import com.wulee.administrator.zuji.widget.TitleLayoutClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by wulee on 2017/9/19 10:46
 */

public class MessageBoardActivity extends BaseActivity {

    @InjectView(R.id.titlelayout)
    BaseTitleLayout titlelayout;
    @InjectView(R.id.recyclerview)
    RecyclerView recyclerview;
    @InjectView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;

    private EditText etMessage;
    private Button   btnSubmit;

    private MessageAdapter mAdapter;
    private ArrayList<MessageInfo> messageList = new ArrayList<>();

    private PersonInfo piInfo;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_board_list_main);
        ButterKnife.inject(this);

        piInfo = (PersonInfo) getIntent().getSerializableExtra("piInfo");

        init();
        addListener();
        getMessageList();
    }

    private void addListener() {
        titlelayout.setOnTitleClickListener(new TitleLayoutClickListener() {
            @Override
            public void onLeftClickListener() {
                super.onLeftClickListener();
                finish();
            }
        });
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                getMessageList();
            }
        });
    }

    private void init() {
        View headerView = LayoutInflater.from(this).inflate(R.layout.message_board_header,null);
        etMessage = (EditText) headerView.findViewById(R.id.et_message_content);
        btnSubmit = (Button) headerView.findViewById(R.id.btn_submit_message);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageContent = etMessage.getText().toString().trim();
                if(TextUtils.isEmpty(messageContent)){
                    toast("说点什么吧...");
                    return;
                }
                PersonInfo personInfo = PersonInfo.getCurrentUser(PersonInfo.class);

                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setContent(messageContent);
                if(personInfo != null)
                   messageInfo.piInfo = personInfo;
                if(piInfo != null)
                    messageInfo.owner = piInfo;

                //将当前用户添加到MessageInfo表中的sender字段值中，表明当前用户留了言
                BmobRelation relation = new BmobRelation();
                //将当前用户添加到多对多关联中
                relation.add(personInfo);
                //多对多关联指向MessageInfo的`sender`字段
                messageInfo.setSender(relation);

                showProgressDialog(false);
                messageInfo.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        stopProgressDialog();
                         if(e == null){
                             if(!TextUtils.isEmpty(s)){
                                 toast("留言成功");
                                 getMessageList();
                             }
                         }
                    }
                });
            }
        });
        mAdapter = new MessageAdapter(R.layout.message_board_list_item,null);
        mAdapter.addHeaderView(headerView);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(mAdapter);
    }


    public void getMessageList() {
        BmobQuery<MessageInfo> messageQuery = new BmobQuery<>();
        messageQuery.include("piInfo");
        messageQuery.addWhereEqualTo("owner",piInfo);    // 查询指定用户的所有留言信息
        messageQuery.order("-createdAt");

        if(!isRefresh)
           showProgressDialog(false);
        messageQuery.findObjects(new FindListener<MessageInfo>() {
            @Override
            public void done(List<MessageInfo> list, BmobException e) {
                swipeLayout.setRefreshing(false);
                stopProgressDialog();
                if( e== null){
                  if(list != null && list.size()>0){
                      messageList.clear();
                      messageList.addAll(list);
                      mAdapter.setNewData(messageList);
                  }
                }
            }
        });
    }
}
