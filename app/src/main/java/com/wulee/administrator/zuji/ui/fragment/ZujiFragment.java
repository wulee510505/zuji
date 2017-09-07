package com.wulee.administrator.zuji.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.LocationAdapter;
import com.wulee.administrator.zuji.database.bean.LocationInfo;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.BannerInfo;
import com.wulee.administrator.zuji.service.ScreenService;
import com.wulee.administrator.zuji.service.UploadLocationService;
import com.wulee.administrator.zuji.ui.FunPicActivity;
import com.wulee.administrator.zuji.ui.MapActivity;
import com.wulee.administrator.zuji.ui.StepActivity;
import com.wulee.administrator.zuji.ui.ZuJiMapActivity;
import com.wulee.administrator.zuji.ui.weather.WeatherActivity;
import com.wulee.administrator.zuji.utils.GlideImageLoader;
import com.wulee.administrator.zuji.utils.LocationUtil;
import com.wulee.administrator.zuji.utils.Pedometer;
import com.wulee.administrator.zuji.widget.AnimArcButtons;
import com.wulee.administrator.zuji.widget.BaseTitleLayout;
import com.wulee.administrator.zuji.widget.TitleLayoutClickListener;
import com.yanzhenjie.permission.AndPermission;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;

import static com.wulee.administrator.zuji.ui.MainNewActivity.OpenLeftMenu;

/**
 * Created by wulee on 2017/9/6 09:52
 */
public class ZujiFragment extends MainBaseFrag{

    private View mRootView;

    private Banner bannerLayout;
    private SwipeRefreshLayout swipeLayout;
    private EasyRecyclerView mRecyclerView;
    private LocationAdapter mAdapter;

    private BaseTitleLayout titleLayout;

    private Context mContext;

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int PAGE_SIZE = 10;
    private int curPage = 0;
    private boolean isRefresh = false;

    private TextView tvTime;
    private long  currServerTime;

    private LocationChangeReceiver mReceiver;

    private AnimArcButtons menuBtns;

    private Pedometer pedometer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        if (mRootView == null){
            mRootView = inflater.inflate(R.layout.location_list_main,container,false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }
        initView(mRootView);
        addListener();
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!LocationUtil.getInstance().startGetLocation()){
            AndPermission.defaultSettingDialog(mContext).show();
        }

        mContext.startService(new Intent(mContext,UploadLocationService.class));
        mContext.startService(new Intent(mContext,ScreenService.class));

        getLocationList(0, STATE_REFRESH);

        mHandler.postDelayed(mRunnable,1000);

        BmobUpdateAgent.forceUpdate(mContext);

        mReceiver = new LocationChangeReceiver();
        IntentFilter filter  = new IntentFilter(LocationUtil.ACTION_LOCATION_CHANGE);
        mContext.registerReceiver(mReceiver,filter);

        pedometer = new Pedometer(mContext);
        pedometer.register();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(menuBtns.isOpen())
            menuBtns.closeMenu();
    }

    private void addListener() {
        titleLayout.setOnTitleClickListener(new TitleLayoutClickListener() {
            @Override
            public void onLeftClickListener() {
                super.onLeftClickListener();
                OpenLeftMenu();
            }
            @Override
            public void onRightImg1ClickListener() {
                super.onRightImg1ClickListener();
                startActivity(new Intent(mContext, ZuJiMapActivity.class));
            }
        });
        mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                List<LocationInfo> locationInfoList = mAdapter.getData();
                if(null != locationInfoList && locationInfoList.size()>0){
                    LocationInfo location = locationInfoList.get(pos);
                    if(null != location){
                        Intent intent = new Intent(mContext,MapActivity.class);
                        intent.putExtra(MapActivity.INTENT_KEY_LATITUDE,location.getLatitude());
                        intent.putExtra(MapActivity.INTENT_KEY_LONTITUDE,location.getLontitude());
                        startActivity(intent);
                    }
                }
            }
        });
        mAdapter.setOnRecyclerViewItemLongClickListener(new BaseQuickAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int pos) {
                showDeleteDialog(pos);
                return false;
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                curPage = 0;
                getLocationList(curPage, STATE_REFRESH);
            }
        });
        //加载更多
        mAdapter.openLoadMore(PAGE_SIZE, true);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener(){
            @Override
            public void onLoadMoreRequested() {
                getLocationList(curPage, STATE_MORE);
            }
        });
        menuBtns.setOnButtonClickListener(new AnimArcButtons.OnButtonClickListener() {
            @Override
            public void onButtonClick(View v, int id) {
                switch (id){
                    case 0:
                        startActivity(new Intent(mContext,WeatherActivity.class).putExtra("curr_time",currServerTime));
                        break;
                    case 1:
                        if (pedometer.hasStepSensor()) {
                            startActivity(new Intent(mContext, StepActivity.class));
                        } else {
                            Toast.makeText(mContext, "设备没有计步传感器", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        startActivity(new Intent(mContext,FunPicActivity.class));
                        break;
                }
            }
        });
    }

    private void initView(View view) {
        titleLayout = (BaseTitleLayout) view.findViewById(R.id.titlelayout);

        menuBtns = (AnimArcButtons) view.findViewById(R.id.arc_menu_button);


        View headerView = LayoutInflater.from(mContext).inflate(R.layout.main_listview_header,null);

        bannerLayout = (Banner)headerView.findViewById(R.id.banner);
        bannerLayout.setVisibility(View.GONE);

        swipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeLayout);
        mRecyclerView = (EasyRecyclerView)view.findViewById(R.id.recyclerview);

        mAdapter = new LocationAdapter(R.layout.location_list_item,null);
        tvTime = (TextView)view.findViewById(R.id.tv_server_time);

        mAdapter.addHeaderView(headerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);

        initBannerInfo();
    }

    /**
     * 初始化banner
     */
    private void initBannerInfo() {
        final List<String> urls =  new ArrayList<>();
        BmobQuery<BannerInfo> query = new BmobQuery<>();
        query.order("index");  // 根据createdAt字段降序显示数据
        query.findObjects(new FindListener<BannerInfo>() {
            @Override
            public void done(List<BannerInfo> list, BmobException e) {
                if(null != list && list.size()>0){
                    for (BannerInfo banner: list){
                        urls.add(banner.getBanner_url());
                    }
                    if(urls.size()>0){
                        bannerLayout.setVisibility(View.VISIBLE);
                        bannerLayout.setImages(urls);
                        bannerLayout.setBannerAnimation(Transformer.DepthPage);
                        bannerLayout.setImageLoader(new GlideImageLoader());
                        bannerLayout.start();
                    }else{
                        bannerLayout.setVisibility(View.GONE);
                    }
                }
            }
        });
    }


    private void showDeleteDialog(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("确定要删除吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final List<LocationInfo> dataList = mAdapter.getData();
                String objectId = null;
                if(dataList != null && dataList.size()>0){
                    LocationInfo locationInfo = dataList.get(pos);
                    objectId = locationInfo.getObjectId();
                }
                final LocationInfo location = new LocationInfo();
                location.setObjectId(objectId);
                final String finalObjectId = objectId;

                showProgressDialog(getActivity(),false);
                location.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        stopProgressDialog();
                        if(e == null){
                            List<LocationInfo> list =  dataList;
                            Iterator<LocationInfo> iter = list.iterator();
                            while(iter.hasNext()){
                                LocationInfo locationBean = iter.next();
                                if(locationBean.equals(finalObjectId)){
                                    iter.remove();
                                    break;
                                }
                            }
                            isRefresh = true;
                            getLocationList(0, STATE_REFRESH);
                            Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext, "删除失败："+e.getMessage()+","+e.getErrorCode(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run () {
            isRefresh = true;
            getLocationList(0, STATE_REFRESH);
            //syncServerTime();
            mHandler.postDelayed(this,1000 * 60 * 2);
        }
    };


    /**
     * 分页获取数据
     */
    private void getLocationList(final int page, final int actionType){
        PersonInfo piInfo = BmobUser.getCurrentUser(PersonInfo.class);
        BmobQuery<LocationInfo> query = new BmobQuery<LocationInfo>();
        query.addWhereEqualTo("piInfo", piInfo);    // 查询当前用户的所有位置信息
        query.include("piInfo");// 希望在查询位置信息的同时也把当前用户的信息查询出来
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
        query.findObjects(new FindListener<LocationInfo>() {
            @Override
            public void done(List<LocationInfo> dataList, BmobException e) {
                swipeLayout.setRefreshing(false);
                if(e == null){
                    curPage++;
                    if (isRefresh){//下拉刷新需清理缓存
                        mAdapter.setNewData(dataList);
                        isRefresh = false;
                    }else {//正常请求 或 上拉加载更多时处理流程
                        if (dataList.size() > 0) {
                            mAdapter.notifyDataChangedAfterLoadMore(dataList, true);
                        }else {
                            mAdapter.notifyDataChangedAfterLoadMore(false);
                        }
                    }
                }else{
                    Toast.makeText(mContext,"查询失败"+e.getMessage()+","+e.getErrorCode(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onFragmentFirstSelected() {

    }

    public class LocationChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(LocationUtil.ACTION_LOCATION_CHANGE)) {
                isRefresh = true;
                getLocationList(0, STATE_REFRESH);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mReceiver != null){
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        pedometer.register();
    }

    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        bannerLayout.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        bannerLayout.stopAutoPlay();
    }
}
