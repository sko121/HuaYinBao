package com.thtfit.pos.util.receiver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thtfit.pos.activity.MainActivity;
import com.thtfit.pos.conn.Upload;
import com.thtfit.pos.conn.hql.DBContror;
import com.thtfit.pos.model.PushInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;

public class AlarmReceiver  extends BroadcastReceiver {
	private DBContror dbcon;
	private static List<PushInfo> mylist;
	private Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
				System.out.println("收到广播");
				this.mContext = context;
				readToFile(context);

			}

	
	public void readToFile(Context context) {
		dbcon = new DBContror(context);
		mylist = new ArrayList<PushInfo>();
		mylist.addAll(dbcon.queryAllItemByTypeFromPush("GPS"));
		mylist.addAll(dbcon.queryAllItemByTypeFromPush("Logs"));
		mylist.addAll(dbcon.queryAllItemByTypeFromPush("ActionRoutine"));
		mylist.addAll(dbcon.queryAllItemByTypeFromPush("CheckUp"));
		mylist.addAll(dbcon.queryAllItemByTypeFromPush("PayByCard"));

		postPushInfoFile(mylist);

		uploadFile("A301", getMacAddress(), "");
	}

	// 获得MAC
	public String getMacAddress() {
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	

	private void postPushInfoFile(List<PushInfo> mylist) {
		Date date = new Date();
		String timeNow = new SimpleDateFormat("yyyyMMdd")
				.format(date.getTime());
		String filePath = Environment.getExternalStorageDirectory()
				+ File.separator + "THTFIT/PushInfo" + File.separator + timeNow
				+ ".json";
		String content = "";
		for (PushInfo info : mylist) {
			String tmpContent = info.getType().equals("GPS") ? info.getType() + "	" + info.getTime() + "	("
					+ info.getContent() + ")" : info.getType() + "	" + info.getTime() + "	"
							+ info.getContent();
			
			content = content + tmpContent + "\r\n";
		}

		// 检查上传信息文件
		File infoFile = new File(filePath);
		if (!infoFile.exists()) {
			try {
				infoFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			writeByFileWrite(filePath, content);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * 向文件写入内容
	 * 
	 * @param destFile
	 * 
	 * @throws IOException
	 */

	public static void writeByFileWrite(String destFile, String content)

	throws IOException {

		File file = new File(destFile);

		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file, true));
			bw.write("\r\n" + content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void uploadFile(final String dClient, final String dMac,
			final String responseFilter) {
		new Thread() {
			public void run() {
				Date date = new Date();
				String timeNow = new SimpleDateFormat("yyyyMMdd").format(date
						.getTime());
				String filePath = Environment.getExternalStorageDirectory()
						+ File.separator + "THTFIT/PushInfo" + File.separator
						+ timeNow + ".json";

				String[] HttpContent = { "", "0", "0", "" };
				Map<String, String> params = new HashMap<String, String>();
				Map<String, File> files = new HashMap<String, File>();

				files.put(timeNow + ".json", new File(filePath));

				HttpContent = Upload.execute(responseFilter,
						"http://192.168.130.85:8080/SmartPos/clients/infoPush?json={'dClient':'"
								+ dClient + "','dMac':'" + dMac + "'}", params,
						files);
				System.out.println("HttpContent[3]=========" + HttpContent[3]);
			}
		}.start();
	}
	
}
