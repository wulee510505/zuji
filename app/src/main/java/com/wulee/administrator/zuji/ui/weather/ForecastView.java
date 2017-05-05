package com.wulee.administrator.zuji.ui.weather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.entity.Forecast;


/**
 * Created by yarolegovich on 08.03.2017.
 */

public class ForecastView extends LinearLayout {

    private Paint gradientPaint;

    private TextView date;
    private TextView weatherDescription;
    private TextView wind;
    private TextView weatherTemperature;
    private TextView airQuality;
    private TextView airQualityDesc;
    private TextView cityName;
    private ImageView weatherImage;


    public ForecastView(Context context) {
        super(context);
    }

    public ForecastView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForecastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ForecastView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setWillNotDraw(false);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        inflate(getContext(), R.layout.view_forecast, this);

        date = (TextView) findViewById(R.id.date);
        weatherDescription = (TextView) findViewById(R.id.weather_description);
        wind = (TextView) findViewById(R.id.weather_wind);
        weatherImage = (ImageView) findViewById(R.id.weather_image);
        weatherTemperature = (TextView) findViewById(R.id.weather_temperature);
        airQuality = (TextView) findViewById(R.id.weather_air_quality);
        airQualityDesc = (TextView) findViewById(R.id.weather_air_quality_desc);
        cityName = (TextView) findViewById(R.id.weather_city_name);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), gradientPaint);
        super.onDraw(canvas);
    }

    public void setForecast(Forecast forecast) {
        date.setText(forecast.getDate());
        weatherDescription.setText(forecast.getWeather());
        wind.setText(forecast.getWind());
        weatherTemperature.setText(forecast.getTemperature());
        String airqualityStr = "空气质量指数：<strong><font color=\"#f6a6c1\">"+forecast.getAirquality()+"</font><strong>";
        airQuality.setText(Html.fromHtml(airqualityStr));

        // 0－50、51－100、101－150、151－200、201－300 、>300
        //  优      良      轻度污染  中度污染 重度污染   严重污染
        String airQualityDescStr = "";
        int airqurlity = !TextUtils.isEmpty(forecast.getAirquality())? Integer.valueOf(forecast.getAirquality()):0;
        if (airqurlity>0 && airqurlity <= 50){
            airQualityDescStr = "优";
        }else  if (airqurlity>51 && airqurlity <= 100){
            airQualityDescStr = "良";
        }else  if (airqurlity>101 && airqurlity <= 150){
            airQualityDescStr = "轻度污染";
        }else  if (airqurlity >151  && airqurlity<= 200){
            airQualityDescStr = "中度污染";
        }else  if (airqurlity>201 && airqurlity <= 300){
            airQualityDescStr = "重度污染";
        }else  if (airqurlity> 300){
            airQualityDescStr = "严重污染";
        }
        airQualityDesc.setText(airQualityDescStr);


        cityName.setText(forecast.getCityName());
        Glide.with(getContext()).load(forecast.getWeatherimg()).into(weatherImage);
        invalidate();

        weatherImage.animate()
                .scaleX(2.0f).scaleY(2.0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(2000)
                .start();


        AnimationSet animationSet =new AnimationSet(true);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
        ScaleAnimation scaleAnimation =  new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setDuration(4000);
        setAnimation(animationSet);
        animationSet.startNow();
    }

}
