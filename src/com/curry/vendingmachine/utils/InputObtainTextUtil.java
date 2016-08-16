package com.curry.vendingmachine.utils;

import android.widget.TextView;

public class InputObtainTextUtil {
	public static String appendText1(TextView tv, String key) {
		String currStr = tv.getText().toString();
		L.d("currStr:" + currStr);
		if (CurryUtils.isStringEmpty(currStr)) {
			return key;
		} else {
			int currInt = Integer.parseInt(currStr);
			if (currInt <= 0) {
				return key;
			} else if (currInt < 10) {
				StringBuilder builder = new StringBuilder();
				builder.append(currInt);
				builder.append(key);
				return builder.toString();
			} else {
				return key;
			}
		}
	}
	
	public static String appendText2(TextView tv, String key) {
		String currStr = tv.getText().toString();
		L.d("currStr:" + currStr);
		if (CurryUtils.isStringEmpty(currStr)) {
			return key;
		} else {
			int currInt = Integer.parseInt(currStr);
			if (currInt <= 0) {
				return key;
			} else if (currInt < 100) {
				StringBuilder builder = new StringBuilder();
				builder.append(currInt);
				builder.append(key);
				return builder.toString();
			} else {
				return key;
			}
		}
	}
}
