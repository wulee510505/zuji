package com.wulee.administrator.zuji.entity;

import com.wulee.administrator.zuji.database.bean.PersonInfo;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by wulee on 2017/8/22 10:21
 */

public class CircleContent extends BmobObject{
    private long id;

    private String userId;
    private String userNick;
    private String userAvatar;

    private String title;
    private String content;

    private String location;
    public PersonInfo personInfo;

    private ArrayList<CircleImageBean> imageList;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public List<CircleImageBean> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<CircleImageBean> imageList) {
        this.imageList = imageList;
    }

    public static class CircleImageBean {
        private String id;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}
