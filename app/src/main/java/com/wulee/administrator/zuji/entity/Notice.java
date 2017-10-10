package com.wulee.administrator.zuji.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by wulee on 2017/10/10 14:56
 */

public class Notice extends BmobObject {

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
