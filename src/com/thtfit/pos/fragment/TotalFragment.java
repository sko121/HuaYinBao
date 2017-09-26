package com.thtfit.pos.fragment;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.activity.SwipeCardActivity;
import com.thtfit.pos.activity.ZCSwipeCardActivity;
import com.thtfit.pos.adapter.MainGridAdapter;
import com.thtfit.pos.adapter.TotalListAdapter;
import com.thtfit.pos.debug.DebugPrint;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.util.Utils;

@SuppressLint("ValidFragment")
public class TotalFragment extends Fragment implements OnClickListener
{
	private String TAG = TotalFragment.class.getSimpleName();
	private Context mContext;
	private View mView;
	private static TotalFragment totalFragment = null;
	// private View headerView;
	public static ListView listView;

	public static List<String> serialList = new ArrayList<String>();
	public static List<Product> listItems = new ArrayList<Product>();
	public static Map<String, String> numberList = new HashMap<String, String>();

	public static final String KEY_CAREREADERA_PROPERTY="ro.thtfit.carereader";
	public static final String DEF_CAREREADERA_PROPERTY="ges00";
	public static final String GES00_CAREREADERA_NAME="ges00";
	public static final String GES10_CAREREADERA_NAME="ges10";

	public static Button checkout;
	public static Button checkoutright;
	public Button clear;
	public static float subBill = 0;
	private TotalListAdapter totalListAdapter;
	private Product product;
	private String serial;

	public TotalFragment()
	{
	}

	private TotalFragment(Context context)
	{
		this.mContext = context;
	}

	public synchronized TotalFragment getInstance(Bundle bundle)
	{

		TotalFragment totalFragment = new TotalFragment();
		totalFragment.setArguments(bundle);
		return totalFragment;
	}
	
	public synchronized static TotalFragment getInstance(Context context)
	{
		return totalFragment == null ? totalFragment = new TotalFragment(context) : totalFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		DebugPrint.d(TAG, "=====on onCreateView =====");
		if (null == mView)
		{
			mView = inflater.inflate(R.layout.fragment_total, container, false);
		}
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		initView();

		// setView();
	}

	public void initView()
	{
		checkout = (Button) mView.findViewById(R.id.total_checkout);
		checkoutright = (Button) mView.findViewById(R.id.total_checkout_right);
		clear = (Button) mView.findViewById(R.id.total_clear);
		listView = (ListView) mView.findViewById(R.id.fragment_total_list);

		clear.setOnClickListener(this);
		checkout.setOnClickListener(this);
		checkoutright.setOnClickListener(this);
	}
/*
	public void setView() {
		try {
			if (getArguments() != null) {
				product = (Product) getArguments().getSerializable("product");
				serial = product.getSerial() + "";

				String biilPrice = product.getPrice();
				try {
					subBill = subBill + (Float.parseFloat(biilPrice));
					setBill(subBill);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (serialList.contains(serial)) {
					int tmpNumber = Integer.parseInt(numberList.get(serial));
					String number = Integer.toString(tmpNumber + 1);
					numberList.put(serial, number);

					product.setNumber(number);
					// lstItems.remove(product.getSerial());
					totalListAdapter = new TotalListAdapter(listItems,getActivity());
					listView.setAdapter(totalListAdapter);
					totalListAdapter.notifyDataSetChanged();
				} else {
					numberList.put(serial, "1");
					serialList.add(serial);
					product.setNumber("1");
					listItems.add(product);
					totalListAdapter = new TotalListAdapter(listItems,getActivity());
					listView.setAdapter(totalListAdapter);
					totalListAdapter.notifyDataSetChanged();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/
	public static void setBill(float subBill)
	{
		BigDecimal decimal = new BigDecimal(subBill);
		subBill = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

		TotalFragment.checkout.setText("￥" + subBill);
	}

	public void showMSG(String content)
	{
		Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
	}

	public void clearData()
	{
		if (listItems != null)
		{
			if (listItems.size() > 0)
			{
				for (int i = 0; i < listItems.size(); i++)
				{
					listItems.get(i).setNumber("0");
				}
			}
		}
		Intent intent = new Intent(MainGridAdapter.REFLASHACTION_CLEARALL);//REFLASHACTION_CLEARALL
		if(getActivity()!=null)
		{
			getActivity().sendBroadcast(intent);
		}
		
		subBill = 0;
		setBill(subBill);
		serialList.clear();
		numberList.clear();
		listItems.removeAll(listItems);
	}
    public static String getSystempProperties(String key,String def){
        String result = "";
        try {

            Class clazz=Class.forName("android.os.SystemProperties");
            Method methodstr = clazz.getMethod("get",String.class,String.class);
            try {
                result = (String) methodstr.invoke(null,key,def);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return result;
    } 
	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{
			case R.id.total_clear:
				clearData();

				totalListAdapter = TotalListAdapter.getInstans(listItems, getActivity());
				listView.setAdapter(totalListAdapter);
				totalListAdapter.notifyDataSetChanged();
	
				break;
			case R.id.total_checkout:
				checkout();
				break;
			case R.id.total_checkout_right:
				checkout();
		}
	}

	private void checkout()
	{
		if (subBill == 0)
		{
			Toast.makeText(getActivity(), "尚未选择商品", 3000).show();
			return;
		}
		Toast.makeText(getActivity(), "您要支付金额为：" + checkout.getText(), 3000).show();
		// 跳转到支付
		String strCareReaderType = DEF_CAREREADERA_PROPERTY;
		strCareReaderType = getSystempProperties(KEY_CAREREADERA_PROPERTY,strCareReaderType);
		Log.d("niotongyuan",""+strCareReaderType);
		Intent intent1;
		if(strCareReaderType.equals(GES10_CAREREADERA_NAME)){
			intent1 = new Intent(getActivity(), ZCSwipeCardActivity.class);
		}else{
			intent1 = new Intent(getActivity(), SwipeCardActivity.class);
		}
		String amount = checkout.getText().toString();
		Log.d("luzhaojie", "amount == " + amount);
		amount = Utils.removeAmountDollar(amount);
		intent1.putExtra("amount", amount);
		intent1.putExtra("listItems", (Serializable) listItems);
		getActivity().startActivity(intent1);

	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		if (totalListAdapter != null)
		{
			totalListAdapter = null;
		}
	}

	public synchronized void updateList(Product pro)
	{

		DebugPrint.d(TAG,"product=" + pro.getName().toString());
		try
		{
			product = pro;
			serial = product.getSerial() + "";

			String billPrice = product.getPrice();
			try
			{
				subBill = subBill + (Float.parseFloat(billPrice));
				setBill(subBill);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			/**
			 * 这里分两种情况 
			 * 1.没有增加条目的更新，即只是在他原有item局部更新即可 
			 * 2.条目数量改变了，那没办法就要全部刷新一下
			 */
			if (serialList.contains(serial))
			{

				int tmpNumber = Integer.parseInt(numberList.get(serial));
				String number = Integer.toString(tmpNumber + 1);
				numberList.put(serial, number);

				product.setNumber(number);
				pro.setNumber(number);

				totalListAdapter = TotalListAdapter.getInstans(listItems, mContext);
				
				totalListAdapter.updataProducts(listView,product);
			}
			else
			{
				numberList.put(serial, "1");
				serialList.add(serial);
				product.setNumber("1");
				pro.setNumber("1");
				listItems.add(product);

				totalListAdapter = TotalListAdapter.getInstans(listItems, mContext);
				listView.setAdapter(totalListAdapter);

				totalListAdapter.addProducts(product);
			}

			DebugPrint.d(TAG,"listItems.size()=" + listItems.size());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
