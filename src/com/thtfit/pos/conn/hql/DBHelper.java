package com.thtfit.pos.conn.hql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "THTFITPOS.db"; // 数据库名
	public static final int VERSION = 1; // 版本号

	// 表名
	public static final String TABLE_PRO = "producttable";
	public static final String TABLE_PUSH = "pushinfotable";
	public static final String TABLE_TYPE = "typetable";
	public static final String TABLE_TRANSACTION = "transactiontable";
	public static final String TABLE_STOCK = "stocktable";

	// TABLE_PRO表字段名
	public static final String PRO_ID = "pro_id";
	public static final String PRO_SERIAL = "pro_serial";
	public static final String PRO_NAME = "pro_name";
	public static final String PRO_PRICE = "pro_price";
	public static final String PRO_DESCRIBE = "pro_describe";
	public static final String PRO_NOTE = "pro_note";
	public static final String PRO_IMAGE = "pro_imagePath";
	public static final String PRO_TYPE = "type_id";
	public static final String PRO_STOCK = "stock_stock";

	// TABLE_PUSH表字段名
	public static final String PUSH_ID = "push_id";
	public static final String PUSH_TYPE = "push_type";
	public static final String PUSH_CONTENT = "push_content";
	public static final String PUSH_TIME = "push_time";

	// TABLE_TYPE表字段名
	public static final String TYPE_ID = "type_id";
	public static final String TYPE_NAME = "type_name";

	// TABLE_TRANSACTION
	public static final String TSA_ID = "tsa_id";
	public static final String TSA_ORDER_NUMBER = "tsa_orderNumber";
	public static final String TSA_TOTAL_PRICE = "tsa_totalPrice";
	public static final String TSA_LIST_INFO = "tsa_listInfo";
	public static final String TSA_CLERK = "tsa_clerk";
	public static final String TSA_CARD_INFO = "tsa_cardInfo";
	public static final String TSA_TIME = "tsa_time";

	// TABLE_STOCK
	public static final String STOCK_ID = "stock_id";
	public static final String STOCK_PROID = "pro_serial";
	public static final String STOCK_TIME = "stock_time";
	public static final String STOCK_OUT = "stock_out";
	public static final String STOCK_IN = "stock_in";
	public static final String STOCK_STOCK = "stock_stock";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// 创建数据表的操作
		String strSQL1 = "CREATE TABLE IF NOT EXISTS " + TABLE_PRO + " ("
				+ PRO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PRO_SERIAL
				+ " Integer NOT NULL, " + PRO_NAME + " text," + PRO_PRICE
				+ " text, " + PRO_DESCRIBE + " text, " + PRO_NOTE + " text, "
				+ PRO_IMAGE + " text, " + PRO_TYPE + " text, " + PRO_STOCK
				+ " text DEFAULT '100');";

		String strSQL2 = "CREATE TABLE IF NOT EXISTS " + TABLE_PUSH + " ("
				+ PUSH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PUSH_TYPE
				+ " text, " + PUSH_TIME + " text, " + PUSH_CONTENT + " text);";

		String strSQL3 = "CREATE TABLE IF NOT EXISTS " + TABLE_TYPE + " ("
				+ TYPE_ID + " INTEGER PRIMARY KEY, " + TYPE_NAME
				+ " text UNIQUE);";

		String strSQL4 = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTION
				+ " (" + TSA_ID
				+ " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
				+ TSA_ORDER_NUMBER + " text, " + TSA_TOTAL_PRICE + " text, "
				+ TSA_LIST_INFO + " text, " + TSA_CLERK + " text, "
				+ TSA_CARD_INFO + " text, " + TSA_TIME + " text);";

		String strSQL5 = "CREATE TABLE IF NOT EXISTS " + TABLE_STOCK + " ("
				+ STOCK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ STOCK_PROID + " Integer NOT NULL, " + STOCK_TIME + " text, "
				+ STOCK_OUT + " text, " + STOCK_IN + " text, " + STOCK_STOCK
				+ " text);";

		db.execSQL(strSQL1);
		db.execSQL(strSQL2);
		db.execSQL(strSQL3);
		db.execSQL(strSQL4);
		db.execSQL(strSQL5);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
