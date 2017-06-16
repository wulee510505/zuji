package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
                        List<Map.Entry<String,List<StepInfo>>>  retMapList = processDataList(dataList);
                        if(retMapList != null && retMapList.size()>0){
                            initChartData(retMapList);
                        }
                    }
                } else {
                    Toast.makeText(StepHistoryActivity.this, "查询失败" + e.getMessage() + "," + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initChartData( List<Map.Entry<String,List<StepInfo>>>  mapList) {
        String[] xLabel = new String[7];
        String[] datearray = new String[7];
        for (int i = 0 ; i < 7; i++) {
            datearray[i] =  DateTimeUtils.formatTime(DateTimeUtils.getDateBefore(new Date(),i));
        }
        for (int j = 0; j < datearray.length; j++) {
            xLabel[j] =  datearray[datearray.length-1-j];
        }
        String[] yLabel = {"0步", "1000步", "2000步", "3000步", "4000步", "5000步", "6000步", "7000步", "8000步", "9000步","10000步","11000步","12000步","13000步","14000步","15000步"};

        float[] stepdata = new float[7];

        for (int k = 0; k < mapList.size(); k++) {
            Map.Entry<String,List<StepInfo>> map = mapList.get(k);
            String date = map.getKey();
            StepInfo step = map.getValue().get(0);

            for (String xdate : xLabel){
                if(TextUtils.equals(date,xdate)){
                    stepdata[k] = step.getCount();
                }
            }
        }

        List<float[]> data = new ArrayList<>();
        data.add(stepdata);

        List<Integer> color = new ArrayList<>();
        color.add(R.color.colorAccent);
        color.add(R.color.colorAccent);
        color.add(R.color.colorAccent);
        barChart.addView(new CustomBarChart(this, xLabel, yLabel, data, color));
    }

    private   List<Map.Entry<String,List<StepInfo>>> processDataList(List<StepInfo> dataList) {
        Date date1 = DateTimeUtils.getDateBefore(new Date(),7);
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
            if(date.before(date1) || date.after(date2)){//去除7天前的数据，以及当天之后的数据
                iter.remove();
            }
        }

        //对数据按日期分组
        Map<String, List<StepInfo>> map = new HashMap<>();
        for (StepInfo stepInfo: dataList){
            String key = stepInfo.groupKey();
            // 按照key取出子集合
            List<StepInfo> subStepList = map.get(key);

            // 若子集合不存在，则重新创建一个新集合，并把当前stepInfo加入，然后put到map中
            if (subStepList == null){
                subStepList = new ArrayList<>();
                subStepList.add(stepInfo);
                map.put(key, subStepList);
            }else {
                // 若子集合存在，则直接把当前stepInfo加入即可
                subStepList.add(stepInfo);
            }
        }

        List<Map.Entry<String,List<StepInfo>>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,List<StepInfo>>>() {
            //升序排序
            public int compare(Map.Entry<String, List<StepInfo>> o1,
                               Map.Entry<String, List<StepInfo>> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return list;
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
