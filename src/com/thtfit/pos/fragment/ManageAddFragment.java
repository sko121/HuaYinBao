package com.thtfit.pos.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.conn.hql.DBContror;
import com.thtfit.pos.conn.hql.DBHelper;
import com.thtfit.pos.model.ItemType;
import com.thtfit.pos.model.Product;

public class ManageAddFragment extends Fragment implements OnClickListener {
	private View mView;
	private ImageButton imageButton;
	private EditText editText_serial;
	private EditText editText_name;
	private EditText editText_price;
	private EditText editText_describe;
	private EditText editText_stock;
	private Spinner spinner_type;
	private Button button_submit;

	private int productSerial = 0;
	private String productName;
	private String productPrice;
	private String productDescribe;
	private String productStock;
	private String imagePath;

	private static int RESULT_LOAD_IMAGE = 1;

	private DBHelper dBHelper = null;
	private SQLiteDatabase sqlitedb = null;
	private DBContror dbcon = null;
	
	private List<Product> serialList;
	private List<ItemType> typeList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater
				.inflate(R.layout.fragment_manage_add, container, false);
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

		imageButton = (ImageButton) mView.findViewById(R.id.manage_image);
		editText_serial = (EditText) mView.findViewById(R.id.manage_serial);
		editText_name = (EditText) mView.findViewById(R.id.manage_name);
		editText_price = (EditText) mView.findViewById(R.id.manage_price);
		editText_describe = (EditText) mView.findViewById(R.id.manage_describe);
		editText_stock = (EditText) mView.findViewById(R.id.manage_stock);
		spinner_type = (Spinner) mView.findViewById(R.id.manage_type);

		button_submit = (Button) mView.findViewById(R.id.manage_submit);

		setPricePoint(editText_price);

		ArrayAdapter<ItemType> adapter = new ArrayAdapter<ItemType>(
				getActivity(), android.R.layout.simple_spinner_item, getData());
		adapter.setDropDownViewResource(R.layout.drop_down_item);

		spinner_type.setAdapter(adapter);

		imageButton.setOnClickListener(this);
		button_submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.manage_image:

			Intent intentImage = new Intent();
			intentImage.setAction(Intent.ACTION_PICK);
			intentImage.setType("image/*");
			startActivityForResult(intentImage, RESULT_LOAD_IMAGE);
			break;
		case R.id.manage_submit:
			submitProductData();		
			break;
		default:
			break;
		}
	}

	private List<ItemType> getData() {
		// 数据源
		List<ItemType> dataList = new ArrayList<ItemType>();
		dbcon = new DBContror(getActivity());
		dataList = dbcon.queryAllType();
		return dataList;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		getActivity();
		if (requestCode == RESULT_LOAD_IMAGE
				&& resultCode == Activity.RESULT_OK && null != data) {

			Uri selectedImage = data.getData();
			imagePath = selectedImage.toString();
			imageButton.setImageURI(selectedImage);
		}
	}

	private void submitProductData() {
		
		typeList = getData();
		for(int i = 0; i < typeList.size(); i++){
			if(typeList.get(i).getName().equals(((ItemType) spinner_type.getSelectedItem()).getName())){
				System.out.println("----typeList.get(i).getName()-------->"+typeList.get(i).getName());
				serialList = dbcon.queryAllItemByType(((ItemType) spinner_type.getSelectedItem()).getId()+ "");
				System.out.println("------serialList.size()-----"+serialList.size());
				for(int j = 0; j < serialList.size(); j++){
					System.out.println("----serialList.get(j).getSerial()---------"+serialList.get(j).getSerial());
					System.out.println("-------editText_serial.getText().toString()--------------"+editText_serial.getText().toString());
					if(serialList.get(j).getSerial() == Integer.parseInt(editText_serial.getText().toString().trim())){
						ShowMSG("已经有该编号，编号是唯一的");
						return;
					}
				}
			}
		}
		
		
		
		// 判断数据是否为空
		productName = editText_name.getText().toString().trim();
		productPrice = editText_price.getText().toString().trim();
		productDescribe = editText_describe.getText().toString().trim();
		productStock = editText_stock.getText().toString().trim();
		String tmpProductSerial = editText_serial.getText().toString().trim();

		if (productName.equals("") || productPrice.equals("")
				|| productDescribe.equals("") || productStock.equals("")
				|| tmpProductSerial.equals("")) {
			ShowMSG("请将资料填写完整");
			return;
		} else if (imagePath == null || "".equals(imagePath)) {
			ShowMSG("图片为空！");
			return;
		} else {
			try {
				productSerial = Integer.parseInt(tmpProductSerial);
				// 将字节数组保存到数据库中

				Product product = new Product();
				product.setSerial(productSerial);
				product.setName(productName);
				product.setPrice(productPrice);
				product.setDescribe(productDescribe);
				product.setStock(productStock);
				product.setImagePath(imagePath);
				product.setType(((ItemType) spinner_type.getSelectedItem())
						.getId());

				dbcon = new DBContror(getActivity());
				dbcon.insertItem(product);

			} catch (Exception e) {
				ShowMSG("数据存储失败！");
				e.printStackTrace();
				return;
			} finally {
				sqlitedb.close();
			}
			getActivity().finish();
			ShowMSG("数据存储成功！");
			return;
		}

	}

	public static void setPricePoint(final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						editText.setText(s);
						editText.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					editText.setText(s);
					editText.setSelection(2);
				}

				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						editText.setText(s.subSequence(0, 1));
						editText.setSelection(1);
						return;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				/*
				 * if (s.toString().contains(".")) {
				 * editText.setInputType(InputType.TYPE_CLASS_NUMBER); }
				 */
			}

			@Override
			public void afterTextChanged(Editable s) {
				/*
				 * if (!s.toString().contains(".")) {
				 * editText.setInputType(InputType.TYPE_CLASS_TEXT); }
				 */
			}

		});

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
