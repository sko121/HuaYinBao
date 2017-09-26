package com.thtfit.pos.fragment;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.LargeValueFormatter;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
import com.thtfit.pos.R;
import com.thtfit.pos.ui.MyMarkerView;

public class ReportInvoicingFragment extends Fragment implements
		OnSeekBarChangeListener, OnChartValueSelectedListener {
	private View mView;
	private BarChart mChart;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (mView == null) {
			mView = inflater.inflate(R.layout.fragment_invoicing_chart,
					container, false);
		}
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		mChart = (BarChart) mView.findViewById(R.id.chart1);
		mChart.setOnChartValueSelectedListener(this);
		mChart.setDescription("");

		// disable the drawing of values
		mChart.setDrawYValues(false);

		// scaling can now only be done on x- and y-axis separately
		mChart.setPinchZoom(false);
		mChart.setValueFormatter(new LargeValueFormatter());

		mChart.setDrawBarShadow(false);

		mChart.setDrawGridBackground(false);
		mChart.setDrawHorizontalGrid(false);
		
		setData();

		// create a custom MarkerView (extend MarkerView) and specify the layout
		// to use for it
		MyMarkerView mv = new MyMarkerView(getActivity(),
				R.layout.custom_marker_view);

		// define an offset to change the original position of the marker
		// (optional)
		// mv.setOffsets(-mv.getMeasuredWidth() / 2, -mv.getMeasuredHeight());

		// set the marker to the chart
		mChart.setMarkerView(mv);
		
		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
				"OpenSans-Regular.ttf");

		Legend l = mChart.getLegend();
		l.setPosition(LegendPosition.RIGHT_OF_CHART_INSIDE);
		l.setTypeface(tf);

		XLabels xl = mChart.getXLabels();
		xl.setCenterXLabelText(true);
		xl.setTypeface(tf);

		YLabels yl = mChart.getYLabels();
		yl.setTypeface(tf);
		yl.setFormatter(new LargeValueFormatter());

		mChart.setValueTypeface(tf);
	}

	public void setData() {

		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < 12; i++) {
			xVals.add((i + 1990) + "");
		}

		ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
		ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
		ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();

		float mult = 12 * 10000000f;

		for (int i = 0; i < 12; i++) {
			float val = (float) (Math.random() * mult) + 3;
			yVals1.add(new BarEntry(val, i));
		}

		for (int i = 0; i < 12; i++) {
			float val = (float) (Math.random() * mult) + 3;
			yVals2.add(new BarEntry(val, i));
		}

		for (int i = 0; i < 12; i++) {
			float val = (float) (Math.random() * mult) + 3;
			yVals3.add(new BarEntry(val, i));
		}

		// create 3 datasets with different types
		BarDataSet set1 = new BarDataSet(yVals1, "Company A");
		// set1.setColors(ColorTemplate.createColors(getApplicationContext(),
		// ColorTemplate.FRESH_COLORS));
		set1.setColor(Color.rgb(104, 241, 175));
		BarDataSet set2 = new BarDataSet(yVals2, "Company B");
		set2.setColor(Color.rgb(164, 228, 251));
		BarDataSet set3 = new BarDataSet(yVals3, "Company C");
		set3.setColor(Color.rgb(242, 247, 158));

		ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
		dataSets.add(set1);
		dataSets.add(set2);
		dataSets.add(set3);

		BarData data = new BarData(xVals, dataSets);

		// add space between the dataset groups in percent of bar-width
		data.setGroupSpace(110f);

		mChart.setData(data);
		mChart.invalidate();
	}

	@Override
	public void onValueSelected(Entry e, int dataSetIndex) {
		Log.i("VAL SELECTED",
				"Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
						+ ", DataSet index: " + dataSetIndex);

	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

}
