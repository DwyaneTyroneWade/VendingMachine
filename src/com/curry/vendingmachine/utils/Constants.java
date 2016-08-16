package com.curry.vendingmachine.utils;

public class Constants {
	public static final int PAY_TYPE_PAPER = 1;
	public static final int PAY_TYPE_COIN = 2;
	public static final int CASH_TYPE_IN = 1;
	public static final int CASH_TYPE_OUT = 2;

	public static boolean isTimerFinish = false;
	public static boolean isQRpayFinish = false;

	public static final int GOODS_NUM_MAX = 130;
	public static final String CAPACITY_AND_STOCK_MAX = "9999";

	public static final String NEEDLE_TYPE_HTTP = "needle_http";
	public static final String NEEDLE_TYPE_PAPER = "needle_paper";
	public static final String NEEDLE_TYPE_COIN = "needle_coin";
	// public static final String NEEDLE_TYPE_DATABASE = "needle_db";
	public static final String NEEDLE_TYPE_DATABASE_GOODS = "needle_db_goods";
	public static final String NEEDLE_TYPE_DATABASE_SALE_RECORD = "needle_db_sale_record";
	public static final String NEEDLE_TYPE_DATABASE_EQUIP_STATUS = "needle_db_equip_status";
	public static final String NEEDLE_TYPE_LOG = "needle_log";
	public static final String NEEDLE_TYPE_SHIP = "needle_ship";
	public static final String NEEDLE_TYPE_DATE_SET = "needle_date_set";
	public static final String NEEDLE_TYPE_ENCODE_QR_CODE = "needle_encode_qr_code";

	public static final String BUNDLE_KEY_STOCK_TYPE = "stock_type";
	public static final String BUNDLE_KEY_GOODS_NUM = "goods_num";
	// public static final String BUNDLE_KEY_LAYER = "layer";
	// public static final String BUNDLE_KEY_ONE = "one";
	public static final String BUNDLE_KEY_NEED_PRICE = "need_price";
	public static final String BUNDLE_KEY_PAPER_VALUE = "paper_value";
	public static final String BUNDLE_KEY_CHANGE_PAPER_NUM = "change_paper_num";
	public static final String BUNDLE_KEY_CHANGE_COIN_NUM = "change_coin_num";
	public static final String BUNDLE_KEY_CHANGE_ALREADY_NUM = "already_num";
	public static final String BUNDLE_KEY_CHANGE_REAL_COIN_NUM = "real_coin_num";

	public static final String BUNDLE_KEY_RECEIVED_BYTE_BUFFER = "byte_buffer";
	public static final String BUNDLE_KEY_RECEIVED_BYTE_SIZE = "byte_size";
	public static final String BUNDLE_KEY_RECEIVED_PAPER_MONEY = "paper_money";
	public static final String BUNDLE_KEY_RECEIVED_PAPER_AMOUNT = "paper_amount";
	public static final String BUNDLE_KEY_RECEIVED_COIN_STAUTS = "coin_status";
	public static final String BUNDLE_KEY_MOTOR_STATUS_SUC_LIST = "motor_status_suc_list";

	// broadcast
	public static final String ACTION_INIT_GPRS = "com.curry.vendingmachine.INIT_GPRS";
	public static final String ACTION_INIT_GPRS_SUC = "com.curry.vendingmachine.INIT_GPRS_SUC";
	public static final String ACTION_KEYBOARD_RECEIVED = "com.curry.vendingmachine.KEYBOARD_RECEIVED";
	public static final String ACTION_PAPER_MONEY_RECEIVED = "com.curry.vendingmachine.PAPER_RECEIVED";
	public static final String ACTION_DELIVERY_GOODS = "com.curry.vendingmachine.DELIVERY_GOODS";
	public static final String ACTION_COIN_QUERY_STATUS = "com.curry.vendingmachine.COIN_QUERY_STATUS";
	public static final String ACTION_RETURN_CHANGE_START = "com.curry.vendingmachine.RETURN_CHANGE_START";
	public static final String ACTION_RETURN_CHANGE_FINISH = "com.curry.vendingmachine.RETURN_CHANGE_FINISH";
	public static final String ACTION_RETURN_CHANGE_FAILED = "com.curry.vendingmachine.RETURN_CHANGE_FAILED";
	public static final String ACTION_RETURN_CHANGE_SINGLE = "com.curry.vendingmachine.RETURN_CHANGE_SINGLE";
	public static final String ACTION_RETURN_CHANGE_REAL_COIN_NUM = "com.curry.vendingmachine.RETURN_CHANGE_REAL_COIN_NUM";
	public static final String ACTION_DELIVER_SUC = "com.curry.vendingmachine.DELIVER_SUC";
	public static final String ACTION_DELIVER_FAIL = "com.curry.vendingmachine.DELIVER_FAIL";
	public static final String ACTION_PAPER_AMOUNT = "com.curry.vendingmachine.PAPER_AMOUNT";
	public static final String ACTION_PAPER_VALIDATOR_ERROR = "com.curry.vendingmachine.PAPER_VALIDATOR_ERROR";
	public static final String ACTION_COIN_STATUS_RECEIVED = "com.curry.vendingmachine.COIN_STATUS_RECEIVED";
	// public static final String ACTION_DELIVER_ALL_PATH =
	// "com.curry.vendingmachine.DELIVER_ALL_PATH";
	// public static final String ACTION_DELIVER_LAYER_PATH =
	// "com.curry.vendingmachine.DELIVER_LAYER_PATH";
	// public static final String ACTION_DELIVER_ONE_PATH =
	// "com.curry.vendingmachine.DELIVER_ONE_PATH";

	// prefen
	public static final String PREFERENCE_KEY_IS_HTTP_ACT = "pre_is_http_act";
	public static final String PREFERENCE_KEY_PAPER_VALUE = "pre_paper_value";
	public static final String PREFERENCE_KEY_SERVER_IP = "pre_server_ip";
	public static final String PREFERENCE_KEY_SERVER_PORT = "pre_server_port";
	public static final String PREFERENCE_KEY_EQUIP_ID = "pre_equip_id";
	public static final String PREFERENCE_KEY_LOCK_AND_MOTOR_SUC_LIST = "lock_and_motor_suc_list";
	public static final String PREFERENCE_KEY_IS_QR_CODE_ENABLE = "is_qr_code_enable";
	public static final String PREFERENCE_KEY_QR_CODE_DISCOUNT = "qr_code_discount";
	public static final String PREFERENCE_KEY_EQUIP_STATUS = "equip_status";
	public static final String PREFERENCE_KEY_SALE_TOTAL = "sale_total";

	public static String[] DISCOUNT_ARR = { "0.10", "0.20", "0.30", "0.40",
			"0.50", "0.60", "0.70", "0.80", "0.90", "1.00" };// 默认1.00

}
