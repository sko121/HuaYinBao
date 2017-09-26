package com.thtfit.pos.iChart.chart;

import android.webkit.JavascriptInterface;

import com.thtfit.pos.iChart.parameter.Chart;

/**
 * 2D柱形图
 * @author SQ
 *
 */
public class Column2D extends Chart{
	private int colwidth; //单个柱形的宽度，默认会根据坐标系的宽度来自动计算
	
	/**
	 * 2D柱形图
	 * @param title 2D柱形图标题
	 * @param data 2D柱形图所需的单一数据源,是打包好的JSON格式数据
	 */
	public Column2D(String title, String data) {
		super(title, data);
		// TODO Auto-generated constructor stub
	}
	public Column2D(int width, int height, String title, String data){
		super(width,height,title,data);
	}
	@JavascriptInterface
	public int getColwidth() {
		return colwidth;
	}

	@JavascriptInterface
	public void setColwidth(int colwidth) {
		this.colwidth = colwidth;
	}
}
