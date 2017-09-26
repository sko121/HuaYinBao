package com.thtfit.pos.iChart.parameter;

/**
 * 提示框功能
 * @author SQ
 *
 */
public class Tip {
	private boolean enable = false; //是否开启提示框功能, 默认为false
	private String showType = "follow"; //提示框的两种位置模式（follow：跟随模式，提示框出现在鼠标点击的位置；fixed：固定位置），默认为follow
	private boolean animation = false; //是否开启动画效果，默认为false
	private int fade_duration = 300; //动画渐入渐出过程所用的时间，单位为毫秒，默认为300ms
	
	/**
	 * 提示框
	 * @param enable 是否开启提示框
	 * @param showType 提示框的两种位置模式(follow;fixed)
	 */
	public Tip(boolean enable, String showType) {
		super();
		this.enable = enable;
		this.showType = showType;
	}

	/**
	 * 提示框
	 * @param enable 是否开启提示框
	 * @param showType 提示框的两种位置模式（follow
	 * @param animation 是否开启动画效果
	 * @param fade_duration 动画渐入渐出过程所用的时间，单位为毫秒
	 */
	public Tip(boolean enable, String showType, boolean animation,
			int fade_duration) {
		super();
		this.enable = enable;
		this.showType = showType;
		this.animation = animation;
		this.fade_duration = fade_duration;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public boolean isAnimation() {
		return animation;
	}

	public void setAnimation(boolean animation) {
		this.animation = animation;
	}

	public int getFade_duration() {
		return fade_duration;
	}

	public void setFade_duration(int fade_duration) {
		this.fade_duration = fade_duration;
	}
	
	
}
