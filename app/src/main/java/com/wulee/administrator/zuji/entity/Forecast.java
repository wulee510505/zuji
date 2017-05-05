package com.wulee.administrator.zuji.entity;

/**
 * Created by yarolegovich on 08.03.2017.
 */

public class Forecast {

    private final String date;
    private final String cityName;
    private final String temperature;
    private final String airquality;
    private final String weatherimg;
    private final String weather;
    private final String wind;

    public Forecast(String date,String cityName,String temperature,String airquality,String weatherimg ,String weather,String wind) {
        this.date = date;
        this.cityName = cityName;
        this.temperature = temperature;
        this.airquality = airquality;
        this.weatherimg = weatherimg;
        this.weather = weather;
        this.wind = wind;
    }

    public String getDate() {
        return date;
    }

    public String getCityName() {
        return cityName;
    }

    public String getAirquality() {
        return airquality;
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
