package com.thtfit.pos.iChart;

/**
 * 自定义类，用于存储图中每个对象的名称，值和颜色
 * @author SQ
 *
 */
public class Item {
	private String name; //名称
	private double value; //单一数据类型的值
	private double[] values; //多值数据类型的值
	private String color; //颜色
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public double[] getValues() {
		return values;
	}
	public void setValues(double[] values) {
		this.values = values;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

}
