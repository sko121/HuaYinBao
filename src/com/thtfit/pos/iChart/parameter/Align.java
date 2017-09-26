package com.thtfit.pos.iChart.parameter;

/**
 * 图表的内边距、对齐方式和偏移
 * @author SQ
 *
 */
public class Align {
	private int padding = 10; //图表内边距，默认为10px
	private String align = "center"; //图表对齐方式（left、center、right），默认为'center'
	private int offsetx = 0; //主图的x轴偏移量，正数向右偏移，负数向左偏移
	private int offsety = 0; //主图的y轴偏移量，正数向下偏移，负数向上偏移
	
	/**
	 * 图表的内边距、对齐方式和偏移
	 */
	public Align() {
		super();
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public int getOffsetx() {
		return offsetx;
	}

	public void setOffsetx(int offsetx) {
		this.offsetx = offsetx;
	}

	public int getOffsety() {
		return offsety;
	}

	public void setOffsety(int offsety) {
		this.offsety = offsety;
	}
	
	
}
