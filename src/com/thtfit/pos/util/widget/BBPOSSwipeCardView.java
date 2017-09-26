package com.thtfit.pos.util.widget;

import com.thtfit.pos.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class BBPOSSwipeCardView extends LinearLayout{
	
	private Context context;
	private LayoutInflater inflate;
	private ViewGroup parent;
	
	public BBPOSSwipeCardView(LayoutInflater inflater, Context context, ViewGroup parent, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.parent = parent;
		init();
	}

	public BBPOSSwipeCardView(LayoutInflater inflater, Context context, ViewGroup parent) {
		super(context);
		this.context = context;
		this.inflate = inflater;
		this.parent = parent;
		init();
	}
	
	private void init() {
		LayoutInflater.from(context).inflate(R.layout.view_list_bbpos_checkout, null);
	}
}
