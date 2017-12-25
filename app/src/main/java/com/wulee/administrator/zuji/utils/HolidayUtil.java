package com.wulee.administrator.zuji.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wulee on 2017/12/25 11:36
 */

public class HolidayUtil {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static HashMap<String,String> solarHolidaysMap = new HashMap<>(); //阳历节日
    private static HashMap<String,String> lunarHolidaysMap = new HashMap<>(); //农历节日

    private static String currHolidays;

    public static  final String HOLIDAYS_NEWYEARSDAY = "元旦";
    public static  final String HOLIDAYS_LABORDAY = "劳动节";
    public static  final String HOLIDAYS_NATIONALDAY = "国庆节";
    public static  final String HOLIDAYS_CHRISTMAS = "圣诞节";
    public static  final String SPRING_FESTIVAL = "春节";
    public static  final String TANABATA_FESTIVAL = "七夕节";

    /**
     * 静态块初始化阳历节日
     */
    static {
        solarHolidaysMap.put("01-01","元旦");
        solarHolidaysMap.put("05-01","劳动节");
        solarHolidaysMap.put("10-01","国庆节");
        solarHolidaysMap.put("12-25","圣诞节");
    }


    /**
     * 静态块初始化农历节日
     */
    static {
        lunarHolidaysMap.put("一月初一","春节");
        lunarHolidaysMap.put("七月初七","七夕节");
    }

    /**
     * 判断当天是否是节假日 节日只包含1.1；5.1；10.1
     *
     * @param date 时间
     * @return 非工作时间：true;工作时间：false
     */
    public static boolean isHolidayOrFestival(Date date) {
        boolean result = false;
        boolean isHolidayTmp = isHoliday(date);
        if (isHolidayTmp) {
            result = true;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            //周末直接为非工作时间
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                result = true;
            } else {//周内9点到17:30为工作时间
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                if (hour < 9 || (hour == 17 && minute > 30) || hour >= 18) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * 非工作时间获取最近的工作时间
     * @param date 时间
     * @return 返回处理后时间，格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getPreWorkDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        if (!isHolidayOrFestival(date)) {
            return datechange(date, "yyyy-MM-dd HH:mm:ss");
        }
        //如果是周日最近的工作日为周五，日期减去2
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            c.add(Calendar.DAY_OF_MONTH, -2);
        }
        //如果是周六最近的工作日为周五，日期减去1
        else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            c.add(Calendar.DAY_OF_MONTH, -1);
        }
        //如果是周一，并且为早上9点之前，最近的工作日为周五，日期减去3
        else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour < 9) {
                c.add(Calendar.DAY_OF_MONTH, -3);
            }
        }else{
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour < 9) {
                c.add(Calendar.DAY_OF_MONTH, -1);
            }
        }
        c.set(Calendar.HOUR_OF_DAY, 17);
        c.set(Calendar.MINUTE, 30);
        c.set(Calendar.SECOND, 0);
        return datechange(c.getTime(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String datechange(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String demo = sdf.format(date);
        return demo;
    }

    /**
     * 根据判断当前时间是否是节日
     * @param date 时间
     * @return
     */
    public static boolean isHoliday(Date date) {
        boolean result = false;
        String dateStr = sdf.format(date).substring(5,10);
        if (solarHolidaysMap.size() > 0) {
            Iterator<Map.Entry<String, String>> entries = solarHolidaysMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                if (entry.getKey().equals(dateStr)) {
                    currHolidays = entry.getValue();
                    result = true;
                    break;
                }
            }
        }
        //判断是否是农历节日
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String lunar = solarToLunar(cal);
        if (lunarHolidaysMap.size() > 0) {
            Iterator<Map.Entry<String, String>> entries = lunarHolidaysMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                if (entry.getKey().equals(lunar)) {
                    currHolidays = entry.getValue();
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 阳历转农历
     * @return
     */
    public static String solarToLunar(Calendar cl){
        Lunar lunar = new Lunar(cl);
        return  "农历" + lunar;
    }


    public static String  getCurrHolidays(){
        return currHolidays;
    }

}
