package com.wulee.administrator.zuji.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.StepRankingAdapter;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.StepInfo;
import com.wulee.administrator.zuji.utils.DateTimeUtils;
import com.wulee.administrator.zuji.utils.Pedometer;
import com.wulee.administrator.zuji.utils.SortList;
import com.wulee.administrator.zuji.widget.ProgressWheel;
import com.wulee.administrator.zuji.widget.RecycleViewDivider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.wulee.administrator.zuji.App.aCache;


public class StepActivity extends BaseActivity {
    public static final String ACTION_ON_STEP_COUNT_CHANGE = "action_on_step_count_change";
    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.tv_ranking)
    TextView tvRanking;
    @InjectView(R.id.tv_line)
    View tvLine;
    @InjectView(R.id.recyclerview)
    EasyRecyclerView recyclerview;
    @InjectView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;
    @InjectView(R.id.progress_bar)
    ProgressBar progressBar;
    @InjectView(R.id.iv_history)
    ImageView ivHistory;
    @InjectView(R.id.progress_step)
    ProgressWheel progressStep;

    private Pedometer pedometer;
    private OnStepCountChangeReceiver mReceiver;

    private StepRankingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.inject(this);

        initData();
        addListener();

        mReceiver = new OnStepCountChangeReceiver();
        IntentFilter filter = new IntentFilter(ACTION_ON_STEP_COUNT_CHANGE);
        registerReceiver(mReceiver, filter);

        pedometer = new Pedometer(this);
    }


    private void initData() {
        title.setText("今日步数");

        mAdapter = new StepRankingAdapter(this, R.layout.step_rank_list_item, null);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));
        recyclerview.setAdapter(mAdapter);

        queryStepRankList();
    }

    private void addListener() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryStepRankList();
            }
        });
    }

    private void queryStepRankList() {
        BmobQuery<StepInfo> query = new BmobQuery<StepInfo>();
        query.include("personInfo");// 希望在查询计步信息的同时也把当前用户的信息查询出来
        query.findObjects(new FindListener<StepInfo>() {
            @Override
            public void done(List<StepInfo> dataList, BmobException e) {
                swipeLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                if (e == null) {
                    if (null != dataList && dataList.size() > 0) {
                        //数据重复问题，暂未想到解决的好办法
                        mAdapter.setNewData(processReturnList(dataList));
                    }
                } else {
                    Toast.makeText(StepActivity.this, "查询失败" + e.getMessage() + "," + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<StepInfo> processReturnList(List<StepInfo> dataList) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currdate = dateFormat.format(now);

        Iterator<StepInfo> iter = dataList.iterator();
        while (iter.hasNext()) {
            StepInfo step = iter.next();
            if (!TextUtils.equals(currdate, step.getCreatedAt().substring(0, 10))) {//去除非当天的数据
                iter.remove();
            }
        }

        SortList<StepInfo> msList = new SortList<>();
        msList.sortByMethod(dataList, "getCount", true);

        PersonInfo piInfo = BmobUser.getCurrentUser(PersonInfo.class);
        if (null != piInfo) {
            for (int i = 0; i < dataList.size(); i++) {
                StepInfo step = dataList.get(i);
                if (null != step) {
                    if (TextUtils.equals(step.personInfo.getObjectId(), piInfo.getObjectId())) {
                        tvRanking.setText("第 " + (i + 1) + " 名");
                    }
                }
            }
        }
        return dataList;
    }


    @Override
    protected void onResume() {
        super.onResume();
        pedometer.register();
    }


    @OnClick({R.id.iv_back, R.id.iv_history})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_history:
                startActivity(new Intent(StepActivity.this, StepHistoryActivity.class));
                break;
        }
    }


    class OnStepCountChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(ACTION_ON_STEP_COUNT_CHANGE, intent.getAction())) {
                // 支付宝步数统计就是依据了此原理
                progressStep.setStepCountText(pedometer.getStepCount() + "");

                uploadStepInfo(pedometer.getStepCount());
            }
        }
    }


    /**
     * 上传计步信息
     */
    private void uploadStepInfo(final int stepcount) {
        final PersonInfo piInfo = BmobUser.getCurrentUser(PersonInfo.class);

        BmobQuery<StepInfo> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("personInfo", piInfo);
        bmobQuery.findObjects(new FindListener<StepInfo>() {
            @Override
            public void done(List<StepInfo> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        String currdateStr = DateTimeUtils.formatTime(new Date());
                        for (StepInfo step : list) {
                            if (TextUtils.equals(step.getCreatedAt().substring(0, 10), currdateStr)) {//认为一天只创建一条数据，保证数据的唯一性
                                aCache.put("step_info_id", step.getObjectId());
                            }
                        }
                    }

                    final StepInfo stepInfo = new StepInfo();
                    stepInfo.setCount(stepcount);
                    //添加一对一关联
                    stepInfo.personInfo = piInfo;
                    final String stepInfoId = aCache.getAsString("step_info_id");
                    if (TextUtils.isEmpty(stepInfoId)) {
                        stepInfo.save(new SaveListener<String>() {
                            @Override
                            public void done(String objId, BmobException e) {
                                if (e == null) {
                                    aCache.put("step_info_id", objId);
                                    System.out.println("—— 步数同步成功 ——");
                                } else {
                                    System.out.println("—— 步数同步失败 ——");
                                }
                            }
                        });
                    } else {
                        BmobQuery<StepInfo> query = new BmobQuery<StepInfo>();
                        query.getObject(stepInfoId, new QueryListener<StepInfo>() {
                            @Override
                            public void done(StepInfo stepInfo, BmobException e) {
                                if(e == null && stepInfo != null){
                                    stepInfo.setCount(stepcount);
                                    stepInfo.update(stepInfoId, new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                System.out.println("—— 步数更新成功 ——");
                                            } else {
                                                System.out.println("—— 步数更新失败 ——");
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        pedometer.unRegister();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
}