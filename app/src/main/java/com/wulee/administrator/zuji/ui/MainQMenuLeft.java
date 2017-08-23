package com.wulee.administrator.zuji.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.database.bean.PushMessage;
import com.wulee.administrator.zuji.ui.pushmsg.PushMsgListActivity;
import com.wulee.administrator.zuji.utils.AppUtils;
import com.wulee.administrator.zuji.utils.ImageUtil;
import com.wulee.administrator.zuji.utils.LocationUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

import static com.wulee.administrator.zuji.App.aCache;
import static com.wulee.administrator.zuji.R.id.about_me_tv;
import static com.wulee.administrator.zuji.R.id.mml_setting_tv;


/**
 * Created by wulee on 2017/1/22
 * 主页侧滑菜单 (仿QQ效果)
 */
public class MainQMenuLeft extends Fragment implements View.OnClickListener {

    private Context mContext;

    private ImageView rbImage;
    private TextView mTvName;
    private TextView mTvMobile;
    private TextView tvFeedBack,tvSetting,tvLoginOut,tvCheckUpdate,tvAboutme; // 登录、退出登录提示语
    private RelativeLayout mRlMsg;
    private ImageView ivNewMsg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.main_menu_left, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public  void onPushMsgEvent(PushMessage message){
          if(null != message)
              ivNewMsg.setVisibility(View.VISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();

        refreshUserInfo();
    }


    private void initUI(View view) {
        rbImage = (ImageView) view.findViewById(R.id.circle_img_header);
        mTvName = (TextView) view.findViewById(R.id.tv_name);
        mTvMobile = (TextView) view.findViewById(R.id.tv_mobile);

        tvFeedBack= (TextView) view.findViewById(R.id.mml_feedback_tv);
        mRlMsg = (RelativeLayout) view.findViewById(R.id.rl_pushmsg_tv);
        ivNewMsg = (ImageView) view.findViewById(R.id.iv_new_msg);
        tvSetting = (TextView) view.findViewById(mml_setting_tv);
        tvCheckUpdate = (TextView) view.findViewById(R.id.mml_checkupdate_tv);
        tvLoginOut = (TextView) view.findViewById(R.id.mml_loginout_tv);
        tvAboutme= (TextView) view.findViewById(R.id.about_me_tv);

        tvFeedBack.setOnClickListener(this);
        mRlMsg.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        tvLoginOut.setOnClickListener(this);
        tvCheckUpdate.setOnClickListener(this);
        rbImage.setOnClickListener(this);
        tvAboutme.setOnClickListener(this);

    }

    private void refreshUserInfo() {
        PersonInfo piInfo = BmobUser.getCurrentUser(PersonInfo.class);
        if(null != piInfo){
            if(!TextUtils.isEmpty(piInfo.getName()))
                mTvName.setText(piInfo.getName());
            else
                mTvName.setText("游客");
            mTvMobile.setText(piInfo.getMobilePhoneNumber());
            ImageUtil.setCircleImageView(rbImage,piInfo.getHeader_img_url(),R.mipmap.icon_user_def,mContext);
        }else{
            mTvName.setText("游客");
        }
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.mml_loginout_tv:
                 showLogoutDialog();
                 break;
             case R.id.rl_pushmsg_tv:
                 startActivity(new Intent(mContext,PushMsgListActivity.class));
                 ivNewMsg.setVisibility(View.INVISIBLE);
                 break;
             case R.id.mml_checkupdate_tv:
                 BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
                     @Override
                     public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                         // TODO Auto-generated method stub
                         if (updateStatus == UpdateStatus.Yes) {//版本有更新

                         }else if(updateStatus == UpdateStatus.No){
                             Toast.makeText(mContext,"版本无更新", Toast.LENGTH_SHORT).show();
                         }else if(updateStatus==UpdateStatus.EmptyField){//此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
                             Toast.makeText(mContext, "请检查你AppVersion表的必填项，1、target_size（文件大小）是否填写；2、path或者android_url两者必填其中一项。", Toast.LENGTH_SHORT).show();
                         }else if(updateStatus==UpdateStatus.IGNORED){
                             Toast.makeText(mContext, "该版本已被忽略更新", Toast.LENGTH_SHORT).show();
                         }else if(updateStatus==UpdateStatus.ErrorSizeFormat){
                             Toast.makeText(mContext, "请检查target_size填写的格式，请使用file.length()方法获取apk大小。", Toast.LENGTH_SHORT).show();
                         }else if(updateStatus==UpdateStatus.TimeOut){
                             Toast.makeText(mContext, "查询出错或查询超时", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
                 BmobUpdateAgent.forceUpdate(mContext);
                 break;
             case R.id.circle_img_header:
                 startActivity(new Intent(mContext,PersonalInfoActivity.class));
                 break;
             case R.id.mml_feedback_tv:
                 startActivity(new Intent(mContext,FeedBackActivity.class));
                 break;
             case mml_setting_tv:
                 startActivity(new Intent(mContext,SettingActivity.class));
                 break;
             case about_me_tv:
                 startActivity(new Intent(mContext,AboutMeActivity.class));
                 break;
         }
    }


    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("确定要退出吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                aCache.put("has_login","no");
                LocationUtil.getInstance().stopGetLocation();
                AppUtils.AppExit(mContext);
                PersonInfo.logOut();
                startActivity(new Intent(mContext,LoginActivity.class));
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }
}
