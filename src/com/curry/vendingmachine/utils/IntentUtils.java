package com.curry.vendingmachine.utils;

import android.content.Intent;

import com.curry.vendingmachine.base.BaseActivity;

public class IntentUtils {
	/**
	 * 防止多次打开Activity
	 * 
	 * @param activity
	 * @param intent
	 */
	public static void goActivitySingleTop(BaseActivity activity, Intent intent) {
		if (intent == null || activity == null) {
			return;
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		activity.startActivity(intent);
	}
}
