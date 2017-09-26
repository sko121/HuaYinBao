package com.thtfit.pos.model;

import java.io.Serializable;

public class Product implements Serializable{

	/**
	 * 商品类的实体化
	 */
	private static final long serialVersionUID = -230841856084384812L;
	
	private int proId;//产品号
	private int serial;//序列号
	private String name;//产品名称	
	private String price;//价格
	private String describe;//简介
	private String note;//备注
	private String imagePath;//图片存储路径
	private String number;//购物车产品数量
	private Integer type;//产品类别
	private String stock;//产品库存
	
	

	public Product() {
		super();
	}

	public Product(int proId, int serial, String name, String price, String describe,
			String note, String imagePath, String number,Integer type,String stock) {
		super();
		this.proId = proId;
		this.serial = serial;
		this.name = name;
		this.price = price;
		this.describe = describe;
		this.note = note;
		this.imagePath = imagePath;
		this.number = number;
		this.type = type;
		this.stock = stock;
	}

	
	@Override
	public String toString() {
		return "Product [proId=" + proId +", serial=" + serial + ", name=" + name + ", price="
				+ price + ", describe=" + describe + ", note=" + note
				+ ", imagePath=" + imagePath + ", number=" + number + ", type="
				+ type + ", stock=" + stock + "]";
	}

	public int getProId() {
		return proId;
	}
	
	public void setProId(int proId) {
		this.proId = proId;
	}
	
	public int getSerial() {
		return serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}


	public String getDescribe() {
		return describe;
	}


	public void setDescribe(String describe) {
		this.describe = describe;
	}


	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	
	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
