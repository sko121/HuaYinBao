package com.thtfit.pos.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper {  
	  
    public static final String DB_NAME = "mydb";  
  
    public MyDBHelper(Context context) {  
        super(context, DB_NAME, null, 1); // 从1开始  
    }  
  
    //只运行一次  
    @Override  
    public void onCreate(SQLiteDatabase db) { // manage database creation  
    	Log.d("luzhaojie", "create table");
        // 创建表  
        db.execSQL("create table customer(_id integer primary key autoincrement,integral text) ");  
    }  
  
    // manage version management  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        // 假如有新版本:删除旧表，重新新建一个张表  
        if (newVersion > oldVersion) {  
            db.execSQL("drop table customer");  
            onCreate(db);  
        }  
    }  
}  