package com.wulee.administrator.zuji.entity;


import com.wulee.administrator.zuji.database.bean.PersonInfo;

import cn.bmob.v3.BmobObject;

/**
 * Entity mapped to table "FEED_BACK_TABLE".
 */
public class FeedBackInfo extends BmobObject {

    private String mobile;
    private String content;
    public PersonInfo personInfo;
    public String installationId;

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public FeedBackInfo() {
    }

    public FeedBackInfo(String mobile, String content) {
        this.mobile = mobile;
        this.content = content;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
