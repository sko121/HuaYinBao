package com.thtfit.pos.iChart.chart;

import android.webkit.JavascriptInterface;

import com.thtfit.pos.iChart.parameter.Chart;

public class Donut2D extends Chart
{
	private double radius;
	
	public Donut2D(String title, String data){
		super(title, data);
	}
	public Donut2D(int width, int height, String title, String data){
		super(width,height,title,data);
	}
	@JavascriptInterface
	public void setRadius(double radius){
		this.radius = radius;
	}
	@JavascriptInterface
	public double getRadius(){
		return this.radius;
	}
}
