package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.stetho.common.LogUtil;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.CircleContentAdapter;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.CircleComment;
import com.wulee.administrator.zuji.entity.CircleContent;
import com.wulee.administrator.zuji.utils.ImageUtil;
import com.wulee.administrator.zuji.widget.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by wulee on 2017/12/8 14:48
 * 私人圈子
 */

public class PrivateCircleActivity extends BaseActivity {

    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.iv_publish_circle)
    ImageView ivPublishCircle;
    @InjectView(R.id.titlelayout)
    RelativeLayout titlelayout;
    @InjectView(R.id.recyclerview)
    EasyRecyclerView recyclerview;
    @InjectView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;
    @InjectView(R.id.activity_main)
    RelativeLayout activityMain;
    @InjectView(R.id.iv_back)
    ImageView ivBack;


    private View mRootView;
    private AppCompatImageView ivHeaderBg;

    private CircleContentAdapter mAdapter;

    private ArrayList<CircleContent> circleContentList = new ArrayList<>();

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int PAGE_SIZE = 10;
    private int curPage = 0;
    private boolean isRefresh = false;
    private boolean isPullToRefresh = false;


    private PersonInfo personInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.circle_main);
        ButterKnife.inject(this);

        personInfo = (PersonInfo) getIntent().getSerializableExtra("piInfo");
        if (null == personInfo) {
            return;
        }
        initView();
        addListener();
        getCircleContnets(0, STATE_REFRESH);
    }

    private void initView() {
        ivBack.setVisibility(View.VISIBLE);
        ivPublishCircle.setVisibility(View.GONE);

        View headerView = LayoutInflater.from(this).inflate(R.layout.circle_list_header, null);
        ivHeaderBg = headerView.findViewById(R.id.iv_header_bg);

        ImageView ivUserAvatar = headerView.findViewById(R.id.userAvatar);
        TextView tvNick = headerView.findViewById(R.id.userNick);

        if (!TextUtils.isEmpty(personInfo.getName()))
            tvNick.setText(personInfo.getName());
        else
            tvNick.setText("游客");
        ImageUtil.setDefaultImageView(ivUserAvatar, personInfo.getHeader_img_url(), R.mipmap.icon_user_def_rect, this);
        ImageUtil.setDefaultImageView(ivHeaderBg, personInfo.getCircle_header_bg_url(), R.mipmap.bg_circle_header, this);

        mAdapter = new CircleContentAdapter(circleContentList, this);
        mAdapter.addHeaderView(headerView);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, 1, ContextCompat.getColor(this, R.color.grayline)));
        recyclerview.setAdapter(mAdapter);
    }

    private void addListener() {
        swipeLayout.setOnRefreshListener(() -> {
            isPullToRefresh = true;
            isRefresh = true;
            curPage = 0;
            getCircleContnets(curPage, STATE_REFRESH);
        });
        //加载更多
        mAdapter.setEnableLoadMore(true);
        mAdapter.setPreLoadNumber(PAGE_SIZE);
        mAdapter.setOnLoadMoreListener(() -> getCircleContnets(curPage, STATE_MORE));

        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mAdapter.setLikeAndCommentViewGone();
            }
        });
    }

    public void getCircleContnets(final int page, final int actionType) {
        BmobQuery<CircleContent> query = new BmobQuery<>();
        query.addWhereEqualTo("personInfo", personInfo);  // 查询该用户的所有圈子信息
        query.include("personInfo");
        query.order("-createdAt");
        // 如果是加载更多
        if (actionType == STATE_MORE) {
            // 跳过之前页数并去掉重复数据
            query.setSkip(page * PAGE_SIZE + 1);
        } else {
            query.setSkip(0);
        }
        // 设置每页数据个数
        query.setLimit(PAGE_SIZE);
        query.findObjects(new FindListener<CircleContent>() {
            @Override
            public void done(List<CircleContent> list, BmobException e) {
                stopProgressDialog();
                if (swipeLayout != null && swipeLayout.isRefreshing())
                    swipeLayout.setRefreshing(false);
                if (e == null) {
                    curPage++;
                    if (isRefresh) {//下拉刷新需清理缓存
                        mAdapter.setNewData(processCircleContent(list));
                        isRefresh = false;
                    } else {//正常请求 或 上拉加载更多时处理流程
                        if (list.size() > 0) {
                            mAdapter.addData(processCircleContent(list));
                            mAdapter.loadMoreComplete();
                        } else {
                            mAdapter.loadMoreEnd();
                        }
                    }
                } else {
                    LogUtil.d("查询CircleContent失败" + e.getMessage() + "," + e.getErrorCode());
                    mAdapter.loadMoreFail();
                }
            }
        });
    }

    private List<CircleContent> processCircleContent(List<CircleContent> list) {
        for (int i = 0; i < list.size(); i++) {
            final CircleContent content = list.get(i);
            // 查询喜欢这个帖子的所有用户，因此查询的是用户表
            BmobQuery<PersonInfo> query = new BmobQuery<>();
            //likes是CircleContent表中的字段，用来存储所有喜欢该帖子的用户
            query.addWhereRelatedTo("likes", new BmobPointer(content));
            query.findObjects(new FindListener<PersonInfo>() {
                @Override
                public void done(List<PersonInfo> list, BmobException e) {
                    if (e == null) {
                        if (null != list && list.size() > 0) {
                            content.setLikeList(list);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        LogUtil.i("zuji", "失败：" + e.getMessage());
                    }
                }
            });

            BmobQuery<CircleComment> queryComment = new BmobQuery<>();
            queryComment.addWhereEqualTo("circleContent", new BmobPointer(content));
            //希望同时查询该评论的发布者的信息，以及该帖子的作者的信息，这里用到上面`include`的并列对象查询和内嵌对象的查询
            queryComment.include("personInfo,circleContent.personInfo");
            queryComment.findObjects(new FindListener<CircleComment>() {
                @Override
                public void done(List<CircleComment> comments, BmobException e) {
                    if (e == null) {
                        if (null != comments && comments.size() > 0) {
                            content.setCommentList(comments);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        LogUtil.i("zuji", "失败：" + e.getMessage());
                    }
                }
            });

            String[] imgUrls = content.getImgUrls();
            if (imgUrls != null && imgUrls.length > 0) {
                ArrayList<CircleContent.CircleImageBean> imgList = new ArrayList<>();
                for (int j = 0; j < imgUrls.length; j++) {
                    String imgUrl = imgUrls[j];
                    CircleContent.CircleImageBean circleImg = new CircleContent.CircleImageBean();
                    circleImg.setId(j + "");
                    circleImg.setUrl(imgUrl);
                    imgList.add(circleImg);
                }
                content.setImageList(imgList);
            }
        }
        return list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
