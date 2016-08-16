package com.curry.vendingmachine.utils;

import android.content.Context;

public class DeviceUtils {

	public static int getVersionCode(Context context){
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}
	}
	
	public static String getVersionName(Context context){
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			// TODO: handle exception
			return "unknow";
		}
	}
}
