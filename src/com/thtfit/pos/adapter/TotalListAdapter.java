package com.thtfit.pos.adapter;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.thtfit.pos.R;
import com.thtfit.pos.api.CartApi;
import com.thtfit.pos.fragment.FirstFragment;
import com.thtfit.pos.fragment.TotalFragment;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.service.PosApplication;

public class TotalListAdapter extends BaseAdapter
{

	List<Product> products;

	Context context;
	private PosApplication application;

	private static TotalListAdapter instans;

	public static TotalListAdapter getInstans(List<Product> products, Context context)
	{
		if (instans == null)
		{
			instans = new TotalListAdapter(products, context);
		}
		return instans;
	}

	private TotalListAdapter(List<Product> products, Context context)
	{
		super();
		this.products = products;
		this.context = context;
		application = (PosApplication) context.getApplicationContext();
	}

	public static class ViewHolder
	{
		public TextView itemNumber;
		public TextView itemName;
		public TextView itemPrice;
		public ImageView itemImage;
		public Button itemButtonMinus;
		public Button itemButtonAdd;

	}



	public void updataProducts(ListView listview,Product product)
	{
		int index = getProductIndex(product);
		//totalListAdapter.getProducts().get(index).setNumber(product.getNumber());
		View view = listview.getChildAt(index - listview.getFirstVisiblePosition());
		TextView textview = (TextView) view.findViewById(R.id.list_itemNumber);
		textview.setText(product.getNumber());
	}

	public void addProducts(Product product)
	{

		/**
		 * List集合只有引用，实体由外面传递进来的 这种感觉有点怪异
		 */
		// products.add(product);
		notifyDataSetChanged();
	}

	public int getProductIndex(Product product)
	{
		for (int index = 0; index < products.size(); index++)
		{
			if (products.get(index).getSerial() == product.getSerial())
			{
				return index;
			}
		}
		return 0;
	}

	@Override
	public int getCount()
	{
		if (null != products)
		{
			return products.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		return products.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		final ViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.listiteminfo, null);
			viewHolder = new ViewHolder();
			viewHolder.itemImage = (ImageView) convertView.findViewById(R.id.list_itemImage);
			viewHolder.itemName = (TextView) convertView.findViewById(R.id.list_itemName);
			viewHolder.itemNumber = (TextView) convertView.findViewById(R.id.list_itemNumber);
			viewHolder.itemPrice = (TextView) convertView.findViewById(R.id.list_itemPrice);
			viewHolder.itemButtonMinus = (Button) convertView.findViewById(R.id.list_buttonMinus);
			viewHolder.itemButtonAdd = (Button) convertView.findViewById(R.id.list_buttonAdd);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String name = products.get(position).getName();
		String number = products.get(position).getNumber();
		String price = products.get(position).getPrice();

		viewHolder.itemName.setText(name);
		viewHolder.itemNumber.setText(number);
		viewHolder.itemPrice.setText(price);

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
		application.imageLoader.displayImage("http://" + products.get(position).getImagePath(), viewHolder.itemImage,
				options);



		// viewHolder.itemImage.setImageBitmap(imageProcessing(myUri));
		viewHolder.itemButtonAdd.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				int tmpNumber = Integer.parseInt(viewHolder.itemNumber.getText().toString()) + 1;
				String price = products.get(position).getPrice();
				String number = Integer.toString(tmpNumber);
				String serial = products.get(position).getSerial() + "";
				TotalFragment.numberList.put(serial, number);
				viewHolder.itemNumber.setText(number);
				products.get(position).setNumber(number);
				
				Intent intent = new Intent(MainGridAdapter.REFLASHACTION_ITEM);
				intent.putExtra("product", products.get(position));
				context.sendBroadcast(intent);
				
				TotalFragment.subBill = TotalFragment.subBill + (Float.parseFloat(price));
				setBill(TotalFragment.subBill);
			}
		});

		viewHolder.itemButtonMinus.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				int tmpNumber = Integer.parseInt(viewHolder.itemNumber.getText().toString()) - 1;
				if (tmpNumber <= 0)
				{
	
					products.get(position).setNumber("0");
					
					String price = products.get(position).getPrice();
					TotalFragment.subBill = TotalFragment.subBill - (Float.parseFloat(price));
					setBill(TotalFragment.subBill);

					
					Intent intent = new Intent(MainGridAdapter.REFLASHACTION_ITEM);
					intent.putExtra("product", products.get(position));
					context.sendBroadcast(intent);
					
					TotalFragment.serialList.remove(products.get(position).getSerial() + "");
					TotalFragment.numberList.remove(products.get(position).getSerial() + "");
					TotalFragment.listItems.remove(position);
					notifyDataSetChanged();
					
					
					return;
				}
				String number = Integer.toString(tmpNumber);
				String price = products.get(position).getPrice();
				String serial = products.get(position).getSerial() + "";
				TotalFragment.numberList.put(serial, number);
				viewHolder.itemNumber.setText(number);
				products.get(position).setNumber(number);
				
				/**
				 * 这个position是listiew里面的位置  
				 * 跟gridiew里面的position需要“映射”关系
				 * 我需要把product对象传递过去
				 */
				Intent intent = new Intent(MainGridAdapter.REFLASHACTION_ITEM);
				intent.putExtra("product", products.get(position));
				context.sendBroadcast(intent);
				TotalFragment.subBill = TotalFragment.subBill - (Float.parseFloat(price));
				setBill(TotalFragment.subBill);
			}
		});
		return convertView;
	}

	public void setBill(float subBill)
	{
		BigDecimal decimal = new BigDecimal(subBill);
		subBill = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		TotalFragment.checkout.setText("￥" + subBill);
	}

	public Bitmap imageProcessing(Uri myUri)
	{

		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(context, myUri, proj, null, null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
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
