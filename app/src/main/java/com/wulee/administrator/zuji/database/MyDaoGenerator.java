package com.wulee.administrator.zuji.database;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 *
 */
public class MyDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.wulee.administrator.zuji.database.bean");
        //分别指定生成的 Bean 与 DAO 类所在的目录
        schema.setDefaultJavaPackageDao("com.wulee.administrator.zuji.database.dao");
        addAccount(schema);//
        pushMessageInfo(schema);
        addPersonInfo(schema);
        addLocationInfo(schema);
        new DaoGenerator().generateAll(schema, "./app/src/main/java");
    }


    /**当前登录用户信息*/
    private static void addAccount(Schema schema){
        Entity member=schema.addEntity("LoginBean");
        member.setTableName("LOGIN_TABLE");
        member.addStringProperty("mobile");
        member.addLongProperty("current_uid").unique();
        member.addBooleanProperty("logining");
        member.addStringProperty("session");
    }

    /**推送的消息*/
    private static void pushMessageInfo(Schema schema){
        Entity pushMessage = schema.addEntity("PushMessage");
        pushMessage.setTableName("PUSH_MESSAGE_TABLE");
        pushMessage.addStringProperty("type");
        pushMessage.addStringProperty("content");
        pushMessage.addLongProperty("time").unique();
    }

    /**个人信息*/
    private static void addPersonInfo(Schema schema){
        Entity personInfo = schema.addEntity("PersonInfo");
        personInfo.setTableName("PERSONINFO_TABLE");
        personInfo.addStringProperty("mobile").primaryKey();
        personInfo.addStringProperty("name");
        personInfo.addStringProperty("sex");
        personInfo.addStringProperty("birthday");
        personInfo.addStringProperty("address");
        personInfo.addStringProperty("header_img_url");

        personInfo.addDoubleProperty("homeLat");
        personInfo.addDoubleProperty("homeLon");
        personInfo.addStringProperty("homeAddress");
        personInfo.addDoubleProperty("companyLat");
        personInfo.addDoubleProperty("companyLon");
        personInfo.addStringProperty("companyAddress");
    }


    /**位置信息*/
    private static void addLocationInfo(Schema schema){
        Entity locationInfo = schema.addEntity("LocationInfo");
        locationInfo.setTableName("LOCATION_TABLE");
        locationInfo.addStringProperty("nativePhoneNumber");
        locationInfo.addStringProperty("lontitude");
        locationInfo.addStringProperty("latitude");
        locationInfo.addStringProperty("address");
        locationInfo.addStringProperty("locationdescribe");
        locationInfo.addStringProperty("deviceId");
    }
}
