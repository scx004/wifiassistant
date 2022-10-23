package org.daai.wifiassistant.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by zengzheying on 16/1/20.
 */
public class PhoneStateUtil {

	public static String getVersionName(Context context) {
		String result = "";

		try {
			result = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0)
					.versionName;
		} catch (PackageManager.NameNotFoundException ex) {
		}

		return result;
	}

	public static int getVersionCode(Context context) {
		int result = 0;

		try {
			result = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0)
					.versionCode;
		} catch (PackageManager.NameNotFoundException ex) {
		}

		return result;
	}


	/**
	 * 获取应用程序名称
	 */
	public static synchronized String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * [获取应用程序版本名称信息]
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static synchronized String getPackageName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.packageName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
