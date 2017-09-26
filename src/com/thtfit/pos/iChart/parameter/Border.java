package com.thtfit.pos.iChart.parameter;

/**
 * 图表的边框
 * @author SQ
 *
 */
public class Border {
	private boolean enable = false; //是否开启边框特性
	private String color = "#BCBCBC"; //边框颜色,默认为'#BCBCBC'
	private int width = 1; //四边边框宽度相同时使用，默认为1px
	private int[] widths = {1, 1, 1, 1}; //分别定义的四边的边框宽度，默认全为1px
	private int radius = 5; //边框圆角值，默认为5
	
	/**
	 * 图表的边框
	 * @param enable 是否开启边框特性
	 */
	public Border(boolean enable) {
		super();
		this.enable = enable;
	}
	
	/**
	 * 图表的边框
	 * @param enable 是否开启边框特性
	 * @param color 边框颜色
	 * @param width 边框宽度
	 * @param radius 边框圆角值
	 */
	public Border(boolean enable, String color, int width, int radius) {
		super();
		this.enable = enable;
		this.color = color;
		this.width = width;
		this.radius = radius;
	}
	
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	
	
}
