package com.thtfit.pos.iChart.parameter;

/**
 * 图表的阴影效果
 * @author SQ
 *
 */
public class Shadow {
	private boolean shadow = false; //是否启用阴影效果,默认为false
	private int shadow_blur = 4; //阴影效果的模糊值，默认为4px
	private String shadow_color = "#666666"; //阴影颜色,默认为'#666666'
	private int shadow_offsetx = 0; //阴影x轴偏移量，正数向右偏移，负数向左偏移,默认为0px
	private int shadow_offsety = 0; //阴影y轴偏移量，正数向下偏移，负数向上偏移,默认为0px
	
	/**
	 * 图表的阴影效果
	 * @param shadow 是否开启阴影效果
	 */
	public Shadow(boolean shadow) {
		super();
		this.shadow = shadow;
	}

	public boolean isShadow() {
		return shadow;
	}

	public void setShadow(boolean shadow) {
		this.shadow = shadow;
	}

	public int getShadow_blur() {
		return shadow_blur;
	}

	public void setShadow_blur(int shadow_blur) {
		this.shadow_blur = shadow_blur;
	}

	public String getShadow_color() {
		return shadow_color;
	}

	public void setShadow_color(String shadow_color) {
		this.shadow_color = shadow_color;
	}

	public int getShadow_offsetx() {
		return shadow_offsetx;
	}

	public void setShadow_offsetx(int shadow_offsetx) {
		this.shadow_offsetx = shadow_offsetx;
	}

	public int getShadow_offsety() {
		return shadow_offsety;
	}

	public void setShadow_offsety(int shadow_offsety) {
		this.shadow_offsety = shadow_offsety;
	}
	
	
}
