package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.StepInfo;
import com.wulee.administrator.zuji.utils.DateTimeUtils;
import com.wulee.administrator.zuji.widget.CustomBarChart;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

/**
 * Created by wulee on 2017/6/14 15:57
 */

public class StepHistoryActivity extends BaseActivity {

    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.bar_chart)
    LinearLayout barChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_step_history);
        ButterKnife.inject(this);

        initData();
    }

    private void initData() {
        title.setText("周运动历史");

        PersonInfo piInfo = BmobUser.getCurrentUser(PersonInfo.class);
        BmobQuery<StepInfo> query = new BmobQuery<>();
        query.addWhereEqualTo("personInfo", piInfo);    // 查询当前用户的所有计步信息
        query.include("personInfo");
        query.findObjects(new FindListener<StepInfo>() {
            @Override
            public void done(List<StepInfo> dataList, BmobException e) {
                if (e == null) {
                    if (null != dataList && dataList.size() > 0) {
                        List<StepInfo>  remainList = processDataList(dataList);
                        if(remainList != null && remainList.size()>0){
                            initChartData(remainList);
                        }
                    }
                } else {
                    Toast.makeText(StepHistoryActivity.this, "查询失败" + e.getMessage() + "," + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initChartData(List<StepInfo> dataList) {
        String[] xLabel = new String[7];
        String[] datearray = new String[7];
        for (int i = 0 ; i < 7; i++) {
            datearray[i] =  DateTimeUtils.formatTime(DateTimeUtils.getDateBefore(new Date(),i));
        }
        for (int j = 0; j < datearray.length; j++) {
            xLabel[j] =  datearray[datearray.length-1-j];
        }
        String[] yLabel = {"0步", "1000步", "2000步", "3000步", "4000步", "5000步", "6000步", "7000步", "8000步", "9000步","10000步","11000步","12000步","13000步","14000步","15000步"};
        float[] data1 = new float[7];
        for (int k = 0; k < dataList.size(); k++) {
            data1[k] = dataList.get(k).getCount();
        }
        List<float[]> data = new ArrayList<>();
        data.add(data1);
        List<Integer> color = new ArrayList<>();
        color.add(R.color.colorAccent);
        color.add(R.color.colorAccent);
        color.add(R.color.colorAccent);
        barChart.addView(new CustomBarChart(this, xLabel, yLabel, data, color));
    }

    private  List<StepInfo> processDataList(List<StepInfo> dataList) {
        Date date1 = DateTimeUtils.getDateBefore(new Date(),6);
        Date date2 = new Date();
        Iterator<StepInfo> iter = dataList.iterator();
        while (iter.hasNext()) {
            StepInfo step = iter.next();

            Date date = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date =  format.parse(step.getUpdatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(date.before(date1) || date.after(date2)){
                iter.remove();
            }
        }
        return dataList;
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
