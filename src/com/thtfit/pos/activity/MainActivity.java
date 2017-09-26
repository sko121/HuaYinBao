package com.thtfit.pos.activity;

import static android.content.Intent.ACTION_MAIN;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.linphone.LinphoneService;
import org.linphone.mediastream.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.thtfit.pos.R;
import com.thtfit.pos.debug.DebugPrint;
import com.thtfit.pos.service.POSService;
import com.thtfit.pos.service.PosApplication;
import com.thtfit.pos.util.POSLog;
import com.thtfit.pos.util.Utils;

public class MainActivity extends FragmentActivity
{
	private static MainActivity instance;
	private LocationManager locationManager;
	private Location loc;
	private POSLog posLog = new POSLog();
	private static final String KEY = "K9uYlOFfCkoG6I7dC13wqYlm";

	/* debug */
	private static String TAG = MainActivity.class.getSimpleName();
	
	//public static final boolean isDebug = false;
	//public static final boolean isDebug = false;
	//public DebugPrint LOG = new DebugPrint(isDebug, TAG);

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals("com.thtfit.pos.service.Receiver.action.Main.RECEIVE_DATA"))
			{
				final String responseResult = intent.getStringExtra("responseResult");
				
				DebugPrint.d("luzhaojie","response result : " + responseResult);
				if ("{'Result':'1'}".equals(responseResult))
				{
					Toast.makeText(getApplicationContext(), "The background data has been upload", Toast.LENGTH_SHORT)
							.show();
				}
				else if ("{'Result':'2'}".equals(responseResult))
				{
					Toast.makeText(getApplicationContext(), "The background data has been upload", Toast.LENGTH_SHORT)
							.show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "The background to upload data exception",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	private void registerBroadcasts()
	{
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.thtfit.pos.service.Receiver.action.Main.RECEIVE_DATA");
		mIntentFilter.setPriority(100);
		registerReceiver(mBroadcastReceiver, mIntentFilter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initLanguagesConfig();

		instance = this;
		
		/* shows the main UI surface. */
		setContentView(R.layout.activity_main);

		/* started services. */
		initServices();

		/* register system broadcast. */ 
		registerBroadcasts();

		/* login server. */
//		postDriveInfo();  //by Lu : cause A83T6.0 send to server return 505

		/* set system info for test. */
		setSysInfo();

		//by Lu
//		SnmpServer snmpServer = new SnmpServer(this);
//		String string = snmpServer.stringFromJNI();
//		Log.i("luzhaojie", string);
		
		DebugPrint.d(TAG,"start MainActivity");
	}

	private void initLanguagesConfig(){
		Resources resources =getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();
		Context ctx = getApplication();
		SharedPreferences spLanguageConfig = ctx.getSharedPreferences("LANGUAGECONFIG", Context.MODE_PRIVATE);
		String languages = spLanguageConfig.getString("languages", "default");
		if(languages.equals("default")){
			config.locale = Locale.getDefault();
		}else if(languages.equals("zh_CN")){
			config.locale = Locale.SIMPLIFIED_CHINESE;
		}else if(languages.equals("en")){
			config.locale = Locale.ENGLISH;
		}else{
			config.locale = Locale.ENGLISH;
		}
		resources.updateConfiguration(config, dm);
		resources.flushLayoutCache();
	}
	
	private void initServices()
	{
		/* started the daemon of the service */
		Intent serviceIntent = new Intent(MainActivity.this, POSService.class);
		serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(serviceIntent);
		
		startPushServer();
		
		startlinphoneServer(); 
	}                           
	
	private void startlinphoneServer() {             
		// Used to change for the lifetime of the app the name used to tag the logs
				new Log(getResources().getString(R.string.app_name), !getResources().getBoolean(R.bool.disable_every_log));
				
				// Hack to avoid to draw twice LinphoneActivity on tablets
		        if (getResources().getBoolean(R.bool.orientation_portrait_only)) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
		        
		        if (LinphoneService.isReady()) {
					onServiceReady();
				} else {
					// start linphone as background  
					startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							while(!LinphoneService.isReady()){
								SystemClock.sleep(30);
							}
							onServiceReady();
						}
					}).start();
				}
	}

	protected void onServiceReady() {
		LinphoneService.instance().setActivityToLaunchOnIncomingReceived(MainActivity.class);
	}
	
	// 启动推送接收服务
	public void startPushServer()
	{
		if (!Utils.isBind(getApplicationContext()))
		{
			PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, KEY);
		}
	}

	private void postDriveInfo()
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					sleep(3000);

					// 发送后台登录请求
					Intent tmpIntent = new Intent("com.thtfit.pos.service.Receiver.action.Service.RECEIVE_REQUEST");
					tmpIntent.putExtra("requestAction", "MainActivity.device");
					tmpIntent.putExtra("responseFilter", "com.thtfit.pos.service.Receiver.action.Main.RECEIVE_DATA");
					tmpIntent.putExtra("dName", getIMEI());
					tmpIntent.putExtra("dSysversion", Build.DISPLAY);
					tmpIntent.putExtra("dMac", getMacAddress());
					// tmpIntent.putExtra("dGPS",
					// getLocation(MainActivity.this)[0] + ","
					// + getLocation(MainActivity.this)[1]);
					tmpIntent.putExtra("dGPS", "116.417854,39.921988");
					tmpIntent.putExtra("dClient", "A301");
					sendBroadcast(tmpIntent); 
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void postPushInfoFile()
	{
		Date date = new Date();
		String timeNow = new SimpleDateFormat("yyyyMMdd").format(date.getTime());
		String filePath = Environment.getExternalStorageDirectory() + File.separator + "THTFIT/PushInfo"
				+ File.separator + timeNow + ".json";

		String content = "HELLO WORLD !";

		// 检查上传信息文件
		File infoFile = new File(filePath);
		if (!infoFile.exists())
		{
			try
			{
				infoFile.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			writeByFileWrite(filePath, content);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void setSysInfo()
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					sleep(5000);

					posLog.setCheckUp(getApplicationContext(), "THIS IS TEST DATA TOO!");
					posLog.setGPS(getApplicationContext(), "116.417854,39.921988");
					posLog.setGPS(getApplicationContext(), "116.417854,39.921988");
					posLog.setLogs(getApplicationContext(), "THIS IS TEST DATA TOO!");
					posLog.setActionRoutine(getApplicationContext(), "THIS IS TEST DATA TOO!");
					posLog.setLogs(getApplicationContext(), "THIS IS TEST DATA TOO!");
					posLog.setPayByCard(getApplicationContext(), "HELLO WORLD !");
					posLog.setCheckUp(getApplicationContext(), "THIS IS TEST DATA TOO!");
					// postPushInfoFile();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}

	private static void writeByFileWrite(String destFile, String content) throws IOException
	{
		File file = new File(destFile);

		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(file, true));
			bw.write("\r\n" + content);
			bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// 获得设备ID(IMEI)
	private String getIMEI()
	{
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	// 获得MAC
	private String getMacAddress()
	{
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	// 获得位置信息
	private String[] getLocation(Context mContext)
	{
		String[] location = { "0", "0" };
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		boolean isNetworkConnected = isNetworkConnected(MainActivity.this);
		try
		{
			if (network || isNetworkConnected)
			{
				// 使用网络信息获取地理位置
				loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				location[0] = loc.getLatitude() + "";
				location[1] = loc.getLongitude() + "";
			}
			else if (gps)
			{
				// 使用GPS信息获取地理位置
				loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				location[0] = loc.getLatitude() + "";
				location[1] = loc.getLongitude() + "";
			}
			else
			{
				Toast.makeText(mContext, "请打开GPS", Toast.LENGTH_SHORT).show();
			}
		}
		catch (Exception e)
		{
			DebugPrint.d(TAG,"getLocation exception : " + e.toString());
		}
		return location;
	}

	private boolean isNetworkConnected(Context context)
	{
		if (context != null)
		{
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null)
			{
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		PosApplication application = new PosApplication();
		if (application.getIsFirGesture())
		{
			Intent intent = new Intent(this, LockSetupActivity.class);
			startActivity(intent);
			application.setIsFirGesture(false);
		}
		if (application.getIsVerification())
		{
			Intent intent = new Intent(this, LockActivity.class);
			startActivity(intent);
			application.setIsVerification(false);
		}
	}

	// 销毁对象
	public void onDestroy()
	{
		try
		{
			unregisterReceiver(mBroadcastReceiver);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.onDestroy();
	}

	public static boolean isInstanciated() {
		// TODO Auto-generated method stub
		return instance != null;
	}

	public static MainActivity instance() {
		// TODO Auto-generated method stub
		return instance;
	}

	public void sendLogs(Context applicationContext, String info) {
		
	}
}
