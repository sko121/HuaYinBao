package com.thtfit.pos.iChart.chart;

import com.thtfit.pos.iChart.parameter.Chart;

/**
 * 2D面积图
 * @author SQ
 *
 */
public class Area2D extends Chart{
	private String data_labels;
	
	private double area_opacity = 0.3; //面积图区域的透明度，默认为0.3px
	
	/**
	 * 2D面积图
	 * @param title 2D面积图标题
	 * @param data 2D面积图所需的多值数据源,是打包好的JSON格式数据
	 * @param data_labels data对应的标签项，仅多值数据源需要,是打包好的JSON格式数据
	 */
	public Area2D(String title, String data, String data_labels) {
		super(title, data);
		// TODO Auto-generated constructor stub
		this.data_labels = data_labels;
	}

	public double getArea_opacity() {
		return area_opacity;
	}

	public void setArea_opacity(double area_opacity) {
		this.area_opacity = area_opacity;
	}
}
