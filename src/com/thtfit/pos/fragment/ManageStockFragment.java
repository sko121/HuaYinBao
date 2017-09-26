package com.thtfit.pos.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.adapter.StockPagerAdapter;
import com.thtfit.pos.conn.hql.DBContror;
import com.thtfit.pos.model.ItemType;
import com.thtfit.pos.model.Product;

public class ManageStockFragment extends Fragment {
	private View mView;

	private EditText searchEditText;
	private Button searchButton;
	private ListView searchListView;
	private StockPagerAdapter adapter;

	private String searchName = null;
	private Spinner spinner_type;

	private DBContror dbcon;
	private List<Product> myList;
	private List<ItemType> typeList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_manage_stock, container,
				false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView() {

		spinner_type = (Spinner) mView.findViewById(R.id.manage_type);
		searchEditText = (EditText) mView
				.findViewById(R.id.search_editText_input);
		searchButton = (Button) mView.findViewById(R.id.search_button);
		searchListView = (ListView) mView.findViewById(R.id.search_listView);

		ArrayAdapter<ItemType> spinnerAdapter = new ArrayAdapter<ItemType>(
				getActivity(), android.R.layout.simple_spinner_item,
				getData());
		spinnerAdapter.setDropDownViewResource(R.layout.drop_down_item);

		spinner_type.setAdapter(spinnerAdapter);

		// 添加事件Spinner事件监听
		spinner_type.setOnItemSelectedListener(new SpinnerSelectedListener());

		searchButton.setOnClickListener(new ButtonClickListener());
	}

	class ButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			searchName = searchEditText.getText().toString().trim();
			if ("".equals(searchName)) {
				showMSG((String) getActivity().getResources().getText(R.string.search_content_is_not_empty));
				return;
			}
			dbcon = new DBContror(getActivity());
			dealData(dbcon.queryAllItemByName(searchName));
		}
	}

	// 使用数组形式操作
	class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			dbcon = new DBContror(getActivity());
			dealData(dbcon
					.queryAllItemByType(((ItemType) spinner_type.getSelectedItem()).getId()+ ""));
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	private List<ItemType> getData() {
		// 数据源
		typeList = new ArrayList<ItemType>();
		dbcon = new DBContror(getActivity());
		typeList = dbcon.queryAllType();
		return typeList;
	}

	private void dealData(List<Product> products) {
		myList = new ArrayList<Product>();
		myList.addAll(products);
		try {
			if (myList.size() != 0) {
				adapter = new StockPagerAdapter(myList, getActivity());// 自定义适配器
				searchListView.setAdapter(adapter);

				searchListView
						.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parent,
									View v, int position, long id) {
								
								
								
								showMSG("点击了" + myList.get(position).getStock());
							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showMSG(String content) {
		Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
	}
}
