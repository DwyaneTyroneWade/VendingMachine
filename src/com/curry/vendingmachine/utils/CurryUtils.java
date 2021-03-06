package com.curry.vendingmachine.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.curry.vendingmachine.net.ServerConstants;

public class CurryUtils {
	public static Map<String, String> getDefaultHeaders() {
		Map<String, String> headers = new HashMap<String, String>();

		headers.put("Host", ServerConstants.SERVER_URL.substring(
				"http://".length(), ServerConstants.SERVER_URL.length()));

		return headers;
	}

	public static boolean isStringEmpty(CharSequence input) {
		if (TextUtils.isEmpty(input)) {
			return true;
		}
		if (input.equals("null")) {
			return true;
		}
		return false;
	}

	public static int dip2px(Context context, float dip) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	public static byte[] getRealBuffer(byte[] buffer, int size) {
		byte[] realBuffer = new byte[size];
		for (int i = 0; i < size; i++) {
			realBuffer[i] = buffer[i];
		}
		return realBuffer;
	}

	public static StringBuffer newStringBuffer(StringBuffer buffer) {
		if (buffer == null) {
			return new StringBuffer();
		} else {
			buffer.delete(0, buffer.length());
			return buffer;
		}
	}

	/**
	 * 获取屏幕的宽度
	 * 
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获取屏幕的高度
	 * 
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}
}
