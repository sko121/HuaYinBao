package com.thtfit.pos.activity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.thtfit.pos.R;
import com.thtfit.pos.adapter.PayListAdapter;
import com.thtfit.pos.conn.hql.DBContror;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.model.Transaction;
import com.thtfit.pos.ui.PaintView;
import com.thtfit.pos.util.Config;

public class SignatureActivity extends FragmentActivity implements View.OnClickListener {
	private Context mContext;
	
	public static List<Product> listItems = null;
	
	private static final float T = 0.2575758F;
	private static final int s = 101;

	private ProgressDialog U;

	public String q;
	public Boolean r = Boolean.valueOf(false);
	// private Money t;
	private boolean u;

	private TextView mSubtotal;
/*	private TextView mDiscount;
	private TextView mTax;
	private TextView mTotal;*/
	
	private PaintView mPaintView;
	private Button BtnClear;
	private Button BtnConfirm;
	
	private String mAmount;
	private String cardInfo;
	
	private ListView totalList;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		setContentView(R.layout.activity_signature);

		mSubtotal = ((TextView) findViewById(R.id.signature_subtotal));
/*		mDiscount = ((TextView) findViewById(R.id.signature_discount));
		mTax = ((TextView) findViewById(R.id.signature_tax));
		mTotal = ((TextView) findViewById(R.id.signature_total));*/
		mPaintView = ((PaintView) findViewById(R.id.signature_paint));
		BtnClear = (Button) findViewById(R.id.signature_clear);
		BtnConfirm = (Button) findViewById(R.id.signature_confirm);
		totalList =  (ListView) findViewById(R.id.signature_total_list);
		listItems = (List<Product>) getIntent().getSerializableExtra("listItems");
		
		BtnClear.setOnClickListener(this);
		BtnConfirm.setOnClickListener(this);
		
		
		mAmount = getIntent().getStringExtra("amount");
		cardInfo =getIntent().getStringExtra("cardInfo");
		
		mSubtotal.setText(mAmount);
		
		showTotalList();
		
		// add hand writing
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidthDip = dm.widthPixels;
		int screenHeightDip = dm.heightPixels;
		mPaintView.setBitmapSize(screenWidthDip, screenHeightDip);
		//
		// this.mSubtotal.setText(localGeneralTransaction1.getReceiptTaxAmount()
		// .toString());
		// this.mTotal.setText(localGeneralTransaction1.getReceiptTotalAmount()
		// .toString());
	}
	
	public void showTotalList(){
		
		PayListAdapter listAdapter = new PayListAdapter(listItems, mContext);
		totalList.setAdapter(listAdapter);
		
	}

	public void killDialog() {
		if (this.U != null)
			return;
		try {
			this.U.dismiss();
			return;
		} catch (IllegalArgumentException localIllegalArgumentException) {
			while (true)
				localIllegalArgumentException.printStackTrace();
		}
	}

	protected void onActivityResult(int paramInt1, int paramInt2,
			Intent paramIntent) {

	}

	public void onBackPressed() {
	}

	public void onClick(View paramView) {
		int i = paramView.getId();
		if (i == R.id.signature_clear) {
			mPaintView.clear();
		} else if (i == R.id.signature_confirm) {
			//save signature	
			mPaintView.saveMyBitmap("handwriting");
			GenerateOrder();
/*			
			Intent intent = new Intent(mContext, ReceiptActivity.class);
			intent.putExtra("amount", mAmount);
			startActivity(intent);
			SignatureActivity.this.finish();
*/
			Toast.makeText(mContext, "支付完成", Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	
	//生成订单保存到数据库并上传
	private void GenerateOrder() {
		//销售店员
		String sales = Config.get("loginName", "0") != "0" ?  Config.get("loginName") : Config.get("loginNameManager");
		//订单号：销售店员+当前时间
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		String date = sDateFormat.format(new java.util.Date());
		String orderNumber = sales + date;
		//购买的商品列表
		Gson gson = new Gson();
		String listInfo = gson.toJson(listItems).toString();
		//刷卡信息
		
		Transaction transaction = new Transaction();
		transaction.setOrderNumber(orderNumber);
		transaction.setTotalPrice(mAmount);
		transaction.setListInfo(listInfo);
		transaction.setClerk(sales);
		transaction.setTime(date);
		transaction.setCardInfo(cardInfo);
		
		DBContror contror = new DBContror(SignatureActivity.this);
		contror.insertItem(transaction);
		
		// 发送后台登录请求
		Intent tmpIntent = new Intent(
				"com.thtfit.pos.service.Receiver.action.Service.RECEIVE_REQUEST");
		tmpIntent.putExtra("requestAction", "SignatureActivity.Order");
		tmpIntent
				.putExtra("responseFilter",
						"com.thtfit.pos.service.Receiver.action.Order.RECEIVE_DATA");
		tmpIntent.putExtra("transaction", (Serializable) transaction);
		sendBroadcast(tmpIntent);
		
	}

	protected void onPause() {
		// killDialog();
		super.onPause();
	}

	// public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
	// return false;
	// }

	public void showDialog() {
		this.U = new ProgressDialog(this);
		this.U.setProgressStyle(0);
		this.U.setMessage("processing");
		this.U.setCancelable(false);
		this.U.show();
	}

	// public void taskComplete() {
	// killDialog();
	// finish();
	// findViewById(R.id.signature_confirm).setEnabled(true);
	// }
	//
	// public void taskStart() {
	// showDialog();
	// }
	//
	// final class iz
	// implements ViewTreeObserver.OnGlobalLayoutListener
	// {
	// iz(SignatureActivity paramSignatureActivity)
	// {
	// }
	//
	// public final void onGlobalLayout()
	// {
	// SignatureActivity.a(this.a).getLayoutParams().height = ((int)(0.2575758F
	// * SignatureActivity.a(this.a).getWidth()));
	// SignatureActivity.a(this.a).getViewTreeObserver().removeGlobalOnLayoutListener(this);
	// }
	// }
}
