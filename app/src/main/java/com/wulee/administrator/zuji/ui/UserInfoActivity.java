package com.wulee.administrator.zuji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.utils.ImageUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by wulee on 2017/9/19 09:25
 */

public class UserInfoActivity extends BaseActivity {


    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.user_photo)
    ImageView userPhoto;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_gender)
    TextView tvGender;
    @InjectView(R.id.btn_message_board)
    Button btnMessageBoard;
    @InjectView(R.id.rl_circle)
    RelativeLayout rlCircle;

    private PersonInfo personInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_activity);
        ButterKnife.inject(this);

        initData();
    }


    private void initData() {
        title.setText("个人中心");
        personInfo = (PersonInfo) getIntent().getSerializableExtra("piInfo");
        if (null != personInfo) {
            if (!TextUtils.isEmpty(personInfo.getName()))
                tvName.setText(personInfo.getName());
            else
                tvName.setText("游客");

            if (!TextUtils.isEmpty(personInfo.getSex()))
                tvGender.setText(personInfo.getSex());
            else
                tvGender.setText("其他");

            ImageUtil.setCircleImageView(userPhoto, personInfo.getHeader_img_url(), R.mipmap.icon_user_def, this);
        }
    }


    @OnClick({R.id.iv_back, R.id.btn_message_board,R.id.rl_circle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_message_board:
                startActivity(new Intent(this, MessageBoardActivity.class).putExtra("piInfo", personInfo));
                break;
            case R.id.rl_circle:
                startActivity(new Intent(this, PrivateCircleActivity.class).putExtra("piInfo", personInfo));
                break;
        }

    }

}
