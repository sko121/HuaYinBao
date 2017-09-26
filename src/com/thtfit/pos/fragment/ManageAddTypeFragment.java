package com.thtfit.pos.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.conn.hql.DBContror;
import com.thtfit.pos.conn.hql.DBHelper;

public class ManageAddTypeFragment extends Fragment implements OnClickListener {
	private View mView;
	private EditText editText_typeName;
	private Button button_submit;

	private String typeName;

	private DBHelper dBHelper = null;
	private SQLiteDatabase sqlitedb = null;
	private DBContror dbcon;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_manage_addtype, container,
				false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupView();
	}

	private void setupView() {

		// 实例化数据库
		dBHelper = new DBHelper(getActivity());
		// 创建一个可读写的数据库
		sqlitedb = dBHelper.getWritableDatabase();
		editText_typeName = (EditText) mView.findViewById(R.id.manage_typeName);

		button_submit = (Button) mView.findViewById(R.id.manage_submit);

		button_submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.manage_submit:
			submitProductData();
			break;
		default:
			break;
		}
	}

	private void submitProductData() {
		// 判断数据是否为空
		typeName = editText_typeName.getText().toString().trim();

		if ("".equals(typeName)) {
			ShowMSG("type name is null");
			return;
		} else {
			try {
				dbcon = new DBContror(getActivity());

				ShowMSG("Storage Success！=" + dbcon.insertTypeItem(0, typeName));

				/*
				 * if(0 == dbcon.insertTypeItem(typeName)){
				 * ShowMSG("Storage Success！"); }else{
				 * ShowMSG("Storage failure,Category may repeat！"); }
				 */
			} catch (Exception e) {
				ShowMSG("Storage failure！");
				e.printStackTrace();
				return;
			} finally {
				sqlitedb.close();
			}
			return;
		}
	}

	public void onDestroy() {
		super.onDestroy();
		// 关闭数据库
		sqlitedb.close();
	}

	public void ShowMSG(CharSequence msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}
}
