package com.curry.vendingmachine.bean;

import com.curry.vendingmachine.serializable.Key;

public class QRcodeBean extends BaseResultBean {
	@Key(key = "ali_code")
	public String alipayCode;
	@Key(key = "wx_code")
	public String wechatCode;
	@Key(key = "ali_orderno")
	public String alipayOrderNo;
	@Key(key = "wx_orderno")
	public String weChatOrderNo;
}
