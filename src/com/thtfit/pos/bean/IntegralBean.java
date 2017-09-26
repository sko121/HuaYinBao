package com.thtfit.pos.bean;

public class IntegralBean {

	private int id;
	private int integral;
	
	public IntegralBean() {
	}
	
	public IntegralBean(int id, int integral) {
		this.id = id;
		this.integral = integral;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIntegral() {
		return integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}
	
	@Override
	public String toString() {
		return id + "\t\t\t" + integral + "\n";
	}
}
