package com.wulee.administrator.zuji.database;

import com.wulee.administrator.zuji.App;
import com.wulee.administrator.zuji.database.bean.LoginBean;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.database.bean.PushMessage;
import com.wulee.administrator.zuji.database.dao.LoginBeanDao;
import com.wulee.administrator.zuji.database.dao.PersonInfoDao;
import com.wulee.administrator.zuji.database.dao.PushMessageDao;

import java.util.List;


/**
 * Created by wulee on 2016/10/17 10:13
 */

public class DBHandler {
    private static LoginBeanDao loginDao = App.session.getLoginBeanDao();
    private static PushMessageDao pushMessageDao = App.session.getPushMessageDao();
    private static PersonInfoDao personInfoDao = App.session.getPersonInfoDao();

    public static void insertLoginInfo(LoginBean loginInfo) {
        loginDao.insertOrReplace(loginInfo);
    }

    public static LoginBean  getLoginInfo() {
        if (loginDao.loadAll() != null && loginDao.loadAll().size() > 0)
            for (LoginBean loginBean : loginDao.loadAll())
                if (loginBean.getLogining())
                    return loginBean;
        return null;
    }

    public static void clearLoginInfo() {
        loginDao.deleteAll();
    }

    public static void insertPushMessage(PushMessage pushMessage) {
        pushMessageDao.insertOrReplace(pushMessage);
    }


    public static List<PushMessage> getAllPushMessage() {
        return  pushMessageDao.loadAll();
    }

    public static void insertPesonInfo(PersonInfo personInfo) {
        personInfoDao.insertOrReplace(personInfo);
    }


    public static void updatePesonInfo(PersonInfo personInfo) {
        personInfoDao.insertOrReplace(personInfo);
    }
}
