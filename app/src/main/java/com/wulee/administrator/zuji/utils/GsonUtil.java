package com.wulee.administrator.zuji.utils;

import com.google.gson.Gson;

/**
 * Created by wulee on 2017/2/28 16:12
 */

public class GsonUtil {

    //将Json数据解析成相应的映射对象
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }
}
