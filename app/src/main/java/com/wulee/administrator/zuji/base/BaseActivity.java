package com.wulee.administrator.zuji.base;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.utils.AppUtils;
import com.wulee.administrator.zuji.utils.SystemBarTintManager;
import com.wulee.administrator.zuji.widget.BaseProgressDialog;

/**
 * Created by mdw on 2016/1/27.
 */
public class BaseActivity extends AppCompatActivity {
    private BaseProgressDialog mProgressDialog = null;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setStateBarColor();
    }

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


    public void showProgressDialog(BaseProgressDialog.OnCancelListener cancelListener, boolean cancelable, String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = new BaseProgressDialog(this);
        if (cancelListener != null) {
            mProgressDialog.setOnCancelListener(cancelListener);
        }
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.show();
    }

    public void showProgressDialog(boolean cancelable, String msg) {
        showProgressDialog(null, cancelable, msg);
    }

    public void showProgressDialog(boolean cancelable) {
        showProgressDialog(cancelable, "");
    }

    public void stopProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.stop();
        }
        mProgressDialog = null;
    }

    protected void cancelProgressDialog() {
        if (mProgressDialog.cancel()) {
            mProgressDialog = null;
        }
    }

    /**
     * 改变状态栏颜色
     */
    private void setStateBarColor() {
        int res = R.color.colorAccent;
        if(-1 != getStateBarColor()) {
            res = getStateBarColor();
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(res);
    }

    protected int getStateBarColor() {
        return -1;
    }


    /**
     * 检查网络连接
     * @return
     */
    public boolean checkInternetConnection() {
        NetworkInfo info = null;
        if (info == null) {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            info = manager.getActiveNetworkInfo();
        }
        return info != null && info.isAvailable();
    }

}
