package com.thtfit.pos.activity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import com.imagpay.Apdu_Send;
import com.imagpay.MessageHandler;
import com.imagpay.Settings;
import com.imagpay.SwipeEvent;
import com.imagpay.SwipeListener;
import com.imagpay.ttl.TTLHandler;
import com.imagpay.utils.StringUtils;
import com.thtfit.pos.R;
import com.thtfit.pos.adapter.PayListAdapter;
import com.thtfit.pos.api.Money;
import com.thtfit.pos.debug.DebugPrint;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.service.PosApplication;
import com.thtfit.pos.util.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ZCSwipeCardActivity extends FragmentActivity{
private String TAG = "SwipeCardActivity";
	
	public static List<Product> listItems = new ArrayList<Product>();

	private Context mContext;

	private Button btn_nfc;
	private EditText amountEditText;
	private TextView statusEditText;
	private ListView totalList;

	private Button btn_ic;
	private Button btn_mag;
	public static final String POS_BLUETOOTH_ADDRESS = "POS_BLUETOOTH_ADDRESS";
	private AnimationDrawable animScan;
	//private List<BluetoothDevice> lstDevScanned;
	private BroadcastReceiver recvBTScan = null;
	private Handler hdStopScan;
	private Button btn_dev_conn;
	private String mReceiveAmount;
	private String mSuccessAmount;

	private boolean isTest = false;

	private boolean isUart = true;
	private BluetoothAdapter mAdapter;

	private ImageButton mBtnAction;
	
	private String version;
	
	TTLHandler handler;
	Settings settings;
	boolean flag = false;
	MessageHandler _mHandler;
	PosApplication application;
	boolean magFlag = false;
	
	private static final int MSG_ICCARD = 0;
	private static final int MSG_MAGCARD = 1;
	private static final int MSG_NFCCARD = 2;

	private static final int PROGRESS_UP = 1001;
	private Handler updata_handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PROGRESS_UP:
				statusEditText.setText(msg.obj.toString() + "%");
				break;

			default:
				break;
			}
		};
	};


	@SuppressLint("NewApi")
	private void StopScanBTPos() {
		if (mAdapter != null) {
			mAdapter.cancelDiscovery();
			mAdapter = null;
		}

	}


	@SuppressLint("NewApi")
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (!isUart) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		mContext = this;
		setContentView(R.layout.activity_swipe_zc);
		btn_dev_conn = (Button)findViewById(R.id.btn_dev_conn);
		listItems = (List<Product>) getIntent().getSerializableExtra("listItems");
		totalList = (ListView) findViewById(R.id.swpie_total_list);
		mReceiveAmount = getIntent().getStringExtra("amount");
		showTotalList();
		
		hdStopScan = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 10024) {
					StopScanBTPos();
				}
			}
		};

		btn_ic = (Button) findViewById(R.id.btn_ic);
		amountEditText = (EditText) findViewById(R.id.amountEditText);
		statusEditText = (TextView) findViewById(R.id.statusEditText);
		btn_mag = (Button) findViewById(R.id.btn_mag);
		btn_nfc = (Button) findViewById(R.id.btn_nfc);
		mBtnAction = (ImageButton) findViewById(R.id.action);
		
		Money money = new Money(mReceiveAmount);
		if (!money.isGreaterThanZero()) {
			DebugPrint.d(TAG, getString(R.string.price_is_zero));
			Toast.makeText(mContext, getString(R.string.price_is_zero), Toast.LENGTH_SHORT).show();
			//hasNoError = false;
		}else{
			String amount = money.toString();
			amountEditText.setText(amount);
		}
		
		
		handler = new TTLHandler(this);
		// GES10波特率9600
		handler.setParameters("/dev/ttyS1", 9600);
		settings = new Settings(handler);
		_mHandler = new MessageHandler(statusEditText);
		application = (PosApplication) getApplicationContext();
		application.set_handler(handler);
		application.set_setting(settings);
		handler.setShowLog(true);
		handler.addSwipeListener(new SwipeListener() {

			@Override
			public void onStopped(SwipeEvent arg0) {

			}

			@Override
			public void onStarted(SwipeEvent arg0) {

			}

			@Override
			public void onReadData(SwipeEvent arg0) {

			}

			@Override
			public void onParseData(SwipeEvent event) {
				String info = null;
				if (magFlag) {
					sendMessage("Final(16)=> " + event.getValue());
					String[] tmps = event.getValue().trim()
							.replaceAll("..", "$0 ").split(" ");
					StringBuffer sbf = new StringBuffer();
					for (String str : tmps) {
						sbf.append((char) Integer.parseInt(str, 16));
					}
					sendMessage("Final(10)=> " + sbf.toString());
					info = sbf.toString();
				}
				if(info != null){
					if(!info.equals("")){
						toSignature(info);	
					}					
				}
								
			}

			@Override
			public void onDisconnected(SwipeEvent arg0) {

			}

			@Override
			public void onConnected(SwipeEvent arg0) {

			}

			@Override
			public void onPermission(SwipeEvent arg0) {

			}
		});
		


	}
	
	private void toSignature(String content){
		mSuccessAmount = amountEditText.getText()
				.toString();
		//跳转到签名
		Intent intent = new Intent();
		intent.setClass(mContext, SignatureActivity.class);
		intent.putExtra("amount", mSuccessAmount);
		intent.putExtra("listItems", (Serializable) listItems);
		intent.putExtra("cardInfo", content);
		startActivity(intent);
		finish();
	}
	
	
	public void showTotalList(){
		PayListAdapter listAdapter = new PayListAdapter(listItems, mContext);
		totalList.setAdapter(listAdapter);
	}
	
	public void sendMessage(String str) {
		_mHandler.sendMessage(str);
	}
	
	public synchronized void test(View v) {
		switch (v.getId()) {
		case R.id.btn_dev_conn:
			try {
				if(cardHandler.hasMessages(MSG_MAGCARD)){
					cardHandler.removeMessages(MSG_MAGCARD);
				}
				if (!handler.isConnected()) {
					handler.close();
					sendMessage("Connect Res:" + handler.connect());					
				} else {
					handler.close();
					sendMessage("ReConnect Res:" + handler.connect());
					// sendMessage("TLL has connected!");
				}
				if(handler.isConnected()){
					String ver = settings.readVersion();
					sendMessage("Ver:" + ver);
					version = ver;
				}
				if(version == null){
					Toast.makeText(getApplicationContext(), "请确保设备连上刷卡器！", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				sendMessage(e.getMessage());
			}

			break;
		/*case R.id.ttlM1:
			m1Test();
			break;
		case R.id.ttlID:
			Intent intent = new Intent();
			intent.setClass(TTLActivity.this, IDCardActivity.class);
			startActivity(intent);
			break;*/
		case R.id.btn_ic:
			if(handler.isConnected() && version != null){
				iCCardTest();
			}else{
				sendMessage("Connect Res:" + handler.connect());
				Toast.makeText(getApplicationContext(), "请确保设备连上刷卡器！", Toast.LENGTH_SHORT).show();
			}
			
			break;
		/*case R.id.ttlVer:
			sendMessage("Ver:" + settings.readVersion());
			break;*/
		case R.id.btn_mag:
			if(handler.isConnected() && version != null){
				magTest();
			}else{
				sendMessage("Connect Res:" + handler.connect());
				Toast.makeText(getApplicationContext(), "请确保设备连上刷卡器！", Toast.LENGTH_SHORT).show();
			}
			
			break;
		case R.id.btn_nfc:
			/*long start = System.currentTimeMillis();
			Apdu_Send send = new Apdu_Send();
			send.setCommand(StringUtils.convertHexToBytes("FFFFFFFF"));
			sendMessage("NFC APDU1:"
					+ settings.getDataWithAPDU(Settings.SLOT_NFC, send));
			// 80500102000b01000000000000000000
			test();*/
			if(handler.isConnected() && version != null){
				m1Test(); //m1	
			}else{
				sendMessage("Connect Res:" + handler.connect());
				Toast.makeText(getApplicationContext(), "请确保设备连上刷卡器！", Toast.LENGTH_SHORT).show();
			}
			
			break;
		/*case R.id.ttlPrn:
			startActivity(new Intent(TTLActivity.this, PrintTestActivity.class));
			finish();
			break;
		case R.id.ttlEncrypted:
			// startActivity(new Intent(TTLActivity.this,
			// SettingsActivity.class));
			sendMessage("TMK:"
					+ settings.tposWriteTMK("aaaaaaaabbbbbbbbccccccccdddddddd"));
			// PIK:11111111222222223333333344444444
			// acb00adc4a8107a6bc27ed7ea73b84119a67ade1
			// MAC:11223344556677881122334455667788
			// aeaefdf35eee6427aeaefdf35eee64276fb23ead
			// TDK:1234567890abcdef1234567890abcdef
			// 3adf89c14448e7543adf89c14448e754a502016b
			sendMessage("签到:"
					+ settings
							.tposSignIn("acb00adc4a8107a6bc27ed7ea73b84119a67ade1aeaefdf35eee6427aeaefdf35eee64276fb23ead3adf89c14448e7543adf89c14448e754a502016b"));
			// sendMessage("IC计数器:"+settings.tposGetCount());
			break;*/
		default:
			break;
		}
	}
	
	// IC卡获取PBOC银行卡卡号
		private void iCCardTest() {
			if (flag) {
				sendMessage("Running......");
				return;
			}
			flag = true;
			cardHandler.removeCallbacks(iCCardTread);
			cardHandler.removeCallbacks(magCardThread);
			cardHandler.removeCallbacks(nfcCardThread);
			if(cardHandler.hasMessages(MSG_MAGCARD)){
				cardHandler.removeMessages(MSG_MAGCARD);
			}
			cardHandler.post(iCCardTread);
			
			/*new Thread(new Runnable() {
				@Override
				public void run() {
					sendMessage("IC CardNo:" + settings.icCardNo());
					flag = false;
				}
			}).start();*/
		}
		
		Runnable iCCardTread = new Runnable(){

			@Override
			public void run() {
				Message msg = cardHandler.obtainMessage();
				msg.what = MSG_ICCARD;
				cardHandler.sendMessage(msg);				
			}
			
		};
		
		Runnable magCardThread = new Runnable(){

			@Override
			public void run() {
				Message msg = cardHandler.obtainMessage();
				msg.what = MSG_MAGCARD;
				cardHandler.sendMessage(msg);
				
			}
			
		};
		
		Runnable nfcCardThread = new Runnable(){

			@Override
			public void run() {
				Message msg = cardHandler.obtainMessage();
				msg.what = MSG_NFCCARD;
				cardHandler.sendMessage(msg);
				
			}
			
		};
		
		Handler cardHandler = new Handler(){
			public void handleMessage(Message msg) {
				switch(msg.what){
				case MSG_ICCARD:
					String cardNo = settings.icCardNo();
					ZCSwipeCardActivity.this.sendMessage("IC CardNo:" + cardNo);
					flag = false;
					if(cardNo != null){
						Toast.makeText(getApplicationContext(), "刷卡成功！", Toast.LENGTH_SHORT).show();
						toSignature(cardNo);
					}
					break;
				case MSG_MAGCARD:
					ZCSwipeCardActivity.this.sendMessage("Pls swipe your magnetic stripe card......");
					String stateCode = settings.magSwipe(); 
					if (stateCode != null && stateCode.equals("00")) {
						Toast.makeText(getApplicationContext(), "刷卡成功！", Toast.LENGTH_SHORT).show();
						// data format:1byte track statu code+track data(1byte
						// len+data)
						magFlag = true;
						DebugPrint.e(TAG, "mag:" + settings.magRead());
						magFlag = false;
						settings.magReset();						
						break;
					}
					cardHandler.removeCallbacks(magCardThread);
					cardHandler.sendEmptyMessageDelayed(MSG_MAGCARD, 500);
					break;
				case MSG_NFCCARD:
					handler.setShowLog(true);
					String request = settings.m1Request();
					ZCSwipeCardActivity.this.sendMessage(request);
					ZCSwipeCardActivity.this.sendMessage(settings.m1Auth(Settings.M1_KEY_B, "0A",
							"EEEEEEEEEEEE") + "");
					ZCSwipeCardActivity.this.sendMessage(settings.m1ReadBlock("00"));;
					ZCSwipeCardActivity.this.sendMessage(settings.m1WriteBlock("00",
							"aaaaaaaabbbbbbbbccccccccdddddddd"));
					if(request != null){
						Toast.makeText(getApplicationContext(), "刷卡成功！", Toast.LENGTH_SHORT).show();
						toSignature(request);
					}
					
					break;
				}
			};
		};
		
		
		
		
		private void magTest() {
			sendMessage("Start to read magnetic stripe card......");
			settings.magOpen();
			/*new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {// only status code is 00,means ok
						sendMessage("Pls swipe your magnetic stripe card......");
						if (settings.magSwipe().equals("00")) {
							// data format:1byte track statu code+track data(1byte
							// len+data)
							magFlag = true;
							Log.e(TAG, "mag:" + settings.magRead());
							magFlag = false;
							settings.magReset();
							break;
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}
			}).start();*/
			cardHandler.removeCallbacks(iCCardTread);
			cardHandler.removeCallbacks(magCardThread);
			cardHandler.removeCallbacks(nfcCardThread);
			if(cardHandler.hasMessages(MSG_MAGCARD)){
				cardHandler.removeMessages(MSG_MAGCARD);
			}
			cardHandler.post(magCardThread);
		}
		
		private void test(){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Apdu_Send send = new Apdu_Send();
					send.setCommand(StringUtils.convertHexToBytes("80500102"));
					send.setLC((short) 0x0b);
					send.setDataIn(StringUtils
							.convertHexToBytes("0100000000000000000000"));
					flag = true;
					while(flag){
					sendMessage("NFC APDU2:"
							+ settings.getDataWithAPDU(Settings.SLOT_NFC, send));
//					long end = System.currentTimeMillis();
//					Log.e(TAG, "时差:" + (end - start));
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}}
				}
			}).start();
		}
		
		private void m1Test() {
			sendMessage("Start to read M1 card......");
			/*new Thread(new Runnable() {
				@Override
				public void run() {
					// sendMessage(settings.m1ReadSec("ffffffffffff", "00"));
					handler.setShowLog(true);
					sendMessage(settings.m1Request());
					sendMessage(settings.m1Auth(Settings.M1_KEY_B, "0A",
							"EEEEEEEEEEEE") + "");
					sendMessage(settings.m1ReadBlock("00"));
					// sendMessage(settings.m1ReadBlock("01"));
					// sendMessage(settings.m1ReadBlock("02"));
					// sendMessage(settings.m1ReadBlock("03"));
					sendMessage(settings.m1WriteBlock("00",
							"aaaaaaaabbbbbbbbccccccccdddddddd"));
					// sendMessage(settings.m1Request());
					// sendMessage(settings.m1Auth(Settings.M1_KEY_B, "0B",
					// "ffffffffffff")+"");
					// sendMessage(settings.m1ReadBlock("00"));
					// settings.m1WriteSecPass("0A",
					// Settings.M1_KEY_B,"ffffffffffff",
					// Settings.M1_KEY_B,"EEEEEEEEEEEE");
				}
			}).start();*/
			cardHandler.removeCallbacks(iCCardTread);
			cardHandler.removeCallbacks(magCardThread);
			cardHandler.removeCallbacks(nfcCardThread);
			if(cardHandler.hasMessages(MSG_MAGCARD)){
				cardHandler.removeMessages(MSG_MAGCARD);
			}
			cardHandler.removeMessages(MSG_MAGCARD);
			cardHandler.post(nfcCardThread);
		}
		
		@Override
			protected void onDestroy() {
				super.onDestroy();
				handler.close();// 关闭TTL
				flag = false;
				cardHandler.removeCallbacks(iCCardTread);
				cardHandler.removeCallbacks(magCardThread);
				cardHandler.removeCallbacks(nfcCardThread);
			}

}
