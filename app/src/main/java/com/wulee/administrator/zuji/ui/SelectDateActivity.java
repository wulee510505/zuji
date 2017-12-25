package com.wulee.administrator.zuji.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;

import java.lang.reflect.Field;
import java.util.Calendar;

/**
 * Created by wulee on 2017/12/19 16:01
 */

public class SelectDateActivity extends BaseActivity implements View.OnClickListener{

    public static final String SELECT_DATE = "select_date";

    private DatePicker mDatePicker;
    private TextView tvConfirm;
    private TextView tvCancel;

    private int mYear;
    private int mMonth;
    private int mDay;

    private String dateResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_date);

        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        initView();
        initData();
        addListener();
    }

    private void addListener() {
        tvConfirm.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    private void initView() {
        mDatePicker = findViewById(R.id.date_picker);
        tvConfirm= findViewById(R.id.tv_confirm);
        tvCancel= findViewById(R.id.tv_cancel);

        setDatePickerDividerColor(mDatePicker,"#cdcdcd");
    }

    protected int getStateBarColor() {
        return R.color.transparent;
    }

    private void initData(){
        Calendar c =Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        c.add(Calendar.MONTH, 1);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        mDatePicker.init(mYear, mMonth, mDay, (view, year, monthOfYear, dayOfMonth) -> {
            mYear = year;
            mMonth = monthOfYear+1;
            mDay = dayOfMonth;

            StringBuffer lend_time = new StringBuffer();
            lend_time.append(mYear);
            lend_time.append("-");
            lend_time.append(mMonth);
            lend_time.append("-");
            lend_time.append(mDay);

            dateResult = lend_time.toString();
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_confirm:
                Intent intent = getIntent();
                intent.putExtra(SELECT_DATE,dateResult);
                setResult(RESULT_OK,intent);
                finish();
            break;
            case R.id.tv_cancel:
                finish();
                break;
        }
    }

    /**
     * 设置时间选择器的分割线颜色
     * @param datePicker
     * @param  color  "#FF4081"
     *
     */
    private void setDatePickerDividerColor(DatePicker datePicker,String color) {
        // 获取 mSpinners
        LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);
        // 获取 NumberPicker
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);
        for (int i = 0; i < mSpinners.getChildCount(); i++) {
            NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);
            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        pf.set(picker, new ColorDrawable(Color.parseColor(color)));//设置分割线颜色
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}
