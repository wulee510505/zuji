package com.wulee.administrator.zuji.ui.weather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.entity.Forecast;
import com.wulee.administrator.zuji.entity.Weather;
import com.wulee.administrator.zuji.widget.ScalePageTransformer;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;

import static com.wulee.administrator.zuji.App.aCache;


/**
 * Created by yarolegovich on 08.03.2017.
 */

public class WeatherActivity extends BaseActivity {


    private ViewPager viewPager;
    private ForecastPagerAdapter madapter;

    private long  currtime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_weather);

        currtime = getIntent().getLongExtra("curr_time",0L);

        initView();
        initData();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.forecast_viewpager);

        viewPager.setOffscreenPageLimit(4);
        viewPager.setPageMargin(90);

        madapter = new ForecastPagerAdapter();
        viewPager.setAdapter(madapter);
        viewPager.setPageTransformer(true, new ScalePageTransformer());
    }

    private void initData() {
        //String url = "http://api.map.baidu.com/telematics/v3/weather?location=北京&output=json&ak=yourkey&mcode=xxxx";
        String urlprefix = "http://api.map.baidu.com/telematics/v3/weather?&output=json";
        String location = aCache.getAsString("location_city");
        if(TextUtils.isEmpty(location)){
            Toast.makeText(this, "未获取到位置信息", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder urlSb = new StringBuilder();
        String url = urlSb.append(urlprefix).append("&location=").append(location).append("&ak=").append("tTPUoUgnb0BO9f9VEGtwLDclBmExPFEU").append("&mcode=").append("03:4F:6A:06:11:27:02:23:D0:85:7D:4E:CE:22:F3:1E:E0:E6:8A:41;com.wulee.administrator.zuji").toString();
        HttpRequest.get(url, new BaseHttpRequestCallback<Weather>(){
            @Override
            protected void onSuccess(Weather weather) {
                super.onSuccess(weather);

                Weather.ResultsEntity resultenty = null;
                if(weather.getResults() != null && weather.getResults().size()>0){
                    resultenty = weather.getResults().get(0);
                    if(null != resultenty){
                        madapter.setWearthData(resultenty);
                    }
                }
            }
            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
            }
        } );
    }


    /**
     * 判断是否是晚上
     * @return
     */
    public boolean isNight(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String hour= sdf.format(new Date(currtime * 1000L));
        int k  = Integer.parseInt(hour)  ;
        if ((k>=0 && k<6) ||(k >=18 && k<24)){
            return true;
        } else {
            return false;
        }
    }


    class ForecastPagerAdapter extends PagerAdapter{
        private Weather.ResultsEntity mWetherResult;

        public void setWearthData(Weather.ResultsEntity wetherResult){
            this.mWetherResult = wetherResult;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if(mWetherResult != null && mWetherResult.getWeather_data().size()>0)
                return mWetherResult.getWeather_data().size();
            else
                return 0;
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemview = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.weather_pager_item,null);
            ForecastView forecastView = (ForecastView)itemview.findViewById(R.id.forecast_view);

            Weather.ResultsEntity.WeatherDataEntity weatherDataEntity =  mWetherResult.getWeather_data().get(position);
            Forecast forecast = null;
            if(null != weatherDataEntity){
                if(isNight()){
                    forecast = new Forecast(weatherDataEntity.getDate(),mWetherResult.getCurrentCity(),weatherDataEntity.getTemperature(),mWetherResult.getPm25(),weatherDataEntity.getNightPictureUrl(),weatherDataEntity.getWeather(),weatherDataEntity.getWind());
                }else{
                    forecast = new Forecast(weatherDataEntity.getDate(),mWetherResult.getCurrentCity(),weatherDataEntity.getTemperature(),mWetherResult.getPm25(),weatherDataEntity.getDayPictureUrl(),weatherDataEntity.getWeather(),weatherDataEntity.getWind());
                }
                forecastView.setForecast(forecast);
            }
            container.addView(itemview);
            return itemview;
        }

    }

}
