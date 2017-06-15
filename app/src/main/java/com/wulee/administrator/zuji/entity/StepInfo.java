package com.wulee.administrator.zuji.entity;

import com.wulee.administrator.zuji.database.bean.PersonInfo;

import cn.bmob.v3.BmobObject;

/**
 * Created by wulee on 2017/6/9 14:45
 */

public class StepInfo extends BmobObject{

    private int count;
    private String date;
    private int zannum;
    public PersonInfo personInfo;

    public int getZannum() {
        return zannum;
    }

    public void setZannum(int zannum) {
        this.zannum = zannum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
