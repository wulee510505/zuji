package com.wulee.administrator.zuji.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.CircleContentAdapter;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.CircleContent;
import com.wulee.administrator.zuji.ui.PublishCircleActivity;
import com.wulee.administrator.zuji.utils.ImageUtil;
import com.wulee.administrator.zuji.widget.BaseTitleLayout;
import com.wulee.administrator.zuji.widget.TitleLayoutClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by wulee on 2017/9/6 09:52
 */
public class CircleFragment extends MainBaseFrag {

    @InjectView(R.id.titlelayout)
    BaseTitleLayout titlelayout;
    @InjectView(R.id.recyclerview)
    EasyRecyclerView recyclerview;
    @InjectView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;
    private View mRootView;

    private Context mContext;

    private CircleContentAdapter mAdapter;

    private ArrayList<CircleContent> circleContentList = new ArrayList<>();

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int PAGE_SIZE = 10;
    private int curPage = 0;
    private boolean isRefresh = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.circle_main, container, false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        ButterKnife.inject(this, mRootView);

        initView(mRootView);
        addListener();
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCircleContnets(0, STATE_REFRESH);
    }

    @Override
    public void onStart() {
        super.onStart();;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    private void initView(View view) {
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.circle_list_header, null);
        ImageView ivUserAvatar = (ImageView) headerView.findViewById(R.id.userAvatar);
        TextView tvNick = (TextView) headerView.findViewById(R.id.userNick);
        PersonInfo piInfo = BmobUser.getCurrentUser(PersonInfo.class);
        if (null != piInfo) {
            if (!TextUtils.isEmpty(piInfo.getName()))
                tvNick.setText(piInfo.getName());
            else
                tvNick.setText("游客");
            ImageUtil.setRoundImageView(ivUserAvatar, piInfo.getHeader_img_url(), R.mipmap.icon_user_def, mContext);
        } else {
            tvNick.setText("游客");
        }

        mAdapter = new CircleContentAdapter(R.layout.circle_content_list_item, circleContentList, mContext);
        mAdapter.addHeaderView(headerView);
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerview.setAdapter(mAdapter);
    }

    private void addListener() {
        titlelayout.setOnTitleClickListener(new TitleLayoutClickListener() {
            @Override
            public void onLeftClickListener() {
                super.onLeftClickListener();
            }
            @Override
            public void onRightTextClickListener() {
                super.onRightTextClickListener();
            }
            @Override
            public void onRightImg1ClickListener() {
                super.onRightImg1ClickListener();
                startActivity(new Intent(mContext, PublishCircleActivity.class));
            }
            @Override
            public void onRightImg2ClickListener() {
                super.onRightImg2ClickListener();
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                curPage = 0;
                getCircleContnets(curPage, STATE_REFRESH);
            }
        });
        //加载更多
        mAdapter.openLoadMore(PAGE_SIZE, true);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getCircleContnets(curPage, STATE_MORE);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onPublishOkEvent(String event) {
        if (TextUtils.equals(event, "publish ok")) {
            isRefresh = true;
            getCircleContnets(0, STATE_REFRESH);
        }
    }


    public void getCircleContnets(final int page, final int actionType) {
        BmobQuery<CircleContent> query = new BmobQuery<>();
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
                swipeLayout.setRefreshing(false);
                if (e == null) {
                    curPage++;
                    if (isRefresh) {//下拉刷新需清理缓存
                        mAdapter.setNewData(processCircleContent(list));
                        isRefresh = false;
                    } else {//正常请求 或 上拉加载更多时处理流程
                        if (list.size() > 0) {
                            mAdapter.notifyDataChangedAfterLoadMore(processCircleContent(list), true);
                        } else {
                            mAdapter.notifyDataChangedAfterLoadMore(false);
                        }
                    }
                } else {
                    Toast.makeText(mContext, "查询失败" + e.getMessage() + "," + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<CircleContent> processCircleContent(List<CircleContent> list) {
        for (int i = 0; i < list.size(); i++) {
            CircleContent content = list.get(i);
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onFragmentFirstSelected() {

    }
}
