package com.wulee.administrator.zuji.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.recordingibrary.entity.Voice;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by wulee on 2017/9/19 10:53
 */

public class MessageInfo extends BmobObject implements MultiItemEntity{

    private String content;
    public PersonInfo owner;
    public PersonInfo piInfo;
    public Voice voice;
    public String audioUrl;

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_AUDIO = 1;
    private int itemType;

    public MessageInfo(int itemType){
        this.itemType  = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

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
