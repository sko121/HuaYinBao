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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.github.mikephil.charting.utils.YLabels;
import com.github.mikephil.charting.utils.YLabels.YLabelPosition;
import com.thtfit.pos.R;
import com.thtfit.pos.util.widget.MyValueFormatter;

public class ReportTotalSalesFragment extends Fragment implements
		OnSeekBarChangeListener, OnChartValueSelectedListener {
	private View mView;
	private BarChart mChart;
	
    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected String[] mParties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };
    

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (mView == null) {
		mView = inflater.inflate(R.layout.fragment_total_sales_chart, container,
				false);
		}
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mChart = (BarChart) getView().findViewById(R.id.chart1);
		mChart.setOnChartValueSelectedListener(this);


		// enable the drawing of values
		mChart.setDrawYValues(true);

		mChart.setDescription("");

		// if more than 60 entries are displayed in the chart, no values will be
		// drawn
		mChart.setMaxVisibleValueCount(60);

		MyValueFormatter customFormatter = new MyValueFormatter();

		// set a custom formatter for the values inside the chart
		mChart.setValueFormatter(customFormatter);

		// if false values are only drawn for the stack sum, else each value is
		// drawn
		mChart.setDrawValuesForWholeStack(true);

		// disable 3D
		mChart.set3DEnabled(false);
		// scaling can now only be done on x- and y-axis separately
		mChart.setPinchZoom(false);

		mChart.setDrawBarShadow(false);

		// change the position of the y-labels
		YLabels yLabels = mChart.getYLabels();
		yLabels.setPosition(YLabelPosition.BOTH_SIDED);
		yLabels.setLabelCount(5);
		yLabels.setFormatter(customFormatter);

		XLabels xLabels = mChart.getXLabels();
		xLabels.setPosition(XLabelPosition.TOP);
		xLabels.setCenterXLabelText(true);

		// mChart.setDrawXLabels(false);
		// mChart.setDrawYLabels(false);

		setData();

		Legend l = mChart.getLegend();
		l.setPosition(LegendPosition.BELOW_CHART_RIGHT);
		l.setFormSize(8f);
		l.setFormToTextSpace(4f);
		l.setXEntrySpace(6f);
		
		

	}
	
	public void setData(){


        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < 12; i++) {
            xVals.add(mMonths[i % mMonths.length]);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < 12; i++) {
            float mult = (12);
            float val1 = (float) (Math.random() * mult) + mult / 3;
            float val2 = (float) (Math.random() * mult) + mult / 3;
            float val3 = (float) (Math.random() * mult) + mult / 3;

            yVals1.add(new BarEntry(new float[] {
                     val1, val2, val3
            }, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "Statistics Vienna 2014");
        set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set1.setStackLabels(new String[] {
                "Births", "Divorces", "Marriages"
        });

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

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
