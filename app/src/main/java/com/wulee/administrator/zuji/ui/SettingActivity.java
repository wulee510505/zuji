package com.wulee.administrator.zuji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.bean.PersonInfo;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by wulee on 2017/1/11 16:59
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    public static final int INTENT_KEY_HOME =  101;
    public static final int INTENT_KEY_COMPANY =  102;

    private TextView mEtHome;
    private TextView mEtCompany;
    private ImageView ivSave;
    private ImageView ivBack;

    private Double  homeLat;
    private Double  homeLon;
    private String  homeAddress;

    private Double  companyLat;
    private Double  companyLon;
    private String  companyAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.settting);

        initView();
        addListerer();
    }

    private void addListerer() {
        ivBack.setOnClickListener(this);
        ivSave.setOnClickListener(this);
        mEtHome.setOnClickListener(this);
        mEtCompany.setOnClickListener(this);
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivSave = (ImageView) findViewById(R.id.iv_save);
        mEtHome = (TextView) findViewById(R.id.et_home);
        mEtCompany = (TextView) findViewById(R.id.et_company);

        PersonInfo user = BmobUser.getCurrentUser(PersonInfo.class);
        mEtHome.setText(user.getHomeAddress());
        mEtCompany.setText(user.getCompanyAddress());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.et_home:
                startActivityForResult(new Intent(this,MapSelPointActivity.class),INTENT_KEY_HOME);
                break;
            case R.id.et_company:
                startActivityForResult(new Intent(this,MapSelPointActivity.class),INTENT_KEY_COMPANY);
                break;
            case R.id.iv_save:
                PersonInfo user = BmobUser.getCurrentUser(PersonInfo.class);
                user.setHomeLat(homeLat);
                user.setHomeLon(homeLon);
                user.setHomeAddress(homeAddress);

                user.setCompanyLat(companyLat);
                user.setCompanyLon(companyLon);
                user.setCompanyAddress(companyAddress);
                user.update(user.getObjectId(),new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        toast("保存成功");
                        finish();
                    }else{
                        toast("保存失败:" + e.getMessage());
                    }
                }
            });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case INTENT_KEY_HOME:
                    LatLng homeLoction = data.getParcelableExtra("latLng");
                    if(null == homeLoction)
                        return;
                    homeLat = homeLoction.latitude;
                    homeLon = homeLoction.longitude;

                    homeAddress = data.getStringExtra("address");
                    mEtHome.setText(homeAddress);
                break;
                case INTENT_KEY_COMPANY:
                    LatLng companyLoction = data.getParcelableExtra("latLng");
                    if(null == companyLoction)
                        return;
                    companyLat = companyLoction.latitude;
                    companyLon = companyLoction.longitude;

                    companyAddress = data.getStringExtra("address");
                    mEtCompany.setText(companyAddress);
                break;
            }
        }
    }
}
