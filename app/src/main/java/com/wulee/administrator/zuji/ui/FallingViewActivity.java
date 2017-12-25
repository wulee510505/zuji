package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.WindowManager;

import com.liangmayong.text2speech.Text2Speech;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.utils.CountDownHelper;
import com.wulee.administrator.zuji.utils.HolidayUtil;
import com.wulee.administrator.zuji.utils.UIUtils;
import com.wulee.administrator.zuji.widget.FallObject;
import com.wulee.administrator.zuji.widget.FallingView;

public class FallingViewActivity extends AppCompatActivity {

    public  static final String CURR_HOLIDAYS ="curr_holidays";

    private FallingView mFallingView;

    private String currHolidays;

    private int res_snow = R.mipmap.icon_snow;
    private int res_gold = R.mipmap.icon_gold;
    private int res_tree = R.mipmap.icon_tree;
    private int res_rose = R.mipmap.icon_rose;
    private int res_star = R.mipmap.icon_star;
    private int res_balloon = R.mipmap.icon_balloon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.falling_view_activity);

        currHolidays = getIntent().getStringExtra(CURR_HOLIDAYS);
        Text2Speech.speech(this,"祝您"+ currHolidays +"快乐！",true);

        mFallingView = findViewById(R.id.fallingview);

        FallObject.Builder builder = null;
        if(TextUtils.equals(currHolidays, HolidayUtil.HOLIDAYS_NEWYEARSDAY)){//元旦
            builder = new FallObject.Builder(getResources().getDrawable(res_snow));
        }else  if(TextUtils.equals(currHolidays,HolidayUtil.HOLIDAYS_LABORDAY)){//劳动节
            builder = new FallObject.Builder(getResources().getDrawable(res_balloon));
        }else  if(TextUtils.equals(currHolidays,HolidayUtil.HOLIDAYS_NATIONALDAY)){//国庆节
            builder = new FallObject.Builder(getResources().getDrawable(res_star));
        }else  if(TextUtils.equals(currHolidays,HolidayUtil.HOLIDAYS_CHRISTMAS)){//圣诞节
            builder = new FallObject.Builder(getResources().getDrawable(res_tree));
        }else  if(TextUtils.equals(currHolidays,HolidayUtil.SPRING_FESTIVAL)){//春节
            builder = new FallObject.Builder(getResources().getDrawable(res_gold));
        }else  if(TextUtils.equals(currHolidays,HolidayUtil.TANABATA_FESTIVAL)){//七夕节
            builder = new FallObject.Builder(getResources().getDrawable(res_rose));
        }
        FallObject fallObject = builder
                .setSpeed(7,true)
                .setSize(UIUtils.dip2px(32),UIUtils.dip2px(32),true)
                .setWind(5,true,true)
                .build();


        mFallingView.addFallObject(fallObject,100);

        CountDownHelper helper = new CountDownHelper(10, 1);
        helper.setOnFinishListener(() -> {
            FallingViewActivity.this.finish();
            overridePendingTransition(R.anim.push_bottom_in,R.anim.push_bottom_out);
        });
        helper.start();

    }
}
