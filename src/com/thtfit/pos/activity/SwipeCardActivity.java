package com.thtfit.pos.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.util.ByteArrayBuffer;

import android.annotation.SuppressLint;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bbpos.emvswipe.EmvSwipeController.CheckCardMode;
import com.dspread.xpos.QPOSService;
import com.dspread.xpos.QPOSService.CommunicationMode;
import com.dspread.xpos.QPOSService.Display;
import com.dspread.xpos.QPOSService.DoTradeResult;
import com.dspread.xpos.QPOSService.EmvOption;
import com.dspread.xpos.QPOSService.Error;
import com.dspread.xpos.QPOSService.LcdModeAlign;
import com.dspread.xpos.QPOSService.QPOSServiceListener;
import com.dspread.xpos.QPOSService.TransactionResult;
import com.dspread.xpos.QPOSService.TransactionType;
import com.dspread.xpos.QPOSService.UpdateInformationResult;
import com.thtfit.pos.R;
import com.thtfit.pos.adapter.PayListAdapter;
import com.thtfit.pos.api.Money;
import com.thtfit.pos.bbpos.BBPosBTMainActivity;
import com.thtfit.pos.bean.IntegralBean;
import com.thtfit.pos.model.Product;
import com.thtfit.pos.util.DBUtils;
import com.thtfit.pos.util.QPOSUtil;
import com.thtfit.pos.util.Utils;
import com.thtfit.pos.conn.*;

public class SwipeCardActivity extends FragmentActivity {//EMVBaseActivity
	private String LOG_TAG = "SwipeCardActivity";
	
	public static List<Product> listItems = new ArrayList<Product>();

	private Context mContext;

	private Button doTradeButton;
	private Button doQuickTradeButton;//by Lu
	private EditText amountEditText;
	private EditText integralEditText;//by Lu
	private EditText showDb;//by Lu
	private Spinner fidsSpinner; //by Lu
	private boolean isChooseBtnClick; //by Lu
	private boolean isSelectedBBPos;//by Lu
	private boolean isQuickTrade;//by Lu
	private int myIntegral;
	private EditText statusEditText;
	private ListView totalList;
	private ListView appListView;
	private Dialog dialog;

	private Button btnBT;
	private Button btnDisconnect;
	private Button testBtn; // by Lu

	private QPOSService pos;
	private MyPosListener listener;

	private String amount = "";
	private String cashbackAmount = "";
	private boolean isPinCanceled = false;
	private String blueTootchAddress = "";
	public static final String POS_BLUETOOTH_ADDRESS = "POS_BLUETOOTH_ADDRESS";

	private ListView m_ListView;
	private MyListViewAdapter m_Adapter = null;
	private ImageView imvAnimScan;
	private AnimationDrawable animScan;
	private List<BluetoothDevice> lstDevScanned;
	private BroadcastReceiver recvBTScan = null;
	private Handler hdStopScan;

	private boolean isTest = false;

	private boolean isUart = true;
	private BluetoothAdapter mAdapter;

	private ImageButton mBtnAction;
	private String mSuccessAmount;
	private String mReceiveAmount;
	private Boolean mIsWorking = false;
	
	private static Map<String, String> swipeResult;//by Lu
	private static String swipeResultContent;//by Lu 
	
	//by Lu
	private CheckCardMode checkCardMode;
	protected static String fid65WorkingKey = "A1223344556677889900AABBCCDDEEFF";
	protected static String fid65MasterKey = "0123456789ABCDEFFEDCBA9876543210";

	private static String tlvOnlineProcessData = "";
	
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

	private void doScanBTPos() {
		close();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//
		refreshAdapter();
		m_ListView.setVisibility(View.VISIBLE);
	}

	@SuppressLint("NewApi")
	private void StopScanBTPos() {
		if (mAdapter != null) {
			mAdapter.cancelDiscovery();
			mAdapter = null;
		}
	}

	private void onBTPosSelected( View itemView, int index) {
		StopScanBTPos();
		//
		start_time = new Date().getTime();
		if (false  /*index == 0*/) {
			/* 这里是点中音频列表项的处�? */
			Log.d("luzhaojie", "这里是点中音频列表项的处理");
			open(CommunicationMode.AUDIO);
			posType = POS_TYPE.AUDIO;
			pos.openAudio();
		} else if (index == 0) {  //index == 1 && isUart
			/* 这里是点中串口列表项的处�? */
			Log.d("luzhaojie", "这里是点中串口列表项的处理");
			open(CommunicationMode.UART);
			posType = POS_TYPE.UART;
			pos.openUart();
		} else if(index == 1) {
			/* 这里是点中usb列表项的处�? */
			Log.d("luzhaojie", "这里是点中usb列表项的处理");
			open(CommunicationMode.USB);
			posType = POS_TYPE.USB;
			pos.openUsb();
		} else {
			/* 其余是点中蓝牙列表项的处�? */
			Log.d("luzhaojie", "这里是点中蓝牙列表项的处理");
			// open(CommunicationMode.BLUETOOTH_VER2);
			open(CommunicationMode.BLUETOOTH_2Mode);
			posType = POS_TYPE.BLUETOOTH;
			Map<String, ?> dev = (Map<String, ?>) m_Adapter.getItem(index);
			blueTootchAddress = (String) dev.get("ADDRESS");
			sendMsg(1001);
		}
	}

	protected List<Map<String, ?>> generateAdapterData() {
		List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
		//
		Map<String, Object> itmAudio = new HashMap<String, Object>();
		itmAudio.put("ICON", Integer.valueOf(R.drawable.ic_headphones_on));
		itmAudio.put("TITLE", getResources().getString(R.string.audio));
		itmAudio.put("ADDRESS", getResources().getString(R.string.audio));

//		data.add(itmAudio);

		if (isUart) {
			//
			Map<String, Object> itmSerialPort = new HashMap<String, Object>();
			itmSerialPort.put("ICON", Integer.valueOf(R.drawable.serialport));
			itmSerialPort.put("TITLE",
					getResources().getString(R.string.serialport));
			itmSerialPort.put("ADDRESS",
					getResources().getString(R.string.serialport));
			data.add(itmSerialPort);
			
			//by Lu : add the usb swipe card 
			Map<String, Object> usbPort = new HashMap<String, Object>();
			usbPort.put("ICON", Integer.valueOf(R.drawable.serialport));
			usbPort.put("TITLE",
					getResources().getString(R.string.usb));
			usbPort.put("ADDRESS",
					getResources().getString(R.string.usb));
			data.add(usbPort);
			
			//by Lu : add the bbpos view :  initialize in Adapter :: getView
			Map<String, Object> bbPos = new HashMap<String, Object>();
			data.add(bbPos);
			Log.d("luzhaojie", "generateAdapterData :: data : " + data);
		}
		return data;
	}

	private void refreshAdapter() {
		if (m_Adapter != null) {
			m_Adapter.clearData();
			m_Adapter = null;
		}
		//
		List<Map<String, ?>> data = generateAdapterData();
		m_Adapter = new MyListViewAdapter(this, data);
		//
		m_ListView.setAdapter(m_Adapter);
	}

	private class MyListViewAdapter extends BaseAdapter {
		private List<Map<String, ?>> m_DataMap;
		private LayoutInflater m_Inflater;

		public void clearData() {
			m_DataMap.clear();
			m_DataMap = null;
		}

		public MyListViewAdapter(Context context, List<Map<String, ?>> map) {
			this.m_DataMap = map;
			this.m_Inflater = LayoutInflater.from(context);
			Log.d("luzhaojie", "data.size : " + map.size());
		}

		@Override
		public int getCount() {
			return m_DataMap.size() - 1; // -1 : hide the bbpos
		}

		@Override
		public Object getItem(int position) {
			return m_DataMap.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		//by Lu
		@Override
		public int getItemViewType(int position) {
			if(position == 2) {
				//bbpos
				return 1;
			} else{
				return 0;
			}
		}
		
		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			//by Lu 
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
				case 0:
					convertView = m_Inflater.inflate(R.layout.bt_qpos_item, null);
					ImageView m_Icon = (ImageView) convertView
							.findViewById(R.id.item_iv_icon);
					TextView m_TitleName = (TextView) convertView
							.findViewById(R.id.item_tv_lable);
					//
					Map<String, ?> itemdata = (Map<String, ?>) m_DataMap.get(position);
					int idIcon = (Integer) itemdata.get("ICON");
					String sTitleName = (String) itemdata.get("TITLE");
					//
					m_Icon.setBackgroundResource(idIcon);
					Log.d("luzhaojie", "position = " + position);
					m_TitleName.setText(sTitleName);
					break;
				//bbpos usb
				case 1: 
//					convertView = new BBPOSSwipeCardView(m_Inflater, SwipeCardActivity.this, parent);
//					convertView = m_Inflater.inflate(R.layout.view_list_bbpos_checkout, null);
//					fidsSpinner = (Spinner) convertView.findViewById(R.id.fidSpinner);
//					String[] fids = new String[] {
//							"         ",
//			        		"FID22",
//			        		"FID36",
//			        		"FID46",
//			        		"FID54",
//			        		"FID55",
//			        		"FID60",
//							"FID61",
//							"FID64",
//							"FID65",
//					};
//					fidsSpinner.setAdapter(new ArrayAdapter<String>(SwipeCardActivity.this, R.layout.my_spinner_item, fids));
//					fidsSpinner.setSelection(0);
//					fidsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//						@Override
//						public void onItemSelected(AdapterView<?> parent,
//								View view, int position, long id) {
//							isSelectedBBPos = true;
////							emvSwipeController.startUsb(); // start usb
//							promptForConnection();
//							if(isChooseBtnClick == false) { //real choose
//								Log.d("luzhaojie", "onItemSelected :: isChooseBtnClick == " + isChooseBtnClick);
//								m_ListView.setVisibility(View.GONE);
//							}
//							if(isChooseBtnClick == true){ //default
//								isChooseBtnClick = false;
//								Log.d("luzhaojie", "onItemSelected :: isChooseBtnClick == " + isChooseBtnClick);
//							}
//						}
//						@Override
//						public void onNothingSelected(AdapterView<?> parent) {
//						}
//					});
					//跳转到bbpos刷卡界面
					convertView = m_Inflater.inflate(R.layout.btn_to_bbpos, null);
					Button toBBPos = (Button) convertView.findViewById(R.id.tobbpos);
					toBBPos.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(SwipeCardActivity.this, BBPosBTMainActivity.class);
							intent.putExtra("amount", mReceiveAmount);
							startActivity(intent);
						}
					});
					break;
				default:
					break;
				}
			}
			return convertView;
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
		mIsWorking = true;
		isChooseBtnClick = false;
		isSelectedBBPos = false;
		isQuickTrade = false;

		//by Lu
//		MyDBHelper helper = new MyDBHelper(getApplicationContext());
		setContentView(R.layout.activity_swipe);
		mReceiveAmount = getIntent().getStringExtra("amount");
		listItems = (List<Product>) getIntent().getSerializableExtra("listItems");
		totalList = (ListView) findViewById(R.id.swpie_total_list);
		
		// System.out.println("-------------amount ="+mSendAmount+"\n");

		m_ListView = (ListView) findViewById(R.id.lv_indicator_BTPOS);
		m_ListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {	
				onBTPosSelected(view, position);
				m_ListView.setVisibility(View.GONE);
				animScan.stop();
				imvAnimScan.setVisibility(View.GONE);
				isChooseBtnClick = false;
				isSelectedBBPos = false;
				//by Lu : set doTradeBtn & disconnectBtn visible
				doTradeButton.setVisibility(View.VISIBLE);
				doQuickTradeButton.setVisibility(View.VISIBLE);
				btnDisconnect.setVisibility(View.VISIBLE);
			}
		});

		imvAnimScan = (ImageView) findViewById(R.id.img_anim_scanbt);
		animScan = (AnimationDrawable) getResources().getDrawable(
				R.anim.progressanmi);
		imvAnimScan.setBackground(animScan);

		hdStopScan = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 10024) {
					StopScanBTPos();
				}
			}
		};

		doTradeButton = (Button) findViewById(R.id.doTradeButton);
		doQuickTradeButton = (Button) findViewById(R.id.doQuickTradeButton);
		testBtn = (Button) findViewById(R.id.test_btn);
		amountEditText = (EditText) findViewById(R.id.amountEditText);
//		integralEditText = (EditText) findViewById(R.id.integralEditText);
		showDb = (EditText) findViewById(R.id.show_db); 
		statusEditText = (EditText) findViewById(R.id.statusEditText);
		btnBT = (Button) findViewById(R.id.btnBT);
		btnDisconnect = (Button) findViewById(R.id.disconnect);
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		doTradeButton.setOnClickListener(myOnClickListener);
		doQuickTradeButton.setOnClickListener(myOnClickListener);
		btnBT.setOnClickListener(myOnClickListener);
		btnDisconnect.setOnClickListener(myOnClickListener);
		testBtn.setOnClickListener(myOnClickListener);
		//by Lu : hide doTradeBtn and disconnectBtn
		doTradeButton.setVisibility(View.GONE);
		doQuickTradeButton.setVisibility(View.GONE);
		btnDisconnect.setVisibility(View.GONE);
		

		mBtnAction = (ImageButton) findViewById(R.id.action);
		mBtnAction.setOnClickListener(myOnClickListener);
		
		showTotalList();

		/* 这里是点中串口列表项的处�? */
		open(CommunicationMode.UART);
		posType = POS_TYPE.UART;
		pos.openUart();
		m_ListView.setVisibility(View.GONE);
		animScan.stop();
		imvAnimScan.setVisibility(View.GONE);

//		pos.doTrade(80);

	}

	private POS_TYPE posType = POS_TYPE.AUDIO;
	
	public void showTotalList(){
		PayListAdapter listAdapter = new PayListAdapter(listItems, mContext);
		totalList.setAdapter(listAdapter);
	}

	private static enum POS_TYPE {
		BLUETOOTH, AUDIO, UART, USB
	}

	private void open(CommunicationMode mode) {
		listener = new MyPosListener();
		pos = QPOSService.getInstance(mode);
		if (pos == null) {
			statusEditText.setText("CommunicationMode unknow");
			return;
		}
		pos.setConext(getApplicationContext());
		Handler handler = new Handler(Looper.myLooper());
		pos.initListener(handler, listener);
	}

	private void close() {
		if (pos == null) {
			return;
		}
		if (posType == POS_TYPE.AUDIO) {
			Log.d(LOG_TAG, "********************* close POS_TYPE.AUDIO");
			pos.closeAudio();
		} else if (posType == POS_TYPE.BLUETOOTH) {
			Log.d(LOG_TAG, "********************* close POS_TYPE.BLUETOOTH");
			pos.disconnectBT();
		} else if (posType == POS_TYPE.UART) {
			Log.d(LOG_TAG, "********************* close POS_TYPE.UART");
			pos.resetQPOS();
			pos.closeUart();
		} else if (posType == POS_TYPE.USB) {
			Log.d(LOG_TAG, "********************* close POS_TYPE.USB");
			pos.resetQPOS();
			pos.closeUsb();
		} 
		
		//by Lu : bbpos's close
//		if(emvSwipeController == null) {
//			return;
//		}
//		if(posType == POS_TYPE.USB) {
//			emvSwipeController.stopUsb();
//		}
	}

	class UpdateThread extends Thread {
		public void run() {
			while (true) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int progress = pos.getUpdateProgress();
				if (progress < 100) {
					Message msg = updata_handler.obtainMessage();
					msg.what = PROGRESS_UP;
					msg.obj = progress;
					msg.sendToTarget();
					continue;
				}
				Message msg = updata_handler.obtainMessage();
				msg.what = PROGRESS_UP;
				msg.obj = "升级完成";
				msg.sendToTarget();
				break;
			}
		};
	};

	@Override
	public void onPause() {
		super.onPause();
		Log.d(LOG_TAG, "onPause");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "onResume");
	}

	@SuppressLint("NewApi")
	@Override
	public void onDestroy() {
		super.onDestroy();
		//by Lu
		doQuickTradeButton.setVisibility(View.GONE);
		doTradeButton.setVisibility(View.GONE);
		btnDisconnect.setVisibility(View.GONE);
		
		mIsWorking = false;
		dismissDialog();

		Log.d(LOG_TAG, "*********************swipe card onDestroy");
		if (mAdapter != null) {
			mAdapter.cancelDiscovery();
		}
		close();
		if (pos != null) {
			pos.onDestroy();
			Log.d(LOG_TAG, "*********************swipe card pos.onDestroy");
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// System.exit(0);
		// finish();
		
		//by Lu : bbpos
//		if(isSwitchingActivity) {
//    		isSwitchingActivity = false;
//    	} else {
//    		if (emvSwipeController.getConnectionMode() == ConnectionMode.AUDIO)
//    			emvSwipeController.stopAudio();
//    		else if (emvSwipeController.getConnectionMode() == ConnectionMode.USB)
//    			emvSwipeController.stopUsb();
//    		emvSwipeController.resetEmvSwipeController();
//    		emvSwipeController = null;
//    	}
	}

	public void dismissDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	private byte[] readLine(String Filename) {

		String str = "";
		ByteArrayBuffer buffer = new ByteArrayBuffer(0);
		try {
			android.content.ContextWrapper contextWrapper = new ContextWrapper(
					this);
			AssetManager assetManager = contextWrapper.getAssets();
			InputStream inputStream = assetManager.open(Filename);
			// BufferedReader br = new BufferedReader(new
			// InputStreamReader(inputStream));
			// str = br.readLine();
			int b = inputStream.read();
			while (b != -1) {
				buffer.append((byte) b);
				b = inputStream.read();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}

	class MyPosListener implements QPOSServiceListener {

		@Override
		public void onRequestWaitingUser() {
			if (mIsWorking == false) {
				return;
			}
			dismissDialog();
			statusEditText.setText(getString(R.string.waiting_for_card));
		}

		
		//刷卡返回结果
		@Override
		public void onDoTradeResult(DoTradeResult result,
				final Hashtable<String, String> decodeData) {
			if (mIsWorking == false) {
				return;
			}
			dismissDialog();

			if (result == DoTradeResult.NONE) {
				statusEditText.setText(getString(R.string.no_card_detected));
			} else if (result == DoTradeResult.ICC) {
				statusEditText.setText(getString(R.string.icc_card_inserted));
				Log.d(LOG_TAG, "EMV ICC Start");
				pos.doEmvApp(EmvOption.START);
			} else if (result == DoTradeResult.NOT_ICC) {
				statusEditText.setText(getString(R.string.card_inserted));
			} else if (result == DoTradeResult.BAD_SWIPE) {
				statusEditText.setText(getString(R.string.bad_swipe));
			} else if (result == DoTradeResult.MCR) {
				//by Lu
				String content = onDoTradeResultJob(decodeData);
				swipeResultContent = content;//by Lu
//				Toast.makeText(getApplicationContext(), "swipeResultContent : " + swipeResultContent, 1).show();//by Lu
				statusEditText.setText(content);
//				mBtnAction.setVisibility(View.VISIBLE);
				
				Toast.makeText(getApplicationContext(), "刷卡成功！", Toast.LENGTH_SHORT).show();
//				Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
				
				//发送刷卡结果到Apriva后台
				final Hashtable<String, String> hashtable = new Hashtable<String, String>();
				hashtable.put("amount", amount);
				new Thread(new Runnable() {  
					   @Override  
					    public void run() {  
					    	Message msg = new Message();  
					        Bundle data = new Bundle();  
					        data.putString("value",aprivaConn(decodeData,hashtable));  
					        msg.setData(data);  
					        handler.sendMessage(msg);  
						}  
				}).start();
				
				//跳转到签名
//				Intent intent = new Intent();
//				intent.setClass(mContext, SignatureActivity.class);
//				intent.putExtra("amount", mSuccessAmount);
//				intent.putExtra("listItems", (Serializable) listItems);
//				intent.putExtra("cardInfo", content);
//				startActivity(intent);
//				finish();
				
			} else if (result == DoTradeResult.NO_RESPONSE) {
				statusEditText.setText(getString(R.string.card_no_response));
			}
		}

		@Override
		public void onQposInfoResult(Hashtable<String, String> posInfoData) {
			if (mIsWorking == false) {
				return;
			}
			String isSupportedTrack1 = posInfoData.get("isSupportedTrack1") == null ? ""
					: posInfoData.get("isSupportedTrack1");
			String isSupportedTrack2 = posInfoData.get("isSupportedTrack2") == null ? ""
					: posInfoData.get("isSupportedTrack2");
			String isSupportedTrack3 = posInfoData.get("isSupportedTrack3") == null ? ""
					: posInfoData.get("isSupportedTrack3");
			String bootloaderVersion = posInfoData.get("bootloaderVersion") == null ? ""
					: posInfoData.get("bootloaderVersion");
			String firmwareVersion = posInfoData.get("firmwareVersion") == null ? ""
					: posInfoData.get("firmwareVersion");
			String isUsbConnected = posInfoData.get("isUsbConnected") == null ? ""
					: posInfoData.get("isUsbConnected");
			String isCharging = posInfoData.get("isCharging") == null ? ""
					: posInfoData.get("isCharging");
			String batteryLevel = posInfoData.get("batteryLevel") == null ? ""
					: posInfoData.get("batteryLevel");
			String hardwareVersion = posInfoData.get("hardwareVersion") == null ? ""
					: posInfoData.get("hardwareVersion");

			String content = "";
			content += getString(R.string.bootloader_version)
					+ bootloaderVersion + "\n";
			content += getString(R.string.firmware_version) + firmwareVersion
					+ "\n";
			content += getString(R.string.usb) + isUsbConnected + "\n";
			content += getString(R.string.charge) + isCharging + "\n";
			content += getString(R.string.battery_level) + batteryLevel + "\n";
			content += getString(R.string.hardware_version) + hardwareVersion
					+ "\n";
			content += getString(R.string.track_1_supported)
					+ isSupportedTrack1 + "\n";
			content += getString(R.string.track_2_supported)
					+ isSupportedTrack2 + "\n";
			content += getString(R.string.track_3_supported)
					+ isSupportedTrack3 + "\n";

			statusEditText.setText(content);
		}

		//ic卡结果处理
		@Override
		public void onRequestTransactionResult(
				TransactionResult transactionResult) {
			Log.d(LOG_TAG, "onRequestTransactionResult");
			if (mIsWorking == false) {
				return;
			}
			// clearDisplay();
			dismissDialog();

			// statusEditText.setText("");
			dialog = new Dialog(SwipeCardActivity.this);
			dialog.setContentView(R.layout.alert_dialog);
			dialog.setTitle(R.string.transaction_result);
			TextView messageTextView = (TextView) dialog
					.findViewById(R.id.messageTextView);

			if (isQuickTrade) {
				messageTextView.setText("please remove card. and send data to online");
				String customDisplayString = "";
				try {
					byte[] paras = "\nPLS REMOVE CARD".getBytes("GBK");
					customDisplayString = QPOSUtil.byteArray2Hex(paras);
					pos.lcdShowCustomDisplay(LcdModeAlign.LCD_MODE_ALIGNCENTER, customDisplayString,10);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}    
				
			} else {
				if (transactionResult == TransactionResult.APPROVED) {
					Log.d(LOG_TAG, "TransactionResult.APPROVED");
					String message = getString(R.string.transaction_approved)
							+ "\n" + getString(R.string.amount) + ": ￥" + amount
							+ "\n";
					if (!cashbackAmount.equals("")) {
						message += getString(R.string.cashback_amount) + ": ￥"
								+ cashbackAmount;
					}
					messageTextView.setText(message);
				} else if (transactionResult == TransactionResult.TERMINATED) {
					clearDisplay();
					messageTextView
					.setText(getString(R.string.transaction_terminated));
				} else if (transactionResult == TransactionResult.DECLINED) {
					messageTextView
					.setText(getString(R.string.transaction_declined));
				} else if (transactionResult == TransactionResult.CANCEL) {
					clearDisplay();
					messageTextView.setText(getString(R.string.transaction_cancel));
				} else if (transactionResult == TransactionResult.CAPK_FAIL) {
					messageTextView
					.setText(getString(R.string.transaction_capk_fail));
				} else if (transactionResult == TransactionResult.NOT_ICC) {
					messageTextView
					.setText(getString(R.string.transaction_not_icc));
				} else if (transactionResult == TransactionResult.SELECT_APP_FAIL) {
					messageTextView
					.setText(getString(R.string.transaction_app_fail));
				} else if (transactionResult == TransactionResult.DEVICE_ERROR) {
					messageTextView
					.setText(getString(R.string.transaction_device_error));
				} else if (transactionResult == TransactionResult.CARD_NOT_SUPPORTED) {
					messageTextView.setText(getString(R.string.card_not_supported));
				} else if (transactionResult == TransactionResult.MISSING_MANDATORY_DATA) {
					messageTextView
					.setText(getString(R.string.missing_mandatory_data));
				} else if (transactionResult == TransactionResult.CARD_BLOCKED_OR_NO_EMV_APPS) {
					messageTextView
					.setText(getString(R.string.card_blocked_or_no_evm_apps));
				} else if (transactionResult == TransactionResult.INVALID_ICC_DATA) {
					messageTextView.setText(getString(R.string.invalid_icc_data));
				}
			}

			dialog.findViewById(R.id.confirmButton).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							dismissDialog();
							//跳转到签名
//							Intent intent = new Intent();
//							intent.setClass(mContext, SignatureActivity.class);
//							intent.putExtra("amount", mSuccessAmount);
//							intent.putExtra("listItems", (Serializable) listItems);
//							intent.putExtra("cardInfo", "ICC");//by Lu
//							startActivity(intent);
//							finish();
						}
					});
			dialog.show();

			amount = "";
			cashbackAmount = "";
			amountEditText.setText("");
//			integralEditText.setText("");
			isQuickTrade = false;
		}

		@Override
		public void onRequestBatchData(String tlv) {
			if (mIsWorking == false) {
				return;
			}
			Log.d(LOG_TAG, "ICC交易结束");
			// dismissDialog();
			String content = getString(R.string.batch_data);
			Log.d(LOG_TAG, "tlv:" + tlv);
			content += tlv;
			statusEditText.setText(content); 
			mBtnAction.setVisibility(View.VISIBLE);
		}

		@Override
		public void onRequestTransactionLog(String tlv) {
			if (mIsWorking == false) {
				return;
			}
			dismissDialog();
			String content = getString(R.string.transaction_log);
			content += tlv;
			statusEditText.setText(content);  
		}

		@Override
		public void onQposIdResult(Hashtable<String, String> posIdTable) {
			if (mIsWorking == false) {
				return;
			}
			String posId = posIdTable.get("posId") == null ? "" : posIdTable
					.get("posId");
			String csn = posIdTable.get("csn") == null ? "" : posIdTable
					.get("csn");

			String content = "";
			content += getString(R.string.posId) + posId + "\n";
			content += "csn: " + csn + "\n";
			statusEditText.setText(content);
			if (isTest) {
				sendMsg(1003);
			}

		}

		@Override
		public void onRequestSelectEmvApp(ArrayList<String> appList) {
			if (mIsWorking == false) {
				return;
			}
			Log.d(LOG_TAG, "请�?择App -- S");
			dismissDialog();

			dialog = new Dialog(SwipeCardActivity.this);
			dialog.setContentView(R.layout.emv_app_dialog);
			dialog.setTitle(R.string.please_select_app);

			String[] appNameList = new String[appList.size()];
//			Toast.makeText(mContext, "appNameList = " + appNameList, 1).show();//by Lu
			for (int i = 0; i < appNameList.length; ++i) {
				Log.d(LOG_TAG, "i=" + i + "," + appList.get(i));
				appNameList[i] = appList.get(i);
			}

			appListView = (ListView) dialog.findViewById(R.id.appList);
			appListView.setAdapter(new ArrayAdapter<String>(
					SwipeCardActivity.this,
					android.R.layout.simple_list_item_1, appNameList));
			appListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					pos.selectEmvApp(position);
					Log.d(LOG_TAG, "请�?择App -- 结束 position = " + position);
					dismissDialog();
				}

			});
			dialog.findViewById(R.id.cancelButton).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							pos.cancelSelectEmvApp();
							dismissDialog();
						}
					});
			dialog.show();
		}

		// 设置金额
		@Override
		public void onRequestSetAmount() {
			if (mIsWorking == false) {
				return;
			}
			Log.d(LOG_TAG, "准备刷卡  -- 启动");
			Money money = new Money(mReceiveAmount);
			boolean hasNoError = true;
			if (!money.isGreaterThanZero()) {
				Log.d(LOG_TAG, getString(R.string.price_is_zero));
				Toast.makeText(mContext, getString(R.string.price_is_zero), Toast.LENGTH_SHORT).show();
				hasNoError = false;
			}
			if (!hasNoError) {
				return;
			}
			
			TransactionType transactionType = TransactionType.GOODS;
			
			String amount = money.toString();
			amountEditText.setText(amount);
//			integralEditText.setText(money.divide(money, 1000).toStringForIntegral()); // by Lu
			myIntegral = money.toInt() / 1000; // by Lu
			mSuccessAmount = amountEditText.getText()
					.toString();
			pos.setAmount(Utils.removeAmountSymbol(amount),
					cashbackAmount, "0840", transactionType);
			SwipeCardActivity.this.amount = Utils
					.removeAmountDollar(amount);

			SwipeCardActivity.this.cashbackAmount = cashbackAmount;
			Log.d(LOG_TAG, "准备刷卡  -- 就绪");

		}

		@Override
		public void onRequestIsServerConnected() {
			if (mIsWorking == false) {
				return;
			}
			Log.d(LOG_TAG, "在线过程请求");
			if (isQuickTrade) {
				pos.isServerConnected(true);
				
			} else {
				dismissDialog();
				dialog = new Dialog(SwipeCardActivity.this);
				dialog.setContentView(R.layout.alert_dialog);
				dialog.setTitle(R.string.online_process_requested);
				
				((TextView) dialog.findViewById(R.id.messageTextView))
				.setText(R.string.replied_connected);
				
				dialog.findViewById(R.id.confirmButton).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								pos.isServerConnected(true);
								dismissDialog();
							}
						});
				dialog.show();
			}
		}

		@Override
		public void onRequestOnlineProcess(String tlv) {
			if (mIsWorking == false) {
				return;
			}
			if (isQuickTrade) {
				pos.sendOnlineProcessResult("8A023030");
				//8A025A33:reversal data  / 8A023030:batch data
				
			} else {
				Log.d(LOG_TAG, "向服务器请求数据");
				dismissDialog();
				dialog = new Dialog(SwipeCardActivity.this);
				dialog.setContentView(R.layout.alert_dialog);
				dialog.setTitle(R.string.request_data_to_server);
				Log.d(LOG_TAG, "tlv:" + tlv);
				// Log.d(LOG_TAG, "str:"+str);
				Hashtable<String, String> decodeData = pos.anlysEmvIccData(tlv);
				Log.d(LOG_TAG, "onlineProcess: " + decodeData);
				if (isPinCanceled) {
					((TextView) dialog.findViewById(R.id.messageTextView))
					.setText(R.string.replied_failed);
				} else {
					((TextView) dialog.findViewById(R.id.messageTextView))
					.setText(R.string.replied_success);
				}
				
				tlvOnlineProcessData = tlv;
				dialog.findViewById(R.id.confirmButton).setOnClickListener(
						new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								if (isPinCanceled) {
//								Toast.makeText(mContext, "onRequestOnlineProcess :: isPinCanceled : " + isPinCanceled, 1).show();//by Lu
									pos.sendOnlineProcessResult(null);
								} else {
//								Toast.makeText(mContext, "onRequestOnlineProcess :: isPinCanceled : " + isPinCanceled, 1).show();//by Lu
//								pos.sendOnlineProcessResult("8A023030" + tlvOnlineProcessData);//server accept
									pos.sendOnlineProcessResult("8A023030");//by Lu
									// emvSwipeController.sendOnlineProcessResult(str);
								}
								dismissDialog();
							}
						});
				
				dialog.show();
			}
		}

		public void onServerDecline() {
			if (!tlvOnlineProcessData.isEmpty()) {
				pos.sendOnlineProcessResult("8A023035" + tlvOnlineProcessData);//server decline
				tlvOnlineProcessData = "";
			}
		}
		
		@Override
		public void onRequestTime() {
			if (mIsWorking == false) {
				return;
			}
			Log.d(LOG_TAG, "要求终端时间。已回覆");
			dismissDialog();
			String terminalTime = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(Calendar.getInstance().getTime());
			pos.sendTime(terminalTime);
			statusEditText.setText(getString(R.string.request_terminal_time)
					+ " " + terminalTime);
		}

		@Override
		public void onRequestDisplay(Display displayMsg) {
			if (mIsWorking == false) {
				return;
			}
			dismissDialog();

			String msg = "";
			if (displayMsg == Display.CLEAR_DISPLAY_MSG) {
				msg = "";
			} else if (displayMsg == Display.PLEASE_WAIT) {
				msg = getString(R.string.wait);
			} else if (displayMsg == Display.REMOVE_CARD) {
				msg = getString(R.string.remove_card);
			} else if (displayMsg == Display.TRY_ANOTHER_INTERFACE) {
				msg = getString(R.string.try_another_interface);
			} else if (displayMsg == Display.PROCESSING) {
				msg = getString(R.string.processing);
			} else if (displayMsg == Display.INPUT_PIN_ING) {
				msg = "please input pin on pos";
			} else if (displayMsg == Display.MAG_TO_ICC_TRADE) {
				msg = "please insert chip card on pos";
			}
			statusEditText.setText(msg);
		}

		@Override
		public void onRequestFinalConfirm() {
			if (mIsWorking == false) {
				return;
			}
			Log.d(LOG_TAG, "确认金额-- S");
			dismissDialog();
			if (!isPinCanceled) {
				dialog = new Dialog(SwipeCardActivity.this);
				dialog.setContentView(R.layout.confirm_dialog);
				dialog.setTitle(getString(R.string.confirm_amount));

				String message = getString(R.string.amount) + ": ￥" + amount;
				if (!cashbackAmount.equals("")) {
					message += "\n" + getString(R.string.cashback_amount)
							+ ": ￥" + cashbackAmount;
				}

				((TextView) dialog.findViewById(R.id.messageTextView))
						.setText(message);

				dialog.findViewById(R.id.confirmButton).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								pos.finalConfirm(true);
								dialog.dismiss();
								Log.d(LOG_TAG, "确认金额-- 结束");
							}
						});

				dialog.findViewById(R.id.cancelButton).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								pos.finalConfirm(false);
								dialog.dismiss();
							}
						});

				dialog.show();
			} else {
				pos.finalConfirm(false);
			}
		}

		@Override
		public void onRequestNoQposDetected() {
			if (mIsWorking == false) {
				return;
			}
			dismissDialog();
			statusEditText.setText(getString(R.string.no_device_detected));
		}

		@Override
		public void onRequestQposConnected() {
			if (mIsWorking == false) {
				return;
			}
			dismissDialog();
			long use_time = new Date().getTime() - start_time;
			statusEditText.setText(getString(R.string.device_plugged) + "--用时"
					+ Utils.formatLongToTimeStr(use_time));
			doTradeButton.setEnabled(true);
			doQuickTradeButton.setEnabled(true);
			btnDisconnect.setEnabled(true);
		}

		@Override
		public void onRequestQposDisconnected() {
			if (mIsWorking == false) {
				return;
			}
			dismissDialog();
			statusEditText.setText(getString(R.string.device_unplugged));
			btnDisconnect.setEnabled(false);
			doTradeButton.setEnabled(false);
			doQuickTradeButton.setEnabled(false);
		}

		@Override
		public void onError(Error errorState) {
			if (mIsWorking == false) {
				return;
			}
			dismissDialog();
			amountEditText.setText("");
//			integralEditText.setText("");
			if (errorState == Error.CMD_NOT_AVAILABLE) {
				statusEditText
						.setText(getString(R.string.command_not_available));
			} else if (errorState == Error.TIMEOUT) {
				statusEditText.setText(getString(R.string.device_no_response));
			} else if (errorState == Error.DEVICE_RESET) {
				statusEditText.setText(getString(R.string.device_reset));
			} else if (errorState == Error.UNKNOWN) {
				statusEditText.setText(getString(R.string.unknown_error));
			} else if (errorState == Error.DEVICE_BUSY) {
				statusEditText.setText(getString(R.string.device_busy));
			} else if (errorState == Error.INPUT_OUT_OF_RANGE) {
				statusEditText.setText(getString(R.string.out_of_range));
			} else if (errorState == Error.INPUT_INVALID_FORMAT) {
				statusEditText.setText(getString(R.string.invalid_format));
			} else if (errorState == Error.INPUT_ZERO_VALUES) {
				statusEditText.setText(getString(R.string.zero_values));
			} else if (errorState == Error.INPUT_INVALID) {
				statusEditText.setText(getString(R.string.input_invalid));
			} else if (errorState == Error.CASHBACK_NOT_SUPPORTED) {
				statusEditText
						.setText(getString(R.string.cashback_not_supported));
			} else if (errorState == Error.CRC_ERROR) {
				statusEditText.setText(getString(R.string.crc_error));
			} else if (errorState == Error.COMM_ERROR) {
				statusEditText.setText(getString(R.string.comm_error));
			} else if (errorState == Error.MAC_ERROR) {
				statusEditText.setText(getString(R.string.mac_error));
			} else if (errorState == Error.CMD_TIMEOUT) {
				statusEditText.setText(getString(R.string.cmd_timeout));
			}
		}

		@Override
		public void onReturnReversalData(String tlv) {
			if (mIsWorking == false) {
				return;
			}
			String content = getString(R.string.reversal_data);
			content += tlv;
			Log.d(LOG_TAG, "listener: onReturnReversalData: " + tlv);
			statusEditText.setText(content);
		}

		@Override
		public void onReturnGetPinResult(Hashtable<String, String> result) {
			if (mIsWorking == false) {
				return;
			}
			String pinBlock = result.get("pinBlock");
			String pinKsn = result.get("pinKsn");
			String content = "get pin result\n";

			content += getString(R.string.pinKsn) + " " + pinKsn + "\n";
			content += getString(R.string.pinBlock) + " " + pinBlock + "\n";
			statusEditText.setText(content);
			Log.d(LOG_TAG, content);

		}

		@Override
		public void onReturnApduResult(boolean arg0, String arg1, int arg2) {

		}

		@Override
		public void onReturnPowerOffIccResult(boolean arg0) {

		}

		@Override
		public void onReturnPowerOnIccResult(boolean arg0, String arg1,
				String arg2, int arg3) {

		}

		@Override
		public void onReturnSetSleepTimeResult(boolean isSuccess) {
			if (mIsWorking == false) {
				return;
			}
			String content = "";
			if (isSuccess) {
				content = "set the sleep time success.";
			} else {
				content = "set the sleep time failed.";
			}
			statusEditText.setText(content);

		}

		@Override
		public void onGetCardNoResult(String cardNo) {
			if (mIsWorking == false) {
				return;
			}
			statusEditText.setText("cardNo: " + cardNo);

		}

		@Override
		public void onRequestCalculateMac(String calMac) {
			if (mIsWorking == false) {
				return;
			}
			statusEditText.setText("calMac: " + calMac);

		}

		@Override
		public void onRequestSignatureResult(byte[] arg0) {

		}

		@Override
		public void onRequestUpdateWorkKeyResult(UpdateInformationResult result) {
			if (mIsWorking == false) {
				return;
			}
			if (result == UpdateInformationResult.UPDATE_SUCCESS) {
				statusEditText.setText("update work key success");
			} else if (result == UpdateInformationResult.UPDATE_FAIL) {
				statusEditText.setText("update work key fail");
			} else if (result == UpdateInformationResult.UPDATE_PACKET_VEFIRY_ERROR) {
				statusEditText.setText("update work key packet vefiry error");
			} else if (result == UpdateInformationResult.UPDATE_PACKET_LEN_ERROR) {
				statusEditText.setText("update work key packet len error");
			}

		}

		@Override
		public void onReturnCustomConfigResult(boolean isSuccess, String result) {
			if (mIsWorking == false) {
				return;
			}
			String reString = "Failed";
			if (isSuccess) {
				reString = "Success";
			}
			statusEditText.setText("result: " + reString + "\ndata: " + result);

		}

		@Override
		public void onRequestSetPin() {
			if (mIsWorking == false) {
				return;
			}
			dismissDialog();

			dialog = new Dialog(SwipeCardActivity.this);
			dialog.setContentView(R.layout.pin_dialog);
			dialog.setTitle(getString(R.string.enter_pin));

			dialog.findViewById(R.id.confirmButton).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							String pin = ((EditText) dialog
									.findViewById(R.id.pinEditText)).getText()
									.toString();
							if (pin.length() >= 4 && pin.length() <= 12) {
								pos.sendPin(pin);
								dismissDialog();
							}
						}
					});

			dialog.findViewById(R.id.bypassButton).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
//							pos.emptyPin();
							pos.sendPin("");
							dismissDialog();
						}
					});

			dialog.findViewById(R.id.cancelButton).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							isPinCanceled = true;
							pos.cancelPin();
							dismissDialog();
						}
					});

			dialog.show();

		}

		@Override
		public void onReturnSetMasterKeyResult(boolean isSuccess) {
			if (mIsWorking == false) {
				return;
			}
			statusEditText.setText("result: " + isSuccess);

		}

		@Override
		public void onReturnBatchSendAPDUResult(

		LinkedHashMap<Integer, String> batchAPDUResult) {
			if (mIsWorking == false) {
				return;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("APDU Responses: \n");
			for (HashMap.Entry<Integer, String> entry : batchAPDUResult
					.entrySet()) {
				sb.append("[" + entry.getKey() + "]: " + entry.getValue()
						+ "\n");
			}
			statusEditText.setText("\n" + sb.toString());

		}

		@Override
		public void onBluetoothBondFailed() {
			if (mIsWorking == false) {
				return;
			}
			statusEditText.setText("bond failed");

		}

		@Override
		public void onBluetoothBondTimeout() {
			if (mIsWorking == false) {
				return;
			}
			statusEditText.setText("bond timeout");

		}

		@Override
		public void onBluetoothBonded() {
			if (mIsWorking == false) {
				return;
			}
			statusEditText.setText("bond success");

		}

		@Override
		public void onBluetoothBonding() {
			if (mIsWorking == false) {
				return;
			}
			statusEditText.setText("bonding .....");

		}

		@Override
		public void onReturniccCashBack(Hashtable<String, String> result) {
			if (mIsWorking == false) {
				return;
			}
			String s = "serviceCode: " + result.get("serviceCode");
			s += "\n";
			s += "trackblock: " + result.get("trackblock");

			statusEditText.setText(s);

		}

		@Override
		public void onLcdShowCustomDisplay(boolean arg0) {

		}

		@Override
		public void onUpdatePosFirmwareResult(UpdateInformationResult arg0) {
			if (mIsWorking == false) {
				return;
			}
			if (arg0 == UpdateInformationResult.UPDATE_SUCCESS) {
				statusEditText.setText("升级完成");
			} else if (arg0 == UpdateInformationResult.UPDATE_LOWPOWER) {
				statusEditText.setText("电量");
			} else {
				statusEditText.setText("升级失败");
			}

		}

	}

	private void clearDisplay() {
		statusEditText.setText("");
	}

	class MyOnClickListener implements OnClickListener {

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			String content = getString(R.string.card_swiped);
			statusEditText.setText("");
			if (selectBTFlag) {
				// statusEditText.setText(R.string.wait);
				return;
			}
			if (v == doTradeButton) {
				//by Lu : BBPos's job
//				if(isSelectedBBPos == true) { 
//					promptForCheckCard(); //bbpos‘s Start btn
//				} else { 
					//QPos's job
				if (pos == null) {
					statusEditText.setText(R.string.scan_bt_pos_error);
					return;
				}

				if (posType == POS_TYPE.BLUETOOTH) {
					if (blueTootchAddress == null
							|| "".equals(blueTootchAddress)) {
						statusEditText.setText(R.string.scan_bt_pos_error);
						return;
					}
				}
				isPinCanceled = false;
				amountEditText.setText("");
				//					integralEditText.setText("");
				statusEditText.setText(R.string.starting);

				pos.doTrade(60);
//				}
			} else if(v == doQuickTradeButton) { //by Lu
				isQuickTrade = true;
				if (pos == null) {
					statusEditText.setText(R.string.scan_bt_pos_error);
					return;
				}
				if (posType == POS_TYPE.BLUETOOTH) {
					if (blueTootchAddress == null
							|| "".equals(blueTootchAddress)) {
						statusEditText.setText(R.string.scan_bt_pos_error);
						return;
					}
				}
				isPinCanceled = false;
				amountEditText.setText("");
				//integralEditText.setText("");
				statusEditText.setText(R.string.starting);
				pos.doTrade(60);
			} else if (v == btnBT) {
				//by Lu
				isChooseBtnClick = true;
				close();
				doScanBTPos();
			} else if (v == btnDisconnect) {
				if (mAdapter != null) {
					mAdapter.cancelDiscovery();
				}
				close();
			} else if (v == mBtnAction) {
				
				//跳转到签名
				Intent intent = new Intent();
				intent.setClass(mContext, SignatureActivity.class);
				intent.putExtra("amount", mSuccessAmount);
				intent.putExtra("listItems", (Serializable) listItems);
				intent.putExtra("cardInfo", content);
				startActivity(intent);
				finish();
				// startActivityForResult(intent, VariablesComm.RESULT_OK);
			} else if (v == testBtn) { //by Lu : 积分刷卡测试
				Log.d("luzhaojie", "click test_btn");
				IntegralBean integralBean = new IntegralBean();
				integralBean.setIntegral(myIntegral);
				DBUtils.insert(getApplicationContext(), integralBean);
				Toast.makeText(getApplicationContext(), "积分已经录入数据库", 0).show();
				
				List<IntegralBean> integrals = DBUtils.query(getApplicationContext());
				StringBuilder result = new StringBuilder();
				result.append("_id\t\tintegral\n");
				result.append("-----------------------------------\n");
				for(int i = 0; i < integrals.size(); i++) {
					result.append(integrals.get(i).toString());
				}
				showDb.setText(result);
				return;
			}
		}
	}

	public void onSelectBluetoothName(final ArrayList<String> btList) {
		dismissDialog();

		dialog = new Dialog(SwipeCardActivity.this);
		dialog.setContentView(R.layout.search_bt_name);
		dialog.setTitle(R.string.please_select_bt_name);

		String[] appNameList = new String[btList.size()];
		for (int i = 0; i < appNameList.length; ++i) {
			Log.d(LOG_TAG, "i=" + i + "," + btList.get(i));
			appNameList[i] = btList.get(i).split(",")[0];
		}

		ListView btListView = (ListView) dialog.findViewById(R.id.btList);
		btListView.setAdapter(new ArrayAdapter<String>(SwipeCardActivity.this,
				android.R.layout.simple_list_item_1, appNameList));
		btListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				blueTootchAddress = btList.get(position).split(",")[1];
				dismissDialog();
				Log.d(LOG_TAG, "blueTootchAddress:" + blueTootchAddress);
				sendMsg(1001);

			}

		});
		dialog.show();
	}

	private void sendMsg(int what) {
		Message msg = new Message();
		msg.what = what;
		mHandler.sendMessage(msg);
	}

	private boolean selectBTFlag = false;
	private long start_time = 0l;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1001:
				btnBT.setEnabled(false);
				doTradeButton.setEnabled(false);
				selectBTFlag = true;
				statusEditText.setText(R.string.connecting_bt_pos);
				sendMsg(1002);
				break;
			case 1002:
				pos.connectBluetoothDevice(20, blueTootchAddress);
				btnBT.setEnabled(true);
				// doTradeButton.setEnabled(true);
				selectBTFlag = false;
				break;
			case 1003:
				pos.doTrade();
				break;
			default:
				break;
			}
		}
	};
	
	private String ksn;
	private String encTrack2;
	private String maskedPAN;
	//by Lu : QPosS刷卡结果处理
	public String onDoTradeResultJob(Hashtable<String, String> decodeData){
		Log.d(LOG_TAG, "decodeData: " + decodeData);
		String content = getString(R.string.card_swiped);
		String formatID = decodeData.get("formatID");
		if (formatID.equals("31") || formatID.equals("40")
				|| formatID.equals("37") || formatID.equals("17")
				|| formatID.equals("11") || formatID.equals("10")) {
			String maskedPAN = decodeData.get("maskedPAN");
			String expiryDate = decodeData.get("expiryDate");
			String cardHolderName = decodeData.get("cardholderName");
			String serviceCode = decodeData.get("serviceCode");
			String trackblock = decodeData.get("trackblock");
			String psamId = decodeData.get("psamId");
			String posId = decodeData.get("posId");
			String pinblock = decodeData.get("pinblock");
			String macblock = decodeData.get("macblock");
			String activateCode = decodeData.get("activateCode");
			String trackRandomNumber = decodeData
					.get("trackRandomNumber");

			content += getString(R.string.format_id) + " " + formatID
					+ "\n";
			content += getString(R.string.masked_pan) + " " + maskedPAN
					+ "\n";
			content += getString(R.string.expiry_date) + " "
					+ expiryDate + "\n";
			content += getString(R.string.cardholder_name) + " "
					+ cardHolderName + "\n";

			content += getString(R.string.service_code) + " "
					+ serviceCode + "\n";
			content += "trackblock: " + trackblock + "\n";
			content += "psamId: " + psamId + "\n";
			content += "posId: " + posId + "\n";
			content += getString(R.string.pinBlock) + " " + pinblock
					+ "\n";
			content += "macblock: " + macblock + "\n";
			content += "activateCode: " + activateCode + "\n";
			content += "trackRandomNumber: " + trackRandomNumber + "\n";
		} else {
			String maskedPAN = decodeData.get("maskedPAN"); this.maskedPAN = maskedPAN;
			String expiryDate = decodeData.get("expiryDate");
			String cardHolderName = decodeData.get("cardholderName");
			String ksn = decodeData.get("ksn"); 
			String serviceCode = decodeData.get("serviceCode");
			String track1Length = decodeData.get("track1Length");
			String track2Length = decodeData.get("track2Length");
			String track3Length = decodeData.get("track3Length");
			String encTracks = decodeData.get("encTracks");
			String encTrack1 = decodeData.get("encTrack1");
			String encTrack2 = decodeData.get("encTrack2"); this.encTrack2 = encTrack2;
			String encTrack3 = decodeData.get("encTrack3");
			String partialTrack = decodeData.get("partialTrack");
			String pinKsn = decodeData.get("pinKsn");
			String trackksn = decodeData.get("trackksn"); this.ksn = trackksn;
			String pinBlock = decodeData.get("pinBlock");
			String encPAN = decodeData.get("encPAN");
			String trackRandomNumber = decodeData
					.get("trackRandomNumber");
			String pinRandomNumber = decodeData.get("pinRandomNumber");

			content += getString(R.string.format_id) + " " + formatID
					+ "\n";
			content += getString(R.string.masked_pan) + " " + maskedPAN
					+ "\n";
			content += getString(R.string.expiry_date) + " "
					+ expiryDate + "\n";
			content += getString(R.string.cardholder_name) + " "
					+ cardHolderName + "\n";
			content += getString(R.string.ksn) + " " + ksn + "\n";
			content += getString(R.string.pinKsn) + " " + pinKsn + "\n";
			content += getString(R.string.trackksn) + " " + trackksn
					+ "\n";
			content += getString(R.string.service_code) + " "
					+ serviceCode + "\n";
			content += getString(R.string.track_1_length) + " "
					+ track1Length + "\n";
			content += getString(R.string.track_2_length) + " "
					+ track2Length + "\n";
			content += getString(R.string.track_3_length) + " "
					+ track3Length + "\n";
			content += getString(R.string.encrypted_tracks) + " "
					+ encTracks + "\n";
			content += getString(R.string.encrypted_track_1) + " "
					+ encTrack1 + "\n";
			content += getString(R.string.encrypted_track_2) + " "
					+ encTrack2 + "\n";
			content += getString(R.string.encrypted_track_3) + " "
					+ encTrack3 + "\n";
			content += getString(R.string.partial_track) + " "
					+ partialTrack + "\n";
			content += getString(R.string.pinBlock) + " " + pinBlock
					+ "\n";
			content += "encPAN: " + encPAN + "\n";
			content += "trackRandomNumber: " + trackRandomNumber + "\n";
			content += "pinRandomNumber:" + " " + pinRandomNumber
					+ "\n";

		}
		Log.d(LOG_TAG, "swipe card:" + content);
		return content;
	}
	
	Handler handler = new Handler() {  
	    @Override  
	    public void handleMessage(Message msg) {  
	        super.handleMessage(msg);  
	        Bundle data = msg.getData();  
	        String val = data.getString("value");  
	        statusEditText.setText(val);
	        Log.i("POS_LOG", "response result-->" + val);  
	    }  
	}; 
	
	/*
	 * 发送刷卡结果到Apriva后台
	 */
	private String aprivaConn(Hashtable<String, String> decodeData,Hashtable<String, String> otherInfo) { 
		AprivaConn aprivaConn = new AprivaConn(SwipeCardActivity.this,decodeData,otherInfo);
		String content = getString(R.string.card_swiped);
		try {
			content = aprivaConn.connect();
			return content;
		} catch (Exception e) {
			Log.e("POS_LOG","AprivaConn fail");
			return "AprivaConn fail";
		}
	}
}
