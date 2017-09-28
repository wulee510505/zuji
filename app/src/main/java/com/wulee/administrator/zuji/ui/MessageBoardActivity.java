package com.wulee.administrator.zuji.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.wulee.administrator.zuji.entity.Constant;
import com.wulee.administrator.zuji.entity.MessageInfo;
import com.wulee.administrator.zuji.widget.BaseTitleLayout;
import com.wulee.administrator.zuji.widget.TitleLayoutClickListener;
import com.wulee.recordingibrary.entity.Voice;
import com.wulee.recordingibrary.view.RecordVoiceButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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
    @InjectView(R.id.btn_submit_message)
    Button btnSubmitMessage;
    @InjectView(R.id.btn_record)
    RecordVoiceButton btnRecord;


    private MessageAdapter mAdapter;
    private ArrayList<MessageInfo> messageList = new ArrayList<>();

    private PersonInfo piInfo;
    private boolean isRefresh = false;

    private PersonInfo currPiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_board_list_main);
        ButterKnife.inject(this);

        piInfo = (PersonInfo) getIntent().getSerializableExtra("piInfo");
        currPiInfo = PersonInfo.getCurrentUser(PersonInfo.class);

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
        btnRecord.setAudioSavePath(Constant.SAVE_AUDIO);
        btnRecord.setEnrecordVoiceListener(new RecordVoiceButton.EnRecordVoiceListener() {
            @Override
            public void onFinishRecord(long length, String strLength, String filePath) {
                //adapter.add(new Voice(length, strLength, filePath) )

                MessageInfo messageInfo = new MessageInfo(MessageInfo.TYPE_AUDIO);
                messageInfo.voice = new Voice(length, strLength, filePath);
                if (currPiInfo != null)
                    messageInfo.piInfo = currPiInfo;
                if (piInfo != null)
                    messageInfo.owner = piInfo;

                //将当前用户添加到MessageInfo表中的sender字段值中，表明当前用户留了言
                BmobRelation relation = new BmobRelation();
                //将当前用户添加到多对多关联中
                relation.add(currPiInfo);
                //多对多关联指向MessageInfo的`sender`字段
                messageInfo.setSender(relation);

                showProgressDialog(false);
                messageInfo.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        stopProgressDialog();
                        if (e == null) {
                            if (!TextUtils.isEmpty(s)) {
                                getMessageList();
                            }
                        }
                    }
                });
            }
        });

        mAdapter = new MessageAdapter(null, this);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(mAdapter);
    }

    //留言Dialog
    private void showMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("留言");
        View dialogView = LayoutInflater.from(this).inflate(R.layout.circle_comment_dialog, null);
        final EditText etMessage = (EditText) dialogView.findViewById(R.id.et_comment);

        builder.setView(dialogView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String messageContent = etMessage.getText().toString().trim();
                if (TextUtils.isEmpty(messageContent)) {
                    toast("说点什么吧...");
                    return;
                }
                MessageInfo messageInfo = new MessageInfo(MessageInfo.TYPE_TEXT);
                messageInfo.setContent(messageContent);
                if (currPiInfo != null)
                    messageInfo.piInfo = currPiInfo;
                if (piInfo != null)
                    messageInfo.owner = piInfo;

                //将当前用户添加到MessageInfo表中的sender字段值中，表明当前用户留了言
                BmobRelation relation = new BmobRelation();
                //将当前用户添加到多对多关联中
                relation.add(currPiInfo);
                //多对多关联指向MessageInfo的`sender`字段
                messageInfo.setSender(relation);

                showProgressDialog(false);
                messageInfo.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        stopProgressDialog();
                        if (e == null) {
                            if (!TextUtils.isEmpty(s)) {
                                getMessageList();
                            }
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("取消", null);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void getMessageList() {
        BmobQuery<MessageInfo> messageQuery = new BmobQuery<>();
        messageQuery.include("piInfo");
        messageQuery.addWhereEqualTo("owner", piInfo);    // 查询指定用户的所有留言信息
        messageQuery.order("-createdAt");

        if (!isRefresh)
            showProgressDialog(false);
        messageQuery.findObjects(new FindListener<MessageInfo>() {
            @Override
            public void done(List<MessageInfo> list, BmobException e) {
                swipeLayout.setRefreshing(false);
                stopProgressDialog();
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        messageList.clear();
                        messageList.addAll(list);
                        mAdapter.setNewData(messageList);
                    }
                }
            }
        });
    }

    @OnClick({R.id.btn_submit_message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_submit_message:
                showMessageDialog();
                break;
        }
    }
}
