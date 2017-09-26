package com.thtfit.pos.model;

import java.io.Serializable;

public class Stock implements Serializable {
	
	/**
	 * 库存出入库记录
	 * 
	 */
	private static final long serialVersionUID = -6597851091438774376L;
	
	private int proId;
	private String time;
	private String in;
	private String out;
	private String stock;

	

	@Override
	public String toString() {
		return "Stock [proId=" + proId + ", time=" + time + ", in=" + in
				+ ", out=" + out + ", stock=" + stock + "]";
	}

	public int getProId() {
		return proId;
	}

	public void setProId(int proId) {
		this.proId = proId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getIn() {
		return in;
	}

	public void setIn(String in) {
		this.in = in;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

}
