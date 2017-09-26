package com.thtfit.pos.util.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.GridView;

public class MainGridView extends GridView implements OnScrollChangedListener {

	public MainGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MainGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MainGridView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec;
		if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
			expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
					MeasureSpec.AT_MOST);
		} else {
			expandSpec = heightMeasureSpec;
		}
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	public void onScrollChanged() {

	}

}
