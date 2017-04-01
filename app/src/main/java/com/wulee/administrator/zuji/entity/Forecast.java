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
    private final String wind;

    public Forecast(String cityName,String temperature,String pmvalue,String weatherimg ,String weather,String wind) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.pmvalue = pmvalue;
        this.weatherimg = weatherimg;
        this.weather = weather;
        this.wind = wind;
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

    public String getWind() {
        return wind;
    }
}
