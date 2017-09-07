package com.wulee.administrator.zuji.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by wulee on 2017/9/7 11:25
 */

public class NewsUrl extends BmobObject {


    private int index;
    private String title;
    private String url;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
