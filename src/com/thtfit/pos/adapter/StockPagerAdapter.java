package com.thtfit.pos.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.thtfit.pos.R;
import com.thtfit.pos.conn.hql.DBContror;
import com.thtfit.pos.fragment.TotalFragment;
import com.thtfit.pos.model.CustomDialog;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.model.Stock;
import com.thtfit.pos.service.PosApplication;

public class StockPagerAdapter extends BaseAdapter {

	public static final int MESSAGE_IN_STOCK = 1;
	public static final int MESSAGE_OUT_STOCK = 2;

	List<Product> products;
	Context context;
	EditText editText;
	String stockNumber;
	String proStock;
	int serial;

	private PosApplication application;
	private DBContror dbcon;

	public StockPagerAdapter(List<Product> products, Context context) {
		super();
		this.products = products;
		this.context = context;

		application = (PosApplication) context.getApplicationContext();

	}

	public static class ViewHolder {
		public TextView itemName;
		public TextView itemPrice;
		public ImageView itemImage;
		public TextView itemSerial;
		public TextView itemStock;
		public Button buttonMinus;
		public Button buttonAdd;

	}

	@Override
	public int getCount() {
		if (null != products) {
			return products.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return products.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listiteminfo_stock, null);
			viewHolder = new ViewHolder();
			viewHolder.itemImage = (ImageView) convertView
					.findViewById(R.id.list_itemImage);
			viewHolder.itemName = (TextView) convertView
					.findViewById(R.id.list_itemName);
			viewHolder.itemPrice = (TextView) convertView
					.findViewById(R.id.list_itemPrice);
			viewHolder.itemSerial = (TextView) convertView
					.findViewById(R.id.list_itemSerial);
			viewHolder.itemStock = (TextView) convertView
					.findViewById(R.id.list_itemStock);
			viewHolder.buttonAdd = (Button) convertView
					.findViewById(R.id.list_buttonAdd);
			viewHolder.buttonMinus = (Button) convertView
					.findViewById(R.id.list_buttonMinus);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String name = products.get(position).getName();
		// String number = products.get(position).getNumber();
		String price = products.get(position).getPrice();
		serial = products.get(position).getSerial();
		String stock = products.get(position).getStock();

		viewHolder.itemName.setText(name);
		viewHolder.itemPrice.setText(price);
		viewHolder.itemSerial.setText(String.valueOf(serial));
		viewHolder.itemStock.setText(stock);

		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder().cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis为你设置的下载前的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// .preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
				.build();// 构建完成

		application.imageLoader.displayImage("http://"
				+ products.get(position).getImagePath(), viewHolder.itemImage,
				options);

		viewHolder.buttonAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CustomDialog.Builder builder = new CustomDialog.Builder(context);
				builder.setTitle("输入产品入库数量");
				editText = (EditText) builder.getEditText();
				
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stockNumber = editText.getText().toString();
								proStock = String.valueOf(Integer
										.parseInt(viewHolder.itemStock
												.getText().toString())
										+ Integer.parseInt(stockNumber));

								viewHolder.itemStock.setText(proStock);
								products.get(position).setStock(proStock);
								new Thread() {
									@Override
									public void run() {
										super.run();
										Message msg = new Message();
										msg.what = MESSAGE_IN_STOCK;
										handler.sendMessage(msg);
									}
								}.start();
								dialog.dismiss();
							}
						});

				builder.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();

			}
		});
		viewHolder.buttonMinus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomDialog.Builder builder = new CustomDialog.Builder(context);
				builder.setTitle("输入产品出库数量");
				editText = (EditText) builder.getEditText();				
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								
								if(viewHolder.itemStock.getText().toString() == null){
									return;
								}
								stockNumber = editText.getText().toString();
								int tmpStock = Integer.parseInt(viewHolder.itemStock.getText().toString())- Integer.parseInt(stockNumber);
								proStock = tmpStock < 0 ? "0" : String
										.valueOf(tmpStock);
								viewHolder.itemStock.setText(proStock);
								products.get(position).setStock(proStock);
								new Thread() {
									@Override
									public void run() {
										super.run();
										Message msg = new Message();
										msg.what = MESSAGE_OUT_STOCK;
										handler.sendMessage(msg);
									}
								}.start();

								dialog.dismiss();
							}
						});

				builder.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();

			}
		});

		return convertView;
	}

	// handler类接收数据
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_IN_STOCK:
				setStock(MESSAGE_IN_STOCK);
				break;

			case MESSAGE_OUT_STOCK:
				setStock(MESSAGE_OUT_STOCK);
				break;

			default:
				break;
			}
		};
	};

	public void setStock(int messageStockType) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String stockTime = formatter.format(curDate);

		Stock stock = new Stock();
		stock.setProId(serial);
		stock.setTime(stockTime);
		stock.setStock(proStock);
		System.out.println("--setStock----proStock-----"+proStock);
		if (MESSAGE_IN_STOCK == messageStockType) {
			stock.setIn(stockNumber);
			stock.setOut("0");
		} else if (MESSAGE_OUT_STOCK == messageStockType) {
			stock.setOut("0");
			stock.setIn(stockNumber);
		}
		dbcon = new DBContror(context);
		dbcon.insertItem(stock);
	}

	public void setBill(int subBill) {
		TotalFragment.checkout.setText(Integer.toString(subBill));
	}

	public Bitmap imageProcessing(Uri myUri) {

		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(context, myUri, proj, null,
				null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		String img_path = cursor.getString(column_index);

		File file = new File(img_path);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 4;
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

		// Bitmap bmp =
		// MediaStore.Images.Media.getBitmap(context.getContentResolver(),
		// Uri.fromFile(file));

		cursor.close();
		return bitmap;

	}

}
