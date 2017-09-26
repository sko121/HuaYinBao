package com.thtfit.pos.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

import com.thtfit.pos.conn.hql.DBContror;
import com.thtfit.pos.model.PushInfo;

/**
 * 处理即将上传的日志内容
 * 
 * @param value
 * @param content
 * 
 * @return
 */

public class POSLog {
	private DBContror db;
	private Date date = new Date();

	public void writeData(Context context,String type, String content) {
		db = new DBContror(context);
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		PushInfo pushInfo = new PushInfo();
		pushInfo.setType(type);
		pushInfo.setContent(content);
		pushInfo.setTime(time);
		
		db.insertItemToPush(pushInfo);
	}
	
	public void setPayByCard(Context context,String content) {
		writeData(context,"PayByCard", content);
	}

	public void setCheckUp(Context context,String content) {
		writeData(context,"CheckUp", content);
	}

	public void setActionRoutine(Context context,String content) {
		writeData(context,"ActionRoutine", content);
	}

	public void setLogs(Context context,String content) {
		writeData(context,"Logs", content);
	}

	public void setGPS(Context context,String content) {
		writeData(context,"GPS", content);
	}

}
