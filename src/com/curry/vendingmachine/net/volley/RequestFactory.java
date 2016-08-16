package com.curry.vendingmachine.net.volley;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.curry.vendingmachine.bean.BaseResultBean;
import com.curry.vendingmachine.bean.CashPayBean;
import com.curry.vendingmachine.bean.PayStatusBean;
import com.curry.vendingmachine.bean.QRcodeBean;
import com.curry.vendingmachine.bean.VideoBean;
import com.curry.vendingmachine.net.ServerConstants;
import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.MD5;

public class RequestFactory {
	
	public static GsonRequest<QRcodeBean> getQRcodeRequest(
			Listener<QRcodeBean> listener, ErrorListener errListener,
			String huodao, String amt) {
		String url = ServerConstants.SERVER_URL
				+ ServerConstants.SERVER_URL_DOMAIN
				+ ServerConstants.ACTION_SCANPAY;
		Map<String, String> contentData = new HashMap<String, String>();

		// "866104023279923"
		contentData.put("sbId", "866104023279923");
		contentData.put("huodao", huodao);
		contentData.put("amt", amt);
		contentData.put("md5Code", MD5.encryptByMD5(amt));

		GsonRequest<QRcodeBean> request = GsonRequest.newGsonPostRequest(url,
				contentData, listener, errListener,
				CurryUtils.getDefaultHeaders(), QRcodeBean.class);

		return request;
	}

	public static GsonRequest<BaseResultBean> getDevStatusUploadRequest(
			Listener<BaseResultBean> listener, ErrorListener errListener) {
		String url = ServerConstants.SERVER_URL
				+ ServerConstants.SERVER_URL_DOMAIN
				+ ServerConstants.ACTION_STATUS;
		Map<String, String> contentData = new HashMap<String, String>();
		contentData.put("sbId", "866104023279923");
		contentData.put("zbq_zs", "20");
		contentData.put("ybq_zs", "40");
		contentData.put("status", "0");
		contentData.put("zbq_status", "0");
		contentData.put("ybq_status", "1");
		contentData.put("ysj_status", "1");
		contentData.put("kzb_status", "1");
		contentData.put("gprs_status", "1");
		contentData.put("network", "32");

		GsonRequest<BaseResultBean> request = GsonRequest.newGsonPostRequest(
				url, contentData, listener, errListener,
				CurryUtils.getDefaultHeaders(), BaseResultBean.class);

		return request;
	}

	public static GsonRequest<BaseResultBean> getShelfUploadRequest(
			String jsonArray, Listener<BaseResultBean> listener,
			ErrorListener errListener) {
		String url = ServerConstants.SERVER_URL
				+ ServerConstants.SERVER_URL_DOMAIN
				+ ServerConstants.ACTION_SHELF;
		Map<String, String> contentData = new HashMap<String, String>();
		contentData.put("sbId", "866104023279923");
		contentData.put("huodao", "20");
		contentData.put("array", jsonArray);

		GsonRequest<BaseResultBean> request = GsonRequest.newGsonPostRequest(
				url, contentData, listener, errListener,
				CurryUtils.getDefaultHeaders(), BaseResultBean.class);

		return request;
	}

	public static GsonRequest<PayStatusBean> getPayStatusRequest(
			Listener<PayStatusBean> listener, ErrorListener errListener,
			String aliOrderNo, String wechatOrderNo) {
		String url = ServerConstants.SERVER_URL
				+ ServerConstants.SERVER_URL_DOMAIN
				+ ServerConstants.ACTION_PAY_STATUS;
		Map<String, String> contentData = new HashMap<String, String>();
		contentData.put("sbId", "866104023279923");
		contentData.put("ali_orderno", aliOrderNo);
		contentData.put("wx_orderno", wechatOrderNo);

		GsonRequest<PayStatusBean> request = GsonRequest.newGsonPostRequest(
				url, contentData, listener, errListener,
				CurryUtils.getDefaultHeaders(), PayStatusBean.class);

		return request;
	}

	public static GsonRequest<BaseResultBean> getDeliveryRequest(
			Listener<BaseResultBean> listener, ErrorListener errListener) {
		String url = ServerConstants.SERVER_URL
				+ ServerConstants.SERVER_URL_DOMAIN
				+ ServerConstants.ACTION_DELIVERY;
		Map<String, String> contentData = new HashMap<String, String>();
		contentData.put("sbId", "866104023279923");
		contentData.put("huodao", "2");
		contentData.put("amt", "2");
		contentData.put("md5Code", MD5.encryptByMD5("2"));

		GsonRequest<BaseResultBean> request = GsonRequest.newGsonPostRequest(
				url, contentData, listener, errListener,
				CurryUtils.getDefaultHeaders(), BaseResultBean.class);

		return request;
	}

	public static GsonRequest<CashPayBean> getCashPayRequest(
			Listener<CashPayBean> listener, ErrorListener errListener) {
		String url = ServerConstants.SERVER_URL
				+ ServerConstants.SERVER_URL_DOMAIN
				+ ServerConstants.ACTION_CASHPAY;
		Map<String, String> contentData = new HashMap<String, String>();
		contentData.put("sbId", "866104023279923");
		contentData.put("huodao", "2");
		contentData.put("payway", "1");
		contentData.put("amt", "10");
		contentData.put("md5Code", MD5.encryptByMD5("10"));

		GsonRequest<CashPayBean> request = GsonRequest.newGsonPostRequest(url,
				contentData, listener, errListener,
				CurryUtils.getDefaultHeaders(), CashPayBean.class);

		return request;
	}

	public static GsonRequest<VideoBean> getVideoRequest(
			Listener<VideoBean> listener, ErrorListener errListener) {
		String url = ServerConstants.SERVER_URL
				+ ServerConstants.SERVER_URL_DOMAIN
				+ ServerConstants.ACTION_VIDEO;
		Map<String, String> contentData = new HashMap<String, String>();
		contentData.put("sbId", "866104023279923");

		GsonRequest<VideoBean> request = GsonRequest.newGsonPostRequest(url,
				contentData, listener, errListener,
				CurryUtils.getDefaultHeaders(), VideoBean.class);

		return request;
	}
}
