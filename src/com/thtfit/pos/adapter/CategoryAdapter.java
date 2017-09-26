package com.thtfit.pos.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thtfit.pos.R;

public class CategoryAdapter extends BaseAdapter {
	public final static String DEBUG_TAG = "CategoryAdapter";

	private Context context;
	private List<String> list;
	private LayoutInflater mInflater;

	public CategoryAdapter(Context c) {
		super();
		this.context = c;
	}

	public void setList(List<String> list) {
		this.list = list;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public List<String> getList() {
		return list;
	}

	public int getCount() {
		if (null != list)
			return list.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int index) {
		if (null != list)
			return list.get(index);
		else
			return null;
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	static class ViewHolder {
		TextView text;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {/*
			convertView = mInflater.inflate(R.layout.textview, parent, false);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.item_text);
			convertView.setTag(holder);
		*/} else {
			holder = (ViewHolder) convertView.getTag();
		}
//		holder.text.setText(list.get(position));
//		holder.text.setTextColor(Color.WHITE);

		return convertView;
	}

}