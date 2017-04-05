package com.wulee.administrator.zuji.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.wulee.administrator.zuji.App;

import java.util.List;
import java.util.Stack;

//应用程序Activity管理类：用于Activity管理和应用程序退出 
public class AppUtils {
	private static Stack<Activity> activityStack;
	private static AppUtils instance;

	private AppUtils() {
	}

	/**
	 * 单一实例
	 */
	public static AppUtils getAppManager() {
		if (instance == null) {
			instance = new AppUtils();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public static void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public static Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public static void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public static void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public static void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public static void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	public static void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
		}
	}


	public static boolean isServiceRunning(String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) App.context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
		if (!(serviceList.size()>0)) {
			return false;
		}
		for (int i=0; i<serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * 获取版本号
	 * 也可使用 BuildConfig.VERSION_NAME 替换
	 * @return 版本号
	 */
	public static String getVersionName() {
		PackageManager packageManager = App.context.getPackageManager();
		String packageName = App.context.getPackageName();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
			return packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "1.0.0";
	}

	/**
	 * 获取版本code
	 * 也可使用 BuildConfig.VERSION_CODE 替换
	 * @return 版本code
	 */
	public static int getVersionCode() {
		PackageManager packageManager = App.context.getPackageManager();
		String packageName = App.context.getPackageName();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}
}
