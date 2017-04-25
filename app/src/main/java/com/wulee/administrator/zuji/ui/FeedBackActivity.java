package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.FeedBackInfo;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by wulee on 2017/4/10 13:44
 */

public class FeedBackActivity extends BaseActivity implements View.OnClickListener {
    private Button buttonSave;
    private EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_back);

        initView();
        addListener();
    }

    private void addListener() {
        buttonSave.setOnClickListener(this);
    }

    public void initView() {
        ((TextView)findViewById(R.id.title)).setText("问题反馈");
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonSave = (Button) findViewById(R.id.button_save);
        etContent = (EditText) findViewById(R.id.et_content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_save:
                submitFeedback();
                break;
        }
    }

    public void submitFeedback() {
        String content = etContent.getText().toString().trim();
        if(TextUtils.isEmpty(content)){
            toast("请输入内容");
            return;
        }
        showProgressDialog(false);
        PersonInfo user = BmobUser.getCurrentUser(PersonInfo.class);

        FeedBackInfo feedBackInfo = new FeedBackInfo();
        feedBackInfo.personInfo  = user;
        feedBackInfo.setContent(content);
        feedBackInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                stopProgressDialog();
                if(e == null){
                    toast("反馈成功");
                    FeedBackActivity.this.finish();
                }else{
                    toast("反馈失败");
                }
            }
        });

    }

}
