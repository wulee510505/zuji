package com.wulee.administrator.zuji.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.entity.PersonalInfo;
import com.wulee.administrator.zuji.ui.pushmsg.PushMsgListActivity;
import com.wulee.administrator.zuji.utils.AppUtils;
import com.wulee.administrator.zuji.utils.LocationUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;

import static com.wulee.administrator.zuji.App.aCache;


/**
 * Created by wulee on 2017/1/22
 * 主页侧滑菜单 (仿QQ效果)
 */
public class MainQMenuLeft extends Fragment implements View.OnClickListener {

    private static final String REQ_CHECK_UPDATE = "menu_check_update";
    private static final int REQUEST_CODE_IMAGE = 100;

    private Context mContext;

    private ImageView rbImage;
    private TextView mTvMobile;

    private TextView tvMsg,tvSetting,tvLoginOut,tvCheckUpdate; // 登录、退出登录提示语

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.main_menu_left, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initUI(View view) {
        rbImage = (ImageView) view.findViewById(R.id.circle_img_header);
        mTvMobile = (TextView) view.findViewById(R.id.tv_mobile);

        tvMsg = (TextView) view.findViewById(R.id.mml_pushmsg_tv);
        tvSetting = (TextView) view.findViewById(R.id.mml_setting_tv);
        tvCheckUpdate = (TextView) view.findViewById(R.id.mml_checkupdate_tv);
        tvLoginOut = (TextView) view.findViewById(R.id.mml_loginout_tv);

        tvMsg.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        tvLoginOut.setOnClickListener(this);
        tvCheckUpdate.setOnClickListener(this);
        rbImage.setOnClickListener(this);

        PersonalInfo piInfo = BmobUser.getCurrentUser(PersonalInfo.class);
        if(null != piInfo){
            mTvMobile.setText(piInfo.getMobilePhoneNumber());
        }
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.mml_loginout_tv:
                 aCache.put("has_login","no");
                 LocationUtil.getInstance().stopGetLocation();
                 AppUtils.AppExit(mContext);
                 PersonalInfo.logOut();
                 startActivity(new Intent(mContext,LoginActivity.class));
                 break;
             case R.id.mml_pushmsg_tv:
                 startActivity(new Intent(mContext,PushMsgListActivity.class));
                 break;
             case R.id.mml_checkupdate_tv:
                 BmobUpdateAgent.update(mContext);
                 break;
             case R.id.circle_img_header:

                 break;
             case R.id.mml_setting_tv:
                 startActivity(new Intent(mContext,SettingActivity.class));
                 break;
         }

    }
}
