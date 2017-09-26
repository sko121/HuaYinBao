package com.thtfit.pos.iChart.parameter;

/**
 * 坐标轴线
 * @author SQ
 *
 */
public class Coordinate {
	private boolean enable; //是否设置其值
	private int[] width = new int[4]; // 轴四边的宽度(四个值一次代表上-右-下-左)
	
	//值轴scale
	private String position; //刻度的位置 （left/right/top/bottom）
	private double start_scale; //起始刻度值
	private double end_scale; //结束刻度值
	private double scale_space; //刻度间距值
	private boolean scale2grid; //是否按照刻度画网格参考线
	private boolean scale_enable; //是否显示刻度线
	private int scale_size; //刻度线的大小（线条宽度）
	private int scale_width; //刻度线的长度
	private String scale_color; //刻度线颜色
	private String scaleAlign; //刻度线对齐方式
	private String labels; //自定义的刻度文本，一般作为非值轴的轴文本的设置
	
	//有效坐标区域
	private int valid_width; //有效的坐标区域宽度，必须要小于坐标宽度
	private int valid_height; //有效的坐标区域高度，必须要小于坐标高度
	
	//网格grids：水平网格配置项horizontal、垂直网格配置项vertical
	private String way; //网格设置方式，可选项：share_alike和given_value
	private int value; //根据属性way值的不同有着不同的意义，当way取值share_alike时表示网格间隙的数量。间隙的数量=网格线数量-1。当 way取值given_value时表示网格间隙的距离。间隙的数量=坐标轴宽度/网格间隙的距离。
	
	private boolean alternate_color; //是否隔行换色

	/**
	 * 坐标轴线
	 * @param enable 是否设置坐标轴线的值
	 */
	public Coordinate(boolean enable) {
		super();
		this.enable = enable;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public int[] getWidth() {
		return width;
	}

	public void setWidth(int[] width) {
		this.width = width;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public double getStart_scale() {
		return start_scale;
	}

	public void setStart_scale(double start_scale) {
		this.start_scale = start_scale;
	}

	public double getEnd_scale() {
		return end_scale;
	}

	public void setEnd_scale(double end_scale) {
		this.end_scale = end_scale;
	}

	public double getScale_space() {
		return scale_space;
	}

	public void setScale_space(double scale_space) {
		this.scale_space = scale_space;
	}

	public boolean isScale2grid() {
		return scale2grid;
	}

	public void setScale2grid(boolean scale2grid) {
		this.scale2grid = scale2grid;
	}

	public boolean isScale_enable() {
		return scale_enable;
	}

	public void setScale_enable(boolean scale_enable) {
		this.scale_enable = scale_enable;
	}

	public int getScale_size() {
		return scale_size;
	}

	public void setScale_size(int scale_size) {
		this.scale_size = scale_size;
	}

	public int getScale_width() {
		return scale_width;
	}

	public void setScale_width(int scale_width) {
		this.scale_width = scale_width;
	}

	public String getScale_color() {
		return scale_color;
	}

	public void setScale_color(String scale_color) {
		this.scale_color = scale_color;
	}

	public String getScaleAlign() {
		return scaleAlign;
	}

	public void setScaleAlign(String scaleAlign) {
		this.scaleAlign = scaleAlign;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public int getValid_width() {
		return valid_width;
	}

	public void setValid_width(int valid_width) {
		this.valid_width = valid_width;
	}

	public int getValid_height() {
		return valid_height;
	}

	public void setValid_height(int valid_height) {
		this.valid_height = valid_height;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isAlternate_color() {
		return alternate_color;
	}

	public void setAlternate_color(boolean alternate_color) {
		this.alternate_color = alternate_color;
	}
	
	
	
}
