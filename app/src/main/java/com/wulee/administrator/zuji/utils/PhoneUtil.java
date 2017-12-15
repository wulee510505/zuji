package com.wulee.administrator.zuji.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.wulee.administrator.zuji.App;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by wulee on 2016/12/8 09:37
 */

public class PhoneUtil {
    /**
     * Role:获取当前设置的电话号码
     */
    public static String getNativePhoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) App.context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String NativePhoneNumber = telephonyManager.getLine1Number();
        return NativePhoneNumber;
    }

    /**
     * 获取设备的串号
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceId() {
        TelephonyManager tm = (TelephonyManager)App.context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial,androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + Settings.Secure.getString(App.context.getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }


    /**
     * 获取手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 随机生成一个UUID
     * @return String UUID
     */
    public static String getRandomUUID(){
        String s = UUID.randomUUID().toString();
        //去掉“-”符号
        return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
    }

    /**
     * 获取设备UDID
     * @return String UUID
     */
    @SuppressLint("MissingPermission")
    public synchronized static String getUDID() {
        String uuid = "";
        final String androidId = Settings.Secure.getString(App.context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            if (!"9774d56d682e549c".equals(androidId)) {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
            } else {
                final String deviceId = ((TelephonyManager) App.context.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                uuid = deviceId!=null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString() : UUID.randomUUID().toString();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return uuid;
    }

    /**
     *  将手机号码第4位到第7位替换成*
     */
    public static String encryptTelNum(String telnum){
        // 括号表示组，被替换的部分$n表示第n组的内容
       return telnum.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

}
