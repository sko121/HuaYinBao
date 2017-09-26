package com.thtfit.pos.iChart.parameter;

import java.util.Vector;

import android.webkit.JavascriptInterface;

import com.thtfit.pos.iChart.Item;

/**
 * 图表
 * @author SQ
 *
 */
public class Chart {
	private String data;
	
	private int width; //图表宽度
	private int height; //图表高度
	
	//标题
	private String title; //图表主标题配置项
	private String title_algin; //标题的对齐方式,可选项有：'left'、'center'、'right'
	private String subtitle; //副标题配置项
	
	//字体样式
	private String font = "Verdana"; //定义文本的字体系列，默认为'Verdana'
	private int fontsize = 12; //字体大小,默认为12px
	private String fontweight = "normal"; //字体粗细值，默认为'normal'
	
	/**
	 * 图表
	 */
	public Chart(String title, String data) {
		super();
		this.title = title;
		this.data = data;
	}

	/**
	 * 图表
	 * @param width 宽度
	 * @param height 高度
	 * @param title 标题
	 */
	public Chart(int width, int height, String title, String data) {
		super();
		this.width = width;
		this.height = height;
		this.title = title;
		this.data = data;
	}
	@JavascriptInterface
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	@JavascriptInterface
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@JavascriptInterface
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	@JavascriptInterface
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	@JavascriptInterface
	public String getTitle_algin() {
		return title_algin;
	}

	public void setTitle_algin(String title_algin) {
		this.title_algin = title_algin;
	}
	@JavascriptInterface
	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	@JavascriptInterface
	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}
	@JavascriptInterface
	public int getFontsize() {
		return fontsize;
	}

	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}
	@JavascriptInterface
	public String getFontweight() {
		return fontweight;
	}

	public void setFontweight(String fontweight) {
		this.fontweight = fontweight;
	}

	
}
