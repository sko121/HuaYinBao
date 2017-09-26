package com.thtfit.pos.iChart.parameter;

/**
 * 图表的过渡动画效果
 * @author SQ
 *
 */
public class Animation {
	private boolean animation; //是否开启过渡动画效果
	private int duration_animation_duration; //过渡动画过程所用的时间，单位毫秒
	
	/**
	 * 图表的过渡动画效果
	 * @param animation 是否开启过渡动画效果
	 * @param duration_animation_duration 过渡动画过程所用的时间，单位毫秒
	 */
	public Animation(boolean animation, int duration_animation_duration) {
		super();
		this.animation = animation;
		this.duration_animation_duration = duration_animation_duration;
	}

	public boolean isAnimation() {
		return animation;
	}

	public void setAnimation(boolean animation) {
		this.animation = animation;
	}

	public int getDuration_animation_duration() {
		return duration_animation_duration;
	}

	public void setDuration_animation_duration(int duration_animation_duration) {
		this.duration_animation_duration = duration_animation_duration;
	}
	
	
}
