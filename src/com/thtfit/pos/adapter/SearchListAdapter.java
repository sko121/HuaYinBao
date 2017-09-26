package com.thtfit.pos.adapter;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.thtfit.pos.R;
import com.thtfit.pos.fragment.TotalFragment;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.service.PosApplication;

public class SearchListAdapter extends BaseAdapter {

	List<Product> products;
	Context context;
	private PosApplication application;

	public SearchListAdapter(List<Product> products, Context context) {
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
			convertView = inflater.inflate(R.layout.listiteminfo_search, null);
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
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String name = products.get(position).getName();
//		String number = products.get(position).getNumber();
		String price = products.get(position).getPrice();
		String serial = products.get(position).getSerial()+"";
		String stock = products.get(position).getStock();
		
		viewHolder.itemName.setText(name);
		viewHolder.itemPrice.setText(price);
		viewHolder.itemSerial.setText(serial);
		viewHolder.itemStock.setText(stock);
		
		 DisplayImageOptions options;  
		    options = new DisplayImageOptions.Builder()  
		    .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
		    .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中  
		    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
		    //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
		    //设置图片加入缓存前，对bitmap进行设置  
		    //.preProcessor(BitmapProcessor preProcessor)  
		    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
		    .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
		    .build();//构建完成 
		
		application.imageLoader.displayImage("http://"+products.get(position).getImagePath(), viewHolder.itemImage,options);
		
//		viewHolder.itemImage.setImageBitmap(imageProcessing(myUri));
		

		return convertView;
	}
	
	
	public void setBill(int subBill){
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
