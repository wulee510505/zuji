package com.wulee.administrator.zuji.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.LocationAdapter;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.bean.LocationInfo;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.service.ScreenService;
import com.wulee.administrator.zuji.service.UploadLocationService;
import com.wulee.administrator.zuji.ui.weather.WeatherActivity;
import com.wulee.administrator.zuji.utils.LocationUtil;
import com.wulee.administrator.zuji.widget.FloatingButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;

import static com.wulee.administrator.zuji.App.aCache;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private SwipeRefreshLayout swipeLayout;
    private RecyclerView mRecyclerView;
    private LocationAdapter mAdapter;

    private ImageView ivMenu;
    private ImageView ivSetting;
    private FloatingButton floatingButton ;
    private DrawerLayout mDrawerLayout;


    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int PAGE_SIZE = 10;
    private int curPage = 0;
    private boolean isRefresh = false;

    private TextView tvTime;
    private long  currServerTime;

    private LocationChangeReceiver mReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_list_main);

        initView();
        addListener();

        startService(new Intent(MainActivity.this,UploadLocationService.class));
        startService(new Intent(MainActivity.this,ScreenService.class));

        mHandler.postDelayed(mRunnable,1000);

        BmobUpdateAgent.forceUpdate(this);

        mReceiver = new LocationChangeReceiver();
        IntentFilter filter  = new IntentFilter(LocationUtil.ACTION_LOCATION_CHANGE);
        registerReceiver(mReceiver,filter);
    }

    /*
      * 获取服务器时间
     */
    private void syncServerTime() {
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long time, BmobException e) {
                if(e == null){
                    currServerTime = time;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String times = formatter.format(new Date(time * 1000L));
                    tvTime.setText(times);
                }else{
                    toast("获取服务器时间失败:" + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        isRefresh = true;
        query(0, STATE_REFRESH);
    }

    private void addListener() {
        floatingButton.setOnClickListener(this);
        ivMenu.setOnClickListener(this);
        ivSetting.setOnClickListener(this);
        mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                List<LocationInfo> locationInfoList = mAdapter.getData();
                if(null != locationInfoList && locationInfoList.size()>0){
                    LocationInfo location = locationInfoList.get(pos);
                    if(null != location){
                        Intent intent = new Intent(MainActivity.this,MapActivity.class);
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
                query(curPage, STATE_REFRESH);
        }
        });
        //加载更多
        mAdapter.openLoadMore(PAGE_SIZE, true);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener(){
            @Override
            public void onLoadMoreRequested() {
                query(curPage, STATE_MORE);
            }
        });
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        mDrawerLayout.setScrimColor(0x00000000);

        floatingButton = (FloatingButton) findViewById(R.id.floatingbutton);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) floatingButton.getLayoutParams();
        rlp.rightMargin =  120;
        rlp.bottomMargin = 120;
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        floatingButton.setLayoutParams(rlp);

        ivMenu = (ImageView) findViewById(R.id.iv_menu);
        ivSetting = (ImageView) findViewById(R.id.iv_setting);
        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipeLayout);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);

        mAdapter = new LocationAdapter(R.layout.location_list_item,null);
        View headerView = LayoutInflater.from(this).inflate(R.layout.location_list_header,null);
        tvTime = (TextView) headerView.findViewById(R.id.tv_server_time);
        mAdapter.addHeaderView(headerView);

        //mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, 2, ContextCompat.getColor(this,R.color.divider_color)));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }


    private void showDeleteDialog(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                location.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
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
                            query(0, STATE_REFRESH);
                            Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "删除失败："+e.getMessage()+","+e.getErrorCode(), Toast.LENGTH_SHORT).show();
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
            query(0, STATE_REFRESH);
            syncServerTime();
            mHandler.postDelayed(this,1000 * 60 * 1);
        }
    };


    /**
     * 分页获取数据
     */
    private void query(final int page, final int actionType){
        if(!TextUtils.equals("yes",aCache.getAsString("isUploadLocation"))){
            return;
        }
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
                        if (mAdapter.getData().size() == 0) {
                            mAdapter.setEmptyView(LayoutInflater.from(MainActivity.this).inflate(R.layout.empty_view, (ViewGroup) mRecyclerView.getParent(), false));
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this,"查询失败"+e.getMessage()+","+e.getErrorCode(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:
                startActivity(new Intent(this,ZuJiMapActivity.class));
                break;
            case R.id.iv_menu:
                OpenLeftMenu();
                break;
            case R.id.floatingbutton:
                startActivity(new Intent(this,WeatherActivity.class).putExtra("curr_time",currServerTime));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //既没有阻碍用户操作（回到桌面），又没有关闭掉我们的应用（后台运行中），间接提高 App 的存活时间
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(launcherIntent);
    }


    /**
     * 打开左侧Menu的监听事件
     */
    public void OpenLeftMenu() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }
    /**
     * 关闭Menu
     */
    public boolean CloseMenu() {
        if (mDrawerLayout != null && (mDrawerLayout.isDrawerOpen(Gravity.LEFT)
                || mDrawerLayout.isDrawerOpen(Gravity.RIGHT))) {
            mDrawerLayout.closeDrawers();
            return true;
        }
        return false;
    }


    public class LocationChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(LocationUtil.ACTION_LOCATION_CHANGE)) {
                isRefresh = true;
                query(0, STATE_REFRESH);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
