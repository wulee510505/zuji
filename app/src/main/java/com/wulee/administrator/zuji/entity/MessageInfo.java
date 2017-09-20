package com.wulee.administrator.zuji.entity;

import com.wulee.administrator.zuji.database.bean.PersonInfo;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by wulee on 2017/9/19 10:53
 */

public class MessageInfo extends BmobObject {

    private String content;
    public PersonInfo owner;
    public PersonInfo piInfo;

    public BmobRelation getSender() {
        return sender;
    }

    public void setSender(BmobRelation sender) {
        this.sender = sender;
    }

    private BmobRelation sender;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
