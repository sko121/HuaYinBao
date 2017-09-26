package com.thtfit.pos.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.thtfit.pos.conn.HttpConn;
import com.thtfit.pos.conn.hql.DBContror;
import com.thtfit.pos.conn.hql.DBHelper;
import com.thtfit.pos.debug.DebugPrint;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.model.Transaction;
import com.thtfit.pos.util.Config;
import com.thtfit.pos.util.POSLog;
import com.thtfit.pos.util.RootCmd;
import com.thtfit.pos.util.Utils;
import com.thtfit.pos.util.receiver.AlarmReceiver;

public class POSService extends Service
{
	//http://58.61.149.122:8082/SmartPos//clients/clientsLogin.action?loginName=admin@admin&passWord=123456
	//http://192.168.70.66:2300/SmartPos/management/main
	public static final String RECEIVE_DATA = "com.thtfit.pos.service.Receiver.action.Main.RECEIVE_DATA";
	// 接收前台 请求
	public static final String RECEIVE_REQUEST = "com.thtfit.pos.service.Receiver.action.Service.RECEIVE_REQUEST";
	// 登录验证
	public static final String LOGINA_CTIVITY = "LoginActivity.login";
	// 提交设备信息
	public static final String MAINACTIVITY_DEVICE = "MainActivity.device";
	// 拉取商品数据
	public static final String GET_MAIN_DATA = "POSService.getMainData";
	// 上传订单数据
	public static final String SIGNATRUE_ACTIVITY_ORDER = "SignatureActivity.Order";

	public DBHelper dBHelper = null;

	// 请求root权限
	private static final int TAG_ROOTLIMIT = 0;
	// 启动定时上传任务
	private static final int TAG_UPLOADALARM = 1;
	// 启动定时获取位置任务
	private static final int TAG_GPSALARM = 2;
// 启动推送接收服务
	private static final int TAG_PUSH = 3;

	private String TAG = POSService.class.getSimpleName();
	
	private POSLog posLog = new POSLog();

	private int loginStatus = 0;

	private String login_UserName = "";
	private String login_Password = "";

	private String mainLoginResponseFilter = "";
	private String mainLoginResult = "";

	private BroadcastReceiver Receiver_Service;

	private MyHandler myHandler = new MyHandler(this);

	public String sessionID = "";

	public int connRetryCount = 0;

	private final static String serverAddress = "192.168.200.239:8080/SmartPos";
	//private final static String serverAddress = "121.15.134.44:020/SmartPos"
//	private final static String serverAddress = "58.61.149.122:8082/SmartPos";
	//private final static String serverAddress = "192.168.70.66:8080/SmartPos";
//	private final static String serverAddress = "http://192.168.70.66:2300/SmartPos";

	private DBContror dbcon = null;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		DebugPrint.d(TAG, "====start POSService====");

		initDirFile();

		myHandler.sendEmptyMessage(TAG_ROOTLIMIT);
		myHandler.sendEmptyMessage(TAG_UPLOADALARM);
		myHandler.sendEmptyMessage(TAG_GPSALARM);
		myHandler.sendEmptyMessage(TAG_PUSH);

		// 接收广播
		Receiver_Service = new BroadcastReceiver()
		{

			@Override
			public void onReceive(Context context, Intent intent)
			{

				String receiveAction = intent.getAction();
				// 如果action为空则receiveAction为"",否则为本身
				receiveAction = receiveAction == null ? "" : receiveAction;

				DebugPrint.d(TAG, "====at Receiver_Service====");
				// 接收前台 请求
				if (receiveAction
						.equals("com.thtfit.pos.service.Receiver.action.Service.RECEIVE_REQUEST")) {
					
					String requestAction = intent
							.getStringExtra("requestAction");
					requestAction = requestAction == null ? "" : requestAction;
					DebugPrint.d(TAG, "POSService接收到广播动作:requestAction:"
							+ requestAction);

					String responseFilter = intent
							.getStringExtra("responseFilter");
					responseFilter = responseFilter == null ? ""
							: responseFilter;
					DebugPrint.d(TAG, "POSService接收到返回路径:" + responseFilter);

					// 登录验证
					if (requestAction.equals("LoginActivity.login")) {
						DebugPrint.d(TAG, "====start LoginActivity.login====");

						String tmpLogin_UserName = intent
								.getStringExtra("loginName");
						if (tmpLogin_UserName != null
								&& tmpLogin_UserName.length() > 0) {
							login_UserName = tmpLogin_UserName;
						}
						String tmpLogin_Password = intent
								.getStringExtra("passWord");
						if (tmpLogin_Password != null
								&& tmpLogin_Password.length() > 0) {
							login_Password = tmpLogin_Password;
						}
						mainLoginResponseFilter = responseFilter;

						List<NameValuePair> parameter = new ArrayList<NameValuePair>();
						parameter.add(new BasicNameValuePair("loginName",
								login_UserName));
						parameter.add(new BasicNameValuePair("passWord",
								login_Password));

						//http://192.168.200.239:8080/SmartPos/clients/clientsLogin.action?loginName=admin@admin&passWord=123456 
						//new : 58.61.149.122
						login("http://" + serverAddress
								+ "/clients/clientsLogin.action?", parameter);
					}
					// 提交设备信息
					else if (requestAction.equals("MainActivity.device")) {
						DebugPrint.d(TAG, "====at MainActivity.device====");
						// 处理提交的参数

						JSONObject jsonObject = new JSONObject();
						try {
							jsonObject.put("dClient",
									intent.getStringExtra("dClient"));
							jsonObject.put("dSysversion",
									intent.getStringExtra("dSysversion"));
							jsonObject.put("dName",
									intent.getStringExtra("dName"));
							jsonObject.put("dMac",
									intent.getStringExtra("dMac"));
							jsonObject.put("dGPS",
									intent.getStringExtra("dGPS"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						String tmpJson = jsonObject.toString();
						String json = tmpJson.replace("\"", "'");

						// http://192.168.200.239:8080/SmartPos/clients/registration.isu?json={'dClient':'A001','dSysversion':'1.0','info':[{'dName':'Brett','dGPS':'SDF','dMac':'00-00-00-00-00-00'},{'dName':'Ee','dGPS':'SDF','dMac':'00-00-00-00-00-01'}]}
//						Log.d("luzhaojie", "url : " + "http://" + serverAddress + "/clients/registration.action?json=" + json);
						connPostData("http://" + serverAddress + "/clients/registration.action?json=" + json, "",
						 responseFilter, "postData");
					}

					// 拉取商品数据
					else if (requestAction.equals("POSService.getMainData")) {
//						 http://192.168.200.239:8080/SmartPos/clients/products.action
						Log.d("luzhaojie", "broadcast : POSService.getMainData");// by Lu
						Log.d("luzhaojie", "responseFilter == " + responseFilter);// by Lu
						getMainData("http://" + serverAddress + "/clients/products.action",null,responseFilter,"");
					}
					//上传订单数据
					else if(requestAction.equals("SignatureActivity.Order")){
						Transaction transaction = (Transaction) intent.getSerializableExtra("transaction");
						List<NameValuePair> parameter = new ArrayList<NameValuePair>();
						parameter.add(new BasicNameValuePair("orderNumber",
								transaction.getOrderNumber()));
						parameter.add(new BasicNameValuePair("totalPrice",
								transaction.getTotalPrice()));
						parameter.add(new BasicNameValuePair("listInfo",
								transaction.getListInfo()));
						parameter.add(new BasicNameValuePair("clerk",
								transaction.getClerk()));
						parameter.add(new BasicNameValuePair("cardInfo",
								transaction.getCardInfo()));
						parameter.add(new BasicNameValuePair("createTime",
								transaction.getTime()));
						
						connPostData("http://" + serverAddress + "/dealservice/addDeal.action?", parameter,
								 responseFilter, "Order");
					}
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter(
				"com.thtfit.pos.service.Receiver.action.Service.RECEIVE_REQUEST");
		intentFilter
				.addAction("com.thtfit.pos.service.Receiver.action.START_AUTO_GET_SERVICE_DATA");
		intentFilter.addAction("com.thtfit.pos.service.Receiver.action.LOGIN");
		registerReceiver(Receiver_Service, intentFilter);
		
		//by Lu : 打开app即从网上拉取数据
//		Log.d("luzhaojie", "打开app即从网上拉取数据 : POSService");
//		Intent tmpIntent = new Intent(
//				"com.thtfit.pos.service.Receiver.action.Service.RECEIVE_REQUEST");
//		tmpIntent.putExtra("requestAction",
//				"POSService.getMainData");
//		sendBroadcast(tmpIntent);
		
		//卸载重装之后偶尔出现打开商品界面商品加载不出来的情况，故取消广播通信，直接加载数据
		getMainData("http://" + serverAddress + "/clients/products.action",null,null,"");
	}

	private void getMainData(final String url,
			final List<NameValuePair> parameter, final String responseFilter,
			final String responseAction) {
		new Thread() {
			public void run() {
				connRetryCount++;
				// 开始连接
				String[] HttpContent = { "", "", "0", "0", "" };
				HttpContent = HttpConn.getServerHttpData(url, parameter,
						sessionID);

				// 连接失败
				if (!HttpContent[0].equals("200")) {
					Intent intent = new Intent(responseFilter);
					intent.putExtra("responseCode", "-1");
					sendBroadcast(intent);
					return;
				}

				DebugPrint.d(TAG, "posservice=========HttpContent[4]="
						+ HttpContent[4]);

				// 会话断开
				if (HttpContent[4].equals("{'Result':'0'}")) {
					// 重试超过次数
					if (connRetryCount >= 2) {
						return;
					}
					return;
				}

				JSONObject jsonObject = null;
				JSONArray productsArray = null;
				JSONArray typeArray = null;
				try {
					jsonObject = new JSONObject(HttpContent[4]);

					productsArray = jsonObject.getJSONArray("products");
					typeArray = jsonObject.getJSONArray("typeArray");

					if (typeArray.length() > 0) {
						dbcon = new DBContror(getApplicationContext());
						dbcon.clearTypeDate();
						dbcon.clearProDate();

						for (int i = 0; i < typeArray.length(); i++) {
							JSONObject jsonItem = typeArray.getJSONObject(i);
							Integer typeId = Integer.valueOf(jsonItem.getString("typeId"));
							String typeName = jsonItem.getString("typeName");
							dbcon.insertTypeItem(typeId, typeName);
						}
					}

					for (int i = 0; i < productsArray.length(); i++) {
						JSONObject jsonItem = productsArray.getJSONObject(i);
						String imagePath = serverAddress
								+ jsonItem.getString("faceImage");
						Integer typeName = Integer.valueOf(jsonItem.getString("typeId"));

						Product product = new Product();
						product.setDescribe(jsonItem.getString("description"));
						product.setImagePath(imagePath);
						product.setType(typeName);
						product.setName(jsonItem.getString("proName"));
						product.setPrice(jsonItem.getString("proPrice"));
						product.setStock(jsonItem.getString("proStore"));
						product.setProId(Integer.parseInt(jsonItem
								.getString("proId")));
						product.setSerial(Integer.parseInt(jsonItem
								.getString("proSerial")));
						product.setNote(jsonItem.optString("proNote"));

						dbcon = new DBContror(getApplicationContext());
						dbcon.insertItem(product);

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	private void connPostData(final String url,
			final List<NameValuePair> parameter, final String responseFilter,
			final String responseAction) {
		new Thread() {
			public void run() {
				connRetryCount++;
				// 开始连接
				String[] HttpContent = { "", "", "0", "0", "" };
				HttpContent = HttpConn.getServerHttpData(url, parameter,
						sessionID);
				
				// 连接失败
				if (!HttpContent[0].equals("200")) {
					Intent intent = new Intent(responseFilter);
					intent.putExtra("responseCode", "-1");
					sendBroadcast(intent);
					return;
				}
				// 会话断开
				if (HttpContent[4].equals("{'Result':'0'}")) {
					// 重试超过次数
					if (connRetryCount >= 2) {
						return;
					}
					return;
				}
				// 返回数据
				Intent tmpIntent = new Intent(responseFilter);
				tmpIntent.putExtra("responseAction", responseAction);
				tmpIntent.putExtra("responseCode", "1");
				tmpIntent.putExtra("responseResult", HttpContent[4]);
				sendBroadcast(tmpIntent);
			}
		}.start();
	}
	
	private void connPostData(final String url,
			final String parameter, final String responseFilter,
			final String responseAction) {
		new Thread() {
			public void run() {
				connRetryCount++;
				// 开始连接
				String[] HttpContent = { "", "", "0", "0", "" };
				HttpContent = HttpConn.getServerHttpData(url, null,
						sessionID);
//				for(int i = 0; i < HttpContent.length; i++) {
//					Log.d("luzhaojie", "HttpContent[" + i + "] : " + HttpContent[i]);
//				}

				// 连接失败
				if (!HttpContent[0].equals("200")) {
					Log.d("luzhaojie", "connPostData : 连接失败...");
					Intent intent = new Intent(responseFilter);
					intent.putExtra("responseCode", "-1");
					sendBroadcast(intent);
					return;
				}
				// 会话断开
				if (HttpContent[4].equals("{'Result':'0'}")) {
					// 重试超过次数
					if (connRetryCount >= 2) {
						return;
					}
					return;
				}
				// 返回数据
				Intent tmpIntent = new Intent(responseFilter);
				tmpIntent.putExtra("responseAction", responseAction);
				tmpIntent.putExtra("responseCode", "1");
				tmpIntent.putExtra("responseResult", HttpContent[4]);
				sendBroadcast(tmpIntent);
			}
		}.start();
	}

	// 登录处理
	private void login(final String url, final List<NameValuePair> parameter) {

		new Thread() {
			public void run() {
				connRetryCount++;
				// 开始连接
				String[] HttpContent = { "", "", "0", "0", "" };
				HttpContent = HttpConn.getServerHttpData(url, parameter,
						sessionID);

				// 连接失败
				if (!HttpContent[0].equals("200")) {
					mainLoginResult = "服务器连接失败";
					// 登录返回响应
					Intent responseIntent = new Intent(mainLoginResponseFilter);
					responseIntent.putExtra("loginResult", mainLoginResult);
					sendBroadcast(responseIntent);
				}

				if (HttpContent[4] != null) {
					JSONObject jsonObject;
					//by Lu
					Pattern p=Pattern.compile("用户不存在"); //[\u4e00-\u9fa5]
					Matcher m=p.matcher(HttpContent[4]);
					if(m.find()) {//by Lu 用户不存在或禁用，请重新登陆！
						Log.d("luzhaojie", "It's Chinese.");
						Intent responseIntent = new Intent(mainLoginResponseFilter);
						responseIntent.putExtra("loginResult", HttpContent[4]);
						sendBroadcast(responseIntent);
					} else {
						try {
							jsonObject = new JSONObject(HttpContent[4]);
							String loginResult = jsonObject.getString("Result")
									.toString();
							
							if ("success".equals(loginResult)) {
								
								int loginStatus = 2;
								
								SharedPreferences preferences = getSharedPreferences(
										"login", Context.MODE_PRIVATE);
								
								int loginStatusManager = preferences.getInt(
										"loginStatusManager", 0);
								
								if (0 == loginStatusManager){
									
									Config.addSaveOption("loginNameManager",
											parameter.get(0).getValue());
									Config.addSaveOption("passWordManager", parameter.get(1)
											.getValue());
								}
								
								Config.addSaveOption("loginName", parameter.get(0)
										.getValue());
								Config.addSaveOption("passWord", parameter.get(1)
										.getValue());
								Config.save();
								
								loginStatusManager = loginStatusManager <= 0 ? loginStatusManager = 1
										: loginStatusManager;
								
								Editor editor = preferences.edit();
								editor.putInt("loginStatus", loginStatus);
								editor.putInt("loginStatusManager",
										loginStatusManager);
								editor.commit();
								
								// 登录返回响应
								Intent responseIntent = new Intent(
										mainLoginResponseFilter);
								responseIntent.putExtra("loginResult",
										loginResult);
								sendBroadcast(responseIntent);
								
								// 登录成功从网络拉取数据
//								Intent tmpIntent = new Intent(
//										"com.thtfit.pos.service.Receiver.action.Service.RECEIVE_REQUEST");
//								tmpIntent.putExtra("requestAction",
//										"POSService.getMainData");
//								sendBroadcast(tmpIntent);
							} else {
								// 登录返回响应
								Intent responseIntent = new Intent(
										mainLoginResponseFilter);
								responseIntent.putExtra("loginResult", loginResult);
								sendBroadcast(responseIntent);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}

	// 初始化文件目录
	private void initDirFile() {

		// 检查目录
		File LogDir = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "THTFIT/PushInfo");
		if (!LogDir.exists()) {
			LogDir.mkdirs();	
		}

		// 检查配置文件
		File configFile = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "THTFIT" + File.separator + "config.ini");
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/*
		 * // 检查上传信息文件 File infoFile = new
		 * File(Environment.getExternalStorageDirectory() + File.separator +
		 * "THTFIT" + File.separator + "pushinfo.json"); if (!infoFile.exists())
		 * { try { infoFile.createNewFile(); } catch (IOException e) {
		 * e.printStackTrace(); } }
		 */
	}

	// 获得Root权限
	public void initRootLimit() {
		if (!RootCmd.haveRoot()) {
			String rootReasult = RootCmd.execRootCmd();
			posLog.setLogs(getApplicationContext(), rootReasult);
		}
	}

	// 启动定时上传任务
	public void startUploadAlarm() {
		Intent intent = new Intent("com.thtfit.pos.alarm");
		intent.setClass(this, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

		alarm.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 10 * 1000, 24 * 60 * 60 * 1000,
				sender);
	}

	// 启动推送接收服务
	public void startPushServer() {

		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(
						getApplicationContext(), "sxO0cXIoMw5d8rDvFmI2GtsZ"));
		PushManager.setNoDisturbMode(getApplicationContext(), 0, 0, 0, 0);
	}

	// 启动定时获取位置任务
	public void startGPSAlarm() {
		Intent intent = new Intent("com.thtfit.pos.alarm");
		intent.setClass(this, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

		alarm.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 10 * 1000, 24 * 60 * 60 * 1000,
				sender);
	}

	// 启动定时上传机制
	public void alarmForUpload() {
		long NowTime = System.currentTimeMillis(); // 当前时间
		Date randomdate = randomdate("00:00:00", "06:00:00");

		long Today00Hour = 0; // 今天0点
		long StartTime = 0; // 定时时间

		try {
			Today00Hour = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.CHINA)).parse(
					new SimpleDateFormat("yyyy-MM-dd 00:00:00", Locale.CHINA)
							.format(new java.util.Date())).getTime();
		} catch (Exception e) {
			DebugPrint.d(TAG, "====autoRunAndCloseProgram===" + e);
		}

		if (NowTime - Today00Hour <= 0) {
			StartTime = Today00Hour - NowTime + randomdate.getTime();
		} else if (Today00Hour + 6 * 60 * 60 * 1000 - NowTime >= 0) {
			StartTime = NowTime - Today00Hour + randomdate.getTime();
		}

		// 初始化定时器
		Intent intent = new Intent("com.thtfit.pos.alarm");
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_NO_CREATE);
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

		// 取消旧定时任务
		alarm.cancel(sender);

		// 启动定时任务
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, StartTime,
				24 * 60 * 60 * 1000, sender);
	}

	/**
	 * 获取指定范围随机时间 设定范围需大于2小时
	 * 
	 * @param begindate
	 * @param enddate
	 * @return
	 */
	private static Date randomdate(String begindate, String enddate) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
			Date start = format.parse(begindate);// 构造开始日期
			Date end = format.parse(enddate);// 构造结束日期
			// gettime()表示返回自 1970 年 1 月 1 日 00:00:00 gmt 以来此 date 对象表示的毫秒数。
			if (start.getTime() >= end.getTime()) {
				return null;
			}
			long date = random(start.getTime(), end.getTime());
			return new Date(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	class MyHandler extends Handler {
		private POSService posService;

		public MyHandler(POSService posService) {
			this.posService = posService;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case POSService.TAG_ROOTLIMIT:
				initRootLimit();
				break;
			case POSService.TAG_UPLOADALARM:
				startUploadAlarm();
				break;
			case POSService.TAG_GPSALARM:
				startGPSAlarm();
				break;
			case POSService.TAG_PUSH:
				startPushServer();
				break;
			}
			super.handleMessage(null);
		}

	}

	private static long random(long begin, long end) {
		long rtn = begin + (long) (Math.random() * (end - begin));
		// 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
		if (rtn == begin || rtn == end) {
			return random(begin, end);
		}
		return rtn;
	}

	// 保证service kill 后能重新建立
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	//
	public void updateTypeDate() {

	}

	// 销毁对象
	public void onDestroy() {
		try {
			unregisterReceiver(Receiver_Service);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

}
