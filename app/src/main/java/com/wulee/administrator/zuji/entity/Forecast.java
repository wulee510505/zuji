package com.wulee.administrator.zuji.entity;

/**
 * Created by yarolegovich on 08.03.2017.
 */

public class Forecast {

    private final String cityName;
    private final String temperature;
    private final String pmvalue;
    private final String weatherimg;
    private final String weather;

    public Forecast(String cityName,String temperature,String pmvalue,String weatherimg ,String weather) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.pmvalue = pmvalue;
        this.weatherimg = weatherimg;
        this.weather = weather;
    }

    public String getCityName() {
        return cityName;
    }

    public String getPmvalue() {
        return pmvalue;
    }

    public String getWeatherimg() {
        return weatherimg;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWeather() {
        return weather;
    }
}
