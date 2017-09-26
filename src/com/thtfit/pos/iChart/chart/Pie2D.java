package com.thtfit.pos.iChart.chart;

import com.thtfit.pos.iChart.parameter.Chart;

/**
 * 2D饼状图
 * @author SQ
 *
 */
public class Pie2D extends Chart{
	private int offsetAngle = 0; //第一个扇形的起始角度，默认为0，即三点钟方向。例-90为12点钟方向
	private double radius; //饼图的半径，单位为px
	
	/**
	 * 2D饼状图
	 * @param title 2D饼状图标题
	 * @param data 2D饼状图所需的单一数据源,是打包好的JSON格式数据
	 */
	public Pie2D(String title, String data) {
		super(title, data);
		// TODO Auto-generated constructor stub
	}

	public int getOffsetAngle() {
		return offsetAngle;
	}

	public void setOffsetAngle(int offsetAngle) {
		this.offsetAngle = offsetAngle;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	
}
