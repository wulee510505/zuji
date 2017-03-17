package com.wulee.administrator.zuji.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by wulee on 2016/12/7 16:50
 */

public class LocationInfo extends BmobObject {
    public String latitude;
    public String lontitude;
    public String address;
    public  String imgurl;

    public String locationdescribe;
    public String nativePhoneNumber;

    public String deviceId;
    public PersonalInfo piInfo; //用户信息
}
