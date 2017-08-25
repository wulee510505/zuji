package com.wulee.administrator.zuji.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.WindowManager;

import com.wulee.administrator.zuji.App;

public class UIUtils {

	public static Context getContext() {
		return App.context;
	}

	// 获取字符串
	public static String getString(int id) {
		return getContext().getResources().getString(id);
	}

	// 获取图片
	public static Drawable getDrawable(int id) {
		return getContext().getResources().getDrawable(id);
	}

	// 获取颜色
	public static int getColor(int id) {
		return getContext().getResources().getColor(id);
	}

	// 获取颜色的状态选择器
	public static ColorStateList getColorStateList(int id) {
		return getContext().getResources().getColorStateList(id);
	}

	// 获取尺寸
	public static int getDimen(int id) {
		return getContext().getResources().getDimensionPixelSize(id);// 返回像素
	}

	public static int dip2px(float dp) {
		return (int) (getContext().getResources().getDisplayMetrics().density
				* dp + 0.5f);
	}

	public static float px2dip(int px) {
		return px / getContext().getResources().getDisplayMetrics().density;
	}

	public static int px2sp(float pxValue) {
		final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	public static int sp2px(float spValue) {
		final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static int[] getScreenWidthAndHeight(Context context){
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		return new int[]{width,height};
	}
}
