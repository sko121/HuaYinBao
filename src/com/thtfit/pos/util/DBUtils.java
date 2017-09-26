package com.thtfit.pos.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.util.Log;

import com.github.mikephil.charting.utils.FileUtils;
import com.thtfit.pos.bean.IntegralBean;

public class DBUtils {  
    
    public static final String TABLE_NAME = "customer";  //积分表
  
    /** 
     * 插入 
     * @param context 
     */  
    public static void insert(Context context, IntegralBean integralBean){  
        //A helper class to manage database creation and version management.  
        MyDBHelper myDBHelper = new MyDBHelper(context);  
        //Create and/or open a database that will be used for reading and writing.  
        SQLiteDatabase db = myDBHelper.getWritableDatabase();  
        ContentValues contentValues = new ContentValues(); //HashMap
        contentValues.put("integral", integralBean.getIntegral());
        db.insert(TABLE_NAME, null, contentValues);
//        for(int i = 0; i < gifts.size(); i++){  
//            IntegralBean IntegralBean = gifts.get(i);  
//            ContentValues contentValues = new ContentValues();  //HashMap  
//            contentValues.put("name", IntegralBean.getGiftName());  
//            db.insert(TABLE_NAME, null, contentValues);  
//        }  
        db.close();  
    }  
      
    public static void insertBySql(Context context){  
//        //装载数据到集合  
//        List<IntegralBean> gifts = FileUtils.readDataFromAssets(context,   
//                IntegralBean.class, "student.txt");  
        MyDBHelper myDBHelper = new MyDBHelper(context);  
        SQLiteDatabase db = myDBHelper.getWritableDatabase();  
//        long start = SystemClock.currentThreadTimeMillis();  
//        for(int i = 0; i < gifts.size(); i++){  
//            IntegralBean gift = gifts.get(i);  
        IntegralBean integralBean = new IntegralBean();
        db.execSQL("insert into customer(integral) values(?)",   
        		new Integer[]{integralBean.getIntegral()});  
//        }  
//        long end = SystemClock.currentThreadTimeMillis();  
//        Log.d("time : ", String.valueOf((end - start)/1000));  
        db.close();  
    }  
      
    /** 
     * 查询所有 
     * @param context 
     * @return 
     */  
    public static List<IntegralBean> query(Context context) {  
        List<IntegralBean> integrals = new ArrayList<IntegralBean>();  
        //创建sqli帮助类  
        MyDBHelper myDBHelper = new MyDBHelper(context);  
        //获取可读数据库  
        SQLiteDatabase db = myDBHelper.getReadableDatabase();  
        //获取数据库的游标  
        //This interface provides random read-write access to the result set returned  
        // by a database query.  
        Cursor cursor = db.query(TABLE_NAME, new String[]{"_id, integral"}, null,   
                null, null, null, null);  
        //迭代游标  
        if(cursor != null){  
            boolean firstCursor = cursor.moveToFirst();  
            if(!firstCursor){  
                return  null;  
            }  
            for(int i = 0; i < cursor.getCount(); i++){  
                IntegralBean integralBean = new IntegralBean();  
                String integral = cursor.getString(cursor.getColumnIndex("integral"));  
                int id = cursor.getInt(cursor.getColumnIndex("_id"));  
                integralBean.setIntegral(Integer.valueOf(integral));  
               integralBean.setId(id);  
                integrals.add(integralBean);  
                cursor.moveToNext();  
            }  
            cursor.close();  
        }  
        db.close();  
        return integrals;  
    }  
      
    /** 
     * @param context 
     * @return 查询 所有的 
     */  
    public static List<IntegralBean> queryBySql(Context context) {  
        // 声明IntegralBean 的集合  
        List<IntegralBean> integrals = new ArrayList<IntegralBean>();  
        MyDBHelper mDBHelper = new MyDBHelper(context);  
        // 获取可读数据库  
        SQLiteDatabase db = mDBHelper.getReadableDatabase();  
        // 获取数据库的游标  
        Cursor cursor = db.rawQuery("select _id,integral from customer", null);  
        // 迭代游标  
        if (cursor != null) {  
            while (cursor.moveToNext()) {  
                IntegralBean integralBean = new IntegralBean();  
                String integral = cursor.getString(cursor  
                        .getColumnIndex("integral"));  
                int id = cursor.getInt(cursor.getColumnIndex("_id"));  
                integralBean.setIntegral(Integer.valueOf(integral));
                integralBean.setId(id);  
                integrals.add(integralBean);  
            }  
        }  
        db.close();  
        return integrals;  
    }  
  
    /** 
     * @param context 
     * @return Curosr 
     */  
    public static Cursor queryCursorBySql(Context context) {  
        MyDBHelper mDBHelper = new MyDBHelper(context);  
        // 获取可读数据库  
        SQLiteDatabase db = mDBHelper.getReadableDatabase();  
        // 获取数据库的游标  
        Cursor cursor = db.rawQuery("select _id,integral from customer", null);  
        return cursor;  
    }  
      
    /** 
     * @param context 
     * @return Curosr.getCount() 
     */  
    public static int getCurcorCount(Context context) {  
        MyDBHelper mDBHelper = new MyDBHelper(context);  
        // 获取可读数据库  
        SQLiteDatabase db = mDBHelper.getReadableDatabase();  
        // 获取数据库的游标  
        Cursor cursor = db.rawQuery("select _id,integral from customer", null);  
        return cursor.getCount();  
    }  
      
    /** 
     * @param context 
     * @param start 开始位置 
     * @param offset 偏移数量 
     * @return 返回从start开始往后的offset条数据的List 
     */  
    public static List<IntegralBean> queryBySqlFromOffset(Context context,  
            int start, int offset) {  
        List<IntegralBean> integrals = new ArrayList<IntegralBean>();  
        MyDBHelper myDBHelper = new MyDBHelper(context);  
        SQLiteDatabase db = myDBHelper.getReadableDatabase();  
        //获取数据库的游标  
        Cursor cursor = db.rawQuery("select _id,integral from customer limit ?,?",  
                new String[]{String.valueOf(start), String.valueOf(offset)});  
        if(cursor != null && cursor.moveToFirst()){  
            while(cursor.moveToNext()){  
                IntegralBean integralBean = new IntegralBean();  
                integralBean.setIntegral(Integer.valueOf((cursor.getString(cursor.getColumnIndex("integral")))));  
                integralBean.setId(cursor.getInt(cursor.getColumnIndex("_id")));  
                integrals.add(integralBean);  
            }  
        }  
        db.close();  
        return integrals;  
    }  
      
    /** 
     * 查询id 
     * @param context 
     * @param id 
     * @return 
     */  
    public static IntegralBean queryById(Context context, int id){  
        MyDBHelper myDBHelper = new MyDBHelper(context);  
        SQLiteDatabase db = myDBHelper.getReadableDatabase();  
        // select _id,name from gift where _id=?;  
        Cursor cursor = db.query(TABLE_NAME, new String[]{"_id", "integral"}, "_id=?",  
                new String[]{String.valueOf(id)}, null, null, null);  
        IntegralBean integralBean = null;  
        // cursor 不为空并且 存在第一条  
        if(cursor != null && cursor.moveToFirst()){  
            integralBean = new IntegralBean();  
            integralBean.setIntegral(Integer.valueOf(cursor.getString(cursor.getColumnIndex("name"))));  
            integralBean.setId(id);  
        }  
        db.close();  
        return integralBean;  
    }  
      
    /** 
     * @param context 
     * @return 查询 通过Id 出现 
     */  
    public static IntegralBean queryByIdBySql(Context context, int id) {  
        // 创建数据库的帮助类  
        MyDBHelper mDBHelper = new MyDBHelper(context);  
        // 获得一个可读数据库  
        SQLiteDatabase db = mDBHelper.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select _id,integral from customer where _id=?",  
                new String[] { String.valueOf(id) });  
        // select _id,name from gift where _id=?;  
        IntegralBean integralBean = null;  
        // cursor 不为空并且 存在第一条  
        if (cursor != null && cursor.moveToFirst()) {  
            integralBean = new IntegralBean();  
            String integral = cursor.getString(cursor.getColumnIndex("integral"));  
            integralBean.setIntegral(Integer.valueOf(integral));  
            integralBean.setId(id);  
        }  
        db.close();  
        return integralBean;  
    }  
      
    /** 
     * @param context 
     * @return the number of rows affected if a whereClause is passed in, 
     *         0 otherwise. To remove all rows and get a count pass "1"  
     *         as the whereClause. 
     */  
    public static int deleteAll(Context context){  
        MyDBHelper myDBHelper = new MyDBHelper(context);  
        SQLiteDatabase db = myDBHelper.getWritableDatabase();  
        int row = db.delete(TABLE_NAME, null, null);  
        db.close();  
        return row;  
    }  
      
    /** 
     * @param context 
     * 删除所有的数据 
     */  
    public static void deleteAllBySql(Context context) {  
        MyDBHelper mDBHelper = new MyDBHelper(context);  
        // 获得一个可读数据库  
        SQLiteDatabase db = mDBHelper.getWritableDatabase();  
        // db.delete(TABLE_NAME, null, null);  
        db.execSQL("delete from customer");  
        db.close();  
    }  
      
    /** 
     * 通过id删除某条记录 
     * @param context 
     * @param id 
     * @return 
     */  
    public static int deleteById(Context context, int id){  
        MyDBHelper myDBHelper = new MyDBHelper(context);  
        SQLiteDatabase db = myDBHelper.getWritableDatabase();  
        int row = db.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});  
        db.close();  
        return row;  
    }  
      
    /** 
     * @param context 
     * @param id 
     *            通过Id 删除某条记录 
     */  
    public static void deleteByIdBySql(Context context, int id) {  
        MyDBHelper mDBHelper = new MyDBHelper(context);  
        // 获得一个可读数据库  
        SQLiteDatabase db = mDBHelper.getWritableDatabase();  
        // db.delete(TABLE_NAME, "_id=?", new String[] { String.valueOf(id) });  
        db.execSQL("delete from customer where _id=?", new Object[] { id });  
        // 关闭数据库  
        db.close();  
    }  
      
    /** 
     * 通过id更新积分 
     * @param context 
     * @param id 
     * @param integral 
     * @return 
     */  
    public static int updateById(Context context, int id, String integral){  
        MyDBHelper myDBHelper = new MyDBHelper(context);  
        SQLiteDatabase db = myDBHelper.getWritableDatabase();  
        ContentValues contentValues = new ContentValues();  
        contentValues.put("integral", integral);  
        // 第二参数：whereClause the optional WHERE clause to apply when updating.  
        // Passing null will update all rows.  
        // 第三参数 ：whereArgs You may include ?s in the where clause, which  
        // will be replaced by the values from whereArgs. The values  
        // will be bound as Strings.  
        int row = db.update(TABLE_NAME, contentValues, "_id=?", new String[]{String.valueOf(id)});  
        db.close();  
        return row;  
    }  
      
    /** 
     * @param context 
     * @param id 
     * @param integral 
     * 通过id 更新名字 
     */  
    public static void updateByIdBySql(Context context, int id, String integral) {  
        MyDBHelper mDBHelper = new MyDBHelper(context);  
        // 获得一个可读数据库  
        SQLiteDatabase db = mDBHelper.getWritableDatabase();  
        db.execSQL("update gift set integral=? where _id=?", new Object[] { integral,  
                id });  
        // update gift set name=? where _id=?  
        db.close();  
    }  
}  
