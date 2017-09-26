package com.thtfit.pos.iChart.parameter;

import android.webkit.JavascriptInterface;

/**
 * 图例功能，围绕在绘图区周围
 * @author SQ
 *
 */
public class Legend {
	private boolean enable; //是否开启图例功能
	//center和middle组合无效
	private String align; //水平方向的对齐方式(left/center/right)
	private String valign; //垂直方向的对齐方式(top/middle/bottom)
	
	//行列布局：若row=max，column=1，则图例为一列排下来；若row=1，column=max，则图例为一行排开
	private int row; //设定的行数
	private int column; //设定的列数
	
	private int line_height; //图例的行高
	
	//图例符号
	private String sign; //符号的形状
	private int sign_size; //符号的大小
	private int sign_space; //符号与文字的间距
	
	private boolean text_with_sign_color = false; //标识文字是否与符号颜色一致

	/**
	 * 图例功能
	 * @param enable 是否开启图例 
	 * @param align 水平方向的对齐方式(left/center/right)
	 * @param valign 垂直方向的对齐方式(top/middle/bottom)
	 */
	public Legend(boolean enable, String align, String valign) {
		super();
		this.enable = enable;
		this.align = align;
		this.valign = valign;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	@JavascriptInterface
	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}
	@JavascriptInterface
	public String getValign() {
		return valign;
	}

	public void setValign(String valign) {
		this.valign = valign;
	}
	@JavascriptInterface
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
	@JavascriptInterface
	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
	@JavascriptInterface
	public int getLine_height() {
		return line_height;
	}

	public void setLine_height(int line_height) {
		this.line_height = line_height;
	}
	@JavascriptInterface
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	@JavascriptInterface
	public int getSign_size() {
		return sign_size;
	}

	public void setSign_size(int sign_size) {
		this.sign_size = sign_size;
	}
	@JavascriptInterface
	public int getSign_space() {
		return sign_space;
	}

	public void setSign_space(int sign_space) {
		this.sign_space = sign_space;
	}

	public boolean isText_with_sign_color() {
		return text_with_sign_color;
	}

	public void setText_with_sign_color(boolean text_with_sign_color) {
		this.text_with_sign_color = text_with_sign_color;
	}
	
	
}
