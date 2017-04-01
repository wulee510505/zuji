package com.wulee.administrator.zuji.ui.weather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.entity.Forecast;
import com.wulee.administrator.zuji.entity.Weather;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;

import static com.wulee.administrator.zuji.App.aCache;


/**
 * Created by yarolegovich on 08.03.2017.
 */

public class WeatherActivity extends AppCompatActivity {

    private Forecast forecast;

    private ForecastView forecastView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        forecastView = (ForecastView) findViewById(R.id.forecast_view);

        initData();
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
                        Weather.ResultsEntity.WeatherDataEntity weatherDataEntity = null;
                        if(resultenty.getWeather_data() != null && resultenty.getWeather_data().size()>0){
                            weatherDataEntity = resultenty.getWeather_data().get(0);

                            if(null != weatherDataEntity){
                                forecast = new Forecast(resultenty.getCurrentCity(),weatherDataEntity.getTemperature(),resultenty.getPm25(),weatherDataEntity.getDayPictureUrl(),weatherDataEntity.getWeather(),weatherDataEntity.getWind());
                                forecastView.setForecast(forecast);
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
            }
        } );
    }

}
