package com.thtfit.pos.iChart.chart;

import com.thtfit.pos.iChart.parameter.Chart;

/**
 * 3D饼状图
 * @author SQ
 *
 */
public class Pie3D extends Chart{
	private double radius; //饼图的半径，默认是由图表计算出的
	private int zRotate = 45; //Z轴的旋转角度值，默认为45，有效值（0-90）
	private int yHeight = 30; //厚度，默认值为30
	
	/**
	 * 3D饼状图
	 * @param title 3D饼状图标题
	 * @param data 3D饼状图所需的单一数据源,是打包好的JSON格式数据
	 */
	public Pie3D(String title, String data) {
		super(title, data);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 3D饼状图
	 * @param width 图表宽度
	 * @param height 图表高度
	 * @param title 3D饼状图标题
	 * @param data 3D饼状图所需的单一数据源,是打包好的JSON格式数据
	 */
	public Pie3D(int width, int height, String title, String data) {
		super(width, height, title, data);
		//TODO
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public int getzRotate() {
		return zRotate;
	}

	public void setzRotate(int zRotate) {
		this.zRotate = zRotate;
	}

	public int getyHeight() {
		return yHeight;
	}

	public void setyHeight(int zHeight) {
		this.yHeight = zHeight;
	}


	
	
}
