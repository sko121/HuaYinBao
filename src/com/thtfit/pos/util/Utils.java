package com.thtfit.pos.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class Utils {
	
    public static final String RESPONSE_METHOD = "method";
    public static final String RESPONSE_CONTENT = "content";
    public static final String RESPONSE_ERRCODE = "errcode";
    public static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
    public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
    public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
    public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
    public static final String EXTRA_ACCESS_TOKEN = "access_token";
    public static final String EXTRA_MESSAGE = "message";
    
    public static String logStringCache = "";
	
    
	private static final String BIND__FLAG = "bind_flag";

	public static boolean isBind(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences("pre_tuisong",
				Context.MODE_PRIVATE);
		return sp.getBoolean(BIND__FLAG, false);
	}

	public static void bind(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences("pre_tuisong",
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(BIND__FLAG, true).commit();
	}
	
	
	public static void unbind(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences("pre_tuisong",
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(BIND__FLAG, false).commit();
	}
    
    
	 // 获取百度推送ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        return apiKey;
    }
    

    // 用share preference来实现是否绑定的开关。在ionBind且成功时设置true，unBind且成功时设置false
    public static boolean hasBind(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        String flag = sp.getString("bind_flag", "");
        if ("ok".equalsIgnoreCase(flag)) {
            return true;
        }
        return false;
    }

    public static void setBind(Context context, boolean flag) {
        String flagStr = "not";
        if (flag) {
            flagStr = "ok";
        }
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("bind_flag", flagStr);
        editor.commit();
    }

    

	/**
	 * 将毫秒转换成时分秒（时长）
	 * 
	 * @param l
	 * @return
	 */
	public static String formatLongToTimeStr(Long l) {
		String str = "";
		long hour = 0;
		long minute = 0;
		float second = 0;
		second = (float) l / (float) 1000;
		if (second > 60) {
			minute = (long) (second / 60);
			second = second % 60;
			if (minute > 60) {
				hour = minute / 60;
				minute = minute % 60;
				str = hour + "h" + minute + "m" + second + "s";
			} else {
				str = minute + "m" + second + "s";
			}
		} else {
			str = second + "s";
		}
		return str;

	}

	public static String removeAmountSymbol(String amount) {
		System.out.println("amount=<<"+amount+">>");
		
		
		amount = amount.replace("￥", "");
		amount = amount.replace(".", "");
		amount = amount.replace(",", "");
		while (amount.startsWith("0")) {
			amount = amount.substring(1);
		}
		System.out.println("amount=<<"+amount+">>");
		return amount;
	}

	public static String removeAmountDollar(String amount) {
		amount = amount.replace("￥", "");
		amount = amount.replace(",", "");

		return amount;
	}
	
	
	
}
