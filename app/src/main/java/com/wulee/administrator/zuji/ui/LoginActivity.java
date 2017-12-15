package com.wulee.administrator.zuji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.DBHandler;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.utils.ConfigKey;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.wulee.administrator.zuji.App.aCache;

/**
 * Created by wulee on 2017/1/12 09:57
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private EditText mEtMobile;
    private EditText mEtPwd;
    private Button  mBtnLogin;
    private TextView tvRegist;
    private TextView tvForgetPwd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initView();
        initData();
        addListener();
    }


    private void addListener() {
        mBtnLogin.setOnClickListener(this);
        tvRegist.setOnClickListener(this);
        tvForgetPwd.setOnClickListener(this);
    }

    private void initView() {
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mEtPwd = (EditText) findViewById(R.id.et_pwd);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        tvRegist = (TextView) findViewById(R.id.tv_regist);
        tvForgetPwd = (TextView) findViewById(R.id.tv_forget_pwd);
    }

    private void initData() {
        String mobile = aCache.getAsString(ConfigKey.KEY_CURR_LOGIN_MOBILE);
        if(!TextUtils.isEmpty(mobile)){
            mEtMobile.setText(mobile);
            mEtMobile.setSelection(mobile.length());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                String mobile = mEtMobile.getText().toString().trim();
                String pwd = mEtPwd.getText().toString().trim();
                if(TextUtils.isEmpty(mobile)){
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(pwd)){
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                doLogin(mobile,pwd);
                break;
            case R.id.tv_regist:
                startActivity(new Intent(this,RegistActivity.class));
                break;
            case R.id.tv_forget_pwd:
                startActivity(new Intent(this,ReSetPwdActivity.class));
                break;
        }
    }



    private void doLogin(final String mobile, String pwd) {
        showProgressDialog(false);
        PersonInfo user = new PersonInfo();
        user.setUsername(mobile);
        user.setPassword(pwd);
        user.login(new SaveListener<PersonInfo>() {
            @Override
            public void done(PersonInfo user, BmobException e) {
                stopProgressDialog();
                if(e == null){
                    aCache.put("has_login","yes");
                    aCache.put(ConfigKey.KEY_CURR_LOGIN_MOBILE,mobile);

                    PersonInfo pi = PersonInfo.getCurrentUser(PersonInfo.class);
                    if(null != pi){
                        pi.setMobile(mobile);
                        DBHandler.insertPesonInfo(pi);
                    }
                    startActivity(new Intent(LoginActivity.this,MainNewActivity.class));
                    LoginActivity.this.finish();
                }else{
                    toast("登录失败:" + e.getMessage());
                }
            }
        });

    }
}

