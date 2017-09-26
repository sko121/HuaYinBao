package com.thtfit.pos.bbpos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.bbpos.bbdevice.BBDeviceController.AudioAutoConfigError;
import com.bbpos.bbdevice.BBDeviceController.NfcDetectCardResult;
import com.bbpos.bbdevice.CAPK;
import com.bbpos.bbdevice.BBDeviceController;
import com.bbpos.bbdevice.BBDeviceController.AmountInputType;
import com.bbpos.bbdevice.BBDeviceController.BatteryStatus;
import com.bbpos.bbdevice.BBDeviceController.CardScheme;
import com.bbpos.bbdevice.BBDeviceController.CheckCardMode;
import com.bbpos.bbdevice.BBDeviceController.CheckCardResult;
import com.bbpos.bbdevice.BBDeviceController.ConnectionMode;
import com.bbpos.bbdevice.BBDeviceController.CurrencyCharacter;
import com.bbpos.bbdevice.BBDeviceController.DisplayText;
import com.bbpos.bbdevice.BBDeviceController.EmvOption;
import com.bbpos.bbdevice.BBDeviceController.EncryptionKeySource;
import com.bbpos.bbdevice.BBDeviceController.EncryptionKeyUsage;
import com.bbpos.bbdevice.BBDeviceController.EncryptionMethod;
import com.bbpos.bbdevice.BBDeviceController.EncryptionPaddingMethod;
import com.bbpos.bbdevice.BBDeviceController.Error;
import com.bbpos.bbdevice.BBDeviceController.PhoneEntryResult;
import com.bbpos.bbdevice.BBDeviceController.PinEntryResult;
import com.bbpos.bbdevice.BBDeviceController.PinEntrySource;
import com.bbpos.bbdevice.BBDeviceController.PrintResult;
import com.bbpos.bbdevice.BBDeviceController.SessionError;
import com.bbpos.bbdevice.BBDeviceController.TerminalSettingStatus;
import com.bbpos.bbdevice.BBDeviceController.TransactionResult;
import com.bbpos.bbdevice.BBDeviceController.TransactionType;
import com.bbpos.bbdevice.BBDeviceController.BBDeviceControllerListener;
import com.thtfit.pos.R;
import com.thtfit.pos.api.Money;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class BaseActivity extends Activity {

	protected enum State {
		GETTING_PSE, READING_RECORD, READING_AID, GETTING_PROCESS_OPTION, READING_DATA
	}

	protected static final String[] DEVICE_NAMES = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	protected static BBDeviceController bbDeviceController;
	protected static MyBBDeviceControllerListener listener;
	protected static BaseActivity currentActivity;

	protected static String masterKey = "11223344556677889900AABBCCDDEEFF";

	protected static String pinSessionKey = "A1223344556677889900AABBCCDDEEFF";
	protected static String encryptedPinSessionKey = "";
	protected static String pinKcv = "";

	protected static String dataSessionKey = "A2223344556677889900AABBCCDDEEFF";
	protected static String encryptedDataSessionKey = "";
	protected static String dataKcv = "";

	protected static String trackSessionKey = "A4223344556677889900AABBCCDDEEFF";
	protected static String encryptedTrackSessionKey = "";
	protected static String trackKcv = "";

	protected static String macSessionKey = "A6223344556677889900AABBCCDDEEFF";
	protected static String encryptedMacSessionKey = "";
	protected static String macKcv = "";

	protected static String fid65WorkingKey = "A1223344556677889900AABBCCDDEEFF";
	protected static String fid65MasterKey = "0123456789ABCDEFFEDCBA9876543210";

	protected static Spinner fidSpinner;
	protected static Button startButton;
	protected static EditText amountEditText;
	protected static EditText statusEditText;
	protected static ListView appListView;
	protected static Dialog dialog;
	protected static ProgressDialog progressDialog;

	protected static Button clearLogButton;
	protected static Button powerOnIccButton;
	protected static Button powerOffIccButton;
	protected static Button apduButton;
	protected static Button getCAPKListButton;
	protected static Button getCAPKDetailButton;
	protected static Button findCAPKButton;
	protected static Button updateCAPKButton;
	protected static Button getEmvReportListButton;
	protected static Button getEmvReportButton;
	
	protected static Button updateGprsSettingButton;
	protected static Button updateWifiSettingButton;
	protected static Button readGprsSettingButton;
	protected static Button readWifiSettingButton;

	protected static ArrayAdapter<String> arrayAdapter;
	protected static String amount = "";
	protected static String cashbackAmount = "";
	protected static boolean isPinCanceled = false;

	protected static List<BluetoothDevice> foundDevices;

	protected static ArrayList<byte[]> receipts;

	protected static String cardholderName;
	protected static String cardNumber;
	protected static String expiryDate;
	protected static String track2 = "";
	protected static String pan = "";
	protected static String aid;
	protected static String appLabel;
	protected static String tc;
	protected static String batchNum;
	protected static String tid;
	protected static String mid;
	protected static String transactionDateTime;
	protected static boolean signatureRequired;

	protected static String ksn = "";
	protected static boolean isApduEncrypted = true;
	protected static final String DATA_KEY = "Data Key";
	protected static final String DATA_KEY_VAR = "Data Key Var";
	protected static final String PIN_KEY_VAR = "PIN Key Var";

	protected static final String ECB = "ECB";
	protected static final String CBC = "CBC";
	protected static String keyMode = DATA_KEY;
	protected static String encryptionMode = CBC;
	protected static State state = null;

	private static int aidCounter = 0;
	private static String[] afls = null;
	private static int aflCounter = 0;
	private static String sfi = "";
	private static int readingFileIndex = 0;
	private static int total = 0;

	protected static long startTime;
	protected static boolean isPKCS7 = false;
	protected boolean isSwitchingActivity = false;
	
	//by Lu
	private String mReceiveAmount;

	private static String[] aids = new String[] { "A0000000031010", "A0000000041010", "A00000002501" };
	
	private static CheckCardMode checkCardMode;
	
	private static final String BDK = "0123456789ABCDEFFEDCBA9876543210";
	private String uid = null;
	
	private static final boolean DEBUG_MODE = false;

	private final static String LOG_TAG = BaseActivity.class.getName();

	private void log(String msg) {
		if (DEBUG_MODE) {
			Log.d(LOG_TAG, "[BaseActivity] " + msg);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (bbDeviceController == null) {
			listener = new MyBBDeviceControllerListener();
			bbDeviceController = BBDeviceController.getInstance(this, listener);
			BBDeviceController.setDebugLogEnabled(true);
			bbDeviceController.setDetectAudioDevicePlugged(true);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isSwitchingActivity) {
			isSwitchingActivity = false;
		} else {
			bbDeviceController.disconnectBT();
		}
	}

	public void dismissDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	public String encrypt(String data, String key) {
		if (key.length() == 16) {
			key += key.substring(0, 8);
		}
		byte[] d = hexToByteArray(data);
		byte[] k = hexToByteArray(key);

		SecretKey sk = new SecretKeySpec(k, "DESede");
		try {
			Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, sk);
			byte[] enc = cipher.doFinal(d);
			return toHexString(enc);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void onStart() {
		super.onStart();
		statusEditText.setText("BBDevice API : " + BBDeviceController.getApiVersion());
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void injectNextSessionKey() {
		if (!encryptedPinSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "1");
			data.put("encSK", encryptedPinSessionKey);
			data.put("kcv", pinKcv);
			statusEditText.setText(getString(R.string.sending_encrypted_pin_session_key));
			encryptedPinSessionKey = "";
			bbDeviceController.injectSessionKey(data);
			return;
		}

		if (!encryptedDataSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "2");
			data.put("encSK", encryptedDataSessionKey);
			data.put("kcv", dataKcv);
			statusEditText.setText(getString(R.string.sending_encrypted_data_session_key));
			encryptedDataSessionKey = "";
			bbDeviceController.injectSessionKey(data);
			return;
		}

		if (!encryptedTrackSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "3");
			data.put("encSK", encryptedTrackSessionKey);
			data.put("kcv", trackKcv);
			statusEditText
					.setText(getString(R.string.sending_encrypted_track_session_key));
			encryptedTrackSessionKey = "";
			bbDeviceController.injectSessionKey(data);
			return;
		}

		if (!encryptedMacSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "4");
			data.put("encSK", encryptedMacSessionKey);
			data.put("kcv", macKcv);
			statusEditText
					.setText(getString(R.string.sending_encrypted_mac_session_key));
			encryptedMacSessionKey = "";
			bbDeviceController.injectSessionKey(data);
			return;
		}
	}

	public void stopConnection() {
		ConnectionMode connectionMode = bbDeviceController.getConnectionMode();
		if (connectionMode == ConnectionMode.BLUETOOTH) {
			bbDeviceController.disconnectBT();
		} else if (connectionMode == ConnectionMode.AUDIO) {
			bbDeviceController.stopAudio();
		} else if (connectionMode == ConnectionMode.SERIAL) {
			bbDeviceController.stopSerial();
		} else if (connectionMode == ConnectionMode.USB) {
			bbDeviceController.stopUsb();
		}
	}

	public void promptForConnection() {
		dismissDialog();
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.connection_dialog);
		dialog.setTitle(getString(R.string.connection));

		String[] connections = new String[4];
		connections[0] = "Bluetooth";
		connections[1] = "Audio";
		connections[2] = "Serial";
		connections[3] = "USB";

		ListView listView = (ListView) dialog.findViewById(R.id.connectionList);
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, connections));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dismissDialog();
				if (position == 0) {
					bbDeviceController.startBTScan(null, 120);//by Lu
					Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
					final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
					for (int i = 0; i < pairedObjects.length; ++i) {
						pairedDevices[i] = (BluetoothDevice) pairedObjects[i];
					}

					final ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_list_item_1);
					for (int i = 0; i < pairedDevices.length; ++i) {
						mArrayAdapter.add(pairedDevices[i].getName());
					}

					dismissDialog();

					dialog = new Dialog(currentActivity);
					dialog.setContentView(R.layout.bluetooth_2_device_list_dialog);
					dialog.setTitle(R.string.bluetooth_devices);

					ListView listView1 = (ListView) dialog.findViewById(R.id.pairedDeviceList);
					listView1.setAdapter(mArrayAdapter);
					listView1.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							statusEditText.setText(getString(R.string.connecting_bluetooth));
							bbDeviceController.connectBT(pairedDevices[position]);
							dismissDialog();
						}

					});

					arrayAdapter = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_list_item_1);
					ListView listView2 = (ListView) dialog.findViewById(R.id.discoveredDeviceList);
					listView2.setAdapter(arrayAdapter);
					listView2.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

							statusEditText.setText(getString(R.string.connecting_bluetooth));
							bbDeviceController.connectBT(foundDevices.get(position));
							dismissDialog();
						}

					});

					dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							bbDeviceController.stopBTScan();
							dismissDialog();
						}
					});
					dialog.setCancelable(false);
					dialog.show();

					bbDeviceController.startBTScan(DEVICE_NAMES, 120);
				} else if (position == 1) {
					bbDeviceController.startAudio();
				} else if (position == 2) {
					bbDeviceController.startSerial();
				} else if (position == 3) {
					bbDeviceController.startUsb();
				}
			}

		});

		dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissDialog();
			}
		});

		dialog.show();
	}
	
	//by Lu
	public void setMReceiveAmount(String mReceiveAmount) {
		this.mReceiveAmount = mReceiveAmount;
	}

	public void promptForAmount() {
		dismissDialog();
		dialog = new Dialog(currentActivity);
		dialog.setContentView(R.layout.amount_dialog);
		dialog.setTitle(getString(R.string.set_amount));
		
		//by Lu
		amountEditText = (EditText) (dialog.findViewById(R.id.amountEditText));
		amountEditText.setText("$" + mReceiveAmount);
		
		String[] symbols = new String[] { "DOLLAR", "RUPEE", "YEN", "POUND", "EURO", "WON", "DIRHAM", "RIYAL", "AED", "BS.", "YUAN", "NULL" };
		((Spinner) dialog.findViewById(R.id.symbolSpinner)).setAdapter(new ArrayAdapter<String>(currentActivity, android.R.layout.simple_spinner_item, symbols));

		String[] transactionTypes = new String[] { "GOODS", "SERVICES", "CASHBACK", "INQUIRY", "TRANSFER", "PAYMENT", "REFUND", "VOID", "REVERSAL" };
		((Spinner) dialog.findViewById(R.id.transactionTypeSpinner)).setAdapter(new ArrayAdapter<String>(currentActivity, android.R.layout.simple_spinner_item, transactionTypes));

		dialog.findViewById(R.id.setButton).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				amountEditText = (EditText) (dialog.findViewById(R.id.amountEditText));
				String amount = amountEditText.getText().toString();
				String cashbackAmount = ((EditText) (dialog.findViewById(R.id.cashbackAmountEditText))).getText().toString();
				String transactionTypeString = (String) ((Spinner) dialog.findViewById(R.id.transactionTypeSpinner)).getSelectedItem();
				String symbolString = (String) ((Spinner) dialog.findViewById(R.id.symbolSpinner)).getSelectedItem();

				TransactionType transactionType = TransactionType.GOODS;
				if (transactionTypeString.equals("GOODS")) {
					transactionType = TransactionType.GOODS;
				} else if (transactionTypeString.equals("SERVICES")) {
					transactionType = TransactionType.SERVICES;
				} else if (transactionTypeString.equals("CASHBACK")) {
					transactionType = TransactionType.CASHBACK;
				} else if (transactionTypeString.equals("INQUIRY")) {
					transactionType = TransactionType.INQUIRY;
				} else if (transactionTypeString.equals("TRANSFER")) {
					transactionType = TransactionType.TRANSFER;
				} else if (transactionTypeString.equals("PAYMENT")) {
					transactionType = TransactionType.PAYMENT;
				} else if (transactionTypeString.equals("REFUND")) {
					transactionType = TransactionType.REFUND;
				} else if (transactionTypeString.equals("VOID")) {
					transactionType = TransactionType.VOID;
				} else if (transactionTypeString.equals("REVERSAL")) {
					transactionType = TransactionType.REVERSAL;
				}

				CurrencyCharacter[] currencyCharacters = new CurrencyCharacter[] { CurrencyCharacter.A, CurrencyCharacter.B, CurrencyCharacter.C };
				if (symbolString.equals("DOLLAR")) {
					currencyCharacters = new CurrencyCharacter[] { CurrencyCharacter.DOLLAR };
				} else if (symbolString.equals("RUPEE")) {
					currencyCharacters = new CurrencyCharacter[] { CurrencyCharacter.RUPEE };
				} else if (symbolString.equals("YEN")) {
					currencyCharacters = new CurrencyCharacter[] { CurrencyCharacter.YEN };
				} else if (symbolString.equals("POUND")) {
					currencyCharacters = new CurrencyCharacter[] { CurrencyCharacter.POUND };
				} else if (symbolString.equals("EURO")) {
					currencyCharacters = new CurrencyCharacter[] { CurrencyCharacter.EURO };
				} else if (symbolString.equals("WON")) {
					currencyCharacters = new CurrencyCharacter[] { CurrencyCharacter.WON };
				} else if (symbolString.equals("DIRHAM")) {
					currencyCharacters = new CurrencyCharacter[] { CurrencyCharacter.DIRHAM };
				} else if (symbolString.equals("RIYAL")) {
					currencyCharacters = new CurrencyCharacter[] { CurrencyCharacter.RIYAL, CurrencyCharacter.RIYAL_2 };
				} else if (symbolString.equals("AED")) {
					currencyCharacters = new CurrencyCharacter[] { CurrencyCharacter.A, CurrencyCharacter.E, CurrencyCharacter.D };
				} else if (symbolString.equals("BS.")) {
					currencyCharacters = new CurrencyCharacter[] { CurrencyCharacter.B, CurrencyCharacter.S, CurrencyCharacter.DOT };
				} else if (symbolString.equals("YUAN")) {
					currencyCharacters = new CurrencyCharacter[] { CurrencyCharacter.YUAN };
				} else if (symbolString.equals("NULL")) {
					currencyCharacters = null;
				}

				String currencyCode;
				if (Locale.getDefault().getCountry().equalsIgnoreCase("CN")) {
					currencyCode = "156";
					currencyCharacters = new CurrencyCharacter[] { CurrencyCharacter.YUAN };
				} else {
					currencyCode = "840";
				}

				//by Lu
				Money money = new Money(mReceiveAmount);
				boolean hasNoError = true;
				if (!money.isGreaterThanZero()) {
					Log.d(LOG_TAG, getString(R.string.price_is_zero));
					Toast.makeText(getApplicationContext(), getString(R.string.price_is_zero), Toast.LENGTH_SHORT).show();
					hasNoError = false;
				}
				if (!hasNoError) {
					return;
				}
				
				amount = money.toDefaultString();
				if (bbDeviceController.setAmount(amount, cashbackAmount, currencyCode, transactionType, currencyCharacters)) {
					amountEditText.setText("$" + amount);
					currentActivity.amount = amount;
					currentActivity.cashbackAmount = cashbackAmount;
					statusEditText.setText(getString(R.string.please_confirm_amount));
					dismissDialog();
				} else {
					promptForAmount();
				}
			}

		});

		dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				bbDeviceController.cancelSetAmount();
				dialog.dismiss();
			}

		});

		dialog.show();
	}
	
	public void promptForCheckCard() {
		dismissDialog();
		dialog = new Dialog(currentActivity);
		dialog.setContentView(R.layout.check_card_mode_dialog);
		dialog.setTitle(getString(R.string.select_mode));

		String[] swipeInsertTap = new String[7];
		swipeInsertTap[0] = getString(R.string.swipe_or_insert);
		swipeInsertTap[1] = getString(R.string.swipe);
		swipeInsertTap[2] = getString(R.string.insert);
		swipeInsertTap[3] = getString(R.string.tap);
		swipeInsertTap[4] = getString(R.string.swipe_or_tap);
		swipeInsertTap[5] = getString(R.string.insert_or_tap);
		swipeInsertTap[6] = getString(R.string.swipe_or_insert_or_tap);

		ListView listView = (ListView) dialog.findViewById(R.id.swipeInsertTapList);
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, swipeInsertTap));

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dismissDialog();
				if (position == 0) {
					checkCardMode = CheckCardMode.SWIPE_OR_INSERT;
					checkCard();
				} else if (position == 1) {
					checkCardMode = CheckCardMode.SWIPE;
					checkCard();
				} else if (position == 2) {
					checkCardMode = CheckCardMode.INSERT;
					checkCard();
				} else if (position == 3) {
					checkCardMode = CheckCardMode.TAP;
					startEmv();
				} else if (position == 4) {
					checkCardMode = CheckCardMode.SWIPE_OR_TAP;
					startEmv();
				} else if (position == 5) {
					checkCardMode = CheckCardMode.INSERT_OR_TAP;
					startEmv();
				} else if (position == 6) {
					checkCardMode = CheckCardMode.SWIPE_OR_INSERT_OR_TAP;
					startEmv();
				}
			}

		});

		dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissDialog();
				statusEditText.setText("");
			}
		});

		dialog.show();
	}
	
	public void promptForStartEmv() {
		dismissDialog();
		dialog = new Dialog(currentActivity);
		dialog.setContentView(R.layout.check_card_mode_dialog);
		dialog.setTitle(getString(R.string.select_mode));

		String[] swipeInsertTap = new String[7];
		swipeInsertTap[0] = getString(R.string.swipe_or_insert);
		swipeInsertTap[1] = getString(R.string.swipe);
		swipeInsertTap[2] = getString(R.string.insert);
		swipeInsertTap[3] = getString(R.string.tap);
		swipeInsertTap[4] = getString(R.string.swipe_or_tap);
		swipeInsertTap[5] = getString(R.string.insert_or_tap);
		swipeInsertTap[6] = getString(R.string.swipe_or_insert_or_tap);

		ListView listView = (ListView) dialog.findViewById(R.id.swipeInsertTapList);
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, swipeInsertTap));

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dismissDialog();
				if (position == 0) {
					checkCardMode = CheckCardMode.SWIPE_OR_INSERT;
				} else if (position == 1) {
					checkCardMode = CheckCardMode.SWIPE;
				} else if (position == 2) {
					checkCardMode = CheckCardMode.INSERT;
				} else if (position == 3) {
					checkCardMode = CheckCardMode.TAP;
				} else if (position == 4) {
					checkCardMode = CheckCardMode.SWIPE_OR_TAP;
				} else if (position == 5) {
					checkCardMode = CheckCardMode.INSERT_OR_TAP;
				} else if (position == 6) {
					checkCardMode = CheckCardMode.SWIPE_OR_INSERT_OR_TAP;
				}
				startEmv();
			}

		});

		dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissDialog();
				statusEditText.setText("");
			}
		});

		dialog.show();
	}
	
	public void promptForGprs() {
		dismissDialog();
		dialog = new Dialog(currentActivity);
		dialog.setContentView(R.layout.gprs_dialog);
		dialog.setTitle(getString(R.string.gprs));

		((EditText) (dialog.findViewById(R.id.gprsOperatorEditText))).setText("CSL");
		((EditText) (dialog.findViewById(R.id.gprsAPNEditText))).setText("hkcsl");
		((EditText) (dialog.findViewById(R.id.gprsUsernameEditText))).setText("guest");
		((EditText) (dialog.findViewById(R.id.gprsPasswordEditText))).setText("guest");

		dialog.findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String gprsOperator = ((EditText) (dialog.findViewById(R.id.gprsOperatorEditText))).getText().toString();
				String gprsAPN = ((EditText) (dialog.findViewById(R.id.gprsAPNEditText))).getText().toString();
				String gprsUsername = ((EditText) (dialog.findViewById(R.id.gprsUsernameEditText))).getText().toString();
				String gprsPassword = ((EditText) (dialog.findViewById(R.id.gprsPasswordEditText))).getText().toString();
				
				Hashtable<String, String> gprsData = new Hashtable<String, String>();
				gprsData.put("operator", gprsOperator);
				gprsData.put("apn", gprsAPN);
				gprsData.put("username", gprsUsername);
				gprsData.put("password", gprsPassword);
				
				bbDeviceController.updateGprsSettings(gprsData);

				dialog.dismiss();
			}

		});

		dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		});

		dialog.show();
	}
	
	public void promptForWifi() {
		dismissDialog();
		dialog = new Dialog(currentActivity);
		dialog.setContentView(R.layout.wifi_dialog);
		dialog.setTitle(getString(R.string.wifi));

		((EditText) (dialog.findViewById(R.id.wifiSSIDEditText))).setText("BBPOS_AP");
		((EditText) (dialog.findViewById(R.id.wifiPasswordEditText))).setText("bb1904@AP");
		((EditText) (dialog.findViewById(R.id.wifiUrlEditText))).setText("ws://chip.mswipetech.com/mswipeGWG2/TxHandlerG2.ashx");
		((EditText) (dialog.findViewById(R.id.wifiPortNumberEditText))).setText("8080");

		dialog.findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String wifiSSID = ((EditText) (dialog.findViewById(R.id.wifiSSIDEditText))).getText().toString();
				String wifiPassword = ((EditText) (dialog.findViewById(R.id.wifiPasswordEditText))).getText().toString();
				String wifiUrl = ((EditText) (dialog.findViewById(R.id.wifiUrlEditText))).getText().toString();
				String wifiPortNumber = ((EditText) (dialog.findViewById(R.id.wifiPortNumberEditText))).getText().toString();
				
				Hashtable<String, String> wifiData = new Hashtable<String, String>();
				if (((CheckBox)(dialog.findViewById(R.id.enableWifiSSIDCheckBox))).isChecked()) {
					wifiData.put("ssid", wifiSSID);
				}
				
				if (((CheckBox)(dialog.findViewById(R.id.enableWifiPasswordCheckBox))).isChecked()) {
					wifiData.put("password", wifiPassword);
				}
				
				if (((CheckBox)(dialog.findViewById(R.id.enableWifiUrlCheckBox))).isChecked()) {
					wifiData.put("url", wifiUrl);
				}
				
				if (((CheckBox)(dialog.findViewById(R.id.enableWifiPortNumberCheckBox))).isChecked()) {
					wifiData.put("portNumber", wifiPortNumber);
				}
				
				bbDeviceController.updateWiFiSettings(wifiData);

				dialog.dismiss();
			}

		});

		dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		});

		dialog.show();
	}	
	
	public void promptForInitSession() {
		dismissDialog();
		dialog = new Dialog(currentActivity);
		dialog.setContentView(R.layout.general_string_input_dialog);
		dialog.setTitle(getString(R.string.init_session));
		
		((TextView)(dialog.findViewById(R.id.general1TextView))).setText("Vendor Token");
		((EditText) (dialog.findViewById(R.id.general1EditText))).setText("2BC1EF345F564C7C");
		
		((TextView)(dialog.findViewById(R.id.general2TextView))).setVisibility(View.GONE);
		((EditText) (dialog.findViewById(R.id.general2EditText))).setVisibility(View.GONE);
		
		((TextView)(dialog.findViewById(R.id.general3TextView))).setVisibility(View.GONE);
		((EditText) (dialog.findViewById(R.id.general3EditText))).setVisibility(View.GONE);
		
		((TextView)(dialog.findViewById(R.id.general4TextView))).setVisibility(View.GONE);
		((EditText) (dialog.findViewById(R.id.general4EditText))).setVisibility(View.GONE);
		
		((TextView)(dialog.findViewById(R.id.general5TextView))).setVisibility(View.GONE);
		((EditText) (dialog.findViewById(R.id.general5EditText))).setVisibility(View.GONE);
		
		((TextView)(dialog.findViewById(R.id.general6TextView))).setVisibility(View.GONE);
		((EditText) (dialog.findViewById(R.id.general6EditText))).setVisibility(View.GONE);
		
		((TextView)(dialog.findViewById(R.id.general7TextView))).setVisibility(View.GONE);
		((EditText) (dialog.findViewById(R.id.general7EditText))).setVisibility(View.GONE);
		
		((TextView)(dialog.findViewById(R.id.general8TextView))).setVisibility(View.GONE);
		((EditText) (dialog.findViewById(R.id.general8EditText))).setVisibility(View.GONE);
		
		dialog.findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				bbDeviceController.initSession(((EditText) (dialog.findViewById(R.id.general1EditText))).getText().toString());
				
				dialog.dismiss();
			}

		});

		dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		});

		dialog.show();
	}
	
	public void checkCard() {
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		if(checkCardMode != null) {
			data.put("checkCardMode", checkCardMode);
		}
		data.put("checkCardMode", checkCardMode);
		data.put("checkCardTimeout", "120");
		if(fidSpinner.getSelectedItem().equals("FID61")) {
			data.put("orderID", "0123456789ABCDEF0123456789ABCDEF");
			data.put("randomNumber", "012345");
		} else if(fidSpinner.getSelectedItem().equals("FID46")) {
			data.put("randomNumber", "0123456789ABCDEF");
		} else if(fidSpinner.getSelectedItem().equals("FID65")) {
			String encWorkingKey = TripleDES.encrypt(fid65WorkingKey, fid65MasterKey);
			String workingKeyKcv = TripleDES.encrypt("0000000000000000", fid65WorkingKey);
			
			data.put("encPinKey", encWorkingKey + workingKeyKcv);
			data.put("encDataKey", encWorkingKey + workingKeyKcv);
			data.put("encMacKey", encWorkingKey + workingKeyKcv);
		}
		bbDeviceController.checkCard(data);
	}
	
	public void startEmv() {
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		data.put("emvOption", EmvOption.START);
		if(fidSpinner.getSelectedItem().equals("FID61")) {
			data.put("orderID", "0123456789ABCDEF0123456789ABCDEF");
			data.put("randomNumber", "012345");
		} else if(fidSpinner.getSelectedItem().equals("FID46")) {
			data.put("randomNumber", "0123456789ABCDEF");
		} else if(fidSpinner.getSelectedItem().equals("FID65")) {
			String encWorkingKey = TripleDES.encrypt(fid65WorkingKey, fid65MasterKey);
			String workingKeyKcv = TripleDES.encrypt("0000000000000000", fid65WorkingKey);
			
			data.put("encPinKey", encWorkingKey + workingKeyKcv);
			data.put("encDataKey", encWorkingKey + workingKeyKcv);
			data.put("encMacKey", encWorkingKey + workingKeyKcv);
		}
		if(checkCardMode != null) {
			data.put("checkCardMode", checkCardMode);
		}
		
		String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
		data.put("terminalTime", terminalTime);
		bbDeviceController.startEmv(data);
	}

	private static String hexString2AsciiString(String hexString) {
		if (hexString == null) 
			return "";
		hexString = hexString.replaceAll(" ", "");
		if (hexString.length() % 2 != 0) {
			return "";
		}
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < hexString.length(); i+=2) {
			String str = hexString.substring(i, i+2);
			output.append((char)Integer.parseInt(str, 16));
		}
		return output.toString();
	}
	
	private static byte[] hexToByteArray(String s) {
		if (s == null) {
			s = "";
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		for (int i = 0; i < s.length() - 1; i += 2) {
			String data = s.substring(i, i + 2);
			bout.write(Integer.parseInt(data, 16));
		}
		return bout.toByteArray();
	}

	private static String toHexString(byte[] b) {
		if (b == null) {
			return "null";
		}
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xFF) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static void setStatus(String message) {
		statusEditText.setText(message + "\n" + statusEditText.getText().toString());
	}

	protected static String toHexString(byte b) {
		return Integer.toString((b & 0xFF) + 0x100, 16).substring(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bbpos_activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_start_connection) {
			promptForConnection();
			return true;
		} else if (item.getItemId() == R.id.menu_stop_connection) {
			stopConnection();
			return true;
		} else if (item.getItemId() == R.id.menu_init_session) {
			statusEditText.setText(R.string.initializing_session);
			promptForInitSession();
		} else if (item.getItemId() == R.id.menu_reset_session) {
			statusEditText.setText(R.string.reset_session);
			bbDeviceController.resetSession();
		} else if (item.getItemId() == R.id.menu_get_deivce_info) {
			statusEditText.setText(R.string.getting_info);
			bbDeviceController.getDeviceInfo();
		} else if(item.getItemId() == R.id.menu_unpair_all) {
    		new Thread(new Runnable() {
				@Override
				public void run() {
					final Handler handler = new Handler(Looper.getMainLooper());
					try {
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(BaseActivity.this, getString(R.string.unpair_all_start), Toast.LENGTH_SHORT).show();
							}
						});
						
						Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
						BluetoothDevice pairedDevices;
						for (int i = 0; i < pairedObjects.length; ++i) {
							pairedDevices = (BluetoothDevice) pairedObjects[i];

							try {
								Method m = pairedDevices.getClass().getMethod("removeBond", (Class[]) null);
								m.invoke(pairedDevices, (Object[]) null);
								Thread.sleep(3000);
							} catch (Exception e) {
							}
						}
						
					} catch (Exception e) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(BaseActivity.this, getString(R.string.unpair_all_fail), Toast.LENGTH_SHORT).show();
							}
						});
						return;
					}
					
					handler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(BaseActivity.this, getString(R.string.unpair_all_end), Toast.LENGTH_SHORT).show();
						}
					});

				}
			}).start();
    		
    		return true;
		} else if (item.getItemId() == R.id.menu_cancel_check_card) {
			statusEditText.setText(R.string.cancel_check_card);
			bbDeviceController.cancelCheckCard();
		} else if (item.getItemId() == R.id.menu_auto_config) {
			progressDialog = new ProgressDialog(this);
    		progressDialog.setCancelable(false);
    		progressDialog.setCanceledOnTouchOutside(false);
    		progressDialog.setTitle(R.string.auto_configuring);
    		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    		progressDialog.setMax(100);
    		progressDialog.setIndeterminate(false);
    		progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					statusEditText.setText(getString(R.string.canceling_auto_config));
					bbDeviceController.cancelAutoConfig();
				}
			});
    		progressDialog.show();
    		bbDeviceController.startAutoConfig();
		} else if (item.getItemId() == R.id.menu_enable_input_amount) {
			Hashtable<String, Object> data = new Hashtable<String, Object>();
			if (Locale.getDefault().getCountry().equalsIgnoreCase("CN")) {
				data.put("currencyCode", "156");
				data.put("currencyCharacters", new CurrencyCharacter[] { CurrencyCharacter.YEN });
			} else {
				data.put("currencyCode", "840");
				data.put("currencyCharacters", new CurrencyCharacter[] { CurrencyCharacter.DOLLAR });
			}
			data.put("amountInputType", AmountInputType.AMOUNT_AND_CASHBACK);
			bbDeviceController.enableInputAmount(data);
		} else if(item.getItemId() == R.id.menu_encrypt_pin) {
    		String encWorkingKey = encrypt(fid65WorkingKey, fid65MasterKey);
			String workingKeyKcv = encrypt("0000000000000000", fid65WorkingKey);
			
    		Hashtable<String, Object> data = new Hashtable<String, Object>();
			data.put("pin", "123456");
			data.put("pan", "123456789012345678");
			data.put("encPinKey", encWorkingKey + workingKeyKcv);
			
			bbDeviceController.encryptPin(data);
    	} else if (item.getItemId() == R.id.menu_encrypt_data) {
			String encWorkingKey = encrypt(fid65WorkingKey, fid65MasterKey);
			String workingKeyKcv = encrypt("0000000000000000", fid65WorkingKey);

			Hashtable<String, Object> data = new Hashtable<String, Object>();
			data.put("data", "0123456789ABCDEF0123456789ABCDEF");
			data.put("encWorkingKey", encWorkingKey + workingKeyKcv);
			data.put("encryptionMethod", EncryptionMethod.MAC_METHOD_1);
			data.put("encryptionKeySource", EncryptionKeySource.BY_SERVER_16_BYTES_WORKING_KEY);
			data.put("encryptionPaddingMethod", EncryptionPaddingMethod.ZERO_PADDING);
			data.put("macLength", "8");
			data.put("randomNumber", "0123456789ABCDEF");
			data.put("keyUsage", EncryptionKeyUsage.TAK);
			data.put("initialVector", "0000000000000000");
			bbDeviceController.encryptDataWithSettings(data);
		} else if (item.getItemId() == R.id.menu_print_sample) {
			receipts = new ArrayList<byte[]>();
			if (Locale.getDefault().getCountry().equalsIgnoreCase("CN")) {
				receipts.add(ReceiptUtility.genReceipt2(this));
			} else {
				receipts.add(ReceiptUtility.genReceipt(this));
			}
			bbDeviceController.startPrint(receipts.size(), 60);
		} else if (item.getItemId() == R.id.menu_main) {
			isSwitchingActivity = true;
			finish();
			Intent in = new Intent(this, BBPosBTMainActivity.class);
			startActivity(in);
		} else if (item.getItemId() == R.id.menu_apdu) {
			isSwitchingActivity = true;
			finish();
			Intent in = new Intent(this, ApduActivity.class);
			startActivity(in);
		} else if(item.getItemId() == R.id.capk_activity) {
    		isSwitchingActivity = true;
    		finish();
    		Intent in = new Intent(this, CAPKActivity.class);
    		startActivity(in);
		} else if(item.getItemId() == R.id.menu_gprs_wifi) {
    		isSwitchingActivity = true;
    		finish();
    		Intent in = new Intent(this, GprsWifiActivity.class);
    		startActivity(in);
		} else if (item.getItemId() == R.id.menu_inject_session_key) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("data", "0057620000001700549907D317C1A2E8A89E489A8FD9414F3A4F120558A58CBD927343326682F5F1999EF07D767D041103F6D914EEC0A6616C178D927BBC48EE9BFE9E4A140F871BDDA0E362A59CFB95EFAA744BD4FB0F5D13");
			bbDeviceController.injectSessionKey(data);
		}
		return true;
	}

	private void handleApduResult(boolean isSuccess, String apdu, int apduLength) {
		dismissDialog();

		try {
			if (isSuccess) {

				if (isApduEncrypted) {
					String key;
					if (keyMode.equals(DATA_KEY)) {
						key = DUKPTServer.GetDataKey(ksn, "0123456789ABCDEFFEDCBA9876543210");
					} else if (keyMode.equals(DATA_KEY_VAR)) {
						key = DUKPTServer.GetDataKeyVar(ksn, "0123456789ABCDEFFEDCBA9876543210");
					} else {
						key = DUKPTServer.GetPinKeyVar(ksn, "0123456789ABCDEFFEDCBA9876543210");
					}

					if (encryptionMode.equals(CBC)) {
						apdu = TripleDES.decrypt_CBC(apdu, key);
					} else {
						apdu = TripleDES.decrypt(apdu, key);
					}

					if (apduLength == 0) {
						int padding = Integer.parseInt(apdu.substring(apdu.length() - 2));
						apdu = apdu.substring(0, apdu.length() - padding * 2);
					} else {
						apdu = apdu.substring(0, apduLength * 2);
					}
				}

				setStatus(getString(R.string.apdu_result) + apdu);

				if (apdu.startsWith("61") && apdu.length() == 4) {
					sendApdu("00C00000" + apdu.substring(2));
					return;
				}

				if (state == State.GETTING_PSE) {
					if (apdu.endsWith("9000")) {
						List<TLV> tlvList = TLVParser.parse(apdu.substring(0, apdu.length() - 4));
						TLV tlv = TLVParser.searchTLV(tlvList, "88");
						if (tlv != null && tlv.value.equals("01")) {
							state = State.READING_RECORD;
							sendApdu("00B2010C00");
							// setStatus("Reading record...");
						}
					} else if (apdu.equalsIgnoreCase("6A82")) {
						aidCounter = 0;
						state = State.READING_AID;
						sendApdu("00A40400" + toHexString((byte) (aids[aidCounter].length() / 2)) + aids[aidCounter]);
						// setStatus("Get PSE Failed.");
						// setStatus("Trying to read AID " + aids[aidCounter] +
						// "...");
					}
				} else if (state == State.READING_RECORD) {
					if (apdu.endsWith("9000")) {
						List<TLV> tlvList = TLVParser.parse(apdu.substring(0, apdu.length() - 4));
						TLV tlv = TLVParser.searchTLV(tlvList, "4F");
						if (tlv != null) {
							state = State.READING_AID;
							sendApdu("00A40400" + tlv.length + tlv.value);
							// setStatus("Reading AID...");
						}
					}
				} else if (state == State.READING_AID) {
					if (apdu.endsWith("9000")) {
						List<TLV> tlvList = TLVParser.parse(apdu.substring(0, apdu.length() - 4));
						TLV tlv = TLVParser.searchTLV(tlvList, "9F38");
						state = State.GETTING_PROCESS_OPTION;
						String command = "80A800000283";
						if (tlv != null) {
							int len = 0;
							List<TLV> challenges = TLVParser.parseWithoutValue(tlv.value);
							for (int i = 0; i < challenges.size(); ++i) {
								len += Integer.parseInt(challenges.get(i).length);
							}

							command = "80A80000" + toHexString((byte) (len + 2)) + "83" + toHexString((byte) len);
							for (int i = 0; i < len; ++i) {
								command += "00";
							}
						} else {
							command += "00";
						}

						sendApdu(command);
						// setStatus("Getting Process Option...");
					} else if (apdu.equalsIgnoreCase("6A82")) {
						++aidCounter;
						if (aidCounter < aids.length) {
							sendApdu("00A40400" + toHexString((byte) (aids[aidCounter].length() / 2)) + aids[aidCounter]);
						} else {
							setStatus(getString(R.string.no_aid_matched));
						}
						// setStatus("Read AID failed");
						// setStatus("Trying to read AID " + aids[aidCounter] +
						// "...");
					}
				} else if (state == State.GETTING_PROCESS_OPTION) {
					if (apdu.endsWith("9000")) {
						List<TLV> tlvList = TLVParser.parse(apdu.substring(0, apdu.length() - 4));
						TLV tlv = TLVParser.searchTLV(tlvList, "94");
						if (tlv != null) {
							aflCounter = 0;
							afls = new String[tlv.value.length() / 8];
							for (int i = 0; i < afls.length; ++i) {
								afls[i] = tlv.value.substring(i * 8, i * 8 + 8);
							}
							readingFileIndex = Integer.parseInt(afls[aflCounter].substring(2, 4), 16);
							total = Integer.parseInt(afls[aflCounter].substring(4, 6), 16);
							sfi = toHexString((byte) (((Integer.parseInt(afls[aflCounter].substring(0, 2), 16) & 0xF8) | 0x04)));

							state = State.READING_DATA;

							sendApdu("00B2" + toHexString((byte) readingFileIndex) + sfi + "00");

							// setStatus("Reading record...");
						} else if (apdu.startsWith("80")) {
							afls = new String[(apdu.length() - 12) / 8];
							for (int i = 0; i < afls.length; ++i) {
								afls[i] = apdu.substring(i * 8 + 8, i * 8 + 16);
							}

							aflCounter = 0;
							readingFileIndex = Integer.parseInt(afls[aflCounter].substring(2, 4), 16);
							total = Integer.parseInt(afls[aflCounter].substring(4, 6), 16);
							sfi = toHexString((byte) (((Integer.parseInt(afls[aflCounter].substring(0, 2), 16) & 0xF8) | 0x04)));

							state = State.READING_DATA;

							sendApdu("00B2" + toHexString((byte) readingFileIndex) + sfi + "00");
							// setStatus("Reading record...");
						}
					}
				} else if (state == State.READING_DATA) {
					if (apdu.endsWith("9000")) {
						List<TLV> tlvList = TLVParser.parse(apdu.substring(0, apdu.length() - 4));
						TLV tlv;
						tlv = TLVParser.searchTLV(tlvList, "5F20");
						if (tlv != null) {
							cardholderName = new String(hexToByteArray(tlv.value));
						}

						tlv = TLVParser.searchTLV(tlvList, "5F24");
						if (tlv != null) {
							expiryDate = tlv.value;
						}

						tlv = TLVParser.searchTLV(tlvList, "57");
						if (tlv != null) {
							track2 = tlv.value;
						}

						tlv = TLVParser.searchTLV(tlvList, "5A");
						if (tlv != null) {
							pan = tlv.value;
						}

						if (!cardholderName.equals("") && !expiryDate.equals("") && !track2.equals("") && !pan.equals("")) {
							setStatus("");
							setStatus("Cardholder Name: " + cardholderName);
							setStatus("Expire Date: " + expiryDate);
							setStatus("Track 2: " + track2);
							setStatus("PAN: " + pan);
							if (startTime != 0) {
								setStatus((System.currentTimeMillis() - startTime) + "ms");
								startTime = 0;
							}
							return;
						}

						++readingFileIndex;
						if (readingFileIndex <= total) {
							sendApdu("00B2" + toHexString((byte) readingFileIndex) + sfi + "00");
						} else if (aflCounter < afls.length - 1) {
							++aflCounter;
							readingFileIndex = Integer.parseInt(afls[aflCounter].substring(2, 4), 16);
							total = Integer.parseInt(afls[aflCounter].substring(4, 6), 16);
							sfi = toHexString((byte) (((Integer.parseInt(afls[aflCounter].substring(0, 2), 16) & 0xF8) | 0x04)));

							state = State.READING_DATA;

							sendApdu("00B2" + toHexString((byte) readingFileIndex) + sfi + "00");
							// setStatus("Reading record...");
						}
					}
				}
				/*
				 * ++count; if(count < apduCommands.length) {
				 * 
				 * //setStatus(getString(R.string.sending) + apduCommands[count]); //emvSwipeController.sendApdu(apduCommands[count], apduCommands[count].length() / 2);
				 * 
				 * setStatus(getString(R.string.sending) + apduCommands[count]);
				 * 
				 * String command = apduCommands[count]; while((command.length() / 2) % 8 != 0) { command = command + "00"; } String encryptedCommand = TripleDES.encrypt_CBC(command, key); emvSwipeController.sendApdu(encryptedCommand, apduCommands[count].length() / 2); }
				 */
			} else {
				setStatus(getString(R.string.apdu_failed));
			}
		} catch (Exception e) {
			setStatus(e.getMessage());
			StackTraceElement[] elements = e.getStackTrace();
			for (int i = 0; i < elements.length; ++i) {
				setStatus(elements[i].toString());
			}
		}
	}

	protected void sendApdu(String command) {
		try {
			if (isApduEncrypted) {
				String key;
				if (keyMode.equals(DATA_KEY)) {
					key = DUKPTServer.GetDataKey(ksn, "0123456789ABCDEFFEDCBA9876543210");
				} else if (keyMode.equals(DATA_KEY_VAR)) {
					key = DUKPTServer.GetDataKeyVar(ksn, "0123456789ABCDEFFEDCBA9876543210");
				} else {
					key = DUKPTServer.GetPinKeyVar(ksn, "0123456789ABCDEFFEDCBA9876543210");
				}

				String temp = command;
				if (isPKCS7) {
					int padding = 8 - (temp.length() / 2) % 8;
					for (int i = 0; i < padding; ++i) {
						temp += "0" + padding;
					}
				} else {
					while ((temp.length() / 2) % 8 != 0) {
						temp += "00";
					}
				}

				String encryptedCommand;

				if (encryptionMode.equals(CBC)) {
					encryptedCommand = TripleDES.encrypt_CBC(temp, key);
				} else {
					encryptedCommand = TripleDES.encrypt(temp, key);
				}

				Hashtable<String, Object> apduInput = new Hashtable<String, Object>();
				apduInput.put("apdu", encryptedCommand);
				if (isPKCS7) {
					bbDeviceController.sendApdu(apduInput);
				} else {
					apduInput.put("apduLength", command.length() / 2);
					bbDeviceController.sendApdu(apduInput);
				}

				setStatus(getString(R.string.sending) + command);
			} else {
				Hashtable<String, Object> apduInput = new Hashtable<String, Object>();
				apduInput.put("apdu", command);
				apduInput.put("apduLength", command.length() / 2);
				bbDeviceController.sendApdu(apduInput);
			}
		} catch (Exception e) {
			setStatus(e.getMessage());
			StackTraceElement[] elements = e.getStackTrace();
			for (int i = 0; i < elements.length; ++i) {
				setStatus(elements[i].toString());
			}
		}
	}

	class MyBBDeviceControllerListener implements BBDeviceControllerListener {

		@Override
		public void onWaitingForCard(CheckCardMode checkCardMode) {
			dismissDialog();
			switch (checkCardMode) {
			case INSERT:
				statusEditText.setText(getString(R.string.please_insert_card));
				break;
			case SWIPE:
				statusEditText.setText(getString(R.string.please_swipe_card));
				break;
			case SWIPE_OR_INSERT:
				statusEditText.setText(getString(R.string.please_swipe_insert_card));
				break;
			case TAP:
				statusEditText.setText(getString(R.string.please_tap_card));
				break;
			case SWIPE_OR_TAP:
				statusEditText.setText(getString(R.string.please_swipe_tap_card));
				break;
			case INSERT_OR_TAP:
				statusEditText.setText(getString(R.string.please_insert_tap_card));
				break;
			case SWIPE_OR_INSERT_OR_TAP:
				statusEditText.setText(getString(R.string.please_swipe_insert_tap_card));
				break;
			default:
				break;
			}
		}

		@Override
		public void onWaitingReprintOrPrintNext() {
			statusEditText.setText(statusEditText.getText() + "\n" + getString(R.string.please_press_reprint_or_print_next));
		}

		@Override
		public void onBTConnected(BluetoothDevice bluetoothDevice) {
			statusEditText.setText(getString(R.string.bluetooth_connected) + ": " + bluetoothDevice.getAddress());
		}

		@Override
		public void onBTDisconnected() {
			statusEditText.setText(getString(R.string.bluetooth_disconnected));
		}

		@Override
		public void onBTReturnScanResults(List<BluetoothDevice> foundDevices) {
			currentActivity.foundDevices = foundDevices;
			if (arrayAdapter != null) {
				arrayAdapter.clear();
				for (int i = 0; i < foundDevices.size(); ++i) {
					arrayAdapter.add(foundDevices.get(i).getName());
				}
				arrayAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void onBTScanStopped() {
			statusEditText.setText(getString(R.string.bluetooth_scan_stopped));
		}

		@Override
		public void onBTScanTimeout() {
			statusEditText.setText(getString(R.string.bluetooth_scan_timeout));
		}

		@Override
		public void onReturnCheckCardResult(CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {
			dismissDialog();
			if(checkCardResult == CheckCardResult.NO_CARD) {
				statusEditText.setText(getString(R.string.no_card_detected));
			} else if(checkCardResult == CheckCardResult.ICC) {
				statusEditText.setText(getString(R.string.icc_card_inserted));
				startEmv();
			} else if(checkCardResult == CheckCardResult.NOT_ICC) {
				statusEditText.setText(getString(R.string.card_inserted));
			} else if(checkCardResult == CheckCardResult.BAD_SWIPE) {
				statusEditText.setText(getString(R.string.bad_swipe));
			} else if(checkCardResult == CheckCardResult.MSR) {
				String formatID = decodeData.get("formatID");
				final String maskedPAN = decodeData.get("maskedPAN");
				String PAN = decodeData.get("PAN");
				final String expiryDate = decodeData.get("expiryDate");
				final String cardHolderName = decodeData.get("cardholderName");
				String ksn = decodeData.get("ksn");
				String serviceCode = decodeData.get("serviceCode");
				String track1Length = decodeData.get("track1Length");
				String track2Length = decodeData.get("track2Length");
				String track3Length = decodeData.get("track3Length");
				String encTracks = decodeData.get("encTracks");
				String encTrack1 = decodeData.get("encTrack1");
				String encTrack2 = decodeData.get("encTrack2");
				String encTrack3 = decodeData.get("encTrack3");
				String track1Status = decodeData.get("track1Status");
				String track2Status = decodeData.get("track2Status");
				String track3Status = decodeData.get("track3Status");
				String partialTrack = decodeData.get("partialTrack");
				String productType = decodeData.get("productType");
				String trackEncoding = decodeData.get("trackEncoding");
				String randomNumber = decodeData.get("randomNumber");
				String finalMessage = decodeData.get("finalMessage");
				String encWorkingKey = decodeData.get("encWorkingKey");
				String mac = decodeData.get("mac");
				String serialNumber = decodeData.get("serialNumber");
				String bID = decodeData.get("bID");
				
				String content = getString(R.string.card_swiped) + "\n";
				content += getString(R.string.format_id) + " " + formatID + "\n";
				content += getString(R.string.masked_pan) + " " + maskedPAN + "\n";
				content += getString(R.string.pan) + " " + PAN + "\n";
				content += getString(R.string.expiry_date) + " " + expiryDate + "\n";
				content += getString(R.string.cardholder_name) + " " + cardHolderName + "\n";
				content += getString(R.string.ksn) + " " + ksn + "\n";
				content += getString(R.string.service_code) + " " + serviceCode + "\n";
				content += getString(R.string.track_1_length) + " " + track1Length + "\n";
				content += getString(R.string.track_2_length) + " " + track2Length + "\n";
				content += getString(R.string.track_3_length) + " " + track3Length + "\n";
				content += getString(R.string.encrypted_tracks) + " " + encTracks + "\n";
				content += getString(R.string.encrypted_track_1) + " " + encTrack1 + "\n";
				content += getString(R.string.encrypted_track_2) + " " + encTrack2 + "\n";
				content += getString(R.string.encrypted_track_3) + " " + encTrack3 + "\n";
				content += getString(R.string.track_1_status) + " " + track1Status + "\n";
				content += getString(R.string.track_2_status) + " " + track2Status + "\n";
				content += getString(R.string.track_3_status) + " " + track3Status + "\n";
				content += getString(R.string.partial_track) + " " + partialTrack + "\n";
				content += getString(R.string.product_type) + " " + productType + "\n";
				content += getString(R.string.track_encoding) + " " + trackEncoding + "\n";
				content += getString(R.string.random_number) + " " + randomNumber + "\n";
				content += getString(R.string.final_message) + " " + finalMessage + "\n";
				content += getString(R.string.encrypted_working_key) + " " + encWorkingKey + "\n";
				content += getString(R.string.mac) + " " + mac + "\n";	
				if ((decodeData != null) && (decodeData.containsKey("data"))) {
					content += getString(R.string.data) + decodeData.get("data");
				}
				
				if ((serialNumber != null) && (!serialNumber.equals(""))) {
					content += getString(R.string.serial_number) + serialNumber + "\n";
				}
				
				if ((bID != null) && (!bID.equals(""))) {
					content += getString(R.string.b_id) + "  :" + bID + "\n";
				}
				statusEditText.setText(content);
			} else if(checkCardResult == CheckCardResult.MAG_HEAD_FAIL) {
				statusEditText.setText(getString(R.string.mag_head_fail));
			} else if(checkCardResult == CheckCardResult.USE_ICC_CARD) {
				String content = getString(R.string.use_icc_card) + "\n";
				
				if(decodeData != null) {
					String formatID = decodeData.get("formatID");
					final String maskedPAN = decodeData.get("maskedPAN");
					String PAN = decodeData.get("PAN");
					final String expiryDate = decodeData.get("expiryDate");
					final String cardHolderName = decodeData.get("cardholderName");
					String ksn = decodeData.get("ksn");
					String serviceCode = decodeData.get("serviceCode");
					String track1Length = decodeData.get("track1Length");
					String track2Length = decodeData.get("track2Length");
					String track3Length = decodeData.get("track3Length");
					String encTracks = decodeData.get("encTracks");
					String encTrack1 = decodeData.get("encTrack1");
					String encTrack2 = decodeData.get("encTrack2");
					String encTrack3 = decodeData.get("encTrack3");
					String track1Status = decodeData.get("track1Status");
					String track2Status = decodeData.get("track2Status");
					String track3Status = decodeData.get("track3Status");
					String partialTrack = decodeData.get("partialTrack");
					String productType = decodeData.get("productType");
					String trackEncoding = decodeData.get("trackEncoding");
					String randomNumber = decodeData.get("randomNumber");
					String encWorkingKey = decodeData.get("encWorkingKey");
					String mac = decodeData.get("mac");
					
					content += getString(R.string.format_id) + " " + formatID + "\n";
					content += getString(R.string.masked_pan) + " " + maskedPAN + "\n";
					content += getString(R.string.pan) + " " + PAN + "\n";
					content += getString(R.string.expiry_date) + " " + expiryDate + "\n";
					content += getString(R.string.cardholder_name) + " " + cardHolderName + "\n";
					content += getString(R.string.ksn) + " " + ksn + "\n";
					content += getString(R.string.service_code) + " " + serviceCode + "\n";
					content += getString(R.string.track_1_length) + " " + track1Length + "\n";
					content += getString(R.string.track_2_length) + " " + track2Length + "\n";
					content += getString(R.string.track_3_length) + " " + track3Length + "\n";
					content += getString(R.string.encrypted_tracks) + " " + encTracks + "\n";
					content += getString(R.string.encrypted_track_1) + " " + encTrack1 + "\n";
					content += getString(R.string.encrypted_track_2) + " " + encTrack2 + "\n";
					content += getString(R.string.encrypted_track_3) + " " + encTrack3 + "\n";
					content += getString(R.string.track_1_status) + " " + track1Status + "\n";
					content += getString(R.string.track_2_status) + " " + track2Status + "\n";
					content += getString(R.string.track_3_status) + " " + track3Status + "\n";
					content += getString(R.string.partial_track) + " " + partialTrack + "\n";
					content += getString(R.string.product_type) + " " + productType + "\n";
					content += getString(R.string.track_encoding) + " " + trackEncoding + "\n";
					content += getString(R.string.random_number) + " " + randomNumber + "\n";
					content += getString(R.string.encrypted_working_key) + " " + encWorkingKey + "\n";
					content += getString(R.string.mac) + " " + mac + "\n";
				}
				statusEditText.setText(content);
			} else if(checkCardResult == CheckCardResult.TAP_CARD_DETECTED) {
				statusEditText.setText(getString(R.string.tap_card_detected));
				startEmv();
			}
		}

		@Override
		public void onReturnCancelCheckCardResult(boolean isSuccess) {
			if (isSuccess) {
				statusEditText.setText(R.string.cancel_check_card_success);
			} else {
				statusEditText.setText(R.string.cancel_check_card_fail);
			}
		}

		@Override
		public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
			dismissDialog();
			uid = deviceInfoData.get("uid");
			String isSupportedTrack1 = deviceInfoData.get("isSupportedTrack1");
			String isSupportedTrack2 = deviceInfoData.get("isSupportedTrack2");
			String isSupportedTrack3 = deviceInfoData.get("isSupportedTrack3");
			String bootloaderVersion = deviceInfoData.get("bootloaderVersion");
			String firmwareVersion = deviceInfoData.get("firmwareVersion");
			String mainProcessorVersion = deviceInfoData.get("mainProcessorVersion");
			String coprocessorVersion = deviceInfoData.get("coprocessorVersion");
			String coprocessorBootloaderVersion = deviceInfoData.get("coprocessorBootloaderVersion");
			String isUsbConnected = deviceInfoData.get("isUsbConnected");
			String isCharging = deviceInfoData.get("isCharging");
			String batteryLevel = deviceInfoData.get("batteryLevel");
			String batteryPercentage = deviceInfoData.get("batteryPercentage");
			String hardwareVersion = deviceInfoData.get("hardwareVersion");
			String productId = deviceInfoData.get("productId");
			String pinKsn = deviceInfoData.get("pinKsn");
			String emvKsn = deviceInfoData.get("emvKsn");
			String trackKsn = deviceInfoData.get("trackKsn");
			String terminalSettingVersion = deviceInfoData.get("terminalSettingVersion");
			String deviceSettingVersion = deviceInfoData.get("deviceSettingVersion");
			String formatID = deviceInfoData.get("formatID");
			String vendorID = deviceInfoData.get("vendorID");
			String csn = deviceInfoData.get("csn");
			String uid = deviceInfoData.get("uid");
			String serialNumber = deviceInfoData.get("serialNumber");
			String modelName = deviceInfoData.get("modelName");
			String macKsn = deviceInfoData.get("macKsn");
			String nfcKsn = deviceInfoData.get("nfcKsn");
			String messageKsn = deviceInfoData.get("messageKsn");
			String bID = deviceInfoData.get("bID");
			
			String vendorIDAscii = "";
			if ((vendorID != null) && (!vendorID.equals(""))) {
				if (!vendorID.substring(0, 2).equalsIgnoreCase("00")) {
					vendorIDAscii = hexString2AsciiString(vendorID);
				}
			}

			String content = "";
			content += getString(R.string.format_id) + formatID + "\n";
			content += getString(R.string.vendor_id_hex) + vendorID + "\n";
			content += getString(R.string.vendor_id_ascii) + vendorIDAscii + "\n";
			content += getString(R.string.bootloader_version) + bootloaderVersion + "\n";
			content += getString(R.string.firmware_version) + firmwareVersion + "\n";
			content += getString(R.string.main_processor_version) + mainProcessorVersion + "\n";
			content += getString(R.string.coprocessor_version) + coprocessorVersion + "\n";
			content += getString(R.string.coprocessor_bootloader_version) + coprocessorBootloaderVersion + "\n";
			content += getString(R.string.usb) + isUsbConnected + "\n";
			content += getString(R.string.charge) + isCharging + "\n";
			content += getString(R.string.battery_level) + batteryLevel + "\n";
			content += getString(R.string.battery_percentage) + batteryPercentage + "\n";
			content += getString(R.string.hardware_version) + hardwareVersion + "\n";
			content += getString(R.string.track_1_supported) + isSupportedTrack1 + "\n";
			content += getString(R.string.track_2_supported) + isSupportedTrack2 + "\n";
			content += getString(R.string.track_3_supported) + isSupportedTrack3 + "\n";
			content += getString(R.string.product_id) + productId + "\n";
			content += getString(R.string.pin_ksn) + pinKsn + "\n";
			content += getString(R.string.emv_ksn) + emvKsn + "\n";
			content += getString(R.string.track_ksn) + trackKsn + "\n";
			content += getString(R.string.terminal_setting_version) + terminalSettingVersion + "\n";
			content += getString(R.string.device_setting_version) + deviceSettingVersion + "\n";
			content += getString(R.string.csn) + csn + "\n";
			content += getString(R.string.uid) + uid + "\n";
			content += getString(R.string.serial_number) + serialNumber + "\n";
			if ((modelName != null) && (!modelName.equals(""))) {
				content += getString(R.string.model_name) + modelName + "\n";
			}
			
			if ((macKsn != null) && (!macKsn.equals(""))) {
				content += getString(R.string.mac_ksn) + macKsn + "\n";
			}
			if ((nfcKsn != null) && (!nfcKsn.equals(""))) {
				content += getString(R.string.nfc_ksn) + nfcKsn + "\n";
			}
			if ((messageKsn != null) && (!messageKsn.equals(""))) {
				content += getString(R.string.message_ksn) + messageKsn + "\n";
			}
			if ((bID != null) && (!bID.equals(""))) {
				content += getString(R.string.b_id) + "  :" + bID + "\n";
			}

			statusEditText.setText(content);

			if (formatID.equals("46")) {
				fidSpinner.setSelection(2);
			} else if (formatID.equals("61")) {
				fidSpinner.setSelection(6);
			} else if (formatID.equals("65")) {
				fidSpinner.setSelection(8);
			} else {
				fidSpinner.setSelection(5);
			}
		}

		@Override
		public void onReturnTransactionResult(TransactionResult transactionResult) {
			dismissDialog();
			dialog = new Dialog(currentActivity);
			dialog.setContentView(R.layout.alert_dialog);
			dialog.setTitle(R.string.transaction_result);
			TextView messageTextView = (TextView) dialog.findViewById(R.id.messageTextView);

			String message = "";
			if (transactionResult == TransactionResult.APPROVED) {
				message = getString(R.string.transaction_approved) + "\n" + getString(R.string.amount) + ": $" + amount + "\n";
				if (!cashbackAmount.equals("")) {
					message += getString(R.string.cashback_amount) + ": $" + cashbackAmount;
				}
			} else if (transactionResult == TransactionResult.TERMINATED) {
				message += getString(R.string.transaction_terminated);
			} else if (transactionResult == TransactionResult.DECLINED) {
				message += getString(R.string.transaction_declined);
			} else if (transactionResult == TransactionResult.CANCEL_OR_TIMEOUT) {
				message += getString(R.string.transaction_cancel);
			} else if (transactionResult == TransactionResult.CAPK_FAIL) {
				message += getString(R.string.transaction_capk_fail);
			} else if (transactionResult == TransactionResult.NOT_ICC) {
				message += getString(R.string.transaction_not_icc);
			} else if (transactionResult == TransactionResult.SELECT_APP_FAIL) {
				message += getString(R.string.transaction_app_fail);
			} else if (transactionResult == TransactionResult.DEVICE_ERROR) {
				message += getString(R.string.transaction_device_error);
			} else if (transactionResult == TransactionResult.APPLICATION_BLOCKED) {
				message += getString(R.string.transaction_application_blocked);
			} else if (transactionResult == TransactionResult.ICC_CARD_REMOVED) {
				messageTextView.setText(getString(R.string.transaction_icc_card_removed));
			} else if (transactionResult == TransactionResult.CARD_BLOCKED) {
				message += getString(R.string.transaction_card_blocked);
			} else if (transactionResult == TransactionResult.CARD_NOT_SUPPORTED) {
				message += getString(R.string.transaction_card_not_supported);
			} else if (transactionResult == TransactionResult.CONDITION_NOT_SATISFIED) {
				message += getString(R.string.transaction_condition_not_satisfied);
			} else if (transactionResult == TransactionResult.INVALID_ICC_DATA) {
				message += getString(R.string.transaction_invalid_icc_data);
			} else if (transactionResult == TransactionResult.MISSING_MANDATORY_DATA) {
				message += getString(R.string.transaction_missing_mandatory_data);
			} else if (transactionResult == TransactionResult.NO_EMV_APPS) {
				message += getString(R.string.transaction_no_emv_apps);
			}

			messageTextView.setText(message);

			amount = "";
			cashbackAmount = "";
			amountEditText.setText("");

			dialog.findViewById(R.id.confirmButton).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismissDialog();
				}
			});

			dialog.show();
		}

		@Override
		public void onReturnBatchData(String tlv) {
			dismissDialog();
			String content = getString(R.string.batch_data) + "\n";
			Hashtable<String, String> decodeData = BBDeviceController.decodeTlv(tlv);
			Object[] keys = decodeData.keySet().toArray();
			Arrays.sort(keys);
			for (Object key : keys) {
				String value = decodeData.get(key);
				content += key + ": " + value + "\n";
			}
			statusEditText.setText(content);
		}

		@Override
		public void onReturnReversalData(String tlv) {
			dismissDialog();
			String content = getString(R.string.reversal_data);
			content += tlv;
			statusEditText.setText(content);
		}

		@Override
		public void onReturnAmountConfirmResult(boolean isSuccess) {
			if (isSuccess) {
				statusEditText.setText(getString(R.string.amount_confirmed));
			} else {
				statusEditText.setText(getString(R.string.amount_canceled));
			}
		}

		@Override
		public void onReturnPinEntryResult(PinEntryResult pinEntryResult, Hashtable<String, String> data) {
			if (pinEntryResult == PinEntryResult.ENTERED) {
				String content = getString(R.string.pin_entered);
				if (data.containsKey("epb")) {
					content += "\n" + getString(R.string.epb) + data.get("epb");
				}
				if (data.containsKey("ksn")) {
					content += "\n" + getString(R.string.ksn) + data.get("ksn");
				}
				if (data.containsKey("randomNumber")) {
					content += "\n" + getString(R.string.random_number) + data.get("randomNumber");
				}
				if (data.containsKey("encWorkingKey")) {
					content += "\n" + getString(R.string.encrypted_working_key) + data.get("encWorkingKey");
				}

				statusEditText.setText(content);
			} else if (pinEntryResult == PinEntryResult.BYPASS) {
				statusEditText.setText(getString(R.string.pin_bypassed));
			} else if (pinEntryResult == PinEntryResult.CANCEL) {
				statusEditText.setText(getString(R.string.pin_canceled));
			} else if (pinEntryResult == PinEntryResult.TIMEOUT) {
				statusEditText.setText(getString(R.string.pin_timeout));
			}
		}

		@Override
		public void onReturnPrintResult(PrintResult printResult) {
			if (printResult == PrintResult.SUCCESS) {
				statusEditText.setText(getString(R.string.printer_command_success));
			} else if (printResult == PrintResult.NO_PAPER) {
				statusEditText.setText(getString(R.string.no_paper));
			} else if (printResult == PrintResult.WRONG_CMD) {
				statusEditText.setText(getString(R.string.wrong_printer_cmd));
			} else if (printResult == PrintResult.OVERHEAT) {
				statusEditText.setText(getString(R.string.printer_overheat));
			} else if (printResult == PrintResult.PRINTER_ERROR) {
				statusEditText.setText(getString(R.string.printer_error));
			}
		}

		@Override
		public void onReturnAmount(Hashtable<String, String> data) {
			String amount = data.get("amount");
			String cashbackAmount = data.get("cashbackAmount");
			String currencyCode = data.get("currencyCode");

			String text = "";
			text += getString(R.string.amount_with_colon) + amount + "\n";
			text += getString(R.string.cashback_with_colon) + cashbackAmount + "\n";
			text += getString(R.string.currency_with_colon) + currencyCode + "\n";

			statusEditText.setText(text);
		}

		@Override
		public void onReturnUpdateTerminalSettingResult(TerminalSettingStatus terminalSettingStatus) {
			dismissDialog();
			if (terminalSettingStatus == TerminalSettingStatus.SUCCESS) {
				statusEditText.setText(getString(R.string.update_terminal_setting_success));
			} else if (terminalSettingStatus == TerminalSettingStatus.TAG_NOT_FOUND) {
				statusEditText.setText(getString(R.string.update_terminal_setting_tag_not_found));
			} else if (terminalSettingStatus == TerminalSettingStatus.LENGTH_INCORRECT) {
				statusEditText.setText(getString(R.string.update_terminal_setting_length_incorrect));
			} else if (terminalSettingStatus == TerminalSettingStatus.TLV_INCORRECT) {
				statusEditText.setText(getString(R.string.update_terminal_setting_tlv_incorrect));
			} else if (terminalSettingStatus == TerminalSettingStatus.BOOTLOADER_NOT_SUPPORT) {
				statusEditText.setText(getString(R.string.update_terminal_setting_bootloader_not_support));
			} else if (terminalSettingStatus == TerminalSettingStatus.TAG_NOT_ALLOWED_TO_ACCESS) {
				statusEditText.setText(getString(R.string.update_terminal_setting_tag_not_allowed_to_change));
			} else if (terminalSettingStatus == TerminalSettingStatus.USER_DEFINED_DATA_NOT_ENALBLED) {
				statusEditText.setText(getString(R.string.update_terminal_setting_user_defined_data_not_allowed_to_change));
			} else if (terminalSettingStatus == TerminalSettingStatus.TAG_NOT_WRITTEN_CORRECTLY) {
				statusEditText.setText(getString(R.string.update_terminal_setting_tag_not_written_correctly));
			}
		}

		@Override
		public void onReturnReadTerminalSettingResult(TerminalSettingStatus terminalSettingStatus, String value) {
			dismissDialog();
			if (terminalSettingStatus == TerminalSettingStatus.SUCCESS) {
				statusEditText.setText(getString(R.string.read_terminal_setting_success) + "\n" + getString(R.string.value) + " " + value);
			} else if (terminalSettingStatus == TerminalSettingStatus.TAG_NOT_FOUND) {
				statusEditText.setText(getString(R.string.read_terminal_setting_tag_not_found));
			} else if (terminalSettingStatus == TerminalSettingStatus.LENGTH_INCORRECT) {
				statusEditText.setText(getString(R.string.read_terminal_setting_length_incorrect));
			} else if (terminalSettingStatus == TerminalSettingStatus.TLV_INCORRECT) {
				statusEditText.setText(getString(R.string.read_terminal_setting_tlv_incorrect));
			} else if (terminalSettingStatus == TerminalSettingStatus.BOOTLOADER_NOT_SUPPORT) {
				statusEditText.setText(getString(R.string.read_terminal_setting_bootloader_not_support));
			} else if (terminalSettingStatus == TerminalSettingStatus.TAG_NOT_ALLOWED_TO_ACCESS) {
				statusEditText.setText(getString(R.string.read_terminal_setting_tag_not_allowed_to_access));
			} else if (terminalSettingStatus == TerminalSettingStatus.USER_DEFINED_DATA_NOT_ENALBLED) {
				statusEditText.setText(getString(R.string.read_terminal_setting_user_defined_data_not_allowed_to_change));
			} else if (terminalSettingStatus == TerminalSettingStatus.TAG_NOT_WRITTEN_CORRECTLY) {
				statusEditText.setText(getString(R.string.read_terminal_setting_tag_not_written_correctly));
			}
		}

		@Override
		public void onReturnEnableInputAmountResult(boolean isSuccess) {
			if (isSuccess) {
				statusEditText.setText(getString(R.string.enable_input_amount_success));
			} else {
				statusEditText.setText(getString(R.string.enable_input_amount_fail));
			}
		}

		@Override
		public void onReturnDisableInputAmountResult(boolean isSuccess) {
			if (isSuccess) {
				statusEditText.setText(getString(R.string.disable_input_amount_success));
			} else {
				statusEditText.setText(getString(R.string.disable_input_amount_fail));
			}
		}

		@Override
		public void onReturnPhoneNumber(PhoneEntryResult phoneEntryResult, String phoneNumber) {
			if (phoneEntryResult == PhoneEntryResult.ENTERED) {
				statusEditText.setText(getString(R.string.phone_number) + " " + phoneNumber);
			} else if (phoneEntryResult == PhoneEntryResult.TIMEOUT) {
				statusEditText.setText(getString(R.string.timeout));
			} else if (phoneEntryResult == PhoneEntryResult.CANCEL) {
				statusEditText.setText(getString(R.string.canceled));
			} else if (phoneEntryResult == PhoneEntryResult.WRONG_LENGTH) {
				statusEditText.setText(getString(R.string.wrong_length));
			} else if (phoneEntryResult == PhoneEntryResult.BYPASS) {
				statusEditText.setText(getString(R.string.bypass));
			}
		}

		@Override
		public void onReturnEmvCardDataResult(boolean isSuccess, String tlv) {
			if (isSuccess) {
				statusEditText.setText(getString(R.string.emv_card_data_result) + tlv);
			} else {
				statusEditText.setText(getString(R.string.emv_card_data_failed));
			}
		}

		@Override
		public void onReturnEmvCardNumber(boolean isSuccess, String cardNumber) {
			statusEditText.setText(getString(R.string.pan) + cardNumber);
		}

		@Override
		public void onReturnEncryptPinResult(boolean isSuccess, Hashtable<String, String> data) {
			String ksn = data.get("ksn");
			String epb = data.get("epb");
			String randomNumber = data.get("randomNumber");
			String encWorkingKey = data.get("encWorkingKey");
			String errorMessage = data.get("errorMessage");
			String content = getString(R.string.ksn) + ksn + "\n";
			content += getString(R.string.epb) + epb + "\n";
			content += getString(R.string.random_number) + randomNumber + "\n";
			content += getString(R.string.encrypted_working_key) + encWorkingKey + "\n";
			content += getString(R.string.error_message) + errorMessage;
			statusEditText.setText(content);
		}

		@Override
		public void onReturnEncryptDataResult(boolean isSuccess, Hashtable<String, String> data) {
			if (isSuccess) {
				String content = "";
				if (data.containsKey("ksn")) {
					content += getString(R.string.ksn) + data.get("ksn") + "\n";
				}
				if (data.containsKey("randomNumber")) {
					content += getString(R.string.random_number) + data.get("randomNumber") + "\n";
				}
				if (data.containsKey("encData")) {
					content += getString(R.string.encrypted_data) + data.get("encData") + "\n";
				}
				if (data.containsKey("mac")) {
					content += getString(R.string.mac) + data.get("mac") + "\n";
				}
				statusEditText.setText(content);
			} else {
				statusEditText.setText(getString(R.string.encrypt_data_failed));
			}
		}
		
		@Override
		public void onReturnInjectSessionKeyResult(boolean isSuccess, Hashtable<String, String> data) {
			String content;
			if (isSuccess) {
				content = getString(R.string.inject_session_key_success);
				if (data.size() == 0) {
					injectNextSessionKey();
				}
			} else {
				content = getString(R.string.inject_session_key_failed);
				content += "\n" + getString(R.string.error_message) + data.get("errorMessage");
			}
			setStatus(content);
		}

		@Override
		public void onReturnApduResult(boolean isSuccess, Hashtable<String, Object> data) {
			try {
				String apdu = "";
				int apduLength = 0;
				
				if ((data != null) && (data.containsKey("apduLength")) && (data.get("apduLength") instanceof String)) {
					apduLength = Integer.parseInt((String)data.get("apduLength"));
				} else if ((data != null) && (data.containsKey("apduLength")) && (data.get("apduLength") instanceof Integer)) {
					apduLength = (Integer)data.get("apduLength");
				}
				
				if ((data != null) && (data.containsKey("apdu"))) {
					apdu = (String)data.get("apdu");
					handleApduResult(isSuccess, apdu, apduLength);
				}
			} catch (Exception e) {
				
			}
		}

		@Override
		public void onReturnPowerOffIccResult(boolean isSuccess) {
			dismissDialog();
			if (isSuccess) {
				setStatus(getString(R.string.power_off_icc_success));
			} else {
				setStatus(getString(R.string.power_off_icc_failed));
			}
		}

		@Override
		public void onReturnPowerOnIccResult(boolean isSuccess, String ksn, String atr, int atrLength) {
			dismissDialog();
			if (isSuccess) {
				BaseActivity.ksn = ksn;

				setStatus(getString(R.string.power_on_icc_success));
				setStatus(getString(R.string.ksn) + ksn);
				setStatus(getString(R.string.atr) + atr);
				setStatus(getString(R.string.atr_length) + atrLength);
			} else {
				setStatus(getString(R.string.power_on_icc_failed));
			}
		}

		@Override
		public void onRequestSelectApplication(ArrayList<String> appList) {
			dismissDialog();

			dialog = new Dialog(currentActivity);
			dialog.setContentView(R.layout.application_dialog);
			dialog.setTitle(R.string.please_select_app);

			String[] appNameList = new String[appList.size()];
			for (int i = 0; i < appNameList.length; ++i) {
				appNameList[i] = appList.get(i);
			}

			appListView = (ListView) dialog.findViewById(R.id.appList);
			appListView.setAdapter(new ArrayAdapter<String>(currentActivity, android.R.layout.simple_list_item_1, appNameList));
			appListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					bbDeviceController.selectApplication(position);
					dismissDialog();
				}

			});

			dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					bbDeviceController.cancelSelectApplication();
					dismissDialog();
				}
			});
			dialog.show();
		}

		@Override
		public void onRequestSetAmount() {
			promptForAmount();
		}

		@Override
		public void onRequestPinEntry(PinEntrySource pinEntrySource) {
			dismissDialog();
			if (pinEntrySource == PinEntrySource.KEYPAD) {
				statusEditText.setText(getString(R.string.enter_pin_on_keypad));
			} else {
				dismissDialog();

				dialog = new Dialog(currentActivity);
				dialog.setContentView(R.layout.pin_dialog);
				dialog.setTitle(getString(R.string.enter_pin));

				dialog.findViewById(R.id.confirmButton).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								String pin = ((EditText) dialog.findViewById(R.id.pinEditText)).getText().toString();
								bbDeviceController.sendPinEntryResult(pin);
								dismissDialog();
							}
						});

				dialog.findViewById(R.id.bypassButton).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								bbDeviceController.bypassPinEntry();
								dismissDialog();
							}
						});

				dialog.findViewById(R.id.cancelButton).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								isPinCanceled = true;
								bbDeviceController.cancelPinEntry();
								dismissDialog();
							}
						});

				dialog.show();
			}
		}

		@Override
		public void onRequestOnlineProcess(String tlv) {
			String content = getString(R.string.request_data_to_server) + "\n";
			Hashtable<String, String> decodeData = BBDeviceController.decodeTlv(tlv);
			Object[] keys = decodeData.keySet().toArray();
			Arrays.sort(keys);
			for (Object key : keys) {
				String value = decodeData.get(key);
				content += key + ": " + value + "\n";
			}
			statusEditText.setText(content);

			dismissDialog();
			dialog = new Dialog(currentActivity);
			dialog.setContentView(R.layout.alert_dialog);
			dialog.setTitle(R.string.request_data_to_server);

			if (isPinCanceled) {
				((TextView) dialog.findViewById(R.id.messageTextView)).setText(R.string.replied_failed);
			} else {
				((TextView) dialog.findViewById(R.id.messageTextView)).setText(R.string.replied_success);
			}

			dialog.findViewById(R.id.confirmButton).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (isPinCanceled) {
								bbDeviceController.sendOnlineProcessResult(null);
							} else {
								bbDeviceController.sendOnlineProcessResult("8A023030");
							}
							dismissDialog();
						}
					});

			dialog.show();
		}

		@Override
		public void onRequestTerminalTime() {
			dismissDialog();
			String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
			bbDeviceController.sendTerminalTime(terminalTime);
			statusEditText.setText(getString(R.string.request_terminal_time) + " " + terminalTime);
		}

		@Override
		public void onRequestDisplayText(DisplayText displayText) {
			dismissDialog();

			String msg = "";
			if (displayText == DisplayText.APPROVED) {
				msg = getString(R.string.approved);
			} else if (displayText == DisplayText.CALL_YOUR_BANK) {
				msg = getString(R.string.call_your_bank);
			} else if (displayText == DisplayText.DECLINED) {
				msg = getString(R.string.decline);
			} else if (displayText == DisplayText.ENTER_PIN) {
				msg = getString(R.string.enter_pin);
			} else if (displayText == DisplayText.INCORRECT_PIN) {
				msg = getString(R.string.incorrect_pin);
			} else if (displayText == DisplayText.INSERT_CARD) {
				msg = getString(R.string.insert_card);
			} else if (displayText == DisplayText.NOT_ACCEPTED) {
				msg = getString(R.string.not_accepted);
			} else if (displayText == DisplayText.PIN_OK) {
				msg = getString(R.string.pin_ok);
			} else if (displayText == DisplayText.PLEASE_WAIT) {
				msg = getString(R.string.wait);
			} else if (displayText == DisplayText.REMOVE_CARD) {
				msg = getString(R.string.remove_card);
			} else if (displayText == DisplayText.USE_MAG_STRIPE) {
				msg = getString(R.string.use_mag_stripe);
			} else if (displayText == DisplayText.TRY_AGAIN) {
				msg = getString(R.string.try_again);
			} else if (displayText == DisplayText.REFER_TO_YOUR_PAYMENT_DEVICE) {
				msg = getString(R.string.refer_payment_device);
			} else if (displayText == DisplayText.TRANSACTION_TERMINATED) {
				msg = getString(R.string.transaction_terminated);
			} else if (displayText == DisplayText.PROCESSING) {
				msg = getString(R.string.processing);
			} else if (displayText == DisplayText.LAST_PIN_TRY) {
				msg = getString(R.string.last_pin_try);
			} else if (displayText == DisplayText.SELECT_ACCOUNT) {
				msg = getString(R.string.select_account);
			} else if (displayText == DisplayText.ENTER_AMOUNT) {
				msg = getString(R.string.enter_amount);
			} else if (displayText == DisplayText.PRESENT_CARD) {
				msg = getString(R.string.insert_or_tap_card);
			} else if (displayText == DisplayText.APPROVED_PLEASE_SIGN) {
				msg = getString(R.string.approved_please_sign);
			} else if (displayText == DisplayText.PRESENT_CARD_AGAIN) {
				msg = getString(R.string.tap_card_again);
			} else if (displayText == DisplayText.AUTHORISING) {
				msg = getString(R.string.authorising);
			} else if (displayText == DisplayText.INSERT_SWIPE_OR_TRY_ANOTHER_CARD) {
				msg = getString(R.string.insert_or_swipe_card_or_tap_another_card);
			} else if (displayText == DisplayText.INSERT_OR_SWIPE_CARD) {
				msg = getString(R.string.insert_or_swipe_card);
			} else if (displayText == DisplayText.MULTIPLE_CARDS_DETECTED) {
				msg = getString(R.string.multiple_cards_detected);
			} else if (displayText == DisplayText.TIMEOUT) {
				msg = getString(R.string.timeout);
			} else if (displayText == DisplayText.APPLICATION_EXPIRED) {
				msg = getString(R.string.application_expired);
			} else if (displayText == DisplayText.FINAL_CONFIRM) {
				msg = getString(R.string.final_confirm);
			} else if (displayText == DisplayText.SHOW_THANK_YOU) {
				msg = getString(R.string.show_thank_you);
			} else if (displayText == DisplayText.PIN_TRY_LIMIT_EXCEEDED) {
				msg = getString(R.string.pin_try_limit_exceeded);
			}

			statusEditText.setText(msg);
		}

		@Override
		public void onRequestClearDisplay() {
			dismissDialog();
			statusEditText.setText("");
		}

		@Override
		public void onRequestFinalConfirm() {
			dismissDialog();
			if (!isPinCanceled) {
				dialog = new Dialog(currentActivity);
				dialog.setContentView(R.layout.bbpos_confirm_dialog);
				dialog.setTitle(getString(R.string.confirm_amount));

				String message = getString(R.string.amount) + ": $" + amount;
				if (!cashbackAmount.equals("")) {
					message += "\n" + getString(R.string.cashback_amount) + ": $" + cashbackAmount;
				}

				((TextView) dialog.findViewById(R.id.messageTextView)).setText(message);

				dialog.findViewById(R.id.confirmButton).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								bbDeviceController.sendFinalConfirmResult(true);
								dialog.dismiss();
							}
						});

				dialog.findViewById(R.id.cancelButton).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								bbDeviceController.sendFinalConfirmResult(false);
								dialog.dismiss();
							}
						});

				dialog.show();
			} else {
				bbDeviceController.sendFinalConfirmResult(false);
			}
		}

		@Override
		public void onRequestPrintData(int index, boolean isReprint) {
			bbDeviceController.sendPrintData(receipts.get(index));
			if (isReprint) {
				statusEditText.setText(getString(R.string.request_reprint_data) + index);
			} else {
				statusEditText.setText(getString(R.string.request_printer_data) + index);
			}
		}

		@Override
		public void onPrintDataCancelled() {
			statusEditText.setText(getString(R.string.printer_operation_cancelled));
		}

		@Override
		public void onPrintDataEnd() {
			statusEditText.setText(getString(R.string.printer_operation_end));
		}
		
		@Override
		public void onBatteryLow(BatteryStatus batteryStatus) {
			if (batteryStatus == BatteryStatus.LOW) {
				statusEditText.setText(getString(R.string.battery_low));
			} else if (batteryStatus == BatteryStatus.CRITICALLY_LOW) {
				statusEditText.setText(getString(R.string.battery_critically_low));
			}
		}

		@Override
		public void onAudioDevicePlugged() {
			statusEditText.setText(getString(R.string.device_plugged));
		}

		@Override
		public void onAudioDeviceUnplugged() {
			statusEditText.setText(getString(R.string.device_unplugged));
		}

		@Override
		public void onError(Error errorState, String errorMessage) {
			dismissDialog();
			if(progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			amountEditText.setText("");

			String content = "";
			if (errorState == Error.CMD_NOT_AVAILABLE) {
				content = getString(R.string.command_not_available);
			} else if (errorState == Error.TIMEOUT) {
				content = getString(R.string.device_no_response);
			} else if (errorState == Error.UNKNOWN) {
				content = getString(R.string.unknown_error);
			} else if (errorState == Error.DEVICE_BUSY) {
				content = getString(R.string.device_busy);
			} else if (errorState == Error.INPUT_OUT_OF_RANGE) {
				content = getString(R.string.out_of_range);
			} else if (errorState == Error.INPUT_INVALID_FORMAT) {
				content = getString(R.string.invalid_format);
				Toast.makeText(currentActivity, getString(R.string.invalid_format), Toast.LENGTH_LONG).show();
			} else if (errorState == Error.INPUT_INVALID) {
				content = getString(R.string.input_invalid);
				Toast.makeText(currentActivity, getString(R.string.input_invalid), Toast.LENGTH_LONG).show();
			} else if (errorState == Error.CASHBACK_NOT_SUPPORTED) {
				content = getString(R.string.cashback_not_supported);
				Toast.makeText(currentActivity, getString(R.string.cashback_not_supported), Toast.LENGTH_LONG).show();
			} else if (errorState == Error.CRC_ERROR) {
				content = getString(R.string.crc_error);
			} else if (errorState == Error.COMM_ERROR) {
				content = getString(R.string.comm_error);
			} else if (errorState == Error.FAIL_TO_START_BT) {
				content = getString(R.string.fail_to_start_bluetooth);
			} else if (errorState == Error.FAIL_TO_START_AUDIO) {
				content = getString(R.string.fail_to_start_audio);
			} else if (errorState == Error.INVALID_FUNCTION_IN_CURRENT_CONNECTION_MODE) {
				content = getString(R.string.invalid_function);
			} else if (errorState == Error.COMM_LINK_UNINITIALIZED) {
				content = getString(R.string.comm_link_uninitialized);
			} else if (errorState == Error.BTV4_NOT_SUPPORTED) {
				content = getString(R.string.bluetooth_4_not_supported);
				Toast.makeText(currentActivity, getString(R.string.bluetooth_4_not_supported), Toast.LENGTH_LONG).show();
			} else if (errorState == Error.CHANNEL_BUFFER_FULL) {
				content = getString(R.string.channel_buffer_full);
			} else if (errorState == Error.BLUETOOTH_PERMISSION_DENIED) {
				content = getString(R.string.bluetooth_permission_denied);
			} else if (errorState == Error.VOLUME_WARNING_NOT_ACCEPTED) {
				content = getString(R.string.volume_warning_not_accepted);
			} else if (errorState == Error.FAIL_TO_START_SERIAL) {
				content = getString(R.string.fail_to_start_serial);
			} else if (errorState == Error.USB_DEVICE_NOT_FOUND) {
				content = getString(R.string.usb_device_not_found);
			} else if (errorState == Error.USB_DEVICE_PERMISSION_DENIED) {
				content = getString(R.string.usb_device_permission_denied);
			} else if (errorState == Error.USB_NOT_SUPPORTED) {
				content = getString(R.string.usb_not_supported);
			}

			if (errorMessage != null && !errorMessage.equals("")) {
				content += "\n" + getString(R.string.error_message) + errorMessage;
			}

			statusEditText.setText(content);
		}

		@Override
		public void onReturnCAPKList(List<CAPK> capkList) {
			String content = getString(R.string.capk);
			for (int i = 0; i < capkList.size(); ++i) {
				CAPK capk = capkList.get(i);
				content += "\n" + i + ": ";
				content += "\n" + getString(R.string.location) + capk.location;
				content += "\n" + getString(R.string.rid) + capk.rid;
				content += "\n" + getString(R.string.index) + capk.index;
				content += "\n";
			}
			setStatus(content);
		}
		
		@Override
		public void onReturnCAPKDetail(CAPK capk) {
			String content = getString(R.string.capk);
			if (capk != null) {
				content += "\n" + getString(R.string.location) + capk.location;
				content += "\n" + getString(R.string.rid) + capk.rid;
				content += "\n" + getString(R.string.index) + capk.index;
				content += "\n" + getString(R.string.exponent) + capk.exponent;
				content += "\n" + getString(R.string.modulus) + capk.modulus;
				content += "\n" + getString(R.string.checksum) + capk.checksum;
				content += "\n" + getString(R.string.size) + capk.size;
				content += "\n";
			} else {
				content += "\nnull \n";
			}
			setStatus(content);
		}

		@Override
		public void onReturnCAPKLocation(String location) {
			setStatus(getString(R.string.location) + location);
		}
		
		@Override
		public void onReturnUpdateCAPKResult(boolean isSuccess) {
			if (isSuccess) {
				setStatus(getString(R.string.update_capk_success));
			} else {
				setStatus(getString(R.string.update_capk_fail));
			}
		}

		@Override
		public void onReturnEmvReport(String tlv) {
			String content = getString(R.string.emv_report) + "\n";

			Hashtable<String, String> decodeData = BBDeviceController.decodeTlv(tlv);
			Object[] keys = decodeData.keySet().toArray();
			Arrays.sort(keys);
			for (Object key : keys) {
				if (((String) key).matches(".*[a-z].*") && decodeData.containsKey(((String) key).toUpperCase(Locale.ENGLISH))) {
					continue;
				}
				String value = decodeData.get(key);
				content += key + ": " + value + "\n";
				
				if (((String) key).toUpperCase(Locale.ENGLISH).equalsIgnoreCase(TagList.EMV_REPORT_TEMPLATE)) {
					Hashtable<String, String> innerDecodeData = BBDeviceController.decodeTlv(value);
					Object[] innerKeys = innerDecodeData.keySet().toArray();
					Arrays.sort(innerKeys);
					for (Object innerKey : innerKeys) {
						if (((String) innerKey).matches(".*[a-z].*") && innerDecodeData.containsKey(((String) innerKey).toUpperCase(Locale.ENGLISH))) {
							continue;
						}
						String innerValue = innerDecodeData.get(innerKey);
						content += "\n" + innerKey + ": " + innerValue;						
					}
				}
			}

			setStatus(content);
		}

		@Override
		public void onReturnEmvReportList(Hashtable<String, String> data) {
			String content = getString(R.string.emv_report_list) + "\n";
			Object[] keys = data.keySet().toArray();
			Arrays.sort(keys);
			for (Object key : keys) {
				String value = data.get(key);
				content += key + ": " + value + "\n";
			}

			setStatus(content);
		}
		
		@Override
		public void onSessionInitialized() {
			setStatus(getString(R.string.session_initialized));
		}

		@Override
		public void onSessionError(SessionError sessionError, String errorMessage) {
			if (sessionError == SessionError.FIRMWARE_NOT_SUPPORTED) {
				setStatus(getString(R.string.session_error_firmware_not_supported));
			} else if (sessionError == SessionError.INVALID_SESSION) {
				setStatus(getString(R.string.session_error_invalid_session));
			} else if (sessionError == SessionError.INVALID_VENDOR_TOKEN) {
				setStatus(getString(R.string.session_error_invalid_vendor_token));
			} else if (sessionError == SessionError.SESSION_NOT_INITIALIZED) {
				setStatus(getString(R.string.session_error_session_not_initialized));
			}
			setStatus(getString(R.string.error_message) + errorMessage);
		}
		
		@Override
		public void onReturnReadGprsSettingsResult(boolean isSuccess, Hashtable<String, Object> data) {
			if (isSuccess) {
				String text = getString(R.string.read_gprs_setting_success);
				text += "\n" + getString(R.string.operator) + (String)data.get("operator");
				text += "\n" + getString(R.string.apn) + (String)data.get("apn");
				text += "\n" + getString(R.string.username) + (String)data.get("username");
				text += "\n" + getString(R.string.password) + (String)data.get("password");
				setStatus(text);
			} else {
				String text = getString(R.string.read_gprs_setting_fail);
				TerminalSettingStatus terminalSettingStatus = (TerminalSettingStatus)data.get("gprs");
				switch (terminalSettingStatus) {
				case SUCCESS:
					setStatus(getString(R.string.read_terminal_setting_success));
					break;
				case LENGTH_INCORRECT:
					setStatus(getString(R.string.length_incorrect));
					break;
				case TLV_INCORRECT:
					setStatus(getString(R.string.tlv_incorrect));
					break;
				case TAG_NOT_FOUND:
					setStatus(getString(R.string.tag_not_found));
					break;
				case BOOTLOADER_NOT_SUPPORT:
					setStatus(getString(R.string.bootloader_not_support));
					break;
				case TAG_NOT_ALLOWED_TO_ACCESS:
					setStatus(getString(R.string.tag_not_allowed_to_access));
					break;
				case USER_DEFINED_DATA_NOT_ENALBLED:
					setStatus(getString(R.string.user_defined_data_not_allowed_to_change));
					break;
				case TAG_NOT_WRITTEN_CORRECTLY:
					setStatus(getString(R.string.tag_not_written_correctly));
					break;
				default:
					break;
				}
				setStatus(text);
			}
		}

		@Override
		public void onReturnReadWiFiSettingsResult(boolean isSuccess, Hashtable<String, Object> data) {
			if (isSuccess) {
				String text = getString(R.string.read_wifi_setting_success);
				text += "\n" + getString(R.string.ssid) + data.get("ssid");
				text += "\n" + getString(R.string.password) + data.get("password");
				text += "\n" + getString(R.string.url) + data.get("url");
				text += "\n" + getString(R.string.portNumber) + data.get("portNumber");
				setStatus(text);
			} else {
				String text = getString(R.string.read_wifi_setting_fail);
				Object[] keys = data.keySet().toArray();
				Arrays.sort(keys);
				for (Object key : keys) {
					text += "\n" + (String)key + " : ";
					TerminalSettingStatus terminalSettingStatus = (TerminalSettingStatus)data.get(key);
					switch (terminalSettingStatus) {
					case SUCCESS:
						text += getString(R.string.read_terminal_setting_success);
						break;
					case LENGTH_INCORRECT:
						text += getString(R.string.length_incorrect);
						break;
					case TLV_INCORRECT:
						text += getString(R.string.tlv_incorrect);
						break;
					case TAG_NOT_FOUND:
						text += getString(R.string.tag_not_found);
						break;
					case BOOTLOADER_NOT_SUPPORT:
						text += getString(R.string.bootloader_not_support);
						break;
					case TAG_NOT_ALLOWED_TO_ACCESS:
						text += getString(R.string.tag_not_allowed_to_access);
						break;
					case USER_DEFINED_DATA_NOT_ENALBLED:
						text += getString(R.string.user_defined_data_not_allowed_to_change);
						break;
					case TAG_NOT_WRITTEN_CORRECTLY:
						text += getString(R.string.tag_not_written_correctly);
						break;
					default:
						break;
					}
				}
				setStatus(text);
			}
		}

		@Override
		public void onReturnUpdateGprsSettingsResult(boolean isSuccess, Hashtable<String, TerminalSettingStatus> data) {
			if (isSuccess) {
				String text = getString(R.string.update_gprs_setting_success);
				setStatus(text);
			} else {
				String text = getString(R.string.update_gprs_setting_fail);
				text += "\n" + getString(R.string.terminal_setting_status) + data.get("gprs");
				setStatus(text);
			}
		}

		@Override
		public void onReturnUpdateWiFiSettingsResult(boolean isSuccess, Hashtable<String, TerminalSettingStatus> data) {
			if (isSuccess) {
				String text = getString(R.string.update_wifi_setting_success);
				setStatus(text);
			} else {
				String text = getString(R.string.update_wifi_setting_fail);
				Object[] keys = data.keySet().toArray();
				Arrays.sort(keys);
				for (Object key : keys) {
					text += "\n" + (String)key + " : ";
					TerminalSettingStatus terminalSettingStatus = (TerminalSettingStatus)data.get(key);
					switch (terminalSettingStatus) {
					case SUCCESS:
						text += getString(R.string.read_terminal_setting_success);
						break;
					case LENGTH_INCORRECT:
						text += getString(R.string.length_incorrect);
						break;
					case TLV_INCORRECT:
						text += getString(R.string.tlv_incorrect);
						break;
					case TAG_NOT_FOUND:
						text += getString(R.string.tag_not_found);
						break;
					case BOOTLOADER_NOT_SUPPORT:
						text += getString(R.string.bootloader_not_support);
						break;
					case TAG_NOT_ALLOWED_TO_ACCESS:
						text += getString(R.string.tag_not_allowed_to_access);
						break;
					case USER_DEFINED_DATA_NOT_ENALBLED:
						text += getString(R.string.user_defined_data_not_allowed_to_change);
						break;
					case TAG_NOT_WRITTEN_CORRECTLY:
						text += getString(R.string.tag_not_written_correctly);
						break;
					default:
						break;
					}
				}
				setStatus(text);
			}
		}

		@Override
		public void onAudioAutoConfigCompleted(boolean isDefaultSettings, String autoConfigSettings) {
			if(progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			
			String outputDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bbpos.bbdevice/";
			String filename = "settings.txt";
			String content = getString(R.string.auto_config_completed);
			if(isDefaultSettings) {
				content += "\n" + getString(R.string.default_settings);
				new File(outputDirectory + filename).delete();
			} else {
				content += "\n" + getString(R.string.settings) + autoConfigSettings;
				
				try {
					File directory = new File(outputDirectory);
					if(!directory.isDirectory()) {
						directory.mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(outputDirectory + filename, false);
					fos.write(autoConfigSettings.getBytes());
					fos.flush();
					fos.close();
					
					content += "\n" + getString(R.string.settings_written_to_external_storage);
				} catch(Exception e) {
				}
			}
			setStatus(content);
		}

		@Override
		public void onAudioAutoConfigError(AudioAutoConfigError autoConfigError) {
			if(progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			
			if(autoConfigError == AudioAutoConfigError.PHONE_NOT_SUPPORTED) {
				statusEditText.setText(getString(R.string.auto_config_error_phone_not_supported));
			} else if(autoConfigError == AudioAutoConfigError.INTERRUPTED) {
				statusEditText.setText(getString(R.string.auto_config_error_interrupted));
			}
		}

		@Override
		public void onAudioAutoConfigProgressUpdate(double percentage) {
			if(progressDialog != null) {
				progressDialog.setProgress((int)percentage);
			}
		}

		@Override
		public void onDeviceHere(boolean arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNoAudioDeviceDetected() {
			dismissDialog();
			if(progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			statusEditText.setText(getString(R.string.no_device_detected));
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(currentActivity, getString(R.string.no_device_detected), Toast.LENGTH_SHORT).show();
				}
			});
		}

		@Override
		public void onReturnNfcDataExchangeResult(boolean isSuccess, Hashtable<String, String> data) {
			// TODO Auto-generated method stub	
		}

		@Override
		public void onReturnNfcDetectCardResult(NfcDetectCardResult nfcDetectCardResult, Hashtable<String, Object> data) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onUsbConnected() {
			setStatus(getString(R.string.usb_connected));
		}

		@Override
		public void onUsbDisconnected() {
			setStatus(getString(R.string.usb_disconnected));			
		}

		@Override
		public void onRequestDisplayAsterisk(int arg0) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onSerialConnected() {
			final ProgressDialog progressDialog = ProgressDialog.show(BaseActivity.this, getString(R.string.please_wait), getString(R.string.initializing));
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
					}
					final Handler handler = new Handler(Looper.getMainLooper());
					handler.post(new Runnable() {
						@Override
						public void run() {
							progressDialog.dismiss();
							statusEditText.setText(getString(R.string.serial_connected));
						}
					});
				}
			}).start();
		}

		@Override
		public void onSerialDisconnected() {
			setStatus(getString(R.string.serial_disconnected));
		}

		@Override
		public void onBarcodeReaderConnected() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onBarcodeReaderDisconnected() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onReturnBarcode(String arg0) {
			// TODO Auto-generated method stub
		}
	}
	
	//by Lu
	public void callMenuSelected(int i) {
		onMenuSelected(i);
	}
	
	//by Lu
	public boolean onMenuSelected(int id) {
		if (id == R.id.menu_start_connection) {
			promptForConnection();
			return true;
		} else if (id == R.id.menu_stop_connection) {
			stopConnection();
			return true;
		} else if (id == R.id.menu_initialize_session) {
			statusEditText.setText(R.string.initializing_session);
			promptForInitSession();
		} else if (id == R.id.menu_reset_session) {
			statusEditText.setText(R.string.reset_session);
			bbDeviceController.resetSession();
		} else if (id == R.id.menu_get_deivce_info) {
			statusEditText.setText(R.string.getting_info);
			bbDeviceController.getDeviceInfo();
		} else if(id == R.id.menu_unpair_all) {
    		new Thread(new Runnable() {
				@Override
				public void run() {
					final Handler handler = new Handler(Looper.getMainLooper());
					try {
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(BaseActivity.this, getString(R.string.unpair_all_start), Toast.LENGTH_SHORT).show();
							}
						});
						
						Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
						BluetoothDevice pairedDevices;
						for (int i = 0; i < pairedObjects.length; ++i) {
							pairedDevices = (BluetoothDevice) pairedObjects[i];

							try {
								Method m = pairedDevices.getClass().getMethod("removeBond", (Class[]) null);
								m.invoke(pairedDevices, (Object[]) null);
								Thread.sleep(3000);
							} catch (Exception e) {
							}
						}
						
					} catch (Exception e) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(BaseActivity.this, getString(R.string.unpair_all_fail), Toast.LENGTH_SHORT).show();
							}
						});
						return;
					}
					
					handler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(BaseActivity.this, getString(R.string.unpair_all_end), Toast.LENGTH_SHORT).show();
						}
					});

				}
			}).start();
    		
    		return true;
		} else if (id == R.id.menu_cancel_check_card) {
			statusEditText.setText(R.string.cancel_check_card);
			bbDeviceController.cancelCheckCard();
		} else if (id == R.id.menu_auto_config) {
			progressDialog = new ProgressDialog(this);
    		progressDialog.setCancelable(false);
    		progressDialog.setCanceledOnTouchOutside(false);
    		progressDialog.setTitle(R.string.auto_configuring);
    		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    		progressDialog.setMax(100);
    		progressDialog.setIndeterminate(false);
    		progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					statusEditText.setText(getString(R.string.canceling_auto_config));
					bbDeviceController.cancelAutoConfig();
				}
			});
    		progressDialog.show();
    		bbDeviceController.startAutoConfig();
		} else if (id == R.id.menu_enable_input_amount) {
			Hashtable<String, Object> data = new Hashtable<String, Object>();
			if (Locale.getDefault().getCountry().equalsIgnoreCase("CN")) {
				data.put("currencyCode", "156");
				data.put("currencyCharacters", new CurrencyCharacter[] { CurrencyCharacter.YEN });
			} else {
				data.put("currencyCode", "840");
				data.put("currencyCharacters", new CurrencyCharacter[] { CurrencyCharacter.DOLLAR });
			}
			data.put("amountInputType", AmountInputType.AMOUNT_AND_CASHBACK);
			bbDeviceController.enableInputAmount(data);
		} else if(id == R.id.menu_encrypt_pin) {
    		String encWorkingKey = encrypt(fid65WorkingKey, fid65MasterKey);
			String workingKeyKcv = encrypt("0000000000000000", fid65WorkingKey);
			
    		Hashtable<String, Object> data = new Hashtable<String, Object>();
			data.put("pin", "123456");
			data.put("pan", "123456789012345678");
			data.put("encPinKey", encWorkingKey + workingKeyKcv);
			
			bbDeviceController.encryptPin(data);
    	} else if (id == R.id.menu_encrypt_data) {
			String encWorkingKey = encrypt(fid65WorkingKey, fid65MasterKey);
			String workingKeyKcv = encrypt("0000000000000000", fid65WorkingKey);

			Hashtable<String, Object> data = new Hashtable<String, Object>();
			data.put("data", "0123456789ABCDEF0123456789ABCDEF");
			data.put("encWorkingKey", encWorkingKey + workingKeyKcv);
			data.put("encryptionMethod", EncryptionMethod.MAC_METHOD_1);
			data.put("encryptionKeySource", EncryptionKeySource.BY_SERVER_16_BYTES_WORKING_KEY);
			data.put("encryptionPaddingMethod", EncryptionPaddingMethod.ZERO_PADDING);
			data.put("macLength", "8");
			data.put("randomNumber", "0123456789ABCDEF");
			data.put("keyUsage", EncryptionKeyUsage.TAK);
			data.put("initialVector", "0000000000000000");
			bbDeviceController.encryptDataWithSettings(data);
		} else if (id == R.id.menu_print_sample) {
			receipts = new ArrayList<byte[]>();
			if (Locale.getDefault().getCountry().equalsIgnoreCase("CN")) {
				receipts.add(ReceiptUtility.genReceipt2(this));
			} else {
				receipts.add(ReceiptUtility.genReceipt(this));
			}
			bbDeviceController.startPrint(receipts.size(), 60);
		} else if (id == R.id.menu_mainactivity) {
			isSwitchingActivity = true;
			finish();
			Intent in = new Intent(this, BBPosBTMainActivity.class);
			startActivity(in);
		} else if (id == R.id.menu_send_apdu) {
			isSwitchingActivity = true;
			finish();
			Intent in = new Intent(this, ApduActivity.class);
			startActivity(in);
		} else if(id == R.id.capk_activity) {
    		isSwitchingActivity = true;
    		finish();
    		Intent in = new Intent(this, CAPKActivity.class);
    		startActivity(in);
		} else if(id == R.id.menu_wifi) {
    		isSwitchingActivity = true;
    		finish();
    		Intent in = new Intent(this, GprsWifiActivity.class);
    		startActivity(in);
		} else if (id == R.id.menu_inject_session_key) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("data", "0057620000001700549907D317C1A2E8A89E489A8FD9414F3A4F120558A58CBD927343326682F5F1999EF07D767D041103F6D914EEC0A6616C178D927BBC48EE9BFE9E4A140F871BDDA0E362A59CFB95EFAA744BD4FB0F5D13");
			bbDeviceController.injectSessionKey(data);
		}
		return true;
	}
}
