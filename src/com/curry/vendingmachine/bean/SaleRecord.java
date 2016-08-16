package com.curry.vendingmachine.bean;

public class SaleRecord {
	private String id;// 货号
	private String price;// 价格
	private String time;// 出售时间

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
