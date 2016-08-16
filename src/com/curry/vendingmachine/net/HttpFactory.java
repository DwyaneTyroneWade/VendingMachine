package com.curry.vendingmachine.net;

import java.util.ArrayList;

import com.curry.vendingmachine.bean.BaseResultBean;
import com.curry.vendingmachine.bean.CashPayBean;
import com.curry.vendingmachine.bean.EquipStatus;
import com.curry.vendingmachine.bean.PayStatusBean;
import com.curry.vendingmachine.bean.QRcodeBean;
import com.curry.vendingmachine.net.HttpNeedle.OnHttpErrorListener;
import com.curry.vendingmachine.net.HttpNeedle.OnHttpProcessListener;
import com.curry.vendingmachine.net.HttpNeedle.OnHttpResponseListener;
import com.curry.vendingmachine.utils.C;
import com.curry.vendingmachine.utils.CurryUtils;
import com.curry.vendingmachine.utils.DeviceUtils;
import com.curry.vendingmachine.utils.L;
import com.curry.vendingmachine.utils.MD5;
import com.curry.vendingmachine.utils.NameValuePair;

public class HttpFactory {
	/**
	 * 获取二维码和订单号
	 * 
	 * @param listener
	 * @param huodao
	 * @param amt
	 */
	public static void postQRcode(OnHttpResponseListener<QRcodeBean> listener,
			OnHttpErrorListener errListener,
			OnHttpProcessListener processListener, String huodao, String amt) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new NameValuePair("sbId", ServerConstants.EQUIP_ID));
		params.add(new NameValuePair("huodao", huodao));
		params.add(new NameValuePair("amt", amt));
		params.add(new NameValuePair("md5Code", MD5.encryptByMD5(amt)));

		String url = ServerConstants.SERVER_URL
				+ ServerConstants.SERVER_URL_DOMAIN
				+ ServerConstants.ACTION_SCANPAY;

		HttpNeedle.getInstance().setPostIsQr(true);
		HttpNeedle.getInstance().setHttpProcessListener(processListener);
		HttpNeedle.getInstance().postNeedlely(url, params, listener,
				errListener, QRcodeBean.class);
	}

	/**
	 * 获取订单支付状态
	 * 
	 * @param listener
	 * @param aliOrderNo
	 * @param wechatOrderNo
	 */
	public static void postPayStatus(
			OnHttpResponseListener<PayStatusBean> listener,
			OnHttpErrorListener errListener, String aliOrderNo,
			String wechatOrderNo) {
		String url = ServerConstants.SERVER_URL
				+ ServerConstants.SERVER_URL_DOMAIN
				+ ServerConstants.ACTION_PAY_STATUS;
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new NameValuePair("sbId", ServerConstants.EQUIP_ID));
		params.add(new NameValuePair("ali_orderno", aliOrderNo));
		params.add(new NameValuePair("wx_orderno", wechatOrderNo));

		HttpNeedle.getInstance().setPostIsQr(false);
		HttpNeedle.getInstance().postNeedlely(url, params, listener,
				errListener, PayStatusBean.class);

	}

	/**
	 * 现金 支付 找零
	 * 
	 * @param listener
	 * @param errListener
	 * @param huodao
	 * @param amt
	 * @param payType
	 *            1纸币器 2硬币器
	 * @param cashtype
	 *            1收款2退款
	 */
	public static void postCashPay(
			OnHttpResponseListener<CashPayBean> listener,
			OnHttpErrorListener errListener, String huodao, String amt,
			int payType, int cashtype) {
		String url = ServerConstants.SERVER_URL
				+ ServerConstants.SERVER_URL_DOMAIN
				+ ServerConstants.ACTION_CASHPAY;
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new NameValuePair("sbId", ServerConstants.EQUIP_ID));
		params.add(new NameValuePair("huodao", huodao));
		params.add(new NameValuePair("paytype", String.valueOf(payType)));
		params.add(new NameValuePair("cashtype", String.valueOf(cashtype)));
		params.add(new NameValuePair("amt", amt));
		params.add(new NameValuePair("timestamp", String.valueOf(System
				.currentTimeMillis())));
		params.add(new NameValuePair("md5Code", MD5.encryptByMD5(amt)));

		HttpNeedle.getInstance().setPostIsQr(false);
		HttpNeedle.getInstance().postNeedlely(url, params, listener,
				errListener, CashPayBean.class);
	}

	/**
	 * 确定出货，售货机出货成功后，再调用
	 * 
	 * @param listener
	 * @param errListener
	 * @param huodao
	 * @param amt
	 */
	public static void postDelivery(
			OnHttpResponseListener<BaseResultBean> listener,
			OnHttpErrorListener errListener, String huodao, String amt,
			String orderNo) {

		String url = ServerConstants.SERVER_URL
				+ ServerConstants.SERVER_URL_DOMAIN
				+ ServerConstants.ACTION_DELIVERY;
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new NameValuePair("sbId", ServerConstants.EQUIP_ID));
		params.add(new NameValuePair("huodao", huodao));
		params.add(new NameValuePair("amt", amt));
		params.add(new NameValuePair("timestamp", String.valueOf(System
				.currentTimeMillis())));
		params.add(new NameValuePair("orderNo", orderNo));
		params.add(new NameValuePair("md5Code", MD5.encryptByMD5(amt)));

		L.d("upload", "postDelivery sbId:" + ServerConstants.EQUIP_ID
				+ "huodao:" + huodao + "amt:" + amt + "orderNo:" + orderNo
				+ "timeStamp:" + String.valueOf(System.currentTimeMillis())
				+ "md5Code:" + MD5.encryptByMD5(amt));
		HttpNeedle.getInstance().setPostIsQr(false);
		HttpNeedle.getInstance().postNeedlely(url, params, listener,
				errListener, BaseResultBean.class);
	}

	public static void postEquipStatus(
			OnHttpResponseListener<BaseResultBean> listener,
			OnHttpErrorListener errListener, EquipStatus es) {
		String url = ServerConstants.SERVER_URL
				+ ServerConstants.SERVER_URL_DOMAIN
				+ ServerConstants.ACTION_STATUS;
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new NameValuePair("sbId",
				CurryUtils.isStringEmpty(es.sbId) ? "" : es.sbId));
		params.add(new NameValuePair("version",
				CurryUtils.isStringEmpty(es.version) ? DeviceUtils
						.getVersionName(C.get()) : es.version));
		params.add(new NameValuePair("zbq_zs", CurryUtils
				.isStringEmpty(es.zbq_zs) ? "0" : es.zbq_zs));
		params.add(new NameValuePair("ybq_zs", CurryUtils
				.isStringEmpty(es.ybq_zs) ? "0" : es.ybq_zs));
		params.add(new NameValuePair("status", "0"));
		params.add(new NameValuePair("zbq_status", CurryUtils
				.isStringEmpty(es.zbq_status) ? "2" : es.zbq_status));
		params.add(new NameValuePair("ybq_status", CurryUtils
				.isStringEmpty(es.ybq_status) ? "2" : es.ybq_status));
		params.add(new NameValuePair("ysj_status", "0"));
		params.add(new NameValuePair("kzb_status", CurryUtils
				.isStringEmpty(es.kzb_status) ? "2" : es.kzb_status));
		params.add(new NameValuePair("gprs_status", CurryUtils
				.isStringEmpty(es.gprs_status) ? "2" : es.gprs_status));
		params.add(new NameValuePair("network", CurryUtils
				.isStringEmpty(es.network) ? "0" : es.network));
		params.add(new NameValuePair("array", CurryUtils.isStringEmpty(es
				.jsonArray()) ? "" : es.jsonArray()));

		HttpNeedle.getInstance().setPostIsQr(false);
		HttpNeedle.getInstance().postNeedlely(url, params, listener,
				errListener, BaseResultBean.class);
	}
}
