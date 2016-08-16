package com.curry.vendingmachine.net;

import com.curry.vendingmachine.utils.Constants;
import com.curry.vendingmachine.utils.PreferenceHelper;

public class ServerConstants {
	// public static String IP = "120.26.70.102";
	// public static String PORT = "8180";

	// old
	// public static String IP = "123.57.71.228";
	// public static String PORT = "8080";

	// new
	// public static String IP = "103.26.0.44";
	// public static String PORT = "80";
	public static String IP = PreferenceHelper.getString(
			Constants.PREFERENCE_KEY_SERVER_IP, "103.26.0.44");
	public static String PORT = PreferenceHelper.getString(
			Constants.PREFERENCE_KEY_SERVER_PORT, "80");
	public static String SERVER_URL = "http://" + IP + ":" + PORT;

	public static final String SERVER_URL_DOMAIN = "/yuanai/protocol/";

	public static final String SERVER_SUCCESS = "true";
	public static final String PAY_STATUS_HAS_PAY = "haspay";

	/* 支付宝/微信 - 请求支付协议 */
	public static final String ACTION_SCANPAY = "scanpay.action";
	/* 设备状态上送协议 */
	public static final String ACTION_STATUS = "status.action";
	/* 货道信息上送协议 */
	public static final String ACTION_SHELF = "shelf.action";
	/* 确定出货协议 */
	public static final String ACTION_DELIVERY = "delivery.action";
	/* 现金 - 请求支付协议 */
	public static final String ACTION_CASHPAY = "cashpay.action";
	/* 获得视频地址 */
	public static final String ACTION_VIDEO = "video.action";
	/* 请求查看支付状态 */
	public static final String ACTION_PAY_STATUS = "paystatus.action";

	// TODO
	public static final String EQUIP_ID = PreferenceHelper.getString(
			Constants.PREFERENCE_KEY_EQUIP_ID, "");

}
