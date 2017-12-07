package com.wulee.administrator.zuji.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.stetho.common.LogUtil;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.UserGroupAdapter;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.ui.UserInfoActivity;
import com.wulee.administrator.zuji.widget.BaseTitleLayout;
import com.wulee.administrator.zuji.widget.RecycleViewDivider;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by wulee on 2017/12/6 09:52
 */
public class UserGroupFragment extends MainBaseFrag {


    @InjectView(R.id.titlelayout)
    BaseTitleLayout titlelayout;
    @InjectView(R.id.recyclerview)
    EasyRecyclerView recyclerview;
    @InjectView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;
    private View mRootView;
    private Context mContext;

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int PAGE_SIZE = 10;
    private int curPage = 0;
    private boolean isRefresh = false;

    private UserGroupAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.user_group_list_main, container, false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        ButterKnife.inject(this, mRootView);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new UserGroupAdapter(R.layout.user_group_list_item,null,mContext);
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerview.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL, 1, ContextCompat.getColor(mContext, R.color.grayline)));
        recyclerview.setAdapter(mAdapter);

        addListener();
    }


    private void addListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            List<PersonInfo> piInfoList = mAdapter.getData();
            if(null != piInfoList && piInfoList.size()>0){
                PersonInfo personInfo = piInfoList.get(position);
                if(null != personInfo){
                    Intent intent = new Intent(mContext,UserInfoActivity.class);
                    intent.putExtra("piInfo",personInfo);
                    startActivity(intent);
                }
            }
        });
        swipeLayout.setOnRefreshListener(() -> {
            isRefresh = true;
            curPage = 0;
            getUserList(curPage, STATE_REFRESH);
        });
        //加载更多
        mAdapter.setEnableLoadMore(true);
        mAdapter.setPreLoadNumber(PAGE_SIZE);
        mAdapter.setOnLoadMoreListener(() -> getUserList(curPage, STATE_MORE));
    }

    @Override
    public void onFragmentFirstSelected() {
        getUserList(0,STATE_REFRESH);
    }


    /**
     * 分页获取数据
     */
    private void getUserList(final int page, final int actionType){
        BmobQuery<PersonInfo> query = new BmobQuery<>();
        query.order("-createdAt");
        // 如果是加载更多
        if(actionType == STATE_MORE){
            // 跳过之前页数并去掉重复数据
            query.setSkip(page * PAGE_SIZE + 1);
        }else{
            query.setSkip(0);
        }
        // 设置每页数据个数
        query.setLimit(PAGE_SIZE);
        query.findObjects(new FindListener<PersonInfo>() {
            @Override
            public void done(List<PersonInfo> dataList, BmobException e) {
                swipeLayout.setRefreshing(false);
                if(e == null){
                    curPage++;
                    if (isRefresh){//下拉刷新需清理缓存
                        mAdapter.setNewData(dataList);
                        isRefresh = false;
                    }else {//正常请求 或 上拉加载更多时处理流程
                        if (dataList.size() > 0) {
                            mAdapter.addData(dataList);
                            mAdapter.loadMoreComplete();
                        }else {
                            mAdapter.loadMoreEnd();
                        }
                    }
                }else{
                    mAdapter.loadMoreFail();
                    LogUtil.d("查询LocationInfo失败"+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
