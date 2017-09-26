package com.thtfit.pos.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DriveInfo extends Activity{
	
	public void getDriveInfo(){
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		  StringBuilder sb = new StringBuilder();  
		  sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());  //获得设备名称/标识（IMEI）
		  String systemBuld = android.os.Build.VERSION.RELEASE; //获得系统版本号
		  Log.e("info", sb.toString()); 
	}
	
	//获得设备位置
	
	
	
	
	
	//获得设备MAC地址
	public String getMac(){
               String macSerial = null;
               String str = "";
               try {
                       Process pp = Runtime.getRuntime().exec(
                                       "cat /sys/class/net/wlan0/address ");
                       InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                       LineNumberReader input = new LineNumberReader(ir);


                       for (; null != str;) {
                               str = input.readLine();
                               if (str != null) {
                                       macSerial = str.trim();// 去空格
                                       break;
                               }
                       }
               } catch (IOException ex) {
                       // 赋予默认值
                       ex.printStackTrace();
               }
            return macSerial;	 
       } 
	
	//获得设备的客户代码

}
