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

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.DBHandler;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.utils.ConfigKey;

import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
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
    private TextView tvQQLogin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initView();
        addListener();
    }

    private void addListener() {
        mBtnLogin.setOnClickListener(this);
        tvRegist.setOnClickListener(this);
        tvForgetPwd.setOnClickListener(this);
        tvQQLogin.setOnClickListener(this);
    }

    private void initView() {
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mEtPwd = (EditText) findViewById(R.id.et_pwd);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        tvRegist = (TextView) findViewById(R.id.tv_regist);
        tvForgetPwd = (TextView) findViewById(R.id.tv_forget_pwd);
        tvQQLogin = (TextView) findViewById(R.id.tv_qq_login);
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
            case R.id.tv_qq_login:
                UMShareAPI.get(LoginActivity.this).getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, authListener);
                break;
        }
    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if(data != null && data.size()>0){
                String uid = data.get("uid");
                String name = data.get("name");
                String headerImg = data.get("profile_image_url");
                String gender = data.get("gender");

                doRegistAndLogin(uid,name,headerImg,gender);
            }
        }
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            toast("失败"+ t.getMessage());
        }
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            toast("取消");
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void doRegistAndLogin(final  String uid,final String name,final String headerImg,final String gender) {
        showProgressDialog(false);

        BmobQuery<PersonInfo> query = new BmobQuery<>();
        query.addWhereEqualTo("uid", uid);
        query.findObjects(new FindListener<PersonInfo>() {
            @Override
            public void done(List<PersonInfo> userList, BmobException e) {
                if(e == null){
                    if(null != userList && userList.size()>0){
                        PersonInfo user = userList.get(0);
                        user.setUsername(uid);
                        user.setPassword("a111111");
                        user.login(new SaveListener<PersonInfo>() {
                            @Override
                            public void done(PersonInfo user, BmobException e) {
                                stopProgressDialog();
                                if(e == null){
                                    aCache.put("has_login","yes");
                                    aCache.put(ConfigKey.KEY_CURR_LOGIN_MOBILE,uid);
                                    PersonInfo pi = PersonInfo.getCurrentUser(PersonInfo.class);
                                    pi.setMobile(uid);
                                    if(null != pi){
                                        DBHandler.insertPesonInfo(pi);
                                    }
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                }else{
                                    toast("登录失败:" + e.getMessage());
                                }
                            }
                        });
                    }else{
                        PersonInfo piInfo = new PersonInfo();
                        piInfo.setUid(uid);
                        piInfo.setName(name);
                        piInfo.setSex(gender);
                        piInfo.setHeader_img_url(headerImg);
                        piInfo.setUsername(uid);
                        piInfo.setPassword("a111111");
                        piInfo.signUp(new SaveListener<PersonInfo>() {
                            @Override
                            public void done(PersonInfo user,BmobException e) {
                                stopProgressDialog();
                                if(e==null){
                                    user.setUsername(uid);
                                    user.setPassword("a111111");
                                    user.login(new SaveListener<PersonInfo>() {
                                        @Override
                                        public void done(PersonInfo user, BmobException e) {
                                            stopProgressDialog();
                                            if(e == null){
                                                aCache.put("has_login","yes");
                                                aCache.put(ConfigKey.KEY_CURR_LOGIN_MOBILE,uid);
                                                PersonInfo pi = PersonInfo.getCurrentUser(PersonInfo.class);
                                                pi.setMobile(uid);
                                                if(null != pi){
                                                    DBHandler.insertPesonInfo(pi);
                                                }
                                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                            }else{
                                                toast("登录失败:" + e.getMessage());
                                            }
                                        }
                                    });
                                }else{
                                    toast("注册失败:" + e.getMessage());
                                }
                            }
                        });
                    }
                }else{
                    toast("查询失败:" + e.getMessage());
                }
            }
        });
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
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }else{
                    toast("登录失败:" + e.getMessage());
                }
            }
        });

    }
}

