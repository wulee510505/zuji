package com.wulee.administrator.zuji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.CircleContentAdapter;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.CircleContent;
import com.wulee.administrator.zuji.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;


/**
 * Created by wulee on 2017/8/18 16:25
 */

public class CircleMainActivity extends AppCompatActivity {

    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.iv_publish)
    ImageView ivPublish;
    @InjectView(R.id.titlelayout)
    RelativeLayout titlelayout;
    @InjectView(R.id.recyclerview)
    EasyRecyclerView recyclerview;
    @InjectView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;

    private CircleContentAdapter mAdapter;

    private ArrayList<CircleContent> circleContentList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.circle_main);
        ButterKnife.inject(this);

        initView();
        addListener();
        getCircleContnets();
        EventBus.getDefault().register(this);
    }


    private void initView() {
        View headerView = LayoutInflater.from(this).inflate(R.layout.circle_list_header, null);
        ImageView ivUserAvatar = (ImageView) headerView.findViewById(R.id.userAvatar);
        TextView tvNick = (TextView) headerView.findViewById(R.id.userNick);
        PersonInfo piInfo = BmobUser.getCurrentUser(PersonInfo.class);
        if(null != piInfo){
            if(!TextUtils.isEmpty(piInfo.getName()))
                tvNick.setText(piInfo.getName());
            else
                tvNick.setText("游客");
            ImageUtil.setRoundImageView(ivUserAvatar,piInfo.getHeader_img_url(),R.mipmap.icon_user_def,this);
        }else{
            tvNick.setText("游客");
        }

        mAdapter = new CircleContentAdapter(R.layout.circle_content_list_item, circleContentList,this);
        mAdapter.addHeaderView(headerView);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(mAdapter);
    }

    private void addListener() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCircleContnets();
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onPublishOkEvent(String event){
        if(TextUtils.equals(event,"publish ok")){
            getCircleContnets();
        }
    }


    @OnClick({R.id.iv_back, R.id.iv_publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_publish:
                startActivity(new Intent(this,PublishCircleActivity.class));
                break;
        }
    }

    public void getCircleContnets() {
       BmobQuery<CircleContent> query = new BmobQuery<>();
        query.order("-createdAt");
        query.findObjects(new FindListener<CircleContent>() {
            @Override
            public void done(List<CircleContent> list, BmobException e) {
                swipeLayout.setRefreshing(false);
                if(e == null){
                    if(null != list && list.size()>0){
                        mAdapter.setNewData(list);
                    }
                }
            }
        });

       /* PersonInfo piInfo = BmobUser.getCurrentUser(PersonInfo.class);
        if(null == piInfo)
            return;
        for (int i = 0; i < 5; i++) {
            CircleContent circlrContent = new CircleContent();
            circlrContent.setId(SystemClock.currentThreadTimeMillis());
            circlrContent.setUserId(piInfo.getUid());
            circlrContent.setUserNick(piInfo.getName());
            circlrContent.setUserAvatar(piInfo.getHeader_img_url());
            circlrContent.setContent("这是内容");
            circlrContent.personInfo = piInfo;
            ArrayList<CircleContent.CircleImageBean> imgList = new ArrayList<>();
            for (int j = 0; j < 9; j++) {
                CircleContent.CircleImageBean  img = new CircleContent.CircleImageBean();
                img.setId(j+"");
                img.setUrl("http://bmob-cdn-8120.b0.upaiyun.com/2017/08/22/94ff4e17f3b84c21b17c8bbfada0486d.jpg");
                imgList.add(img);
            }
            circlrContent.setImageList(imgList);
            circleContentList.add(circlrContent);
        }
        mAdapter.setNewData(circleContentList);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

