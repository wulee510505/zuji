package com.wulee.administrator.zuji.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.NewsAdapter;
import com.wulee.administrator.zuji.entity.NewsInfo;
import com.wulee.administrator.zuji.utils.GsonUtil;
import com.wulee.webactivitylib.WebActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import okhttp3.Headers;

/**
 * Created by wulee on 2017/9/7 10:52
 */
public class NewsChildFragment extends Fragment {


    @InjectView(R.id.recyclerview)
    EasyRecyclerView recyclerview;
    @InjectView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;
    @InjectView(R.id.progress_bar)
    ProgressBar progressBar;
    private View mRootView;
    private Context mContext;

    private boolean isRefresh = false;

    private NewsAdapter mAdapter;
    private ArrayList<NewsInfo.NewsEntity> mDataList = new ArrayList<>();

    private String mUrl;

    public static NewsChildFragment newInstance(String url) {
        NewsChildFragment fragment = new NewsChildFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //这里我只是简单的用num区别标签，其实具体应用中可以使用真实的fragment对象来作为叶片
        mUrl = getArguments() != null ? getArguments().getString("url") : "";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.news_child_list, container, false);
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

        mAdapter = new NewsAdapter(R.layout.news_list_item, mDataList, mContext);
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerview.setAdapter(mAdapter);

        addListener();
        getNews(mUrl);
    }

    private void addListener() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                getNews(mUrl);
            }
        });
        mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                List<NewsInfo.NewsEntity> newsList = mAdapter.getData();
                if(newsList != null && newsList.size()>0){
                    NewsInfo.NewsEntity news = newsList.get(pos);
                    if(news != null){
                        WebActivity.launch(getActivity(),news.getUrl(),news.getTitle(),R.color.colorAccent);
                    }
                }
            }
        });
    }

    private void getNews(String url) {
        HttpRequest.get(url, new BaseHttpRequestCallback() {
            //请求网络前
            @Override
            public void onStart() {
                if (!isRefresh)
                    progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onResponse(String response, Headers headers) {
                super.onResponse(response, headers);

                if (isRefresh)
                    swipeLayout.setRefreshing(false);

                NewsInfo newsInfo = GsonUtil.changeGsonToBean(response, NewsInfo.class);
                if (null != newsInfo) {

                    mDataList.clear();
                    mDataList.addAll(newsInfo.getNewsEntityList());

                    mAdapter.setNewData(mDataList);
                }
            }

            //请求失败（服务返回非法JSON、服务器异常、网络异常）
            @Override
            public void onFailure(int errorCode, String msg) {
                Toast.makeText(mContext, "网络异常~，请检查你的网络是否连接后再试", Toast.LENGTH_SHORT).show();
            }

            //请求网络结束
            @Override
            public void onFinish() {
                if(progressBar != null)
                   progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}
