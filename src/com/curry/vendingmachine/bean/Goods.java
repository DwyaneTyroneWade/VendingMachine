package com.curry.vendingmachine.bean;

public class Goods implements Comparable<Goods> {
	private String id;// 货号
	private String capacity;// 容量
	private String stockNum;// 库存
	private String price;// 价格
//	private int status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public String getStockNum() {
		return stockNum;
	}

	public void setStockNum(String stockNum) {
		this.stockNum = stockNum;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	@Override
	public int compareTo(Goods another) {
		// TODO Auto-generated method stub
		return Integer.parseInt(getId()) - Integer.parseInt(another.getId());
	}

}
