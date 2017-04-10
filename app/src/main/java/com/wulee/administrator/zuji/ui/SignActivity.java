package com.wulee.administrator.zuji.ui;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.test.sign_calender.DPCManager;
import com.test.sign_calender.DPDecor;
import com.test.sign_calender.DPMode;
import com.test.sign_calender.DatePicker;
import com.test.sign_calender.DatePicker2;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by wulee on 2017/4/5 16:10
 */

public class SignActivity extends BaseActivity {

    private ImageView ivBack;
    private DatePicker2 mDatePicker;

    private List<String> dateList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sign_activity);

        initView();
        addListener();
        initData();
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        mDatePicker = (DatePicker2) findViewById(R.id.datepicker);
        mDatePicker.setMode(DPMode.SINGLE);
    }

    private void initData() {
        dateList.add("2017-4-11");
        dateList.add("2017-4-19");
        DPCManager.getInstance().setDecorBG(dateList);

        mDatePicker.setFestivalDisplay(true); //是否显示节日
        mDatePicker.setHolidayDisplay(true); //是否显示假期
        mDatePicker.setDeferredDisplay(true); //是否显示补休


        Calendar c = Calendar.getInstance();//首先要获取日历对象
        int mYear = c.get(Calendar.YEAR); // 获取当前年份
        final int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        mDatePicker.setDate(mYear, mMonth);
        mDatePicker.setDPDecor(new DPDecor() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void drawDecorBG(Canvas canvas, Rect rect, Paint paint) {
                paint.setColor(Color.RED);
                paint.setAntiAlias(true);
                Bitmap mBitmap = null;
                try {
                    try (InputStream is = getResources().openRawResource(R.raw.icon_has_sign)) {
                        mBitmap = BitmapFactory.decodeStream(is);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // canvas.drawBitmap(mBitmap, rect.centerX() - mBitmap.getWidth() / 2f, rect.centerY() - mBitmap.getHeight() / 2f, paint);
            }
        });
    }


    private void addListener(){
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignActivity.this.finish();
            }
        });
        mDatePicker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
               for (String signdate : dateList){
                   if(signdate.equals(date)){
                        toast("已经签过到了");
                   }
               }
            }
        });
    }

}
