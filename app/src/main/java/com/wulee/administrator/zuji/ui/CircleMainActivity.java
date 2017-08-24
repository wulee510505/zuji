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
        title.setText("圈子");
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
                        processCircleContent(list);
                    }
                }
            }
        });
    }

    private void processCircleContent(List<CircleContent> list) {
        for (int i = 0; i < list.size(); i++) {
            CircleContent content =  list.get(i);
            String[] imgUrls  = content.getImgUrls();
            if(imgUrls != null && imgUrls.length>0){
                ArrayList<CircleContent.CircleImageBean> imgList = new ArrayList<>();
                for (int j = 0; j < imgUrls.length; j++) {
                     String imgUrl = imgUrls[j];
                     CircleContent.CircleImageBean circleImg = new CircleContent.CircleImageBean();
                     circleImg.setId(j+"");
                     circleImg.setUrl(imgUrl);
                     imgList.add(circleImg);
                }
                content.setImageList(imgList);
            }
        }
        mAdapter.setNewData(list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

