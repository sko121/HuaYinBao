package com.thtfit.pos.conn.hql;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.thtfit.pos.debug.DebugPrint;
import com.thtfit.pos.model.ItemType;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.model.PushInfo;
import com.thtfit.pos.model.Stock;
import com.thtfit.pos.model.Transaction;

public class DBContror {
	private DBHelper dbHelper;
	private SQLiteDatabase db = null;
	private static String TAG = "DBContror";
	public static final boolean DEBUG = true;
	private static void LOG(String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}


	private String[] mViewpager_title = new String[] { "Inventory",
			"Daily Specials", "Extras", "Salads", "Sides" };

	public DBContror(Context context) {
		dbHelper = new DBHelper(context);

		// 查询全部数据
		List<ItemType> allList = queryAllType();
		if (allList.size() > 0) {
		}
		// 如果不存在数据就初始化数据库并添加数据
		else {
			initDB(mViewpager_title);
		}
	}

	/**
	 * 数据库初始化
	 * 
	 * @param value
	 * @return
	 */
	private void initDB(String[] list) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		dbHelper.onCreate(db);
		ContentValues values = null;
		try {
			for (int i = 0; i < list.length; i++) {
				values = new ContentValues();
				values.put(DBHelper.TYPE_NAME, list[i]);
				db.insert(DBHelper.TABLE_TYPE, null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db.isOpen()) {
				db.close();
			}
		}
	}

	/**
	 * TABLE_TYPE表清空数据
	 */
	public void clearTypeDate() {
		db = dbHelper.getWritableDatabase();
		db.execSQL("delete from " + DBHelper.TABLE_TYPE);
		db.execSQL("update sqlite_sequence set seq=0 where name='"
				+ DBHelper.TABLE_TYPE + "'");
		db.close();
		LOG("clearTypeData success!");
	}

	/**
	 * TABLE_TYPE表查询所有类别
	 * 
	 */
	public List<ItemType> queryAllType() {
		db = dbHelper.getReadableDatabase();

		List<ItemType> list = new ArrayList<ItemType>();
		try {
			Cursor cursor = db.query(DBHelper.TABLE_TYPE, null, null, null,
					null, null, null);
			while (cursor.moveToNext()) {
				list.add(new ItemType(cursor.getInt(cursor
						.getColumnIndex(DBHelper.TYPE_ID)), cursor
						.getString(cursor.getColumnIndex(DBHelper.TYPE_NAME))));
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * TABLE_TYPE表添加数据
	 * 
	 * @param typeName
	 * @return
	 */
	public long insertTypeItem(Integer typeId, String typeName) {
		long row = 0;
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues contentValues = new ContentValues();
			contentValues.put(DBHelper.TYPE_ID, typeId);
			contentValues.put(DBHelper.TYPE_NAME, typeName);
			row = db.insert(DBHelper.TABLE_TYPE, null, contentValues);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * TABLE_TYPE表更新数据
	 * 
	 * @param typeName
	 * @return
	 */
	public long updateTypeItem(String typeId, String typeName) {

		ContentValues cv = new ContentValues();
		cv.put(DBHelper.TYPE_NAME, typeName);
		try {
			db = dbHelper.getWritableDatabase();
			db.update(DBHelper.TABLE_TYPE, cv, DBHelper.TYPE_ID + " = ?",
					new String[] { typeId });
			db.close();
		} catch (Exception e) {
			Log.e("", e.toString());
		}

		long row = 0;
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues contentValues = new ContentValues();
			contentValues.put(DBHelper.TYPE_ID, typeId);
			contentValues.put(DBHelper.TYPE_NAME, typeName);
			row = db.insert(DBHelper.TABLE_TYPE, null, contentValues);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * TABLE_PRO表清除类别下的数据
	 * 
	 * @param String
	 *            typeName
	 * @return
	 */
	public void clearProType(String typeName) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			String[] sb = new String[1];
			sb[0] = typeName;
			db.delete(DBHelper.TABLE_PRO, DBHelper.PRO_TYPE + "=?", sb);
			LOG("clearProType success!");
		} catch (Exception e) {
			e.printStackTrace();
			LOG("clearProType error!");
		}
	}

	/**
	 * TABLE_PRO表添加数据
	 * 
	 * @param Product
	 *            proBean
	 * @return
	 */
	public long insertItem(Product proBean) {
		long row = 0;
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues contentValues = new ContentValues();
			contentValues.put(DBHelper.PRO_ID, proBean.getProId());
			contentValues.put(DBHelper.PRO_SERIAL, proBean.getSerial());
			contentValues.put(DBHelper.PRO_NAME, proBean.getName());
			contentValues.put(DBHelper.PRO_PRICE, proBean.getPrice());
			contentValues.put(DBHelper.PRO_DESCRIBE, proBean.getDescribe());
			contentValues.put(DBHelper.PRO_NOTE, proBean.getNote());
			contentValues.put(DBHelper.PRO_IMAGE, proBean.getImagePath());
			contentValues.put(DBHelper.PRO_TYPE, proBean.getType());
			LOG("PRO_STOCK = " + proBean.getStock());
			contentValues.put(DBHelper.PRO_STOCK, ((proBean.getStock() != null) ? proBean.getStock() : "100"));
			row = db.insert(DBHelper.TABLE_PRO, null, contentValues);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * TABLE_PRO表删除指定编号（serial）的数据
	 * 
	 * @param serial	
	 * @return
	 */
	public int deleteBySerial(int serial) {
		int i = 0;
		try {
			String[] sb = new String[1];
			sb[0] = String.valueOf(serial);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			dbHelper.onCreate(db);
			i = db.delete(DBHelper.TABLE_PRO, DBHelper.PRO_SERIAL + "=?", sb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i;
	}

	/**
	 * TABLE_PRO表查询数据库中是否存在编号为serial的数据
	 * 
	 * @param serial
	 * @return boolean
	 */
	public boolean checkBySerial(int serial) {
		Cursor cursor;
		try {

			SQLiteDatabase db = dbHelper.getReadableDatabase();
			cursor = db.query(DBHelper.TABLE_PRO, null, DBHelper.PRO_SERIAL
					+ "=?", new String[] { String.valueOf(serial) }, null,
					null, null);
			cursor.moveToFirst();
			int count = cursor.getCount();
			db.close();
			if (count > 0) {
				cursor.close();
				return true;
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * TABLE_PRO表查询所有的数据
	 * 
	 * @return List<Product>
	 */
	public List<Product> queryAllItem() {
		db = dbHelper.getReadableDatabase();
		List<Product> productsList = new ArrayList<Product>();
		try {
			Cursor cursor = db.query(DBHelper.TABLE_PRO, null, null, null,
					null, null, null);
			while (cursor.moveToNext()) {
				Product product = new Product();
				
				product.setProId(cursor.getInt(cursor
						.getColumnIndex(DBHelper.PRO_ID)));
				product.setSerial(cursor.getInt(cursor
						.getColumnIndex(DBHelper.PRO_SERIAL)));
				product.setName(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_NAME)));
				product.setPrice(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_PRICE)));
				product.setDescribe(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_DESCRIBE)));
				product.setNote(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_NOTE)));
				product.setImagePath(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_IMAGE)));

				productsList.add(product);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productsList;
	}
	
	/**
	 * TABLE_PRO表清空数据
	 */
	public void clearProDate() {
		db = dbHelper.getWritableDatabase();
		db.execSQL("delete from " + DBHelper.TABLE_PRO);
		db.execSQL("update sqlite_sequence set seq=0 where name='"
				+ DBHelper.TABLE_PRO + "'");
		db.close();
		LOG("clearProData success!");
	}
	
	/**
	 * TABLE_PRO表根据产品类别来查询数据
	 * 
	 * @return List<Product>
	 */
	public List<Product> queryAllItemByType(String typeId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<Product> productsList = new ArrayList<Product>();
		try {

			Cursor cursor = db.query(DBHelper.TABLE_PRO, null,
					DBHelper.PRO_TYPE + "=?", new String[] { typeId }, null,
					null, null);
			while (cursor.moveToNext()) {
				Product product = new Product();

				product.setProId(cursor.getInt(cursor
						.getColumnIndex(DBHelper.PRO_ID)));
				product.setSerial(cursor.getInt(cursor
						.getColumnIndex(DBHelper.PRO_SERIAL)));
				product.setName(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_NAME)));
				product.setPrice(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_PRICE)));
				product.setDescribe(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_DESCRIBE)));
				product.setStock(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_STOCK)));
				product.setImagePath(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_IMAGE)));

				productsList.add(product);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productsList;
	}

	/**
	 * TABLE_PRO表根据产品类别分页查询数据
	 * 
	 * @return List<Product>
	 */
	public List<Product> queryPageByType(String typeId, int page, int pageSize) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<Product> productsList = new ArrayList<Product>();

/*		String queryTypeSql = "select " + DBHelper.TYPE_ID + " from "
				+ DBHelper.TABLE_TYPE + " where " + DBHelper.TYPE_NAME + "='"
				+ type + "'";*/
		try {
/*			Cursor cursor0 = db.rawQuery(queryTypeSql, null);
			String typeId = null;
			while (cursor0.moveToNext()) {
				typeId = cursor0.getString(cursor0
						.getColumnIndex(DBHelper.TYPE_ID));
			}*/
			String queryProSql = "select * from " + DBHelper.TABLE_PRO
					+ " where " + DBHelper.PRO_TYPE + "='" + typeId + "'"
					+ " Limit " + pageSize + " Offset " + page * pageSize;

			Cursor cursor = db.rawQuery(queryProSql, null);
			while (cursor.moveToNext()) {
				Product product = new Product();

				product.setProId(cursor.getInt(cursor
						.getColumnIndex(DBHelper.PRO_ID)));
				product.setSerial(cursor.getInt(cursor
						.getColumnIndex(DBHelper.PRO_SERIAL)));
				product.setName(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_NAME)));
				product.setPrice(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_PRICE)));
				product.setDescribe(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_DESCRIBE)));
				product.setNote(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_NOTE)));
				product.setImagePath(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_IMAGE)));

				productsList.add(product);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productsList;
	}

	/**
	 * TABLE_PRO表根据产品名称来查询数据
	 * 
	 * @return List<Product>
	 */
	public List<Product> queryAllItemByName(String name) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<Product> productsList = new ArrayList<Product>();
		try {
			Cursor cursor = db.query(DBHelper.TABLE_PRO, null,
					DBHelper.PRO_NAME + " LIKE ?", new String[] { "%" + name
							+ "%" }, null, null, null);
			while (cursor.moveToNext()) {
				Product product = new Product();

				product.setProId(cursor.getInt(cursor
						.getColumnIndex(DBHelper.PRO_ID)));
				product.setSerial(cursor.getInt(cursor
						.getColumnIndex(DBHelper.PRO_SERIAL)));
				product.setName(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_NAME)));
				product.setPrice(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_PRICE)));
				product.setDescribe(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_DESCRIBE)));
				product.setStock(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_STOCK)));
				product.setImagePath(cursor.getString(cursor
						.getColumnIndex(DBHelper.PRO_IMAGE)));

				productsList.add(product);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productsList;
	}

	/**
	 * TABLE_PRO表查看picture数据库是否已经存在该图片
	 */
	public boolean checkPic(Product product) {
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			String clientCode = product.getImagePath();
			Cursor cursor = db.query(DBHelper.TABLE_PRO, null, "imageId=?",
					new String[] { clientCode }, null, null, null);
			cursor.moveToFirst();
			int count = cursor.getCount();
			db.close();
			if (count > 0) {
				cursor.close();
				return true;
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * TABLE_PUSH表添加数据
	 * 
	 * @param PushInfo
	 *            pushInfo
	 * @return
	 */
	public long insertItemToPush(PushInfo pushInfo) {
		long row = 0;
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues contentValues = new ContentValues();
			contentValues.put(DBHelper.PUSH_TYPE, pushInfo.getType());
			contentValues.put(DBHelper.PUSH_TIME, pushInfo.getTime());
			contentValues.put(DBHelper.PUSH_CONTENT, pushInfo.getContent());
			row = db.insert(DBHelper.TABLE_PUSH, null, contentValues);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * TABLE_PUSH表根据数据类别来查询数据
	 * 
	 * @return List<PushInfo>
	 */
	public List<PushInfo> queryAllItemByTypeFromPush(String type) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<PushInfo> pushInfoList = new ArrayList<PushInfo>();
		try {
			Cursor cursor = db.query(DBHelper.TABLE_PUSH, null,
					DBHelper.PUSH_TYPE + "=?", new String[] { type }, null,
					null, null);
			while (cursor.moveToNext()) {
				PushInfo pushInfo = new PushInfo();

				pushInfo.setType(cursor.getString(cursor
						.getColumnIndex(DBHelper.PUSH_TYPE)));
				pushInfo.setTime(cursor.getString(cursor
						.getColumnIndex(DBHelper.PUSH_TIME)));
				pushInfo.setContent(cursor.getString(cursor
						.getColumnIndex(DBHelper.PUSH_CONTENT)));

				pushInfoList.add(pushInfo);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pushInfoList;
	}

	/**
	 * TABLE_PUSH表查询所有的数据
	 * 
	 * @return List<PushInfo>
	 */
	public List<PushInfo> queryAllItemFromPush() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<PushInfo> pushInfoList = new ArrayList<PushInfo>();
		try {
			Cursor cursor = db.query(DBHelper.TABLE_PUSH, null, null, null,
					null, null, null);
			while (cursor.moveToNext()) {
				PushInfo pushInfo = new PushInfo();

				pushInfo.setType(cursor.getString(cursor
						.getColumnIndex(DBHelper.PUSH_TYPE)));
				pushInfo.setTime(cursor.getString(cursor
						.getColumnIndex(DBHelper.PUSH_TIME)));
				pushInfo.setContent(cursor.getString(cursor
						.getColumnIndex(DBHelper.PUSH_CONTENT)));
				pushInfoList.add(pushInfo);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pushInfoList;
	}

	/**
	 * TABLE_PUSH表修改type
	 * 
	 * 待修改
	 * 
	 */
	public void modifyPushType(String type) {
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.PUSH_TYPE, type);
		try {
			db = dbHelper.getWritableDatabase();
			db.update(DBHelper.TABLE_PUSH, cv, DBHelper.PUSH_TYPE + " = ?",
					new String[] { type });
			db.close();
		} catch (Exception e) {
			Log.e("", e.toString());
		}
	}

	/**
	 * TABLE_PUSH表删除
	 * 
	 */
	public void deletePush() {
		try {
			db = dbHelper.getReadableDatabase();
			db.execSQL("DROP TABLE IF EXISTS " + DBHelper.TABLE_PUSH);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * TABLE_TRANSACTION表添加数据
	 * 
	 * @param Transaction
	 *            transaction
	 * @return
	 */
	public long insertItem(Transaction transaction) {
		long row = 0;
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues contentValues = new ContentValues();
			contentValues.put(DBHelper.TSA_ORDER_NUMBER,
					transaction.getOrderNumber());
			contentValues.put(DBHelper.TSA_TOTAL_PRICE,
					transaction.getTotalPrice());
			contentValues
					.put(DBHelper.TSA_LIST_INFO, transaction.getListInfo());
			contentValues.put(DBHelper.TSA_CLERK, transaction.getClerk());
			contentValues
					.put(DBHelper.TSA_CARD_INFO, transaction.getCardInfo());
			contentValues.put(DBHelper.TSA_TIME, transaction.getTime());
			row = db.insert(DBHelper.TABLE_TRANSACTION, null, contentValues);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return row;
	}

	/**
	 * TABLE_STOCK表添加出入库数据,更新TABLE_PRO表库存
	 * 
	 * @param Stock
	 *            stock
	 * @return
	 */
	public long insertItem(Stock stock) {
		long row = 0;
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues contentValues = new ContentValues();
			contentValues.put(DBHelper.STOCK_PROID, stock.getProId());
			contentValues.put(DBHelper.STOCK_TIME, stock.getTime());
			contentValues.put(DBHelper.STOCK_OUT, stock.getOut());
			contentValues.put(DBHelper.STOCK_IN, stock.getIn());
			contentValues.put(DBHelper.STOCK_STOCK, stock.getStock());
			row = db.insert(DBHelper.TABLE_STOCK, null, contentValues);
			contentValues.clear();
			contentValues.put(DBHelper.STOCK_STOCK, stock.getStock());
			db.update(DBHelper.TABLE_PRO, contentValues, DBHelper.PRO_SERIAL + " = ?",
					new String[] { String.valueOf(stock.getProId()) });
			
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return row;
	}

}
