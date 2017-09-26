package com.thtfit.pos.model;

import java.io.Serializable;

public class Transaction implements Serializable {
	/**
	 * 交易记录
	 */
	private static final long serialVersionUID = -6449582559960815761L;
	
	private String orderNumber; //交易单号
	private String totalPrice; //交易总价
	private String listInfo; //交易商品列表
	private String clerk; //操作交易店员
	private String cardInfo; //刷卡信息
	private String time;//交易时间
	
	
	public Transaction() {
		super();
	}
	
	
	@Override
	public String toString() {
		return "Transaction [orderNumber=" + orderNumber + ", totalPrice="
				+ totalPrice + ", listInfo=" + listInfo + ", clerk=" + clerk
				+ ", cardInfo=" + cardInfo + ", time=" + time + "]";
	}


	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getListInfo() {
		return listInfo;
	}
	public void setListInfo(String listInfo) {
		this.listInfo = listInfo;
	}
	public String getClerk() {
		return clerk;
	}
	public void setClerk(String clerk) {
		this.clerk = clerk;
	}
	public String getCardInfo() {
		return cardInfo;
	}
	public void setCardInfo(String cardInfo) {
		this.cardInfo = cardInfo;
	}
	
}
