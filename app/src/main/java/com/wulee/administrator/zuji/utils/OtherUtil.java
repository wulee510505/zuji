package com.wulee.administrator.zuji.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wulee.administrator.zuji.App;
import com.wulee.administrator.zuji.R;

import java.util.regex.Pattern;

import static com.wulee.administrator.zuji.App.aCache;

/**
 * Created by wulee on 2017/4/25 10:40
 */

public class OtherUtil {
    /**
     * 密码校验
     * 要求6-16位数字和英文字母组合
     * @param pwd
     * @return
     */
    public static boolean isPassword(String pwd) {
        /**
         * ^ 匹配一行的开头位置(?![0-9]+$) 预测该位置后面不全是数字
         * (?![a-zA-Z]+$) 预测该位置后面不全是字母
         * [0-9A-Za-z] {6,16} 由6-16位数字或这字母组成
         */
        return validation("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$", pwd);
    }

    /**
     * 手机号校验 注：1.支持最新170手机号码 2.支持+86校验
     * @param phoneNum
     * 手机号码
     * @return 验证通过返回true
     */
    public static boolean isMobile(String phoneNum) {
        if (phoneNum == null)
            return false;
        // 如果手机中有+86则会自动替换掉
        return validation("^[1][3,4,5,7,8][0-9]{9}$",
                phoneNum.replace("+86", ""));
    }

    /**
     * 正则校验
     * @param str
     * 需要校验的字符串
     * @return 验证通过返回true
     */
    public static boolean validation(String pattern, String str) {
        if (str == null)
            return false;
        return Pattern.compile(pattern).matcher(str).matches();
    }

    /**
     * 判断两个double类型值是否相等
     * @param num1
     * @param num2
     * @return
     */
    public static boolean equal(double num1,double num2) {
        if((num1-num2 >-0.000001)&&(num1-num2)<0.000001)
            return true;
        else
            return false;
    }

    /**
     * 判断是否登录
     * @return
     */
    public static boolean hasLogin(){
        boolean isLogin = false;
        if(TextUtils.equals("yes",aCache.getAsString("has_login"))){
            isLogin = true;
        }else{
            isLogin = false;
        }
        return isLogin;
    }


    public static void showToastText(String msg) {
        if (App.context != null) {
            showNewToast(App.context, 0, msg, Toast.LENGTH_LONG);
        }
    }

    private static Toast mToast;

    /**
     * 弹出Toast
     *
     * @param context
     * @param resId
     * @param text     提示内容
     * @param duration
     */
    private static void showNewToast(Context context, int resId, CharSequence text, int duration) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast, null);
        ImageView imageView = (ImageView) layout.findViewById(R.id.iv_icon);
        TextView textView = (TextView) layout.findViewById(R.id.tv_content);
        if (resId > 0) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setBackgroundResource(resId);
        } else {
            imageView.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        } else {
            textView.setText("");
        }
        if (null == mToast) {
            mToast = new Toast(context);
        }
        mToast.setView(layout);
        mToast.setDuration(duration);
        mToast.show();
    }
}
