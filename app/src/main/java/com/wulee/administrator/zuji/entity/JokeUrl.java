package com.wulee.administrator.zuji.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by wulee on 2017/9/4 14:25
 */

public class JokeUrl extends BmobObject {


    private int type;
    private String desc;
    private String url;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
