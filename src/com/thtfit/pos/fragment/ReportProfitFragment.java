package com.thtfit.pos.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.thtfit.pos.R;

public class ReportProfitFragment extends Fragment implements
		OnSeekBarChangeListener, OnChartValueSelectedListener {
	private View mView;
	private LineChart mChart;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.fragment_profit_chart, null,
					false);
		}
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mChart = (LineChart) mView.findViewById(R.id.chart1);
		mChart.setOnChartValueSelectedListener(this);

		mChart.setDrawGridBackground(false);

		// mChart.setStartAtZero(true);

		// disable the drawing of values into the chart
		mChart.setDrawYValues(false);

		// enable value highlighting
		mChart.setHighlightEnabled(true);

		// enable touch gestures
		mChart.setTouchEnabled(true);

		// enable scaling and dragging
		mChart.setDragEnabled(true);
		mChart.setScaleEnabled(true);

		// if disabled, scaling can be done on x- and y-axis separately
		mChart.setPinchZoom(false);

		setData();

		Legend l = mChart.getLegend();
		l.setPosition(LegendPosition.RIGHT_OF_CHART);
	}

	public void setData() {

		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < 12; i++) {
			xVals.add((i) + "");
		}

		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();

		for (int z = 0; z < 3; z++) {

			ArrayList<Entry> values = new ArrayList<Entry>();

			for (int i = 0; i < 12; i++) {
				double val = (Math.random() * 12) + 3;
				values.add(new Entry((float) val, i));
			}

			LineDataSet d = new LineDataSet(values, "DataSet " + (z + 1));
			d.setLineWidth(2.5f);
			d.setCircleSize(4f);

			int color = mColors[z % mColors.length];
			d.setColor(color);
			d.setCircleColor(color);
			dataSets.add(d);
		}

		// make the first DataSet dashed
		dataSets.get(0).enableDashedLine(10, 10, 0);
		dataSets.get(0).setColors(ColorTemplate.VORDIPLOM_COLORS);
		dataSets.get(0).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);

		LineData data = new LineData(xVals, dataSets);
		mChart.setData(data);
		mChart.invalidate();
	}

	private int[] mColors = new int[] {
			ColorTemplate.VORDIPLOM_COLORS[0],
			ColorTemplate.VORDIPLOM_COLORS[1],
			ColorTemplate.VORDIPLOM_COLORS[2] };

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
