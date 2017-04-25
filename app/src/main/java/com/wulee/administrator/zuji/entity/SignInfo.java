package com.wulee.administrator.zuji.entity;

import com.wulee.administrator.zuji.database.bean.PersonInfo;

import cn.bmob.v3.BmobObject;

/**
 * Created by wulee on 2017/4/24 15:46
 */

public class SignInfo extends BmobObject {

    public String date;
    public boolean hasSign;
    public PersonInfo personInfo;
}
