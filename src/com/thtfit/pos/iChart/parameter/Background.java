package com.thtfit.pos.iChart.parameter;

/**
 * 背景色和渐变效果
 * @author SQ
 *
 */
public class Background {
	private String background_color = "#FEFEFE"; //背景色,默认为'#FEFEFE'
	private boolean gradient = false; //是否开启渐变，默认false
	private double color_factor = 0.15; //颜色渐变因子，取值越大渐变效果越大，颜色变化时取于背景颜色的，取值范围0.01-0.5，默认值0.15
	
	/**
	 * 背景色
	 * @param background_color 背景颜色
	 */
	public Background(String background_color) {
		super();
		this.background_color = background_color;
	}
	
	/**
	 * 背景色
	 * @param background_color 背景颜色
	 * @param gradient 是否开启渐变，默认false
	 * @param color_factor 颜色渐变因子，取值越大渐变效果越大，颜色变化时取于背景颜色的，取值范围0.01-0.5，默认值0.15
	 */
	public Background(String background_color, boolean gradient,
			double color_factor) {
		super();
		this.background_color = background_color;
		this.gradient = gradient;
		this.color_factor = color_factor;
	}
	
	public String getBackground_color() {
		return background_color;
	}
	public void setBackground_color(String background_color) {
		this.background_color = background_color;
	}
	public boolean isGradient() {
		return gradient;
	}
	public void setGradient(boolean gradient) {
		this.gradient = gradient;
	}
	public double getColor_factor() {
		return color_factor;
	}
	public void setColor_factor(double color_factor) {
		this.color_factor = color_factor;
	}
	
	
}
