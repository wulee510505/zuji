package com.wulee.administrator.zuji.ui.weather;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
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

    private TextView weatherDescription;
    private TextView wind;
    private TextView weatherTemperature;
    private TextView pmValue;
    private TextView cityName;
    private ImageView weatherImage;

    private ArgbEvaluator evaluator;

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
        evaluator = new ArgbEvaluator();

        gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setWillNotDraw(false);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        inflate(getContext(), R.layout.view_forecast, this);

        weatherDescription = (TextView) findViewById(R.id.weather_description);
        wind = (TextView) findViewById(R.id.weather_wind);
        weatherImage = (ImageView) findViewById(R.id.weather_image);
        weatherTemperature = (TextView) findViewById(R.id.weather_temperature);
        pmValue = (TextView) findViewById(R.id.weather_pm_value);
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
        weatherDescription.setText(forecast.getWeather());
        wind.setText(forecast.getWind());
        weatherTemperature.setText(forecast.getTemperature());
        pmValue.setText("PM2.5："+ forecast.getPmvalue());
        cityName.setText(forecast.getCityName());
        Glide.with(getContext()).load(forecast.getWeatherimg()).into(weatherImage);
        invalidate();

        weatherImage.animate()
                .scaleX(1.8f).scaleY(1.8f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(1000)
                .start();
    }

}
