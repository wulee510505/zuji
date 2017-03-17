package com.wulee.administrator.zuji.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.wulee.administrator.zuji.utils.AppUtils;

/**
 * Created by mdw on 2016/1/27.
 */
public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // 结束Activity&从堆栈中移除
        AppUtils.getAppManager().finishActivity(this);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加Activity到堆栈
        AppUtils.getAppManager().addActivity(this);
    }

    /**
     * 意图跳转
     *
     * @param cls
     */
    public void intent2Activity(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    /**
     * toast
     *
     * @param msg 消息
     */
    public void toast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
