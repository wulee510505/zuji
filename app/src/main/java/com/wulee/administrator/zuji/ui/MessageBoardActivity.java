package com.wulee.administrator.zuji.ui;

import android.Manifest;
import android.app.Dialog;
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
import android.widget.LinearLayout;

import com.facebook.stetho.common.LogUtil;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.MessageAdapter;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.Constant;
import com.wulee.administrator.zuji.entity.MessageInfo;
import com.wulee.administrator.zuji.utils.ConfigKey;
import com.wulee.administrator.zuji.widget.BaseTitleLayout;
import com.wulee.administrator.zuji.widget.TitleLayoutClickListener;
import com.wulee.recordingibrary.entity.Voice;
import com.wulee.recordingibrary.view.RecordVoiceButton;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import static com.wulee.administrator.zuji.App.aCache;

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
    LinearLayout llOpt;

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


        llOpt = findViewById(R.id.llayout_opt);
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
        swipeLayout.setOnRefreshListener(() -> {
            isRefresh = true;
            getMessageList();
        });
    }

    private void init() {
        String mobile = aCache.getAsString(ConfigKey.KEY_CURR_LOGIN_MOBILE);
        if (TextUtils.equals(piInfo.getUsername(), mobile)) {
            llOpt.setVisibility(View.GONE);
        } else {
            llOpt.setVisibility(View.VISIBLE);
        }
        btnRecord.setAudioSavePath(Constant.SAVE_AUDIO);
        AndPermission.with(this)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, List<String> grantedPermissions) {
                        btnRecord.setEnrecordVoiceListener(new RecordVoiceButton.EnRecordVoiceListener() {
                            @Override
                            public void onFinishRecord(long length, String strLength, String filePath) {

                                final MessageInfo messageInfo = new MessageInfo(MessageInfo.TYPE_AUDIO);
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

                                final BmobFile bmobFile = new BmobFile(new File(filePath));
                                bmobFile.uploadblock(new UploadFileListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            LogUtil.d("上传文件成功:" + bmobFile.getFileUrl());
                                            messageInfo.audioUrl = bmobFile.getFileUrl();
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
                                        } else {
                                            LogUtil.d("上传文件失败：" + e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onProgress(Integer value) {
                                        // 返回的上传进度（百分比）
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onFailed(int requestCode, List<String> deniedPermissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(MessageBoardActivity.this, deniedPermissions))
                            AndPermission.defaultSettingDialog(MessageBoardActivity.this).show();
                    }
                })
                .start();

        mAdapter = new MessageAdapter(null, this);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(mAdapter);
    }

    //留言Dialog
    private void showMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("留言");
        View dialogView = LayoutInflater.from(this).inflate(R.layout.circle_comment_dialog, null);
        final EditText etMessage = dialogView.findViewById(R.id.et_comment);

        builder.setView(dialogView);
        builder.setPositiveButton("确定", (dialog, which) -> {

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

        if (!isRefresh){
            showProgressDialog(false);
        }
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

                        aCache.put(ConfigKey.KEY_MESSAGE_COUNT,list.size()+"");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.stopPlayAudio();
    }
}
