package com.thtfit.pos.emvswipe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bbpos.emvswipe.CAPK;
import com.bbpos.emvswipe.EmvSwipeController;
import com.bbpos.emvswipe.EmvSwipeController.AutoConfigError;
import com.bbpos.emvswipe.EmvSwipeController.BatteryStatus;
import com.bbpos.emvswipe.EmvSwipeController.CheckCardMode;
import com.bbpos.emvswipe.EmvSwipeController.CheckCardResult;
import com.bbpos.emvswipe.EmvSwipeController.ConnectionMode;
import com.bbpos.emvswipe.EmvSwipeController.DisplayText;
import com.bbpos.emvswipe.EmvSwipeController.EncryptionKeySource;
import com.bbpos.emvswipe.EmvSwipeController.EncryptionKeyUsage;
import com.bbpos.emvswipe.EmvSwipeController.EncryptionMethod;
import com.bbpos.emvswipe.EmvSwipeController.EncryptionPaddingMethod;
import com.bbpos.emvswipe.EmvSwipeController.Error;
import com.bbpos.emvswipe.EmvSwipeController.ReferralResult;
import com.bbpos.emvswipe.EmvSwipeController.StartEmvResult;
import com.bbpos.emvswipe.EmvSwipeController.TerminalSettingStatus;
import com.bbpos.emvswipe.EmvSwipeController.TransactionResult;
import com.bbpos.emvswipe.EmvSwipeController.TransactionType;
import com.thtfit.pos.R;
import com.thtfit.pos.api.Money;
import com.thtfit.pos.ui.PreventCursorPositionEditText;

public class BBPosMainActivity extends EMVBaseActivity {
	
	private Spinner fidSpinner;
	private Button checkCardButton;
	private EditText amountEditText;
	private EditText statusEditText;
	private ListView appListView;
	private ProgressDialog progressDialog;
	private Button menuBtn;//by Lu
    private PopupWindow popupwindow;// by Lu
    //by Lu : menu item 
    private MenuItemClickListener menuItemClickListener;
    private TextView menuStartConnection;
    private TextView menuStopConnection;
    private TextView menuGetDeivceInfo;
    private TextView menuGetKsn;
    private TextView menuCancelCheckCard;
    private TextView menuAutoConfig;
    private TextView menuEncryptData;
    private TextView iccActivity;
    private TextView nfcActivity;
    private TextView capkActivity;
    private TextView integrityCheck;
    private EditText dialogAmountEditText;//by Lu
    private String mReceiveAmount;//by Lu
	
	private boolean isAskingForAmount = false;
	
	private String amount = "";
	private String cashbackAmount = "";
	private boolean isPinCanceled = false;
	
	private CheckCardMode checkCardMode;
	
	private Handler handler;
	
	protected static String apdus[] = {
		"801a1301082012101111245180",
		"80fa000018363232383738303231323231393330373030000000000000",
	};
	protected static int apduIndex;
	
	protected static String fid65WorkingKey = "A1223344556677889900AABBCCDDEEFF";
	protected static String fid65MasterKey = "0123456789ABCDEFFEDCBA9876543210";

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.bbpos_activity_main1);
        
        ((TextView)findViewById(R.id.modelTextView)).setText(Build.MANUFACTURER.toUpperCase(Locale.ENGLISH) + " - " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")");
        
        fidSpinner = (Spinner)findViewById(R.id.fidSpinner);
        checkCardButton = (Button)findViewById(R.id.checkCardButton);
        amountEditText = (EditText)findViewById(R.id.amountEditText);
        statusEditText = (EditText)findViewById(R.id.statusEditText);
        //by Lu
        menuBtn = (Button) findViewById(R.id.btn_menu);
        mReceiveAmount = getIntent().getStringExtra("amount");
        
        MyOnClickListener myOnClickListener = new MyOnClickListener();
        checkCardButton.setOnClickListener(myOnClickListener);
        menuBtn.setOnClickListener(myOnClickListener);
        
        handler = new Handler();
        
        String[] fids = new String[] {
        		"FID22",
        		"FID36",
        		"FID46",
        		"FID54",
        		"FID55",
        		"FID60",
				"FID61",
				"FID64",
				"FID65",
		};
		fidSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.my_spinner_item, fids));
		fidSpinner.setSelection(5);
    }
    
    @Override
	public void onStart() {
		super.onStart();
		statusEditText.setText(EmvSwipeController.getApiVersion());
	}
    
    @Override
    public void onResume() {
    	super.onResume();
    	currentActivity = this;
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if(isSwitchingActivity) {
    		isSwitchingActivity = false;
    	} else {
    		if (emvSwipeController.getConnectionMode() == ConnectionMode.AUDIO)
    			emvSwipeController.stopAudio();
    		else if (emvSwipeController.getConnectionMode() == ConnectionMode.USB)
    			emvSwipeController.stopUsb();
    		emvSwipeController.resetEmvSwipeController();
    		emvSwipeController = null;
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.bbpos_activity_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId() == R.id.menu_start_connection) {
    		promptForConnection();
    	} else if(item.getItemId() == R.id.menu_stop_connection) {
    		stopConnection();
    	} else if(item.getItemId() == R.id.menu_get_deivce_info) {
    		statusEditText.setText(R.string.getting_info);
    		emvSwipeController.getDeviceInfo();
    	} else if(item.getItemId() == R.id.menu_cancel_check_card) {
    		statusEditText.setText("");
    		emvSwipeController.cancelCheckCard();
    	} else if(item.getItemId() == R.id.menu_get_ksn) {
    		statusEditText.setText(R.string.getting_ksn);
    		emvSwipeController.getKsn();
    	} else if(item.getItemId() == R.id.menu_auto_config) {
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
					emvSwipeController.cancelAutoConfig();
				}
			});
    		progressDialog.show();
    		emvSwipeController.startAutoConfig();
    	} else if(item.getItemId() == R.id.menu_encrypt_data) {
    		statusEditText.setText(R.string.encrypting_data);
    		String encWorkingKey = "12042B145F8516D74F0B96AAA5A8B548";
			String workingKeyKcv = "C257CC0FD286CDC4";
			
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
    		
    		emvSwipeController.encryptDataWithSettings(data);
    	} else if(item.getItemId() == R.id.icc_activity) {
    		isSwitchingActivity = true;
    		finish();
    		Intent intent = new Intent(this, IccActivity.class);
    		startActivity(intent);
    	} else if(item.getItemId() == R.id.nfc_activity) {
    		isSwitchingActivity = true;
    		finish();
    		Intent intent = new Intent(this, NfcActivity.class);
    		startActivity(intent);
    	} else if(item.getItemId() == R.id.capk_activity) {
    		isSwitchingActivity = true;
    		finish();
    		Intent intent = new Intent(this, CAPKActivity.class);
    		startActivity(intent);
    	}
    	return true;
    }
    
    public void promptForCheckCard() {
    	dismissDialog();
    	dialog = new Dialog(BBPosMainActivity.this);
		dialog.setContentView(R.layout.bbpos1_check_card_mode_dialog);
		dialog.setTitle(getString(R.string.select_mode));
		
		View.OnClickListener onClickListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				RadioButton swipeRadioButton = (RadioButton)dialog.findViewById(R.id.swipeRadioButton);
				RadioButton insertRadioButton = (RadioButton)dialog.findViewById(R.id.insertRadioButton);
				RadioButton tapRadioButton = (RadioButton)dialog.findViewById(R.id.tapRadioButton);
				RadioButton swipeOrInsertRadioButton = (RadioButton)dialog.findViewById(R.id.swipeOrInsertRadioButton);
				RadioButton swipeOrTapRadioButton = (RadioButton)dialog.findViewById(R.id.swipeOrTapRadioButton);
				RadioButton insertOrTapRadioButton = (RadioButton)dialog.findViewById(R.id.insertOrTapRadioButton);
				RadioButton swipeOrInsertOrTapRadioButton = (RadioButton)dialog.findViewById(R.id.swipeOrInsertOrTapRadioButton);
				
				if(swipeRadioButton.isChecked()) {
					checkCardMode = CheckCardMode.SWIPE;
				} else if(insertRadioButton.isChecked()) {
					checkCardMode = CheckCardMode.INSERT;
				} else if(tapRadioButton.isChecked()) {
					checkCardMode = CheckCardMode.TAP;
				} else if(swipeOrInsertRadioButton.isChecked()) {
					checkCardMode = CheckCardMode.SWIPE_OR_INSERT;
				} else if(swipeOrTapRadioButton.isChecked()) {
					checkCardMode = CheckCardMode.SWIPE_OR_TAP;
				} else if(insertOrTapRadioButton.isChecked()) {
					checkCardMode = CheckCardMode.INSERT_OR_TAP;
				} else if(swipeOrInsertOrTapRadioButton.isChecked()) {
					checkCardMode = CheckCardMode.SWIPE_OR_INSERT_OR_TAP;
				} else {
					dismissDialog();
					return;
				}
				
				isPinCanceled = false;
				amountEditText.setText("");
				statusEditText.setText(R.string.starting);
				
				if ((checkCardMode == CheckCardMode.TAP) || (checkCardMode == CheckCardMode.SWIPE_OR_TAP) || (checkCardMode == CheckCardMode.INSERT_OR_TAP) || (checkCardMode == CheckCardMode.SWIPE_OR_INSERT_OR_TAP)) {
					String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
					Hashtable<String, Object> data = new Hashtable<String, Object>();
					data.put("terminalTime", terminalTime);
					data.put("checkCardTimeout", "120");
					data.put("setAmountTimeout", "120");
					data.put("selectApplicationTimeout", "120");
					data.put("finalConfirmTimeout", "120");
					data.put("onlineProcessTimeout", "120");
					data.put("pinEntryTimeout", "120");
					data.put("emvOption", "START");
					data.put("checkCardMode", checkCardMode);
					if(fidSpinner.getSelectedItem().equals("FID46")) {
						data.put("randomNumber", "0123456789ABCDEF");
					} else if(fidSpinner.getSelectedItem().equals("FID61")) {
						data.put("orderID", "0123456789ABCDEF0123456789ABCDEF");
						data.put("randomNumber", "012345");
					} else if(fidSpinner.getSelectedItem().equals("FID65")) {
						// Note : The following encWorkingKey and workingKeyKcv should be generated and given by the server. 
						// Plain working key should never be transmitted through the mobile application. Here is just an example to demonstrate how to encrypt the working key can calculate the Kcv
						String encWorkingKey = encrypt(fid65WorkingKey, fid65MasterKey);
					    String workingKeyKcv = encrypt("0000000000000000", fid65WorkingKey);
					    
						data.put("encPinKey", encWorkingKey + workingKeyKcv);
						data.put("encDataKey", encWorkingKey + workingKeyKcv);
						data.put("encMacKey", encWorkingKey + workingKeyKcv);
					}
					emvSwipeController.startEmv(data);
				} else {
					Hashtable<String, Object> data = new Hashtable<String, Object>();
					data.put("checkCardTimeout", "120");
					data.put("checkCardMode", checkCardMode);
					if(fidSpinner.getSelectedItem().equals("FID46")) {
						data.put("randomNumber", "0123456789ABCDEF");
					} else if(fidSpinner.getSelectedItem().equals("FID61")) {
						data.put("orderID", "0123456789ABCDEF0123456789ABCDEF");
						data.put("randomNumber", "012345");
					} else if(fidSpinner.getSelectedItem().equals("FID65")) {
						// Note : The following encWorkingKey and workingKeyKcv should be generated and given by the server. 
						// Plain working key should never be transmitted through the mobile application. Here is just an example to demonstrate how to encrypt the working key can calculate the Kcv
						String encWorkingKey = encrypt(fid65WorkingKey, fid65MasterKey);
					    String workingKeyKcv = encrypt("0000000000000000", fid65WorkingKey);
					    
						data.put("encPinKey", encWorkingKey + workingKeyKcv);
						data.put("encDataKey", encWorkingKey + workingKeyKcv);
						data.put("encMacKey", encWorkingKey + workingKeyKcv);
						data.put("amount", "1.0");
					}
					emvSwipeController.checkCard(data);
				}
				
				dismissDialog();
			}
		};
		
		RadioButton swipeRadioButton = (RadioButton)dialog.findViewById(R.id.swipeRadioButton);
		RadioButton insertRadioButton = (RadioButton)dialog.findViewById(R.id.insertRadioButton);
		RadioButton tapRadioButton = (RadioButton)dialog.findViewById(R.id.tapRadioButton);
		RadioButton swipeOrInsertRadioButton = (RadioButton)dialog.findViewById(R.id.swipeOrInsertRadioButton);
		
		swipeRadioButton.setOnClickListener(onClickListener);
		insertRadioButton.setOnClickListener(onClickListener);
		tapRadioButton.setOnClickListener(onClickListener);
		swipeOrInsertRadioButton.setOnClickListener(onClickListener);
		
		dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissDialog();
			}
			
		});
		
		dialog.show();
    }
    
    public void promptForAmount() {
    	dismissDialog();
		dialog = new Dialog(BBPosMainActivity.this);
		dialog.setContentView(R.layout.amount_dialog);
		dialog.setTitle(getString(R.string.set_amount));
		
		// by Lu
		dialogAmountEditText = (EditText)dialog.findViewById(R.id.amountEditText);
		dialogAmountEditText.setText(mReceiveAmount);
		
		String[] transactionTypes = new String[] {
				"GOODS",
				"SERVICES",
				"CASHBACK",
				"INQUIRY",
				"TRANSFER",
				"PAYMENT",
				"REFUND"
		};
		((Spinner)dialog.findViewById(R.id.transactionTypeSpinner)).setAdapter(new ArrayAdapter<String>(BBPosMainActivity.this, android.R.layout.simple_spinner_item, transactionTypes));
		
		dialog.findViewById(R.id.setButton).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				dialogAmountEditText = (EditText)dialog.findViewById(R.id.amountEditTextP);
//				String amount = dialogAmountEditText.getText().toString();
				String cashbackAmount = ((EditText)(dialog.findViewById(R.id.cashbackAmountEditText))).getText().toString();
				String transactionTypeString = (String)((Spinner)dialog.findViewById(R.id.transactionTypeSpinner)).getSelectedItem();
				
				TransactionType transactionType = TransactionType.GOODS;
				if(transactionTypeString.equals("GOODS")) {
					transactionType = TransactionType.GOODS;
				} else if(transactionTypeString.equals("SERVICES")) {
					transactionType = TransactionType.SERVICES;
				} else if(transactionTypeString.equals("CASHBACK")) {
					transactionType = TransactionType.CASHBACK;
				} else if(transactionTypeString.equals("INQUIRY")) {
					transactionType = TransactionType.INQUIRY;
				} else if(transactionTypeString.equals("TRANSFER")) {
					transactionType = TransactionType.TRANSFER;
				} else if(transactionTypeString.equals("PAYMENT")) {
					transactionType = TransactionType.PAYMENT;
				} else if(transactionTypeString.equals("REFUND")) {
					transactionType = TransactionType.REFUND;
				}
				
				String currencyCode;
				if(Locale.getDefault().getCountry().equalsIgnoreCase("CN")) {
					currencyCode = "156";
				} else {
					currencyCode = "840";
				}
				
				//by Lu : receive the amount from SwipeCardActivity
				Money money = new Money(mReceiveAmount);
				boolean hasNoError = true;
				if (!money.isGreaterThanZero()) {
					Log.d("luzhaojie", getString(R.string.price_is_zero));
					hasNoError = false;
				}
				if (!hasNoError) {
					return;
				}
				String amount = money.toDefaultString();
				
				if(emvSwipeController.setAmount(amount, cashbackAmount, currencyCode, transactionType)) {
					dialogAmountEditText.setText("$" + amount);
					BBPosMainActivity.this.amount = amount;
					BBPosMainActivity.this.cashbackAmount = cashbackAmount;
					dismissDialog();
					isAskingForAmount = false;
				} else {
					promptForAmount();
				}
			}
			
		});
		
		dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isAskingForAmount) {
					emvSwipeController.cancelSetAmount();
				}
				isAskingForAmount = false;
				dialog.dismiss();
			}
			
		});
		
		dialog.show();
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
		if(s == null) {
			s = "";
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		for(int i = 0; i < s.length() - 1; i += 2) {
			String data = s.substring(i, i + 2);
			bout.write(Integer.parseInt(data, 16));
		}
		return bout.toByteArray();
	}
    
    private static String toHexString(byte[] b) {
		if(b == null) {
			return "null";
		}
		String result = "";
		for (int i=0; i < b.length; i++) {
			result += Integer.toString( ( b[i] & 0xFF ) + 0x100, 16).substring( 1 );
		}
		return result;
	}
    
    public String encrypt(String data, String key) {
    	if(key.length() == 16) {
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
    
    public void dismissDialog() {
    	if(dialog != null) {
    		dialog.dismiss();
    		dialog = null;
    	}
    }
    
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
			statusEditText.setText(getString(R.string.please_swipe_or_insert_card));
			break;
		case TAP:
			statusEditText.setText(getString(R.string.please_tap_card));
			break;
		default:
			break;
		}
	}
    
    @Override
	public void onBatchDataDetected() {
    	statusEditText.setText(getString(R.string.batch_data_detected));
	}

	@Override
	public void onOnlineProcessDataDetected() {
		statusEditText.setText(getString(R.string.online_process_data_detected));
	}

	@Override
	public void onReversalDataDetected() {
		statusEditText.setText(getString(R.string.reversal_data_detected));
	}

	@Override
	public void onReturnCheckCardResult(CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {
		dismissDialog();
		if(checkCardResult == CheckCardResult.NONE) {
			statusEditText.setText(getString(R.string.no_card_detected));
		} else if(checkCardResult == CheckCardResult.ICC) {
			statusEditText.setText(getString(R.string.icc_card_inserted));
			String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
			Hashtable<String, Object> data = new Hashtable<String, Object>();
			data.put("terminalTime", terminalTime);
			data.put("checkCardTimeout", "120");
			data.put("setAmountTimeout", "120");
			data.put("selectApplicationTimeout", "120");
			data.put("finalConfirmTimeout", "120");
			data.put("onlineProcessTimeout", "120");
			data.put("pinEntryTimeout", "120");
			data.put("emvOption", "START");
			data.put("checkCardMode", checkCardMode);
			data.put("encOnlineMessageTags", new String[] {"9F09"});
			data.put("encBatchDataTags", new String[] {"9F09"});
			data.put("encReversalDataTags", new String[] {"9F09"});
			if(fidSpinner.getSelectedItem().equals("FID46")) {
				data.put("randomNumber", "0123456789ABCDEF");
			} else if(fidSpinner.getSelectedItem().equals("FID61")) {
				data.put("orderID", "0123456789ABCDEF0123456789ABCDEF");
				data.put("randomNumber", "012345");
			} else if(fidSpinner.getSelectedItem().equals("FID65")) {
				// Note : The following encWorkingKey and workingKeyKcv should be generated and given by the server. 
				// Plain working key should never be transmitted through the mobile application. Here is just an example to demonstrate how to encrypt the working key can calculate the Kcv
				String encWorkingKey = encrypt(fid65WorkingKey, fid65MasterKey);
			    String workingKeyKcv = encrypt("0000000000000000", fid65WorkingKey);
			    
				data.put("encPinKey", encWorkingKey + workingKeyKcv);
				data.put("encDataKey", encWorkingKey + workingKeyKcv);
				data.put("encMacKey", encWorkingKey + workingKeyKcv);
			}
			emvSwipeController.startEmv(data);
		} else if(checkCardResult == CheckCardResult.NOT_ICC) {
			statusEditText.setText(getString(R.string.card_inserted));
		} else if(checkCardResult == CheckCardResult.BAD_SWIPE) {
			statusEditText.setText(getString(R.string.bad_swipe));
		} else if(checkCardResult == CheckCardResult.MCR) {
			String formatID = decodeData.get("formatID");
			String maskedPAN = decodeData.get("maskedPAN");
			String PAN = decodeData.get("PAN");
			String expiryDate = decodeData.get("expiryDate");
			String cardHolderName = decodeData.get("cardholderName");
			String ksn = decodeData.get("ksn");
			String serviceCode = decodeData.get("serviceCode");
			String track1Length = decodeData.get("track1Length");
			String track2Length = decodeData.get("track2Length");
			String track3Length = decodeData.get("track3Length");
			String encTracks = decodeData.get("encTracks");
			String encTrack1 = decodeData.get("encTrack1");
			String encTrack2 = decodeData.get("encTrack2");
			String encTrack3 = decodeData.get("encTrack3");
			String partialTrack = decodeData.get("partialTrack");
			String trackEncoding = decodeData.get("trackEncoding");
			String finalMessage = decodeData.get("finalMessage");
			String randomNumber = decodeData.get("randomNumber");
			String encWorkingKey = decodeData.get("encWorkingKey");
			String mac = decodeData.get("mac");
			String data = decodeData.get("data");
			String track2EqKsn = decodeData.get("track2EqKsn");
			String encTrack2Eq = decodeData.get("encTrack2Eq");
			
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
			content += getString(R.string.partial_track) + " " + partialTrack + "\n";
			content += getString(R.string.track_encoding) + " " + trackEncoding + "\n";
			content += getString(R.string.final_message) + " " + finalMessage + "\n";
			content += getString(R.string.random_number) + " " + randomNumber + "\n";
			content += getString(R.string.encrypted_working_key) + " " + encWorkingKey + "\n";
			content += getString(R.string.mac) + " " + mac + "\n";
			if ((decodeData != null) && (decodeData.containsKey("data"))) {
				content += getString(R.string.data) + ": " + decodeData.get("data") + "\n";
			}
			if ((decodeData != null) && (decodeData.containsKey("track2EqKsn"))) {
				content += getString(R.string.track2_eq_ksn) + decodeData.get("track2EqKsn") + "\n";
			}
			if ((decodeData != null) && (decodeData.containsKey("encTrack2Eq"))) {
				content += getString(R.string.encrypted_track2_eq) + decodeData.get("encTrack2Eq") + "\n";
			}
			
			statusEditText.setText(content);
		} else if(checkCardResult == CheckCardResult.NO_RESPONSE) {
			statusEditText.setText(getString(R.string.card_no_response));
		} else if(checkCardResult == CheckCardResult.TRACK2_ONLY) {
			String formatID = decodeData.get("formatID");
			String maskedPAN = decodeData.get("maskedPAN");
			String PAN = decodeData.get("PAN");
			String expiryDate = decodeData.get("expiryDate");
			String cardHolderName = decodeData.get("cardholderName");
			String ksn = decodeData.get("ksn");
			String serviceCode = decodeData.get("serviceCode");
			String track1Length = decodeData.get("track1Length");
			String track2Length = decodeData.get("track2Length");
			String track3Length = decodeData.get("track3Length");
			String encTracks = decodeData.get("encTracks");
			String encTrack1 = decodeData.get("encTrack1");
			String encTrack2 = decodeData.get("encTrack2");
			String encTrack3 = decodeData.get("encTrack3");
			String partialTrack = decodeData.get("partialTrack");
			String trackEncoding = decodeData.get("trackEncoding");
			String finalMessage = decodeData.get("finalMessage");
			String randomNumber = decodeData.get("randomNumber");
			String encWorkingKey = decodeData.get("encWorkingKey");
			String mac = decodeData.get("mac");
			
			String content = getString(R.string.card_swiped_track2_only) + "\n";
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
			content += getString(R.string.partial_track) + " " + partialTrack + "\n";
			content += getString(R.string.track_encoding) + " " + trackEncoding + "\n";
			content += getString(R.string.final_message) + " " + finalMessage + "\n";
			content += getString(R.string.random_number) + " " + randomNumber + "\n";
			content += getString(R.string.encrypted_working_key) + " " + encWorkingKey + "\n";
			content += getString(R.string.mac) + " " + mac + "\n";
			
			statusEditText.setText(content);
			
		} else if(checkCardResult == CheckCardResult.NFC_TRACK2) {
			String formatID = decodeData.get("formatID");
			String maskedPAN = decodeData.get("maskedPAN");
			String PAN = decodeData.get("PAN");
			String expiryDate = decodeData.get("expiryDate");
			String cardHolderName = decodeData.get("cardholderName");
			String ksn = decodeData.get("ksn");
			String serviceCode = decodeData.get("serviceCode");
			String track1Length = decodeData.get("track1Length");
			String track2Length = decodeData.get("track2Length");
			String track3Length = decodeData.get("track3Length");
			String encTracks = decodeData.get("encTracks");
			String encTrack1 = decodeData.get("encTrack1");
			String encTrack2 = decodeData.get("encTrack2");
			String encTrack3 = decodeData.get("encTrack3");
			String partialTrack = decodeData.get("partialTrack");
			String trackEncoding = decodeData.get("trackEncoding");
			String finalMessage = decodeData.get("finalMessage");
			String randomNumber = decodeData.get("randomNumber");
			String encWorkingKey = decodeData.get("encWorkingKey");
			String mac = decodeData.get("mac");
			
			String content = getString(R.string.nfc_track2) + "\n";
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
			content += getString(R.string.partial_track) + " " + partialTrack + "\n";
			content += getString(R.string.track_encoding) + " " + trackEncoding + "\n";
			content += getString(R.string.final_message) + " " + finalMessage + "\n";
			content += getString(R.string.random_number) + " " + randomNumber + "\n";
			content += getString(R.string.encrypted_working_key) + " " + encWorkingKey + "\n";
			content += getString(R.string.mac) + " " + mac + "\n";
			
			statusEditText.setText(content);
			
		} else if(checkCardResult == CheckCardResult.USE_ICC_CARD) {
			statusEditText.setText(getString(R.string.use_icc_card));
		} else if(checkCardResult == CheckCardResult.TAP_CARD_DETECTED) {
			statusEditText.setText(getString(R.string.tap_card_detected));
		}
	}
	
	@Override
	public void onReturnCancelCheckCardResult(boolean isSuccess) {
		if(isSuccess) {
			statusEditText.setText(R.string.cancel_check_card_success);
		} else {
			statusEditText.setText(R.string.cancel_check_card_fail);
		}
	}
	
	@Override
	public void onReturnEncryptPinResult(Hashtable<String, String> data) {
		String epb = data.get("epb");
		String ksn = data.get("ksn");
		String randomNumber = data.get("randomNumber");
		String encWorkingKey = data.get("encWorkingKey");
		
		String content = getString(R.string.ksn) + ksn + "\n";
		content += getString(R.string.epb) + epb + "\n";
		content += getString(R.string.random_number) + randomNumber + "\n";
		content += getString(R.string.encrypted_working_key) + encWorkingKey;
		
		statusEditText.setText(content);
	}
	
	@Override
	public void onReturnEncryptDataResult(boolean isSuccess, Hashtable<String, String> data) {
		if(isSuccess) {
			String content = "";
    		if(data.containsKey("ksn")) {
    			content += getString(R.string.ksn) + data.get("ksn") + "\n";
    		}
    		if(data.containsKey("randomNumber")) {
    			content += getString(R.string.random_number) + data.get("randomNumber") + "\n";
    		}
    		if(data.containsKey("encData")) {
    			content += getString(R.string.encrypted_data) + data.get("encData") + "\n";
    		}
    		if(data.containsKey("mac")) {
    			content += getString(R.string.mac) + data.get("mac") + "\n";
    		}
    		statusEditText.setText(content);
		} else {
			String content = getString(R.string.encrypt_data_failed) + "\n";
    		if(data.containsKey("errorMessage")) {
    			content += getString(R.string.error_message) + data.get("errorMessage") + "\n";
    		}
    		statusEditText.setText(content);
		}
	}
	
	@Override
	public void onReturnStartEmvResult(StartEmvResult startEmvResult, String ksn) {
		if(startEmvResult == StartEmvResult.SUCCESS) {
			statusEditText.setText(getString(R.string.start_emv_success));
		} else {
			statusEditText.setText(getString(R.string.start_emv_fail));
		}
	}
	
	@Override
	public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
		String isSupportedTrack1 = deviceInfoData.get("isSupportedTrack1");
		String isSupportedTrack2 = deviceInfoData.get("isSupportedTrack2");
		String isSupportedTrack3 = deviceInfoData.get("isSupportedTrack3");
		String bootloaderVersion = deviceInfoData.get("bootloaderVersion");
		String firmwareVersion = deviceInfoData.get("firmwareVersion");
		String isUsbConnected = deviceInfoData.get("isUsbConnected");
		String isCharging = deviceInfoData.get("isCharging");
		String batteryLevel = deviceInfoData.get("batteryLevel");
		String batteryPercentage = deviceInfoData.get("batteryPercentage");
		String hardwareVersion = deviceInfoData.get("hardwareVersion");
		String pinKsn = deviceInfoData.get("pinKsn");
		String trackKsn = deviceInfoData.get("trackKsn");
		String emvKsn = deviceInfoData.get("emvKsn");
		String uid = deviceInfoData.get("uid");
		String csn = deviceInfoData.get("csn");
		String formatID = deviceInfoData.get("formatID");
		String vendorID = deviceInfoData.get("vendorID");
		String productID = deviceInfoData.get("productID");
		String terminalSettingVersion = deviceInfoData.get("terminalSettingVersion");
		String deviceSettingVersion = deviceInfoData.get("deviceSettingVersion");
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
		content += getString(R.string.bootloader_version) + bootloaderVersion + "\n";
		content += getString(R.string.firmware_version) + firmwareVersion + "\n";
		content += getString(R.string.usb) + isUsbConnected + "\n";
		content += getString(R.string.charge) + isCharging + "\n";
		content += getString(R.string.battery_level) + batteryLevel + "\n";
		content += getString(R.string.battery_percentage) + batteryPercentage + "\n";
		content += getString(R.string.hardware_version) + hardwareVersion + "\n";
		content += getString(R.string.track_1_supported) + isSupportedTrack1 + "\n";
		content += getString(R.string.track_2_supported) + isSupportedTrack2 + "\n";
		content += getString(R.string.track_3_supported) + isSupportedTrack3 + "\n";
		content += getString(R.string.pin_ksn) + pinKsn + "\n";
		content += getString(R.string.track_ksn) + trackKsn + "\n";
		content += getString(R.string.emv_ksn) + emvKsn + "\n";
		content += getString(R.string.uid) + uid + "\n";
		content += getString(R.string.csn) + csn + "\n";
		content += getString(R.string.vendor_id_hex) + vendorID + "\n";
		content += getString(R.string.vendor_id_ascii) + vendorIDAscii + "\n";
		content += getString(R.string.product_id) + productID + "\n";
		content += getString(R.string.terminal_setting_version) + terminalSettingVersion + "\n";
		content += getString(R.string.device_setting_version) + deviceSettingVersion + "\n";
		content += getString(R.string.serial_number) + serialNumber + "\n";
		content += getString(R.string.model_name) + modelName + "\n";
		
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
			content += getString(R.string.b_id) + bID + "\n";
		}
		
		statusEditText.setText(content);
		
		if(formatID.equals("22")) {
			fidSpinner.setSelection(0);
		} else if(formatID.equals("36")) {
			fidSpinner.setSelection(1);
		} else if(formatID.equals("46")) {
			fidSpinner.setSelection(2);
		} else if(formatID.equals("54")) {
			fidSpinner.setSelection(3);
		} else if(formatID.equals("55")) {
			fidSpinner.setSelection(4);
		} else if(formatID.equals("60")) {
			fidSpinner.setSelection(5);
		} else if(formatID.equals("61")) {
			fidSpinner.setSelection(6);
		} else if(formatID.equals("64")) {
			fidSpinner.setSelection(7);
		} else if(formatID.equals("65")) {
			fidSpinner.setSelection(8);
		} else {
			fidSpinner.setSelection(5);
		}
	}
	
	@Override
	public void onReturnCAPKList(List<CAPK> capkList) {
	}
	
	@Override
	public void onReturnCAPKDetail(CAPK capk) {
	}
	
	@Override
	public void onReturnCAPKLocation(String location) {
	}
	
	@Override
	public void onReturnUpdateCAPKResult(boolean isSuccess) {
	}
	
	@Override
	public void onReturnEmvReportList(Hashtable<String, String> data) {
	}
	
	@Override
	public void onReturnEmvReport(String tlv) {
	}
	
	@Override
	public void onReturnTransactionResult(TransactionResult transactionResult) {
		dismissDialog();
		//statusEditText.setText("");
		dialog = new Dialog(BBPosMainActivity.this);
		dialog.setContentView(R.layout.alert_dialog);
		dialog.setTitle(R.string.transaction_result);
		TextView messageTextView = (TextView)dialog.findViewById(R.id.messageTextView);
		
		if(transactionResult == TransactionResult.APPROVED) {
			String message = getString(R.string.transaction_approved) + "\n"
					+ getString(R.string.amount) + ": $" + amount + "\n";
			if(!cashbackAmount.equals("")) {
				message += getString(R.string.cashback_amount) + ": $" + cashbackAmount;
			}
			messageTextView.setText(message);
		} else if(transactionResult == TransactionResult.TERMINATED) {
			messageTextView.setText(getString(R.string.transaction_terminated));
		} else if(transactionResult == TransactionResult.DECLINED) {
			messageTextView.setText(getString(R.string.transaction_declined));
		} else if(transactionResult == TransactionResult.CANCEL) {
			messageTextView.setText(getString(R.string.transaction_cancel));
		} else if(transactionResult == TransactionResult.CAPK_FAIL) {
			messageTextView.setText(getString(R.string.transaction_capk_fail));
		} else if(transactionResult == TransactionResult.NOT_ICC) {
			messageTextView.setText(getString(R.string.transaction_not_icc));
		} else if(transactionResult == TransactionResult.CARD_BLOCKED) {
			messageTextView.setText(getString(R.string.transaction_card_blocked));
		} else if(transactionResult == TransactionResult.DEVICE_ERROR) {
			messageTextView.setText(getString(R.string.transaction_device_error));
		} else if(transactionResult == TransactionResult.CARD_NOT_SUPPORTED) {
			messageTextView.setText(getString(R.string.card_not_supported));
		} else if(transactionResult == TransactionResult.MISSING_MANDATORY_DATA) {
			messageTextView.setText(getString(R.string.missing_mandatory_data));
		} else if(transactionResult == TransactionResult.NO_EMV_APPS) {
			messageTextView.setText(getString(R.string.no_emv_apps));
		} else if(transactionResult == TransactionResult.INVALID_ICC_DATA) {
			messageTextView.setText(getString(R.string.invalid_icc_data));
		} else if(transactionResult == TransactionResult.CONDITION_NOT_SATISFIED) {
			messageTextView.setText(getString(R.string.condition_not_satisfied));
		} else if(transactionResult == TransactionResult.APPLICATION_BLOCKED) {
			messageTextView.setText(getString(R.string.application_blocked));
		} else if(transactionResult == TransactionResult.ICC_CARD_REMOVED) {
			messageTextView.setText(getString(R.string.icc_card_removed));
		}
		
		dialog.findViewById(R.id.confirmButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissDialog();
			}
		});
		
		dialog.show();
		
		amount = "";
		cashbackAmount = "";
		amountEditText.setText("");
	}
	
	@Override
	public void onReturnTransactionResult(TransactionResult transactionResult, Hashtable<String, String> data) {
		dismissDialog();
		//statusEditText.setText("");
		dialog = new Dialog(BBPosMainActivity.this);
		dialog.setContentView(R.layout.alert_dialog);
		dialog.setTitle(R.string.transaction_result);
		TextView messageTextView = (TextView)dialog.findViewById(R.id.messageTextView);
		
		String message = "";
		if(transactionResult == TransactionResult.APPROVED) {
			message = getString(R.string.transaction_approved) + "\n"
					+ getString(R.string.amount) + ": $" + amount + "\n";
			if(!cashbackAmount.equals("")) {
				message += getString(R.string.cashback_amount) + ": $" + cashbackAmount;
			}
		} else if(transactionResult == TransactionResult.TERMINATED) {
			message = getString(R.string.transaction_terminated);
		} else if(transactionResult == TransactionResult.DECLINED) {
			message = getString(R.string.transaction_declined);
		} else if(transactionResult == TransactionResult.CANCEL) {
			message = getString(R.string.transaction_cancel);
		} else if(transactionResult == TransactionResult.CAPK_FAIL) {
			message = getString(R.string.transaction_capk_fail);
		} else if(transactionResult == TransactionResult.NOT_ICC) {
			message = getString(R.string.transaction_not_icc);
		} else if(transactionResult == TransactionResult.CARD_BLOCKED) {
			message = getString(R.string.transaction_card_blocked);
		} else if(transactionResult == TransactionResult.DEVICE_ERROR) {
			message = getString(R.string.transaction_device_error);
		} else if(transactionResult == TransactionResult.CARD_NOT_SUPPORTED) {
			message = getString(R.string.card_not_supported);
		} else if(transactionResult == TransactionResult.MISSING_MANDATORY_DATA) {
			message = getString(R.string.missing_mandatory_data);
		} else if(transactionResult == TransactionResult.NO_EMV_APPS) {
			message = getString(R.string.no_emv_apps);
		} else if(transactionResult == TransactionResult.INVALID_ICC_DATA) {
			message = getString(R.string.invalid_icc_data);
		} else if(transactionResult == TransactionResult.CONDITION_NOT_SATISFIED) {
			message = getString(R.string.condition_not_satisfied);
		} else if(transactionResult == TransactionResult.APPLICATION_BLOCKED) {
			message = getString(R.string.application_blocked);
		} else if(transactionResult == TransactionResult.ICC_CARD_REMOVED) {
			message = getString(R.string.icc_card_removed);
		}
		
		if(data.get("receiptData") != null) {
			message += "\n" + getString(R.string.receipt_data) + "" + data.get("receiptData");
		}
		
		messageTextView.setText(message);
		
		dialog.findViewById(R.id.confirmButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissDialog();
			}
		});
		
		dialog.show();
		
		amount = "";
		cashbackAmount = "";
		amountEditText.setText("");
	}
	
	@Override
	public void onReturnBatchData(String tlv) {
		dismissDialog();
		String content = getString(R.string.batch_data);
		content += tlv;
		statusEditText.setText(content);
	}
	
	@Override
	public void onReturnTransactionLog(String tlv) {
		dismissDialog();
		String content = getString(R.string.transaction_log);
		content += tlv;
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
	public void onReturnPowerOnIccResult(boolean isSuccess, String ksn, String atr, int atrLength) {
	}
	
	@Override
	public void onReturnPowerOffIccResult(boolean isSuccess) {
	}
	
	@Override
	public void onReturnApduResult(boolean isSuccess, String apdu, int apduLength) {
	}
	
	@Override
	public void onReturnApduResultWithPkcs7Padding(boolean isSuccess, String apdu) {
	}
	
	@Override
	public void onReturnViposExchangeApduResult(String apdu) {
		statusEditText.setText(getString(R.string.apdu_result) + " " + apdu);
		++apduIndex;
		if(apduIndex < apdus.length) {
			emvSwipeController.viposExchangeApdu(apdus[apduIndex]);
		} else {
			emvSwipeController.sendOnlineProcessResult("8A023030");
		}
	}
	//setRequestedOrientation
	@Override
	public void onReturnViposBatchExchangeApduResult(Hashtable<Integer, String> data) {
	}
	
	@Override
	public void onReturnEmvCardBalance(boolean isSuccess, String tlv) {
		if(isSuccess) {
			statusEditText.setText(getString(R.string.emv_card_balance_result) + tlv);
		} else {
			statusEditText.setText(getString(R.string.emv_card_balance_failed));
		}
	}
	
	@Override
	public void onReturnEmvCardDataResult(boolean isSuccess, String tlv) {
	}
	
	@Override
	public void onReturnEmvCardNumber(String cardNumber) {
		statusEditText.setText(getString(R.string.card_number) + cardNumber);
	}
	
	@Override
	public void onReturnEmvTransactionLog(String[] transactionLogs) {
		String content = getString(R.string.transaction_log) + "\n";
		for(int i = 0; i < transactionLogs.length; ++i) {
			content += (i + 1) + ": " + transactionLogs[i] + "\n";
		}
		statusEditText.setText(content);
	}
	
	@Override
	public void onReturnEmvLoadLog(String[] loadLogs) {
		String content = getString(R.string.load_log) + "\n";
		for(int i = 0; i < loadLogs.length; ++i) {
			content += (i + 1) + ": " + loadLogs[i] + "\n";
		}
		statusEditText.setText(content);
	}
	
	@Override
	public void onReturnKsn(Hashtable<String, String> ksnTable) {
		String pinKsn = ksnTable.get("pinKsn") == null? "" : ksnTable.get("pinKsn");
		String trackKsn = ksnTable.get("trackKsn") == null? "" : ksnTable.get("trackKsn");
		String emvKsn = ksnTable.get("emvKsn") == null? "" : ksnTable.get("emvKsn");
		String uid = ksnTable.get("uid") == null? "" : ksnTable.get("uid");
		String csn = ksnTable.get("csn") == null? "" : ksnTable.get("csn");
		
		String content = "";
		content += getString(R.string.pin_ksn) + pinKsn + "\n";
		content += getString(R.string.track_ksn) + trackKsn + "\n";
		content += getString(R.string.emv_ksn) + emvKsn + "\n";
		content += getString(R.string.uid) + uid + "\n";
		content += getString(R.string.csn) + csn + "\n";
		
		statusEditText.setText(content);
	}
	
	@Override
	public void onReturnUpdateTerminalSettingResult(TerminalSettingStatus terminalSettingStatus) {
	}
	
	@Override
	public void onReturnReadTerminalSettingResult(TerminalSettingStatus terminalSettingStatus, String value){
	}
	
	@Override
	public void onRequestSelectApplication(ArrayList<String> appList) {
		dismissDialog();
		
		dialog = new Dialog(BBPosMainActivity.this);
		dialog.setContentView(R.layout.application_dialog);
		dialog.setTitle(R.string.please_select_app);
		
		String[] appNameList = new String[appList.size()];
		for(int i = 0; i < appNameList.length; ++i) {
			appNameList[i] = appList.get(i);
		}
		
		appListView = (ListView)dialog.findViewById(R.id.appList);
		appListView.setAdapter(new ArrayAdapter<String>(BBPosMainActivity.this, android.R.layout.simple_list_item_1, appNameList));
		appListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				emvSwipeController.selectApplication(position);
				dismissDialog();
			}
			
		});
		
		dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				emvSwipeController.cancelSelectApplication();
				dismissDialog();
			}
		});
		dialog.show();
	}
	
	@Override
	public void onRequestSetAmount() {
		isAskingForAmount = true;
		promptForAmount();
	}
	
	@Override
	public void onRequestPinEntry() {
		dismissDialog();
		
		dialog = new Dialog(BBPosMainActivity.this);
		dialog.setContentView(R.layout.pin_dialog);
		dialog.setTitle(getString(R.string.enter_pin));
		
		dialog.findViewById(R.id.confirmButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String pin = ((EditText)dialog.findViewById(R.id.pinEditText)).getText().toString();
				emvSwipeController.sendPinEntryResult(pin);
				dismissDialog();
			}
		});
		
		dialog.findViewById(R.id.bypassButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				emvSwipeController.bypassPinEntry();
				dismissDialog();
			}
		});
		
		dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isPinCanceled = true;
				emvSwipeController.cancelPinEntry();
				dismissDialog();
			}
		});
		
		dialog.show();
	}

	@Override
	public void onRequestVerifyID(String tlv) {
		dismissDialog();
		
		dialog = new Dialog(BBPosMainActivity.this);
		dialog.setContentView(R.layout.verify_id_dialog);
		dialog.setTitle(R.string.verify_id);
		
		String content = "";
		try {
			List<TLV> tlvList = TLVParser.parse(tlv);
			TLV cardholderCertificateTLV = TLVParser.searchTLV(tlvList, "9F61");
			TLV certificateTypeTLV = TLVParser.searchTLV(tlvList, "9F62");
			
			if(cardholderCertificateTLV != null) {
				content += "\n" + getString(R.string.cardholder_certificate) + " " + new String(cardholderCertificateTLV.value);
			}
			
			if(certificateTypeTLV != null){
				content += "\n" + getString(R.string.certificate_type) + " " + certificateTypeTLV.value;
			}
		} catch(Exception e) {
		}
		
		((TextView)dialog.findViewById(R.id.messageTextView)).setText(content);
		
		dialog.findViewById(R.id.successButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				emvSwipeController.sendVerifyIDResult(true);
				dismissDialog();
			}
		});
		
		dialog.findViewById(R.id.failButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				emvSwipeController.sendVerifyIDResult(true);
				dismissDialog();
			}
		});
		
		dialog.show();
	}
	
	@Override
	public void onRequestCheckServerConnectivity() {
		dismissDialog();
		dialog = new Dialog(BBPosMainActivity.this);
		dialog.setContentView(R.layout.alert_dialog);
		dialog.setTitle(R.string.request_check_online_connectivity);
		
		((TextView)dialog.findViewById(R.id.messageTextView)).setText(R.string.replied_connected);
		
		dialog.findViewById(R.id.confirmButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				emvSwipeController.sendServerConnectivity(true);
				dismissDialog();
			}
		});
		
		dialog.show();
	}
	
	@Override
	public void onRequestOnlineProcess(String tlv) {
		dismissDialog();
		
		if(!isPinCanceled) {
			if(fidSpinner.getSelectedItem().equals("FID46")) {
				apduIndex = 0;
				emvSwipeController.viposExchangeApdu(apdus[apduIndex]);
				
				String content = "";
				Hashtable<String, String> decodeData = EmvSwipeController.decodeTlv(tlv);
				Object[] keys = decodeData.keySet().toArray();
				Arrays.sort(keys);
				for(Object key : keys) {
					if(((String)key).matches(".*[a-z].*")) {
						continue;
					}

					String value = decodeData.get(key);
					content += key + ": " + value + "\n";
				}
			} else {
				dialog = new Dialog(BBPosMainActivity.this);
				dialog.setContentView(R.layout.online_response_dialog);
				dialog.setTitle(R.string.select_online_response);
				
				String[] onlineResponses = new String[] {
	    				"8A023030",
	    				"8A023030910A8600965D36A1716E3030",
	    				"8A023030910A6F2BA37E41110DC93030"
	    		};
				((Spinner)dialog.findViewById(R.id.onlineResponseSpinner)).setAdapter(new ArrayAdapter<String>(BBPosMainActivity.this, android.R.layout.simple_spinner_item, onlineResponses));
				
				dialog.findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String response = (String)((Spinner)dialog.findViewById(R.id.onlineResponseSpinner)).getSelectedItem();
						emvSwipeController.sendOnlineProcessResult(response);
						statusEditText.setText(getString(R.string.replied) + " " + response);
						
						dismissDialog();
					}
				});
				
				dialog.show();
				statusEditText.setText(getString(R.string.request_online_process) + ": " + tlv);
			}
		} else {
			dialog = new Dialog(BBPosMainActivity.this);
			dialog.setContentView(R.layout.alert_dialog);
			dialog.setTitle(R.string.request_online_process);
			
			((TextView)dialog.findViewById(R.id.messageTextView)).setText(R.string.replied_failed);
			
			dialog.findViewById(R.id.confirmButton).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					emvSwipeController.sendOnlineProcessResult(null);
				}
			});
			dialog.show();
		}
	}
	
	@Override
	public void onRequestTerminalTime() {
		dismissDialog();
		String terminalTime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
		emvSwipeController.sendTerminalTime(terminalTime);
		statusEditText.setText(getString(R.string.request_terminal_time) + " " + terminalTime);
	}
	
	@Override
	public void onRequestDisplayText(DisplayText displayText) {
		dismissDialog();
		
		String msg = "";
		if(displayText == DisplayText.AMOUNT) {
			msg = getString(R.string.amount);
		} else if(displayText == DisplayText.AMOUNT_OK_OR_NOT) {
			msg = getString(R.string.amount_ok);
		} else if(displayText == DisplayText.APPROVED) {
			msg = getString(R.string.approved);
		} else if(displayText == DisplayText.CALL_YOUR_BANK) {
			msg = getString(R.string.call_your_bank);
		} else if(displayText == DisplayText.CANCEL_OR_ENTER) {
			msg = getString(R.string.cancel_or_enter);
		} else if(displayText == DisplayText.CARD_ERROR) {
			msg = getString(R.string.card_error);
		} else if(displayText == DisplayText.DECLINED) {
			msg = getString(R.string.decline);
		} else if(displayText == DisplayText.ENTER_AMOUNT) {
			msg = getString(R.string.enter_amount);
		} else if(displayText == DisplayText.ENTER_PIN) {
			msg = getString(R.string.enter_pin);
		} else if(displayText == DisplayText.INCORRECT_PIN) {
			msg = getString(R.string.incorrect_pin);
		} else if(displayText == DisplayText.INSERT_CARD) {
			msg = getString(R.string.insert_card);
		} else if(displayText == DisplayText.NOT_ACCEPTED) {
			msg = getString(R.string.not_accepted);
		} else if(displayText == DisplayText.PIN_OK) {
			msg = getString(R.string.pin_ok);
		} else if(displayText == DisplayText.PLEASE_WAIT) {
			msg = getString(R.string.wait); 
		} else if(displayText == DisplayText.PROCESSING_ERROR) {
			msg = getString(R.string.processing_error);
		} else if(displayText == DisplayText.REMOVE_CARD) {
			msg = getString(R.string.remove_card);
		} else if(displayText == DisplayText.USE_CHIP_READER) {
			msg = getString(R.string.use_chip_reader);
		} else if(displayText == DisplayText.USE_MAG_STRIPE) {
			msg = getString(R.string.use_mag_stripe);
		} else if(displayText == DisplayText.TRY_AGAIN) {
			msg = getString(R.string.try_again);
		} else if(displayText == DisplayText.REFER_TO_YOUR_PAYMENT_DEVICE) {
			msg = getString(R.string.refer_payment_device);
		} else if(displayText == DisplayText.TRANSACTION_TERMINATED) {
			msg = getString(R.string.transaction_terminated);
		} else if(displayText == DisplayText.TRY_ANOTHER_INTERFACE) {
			msg = getString(R.string.try_another_interface);
		} else if(displayText == DisplayText.ONLINE_REQUIRED) {
			msg = getString(R.string.online_required);
		} else if(displayText == DisplayText.PROCESSING) {
			msg = getString(R.string.processing);
		} else if(displayText == DisplayText.WELCOME) {
			msg = getString(R.string.welcome);
		} else if(displayText == DisplayText.PRESENT_ONLY_ONE_CARD) {
			msg = getString(R.string.present_one_card);
		} else if(displayText == DisplayText.CAPK_LOADING_FAILED) {
			msg = getString(R.string.capk_failed);
		} else if(displayText == DisplayText.LAST_PIN_TRY) {
			msg = getString(R.string.last_pin_try);
		} else if(displayText == DisplayText.INSERT_OR_TAP_CARD) {
			msg = getString(R.string.insert_or_tap_card);
		} else if (displayText == DisplayText.SELECT_ACCOUNT) {
			msg = getString(R.string.select_account);
		} else if (displayText == DisplayText.APPROVED_PLEASE_SIGN) {
			msg = getString(R.string.approved_please_sign);
		} else if (displayText == DisplayText.TAP_CARD_AGAIN) {
			msg = getString(R.string.tap_card_again);
		} else if (displayText == DisplayText.AUTHORISING) {
			msg = getString(R.string.authorising);
		} else if (displayText == DisplayText.INSERT_OR_SWIPE_CARD_OR_TAP_ANOTHER_CARD) {
			msg = getString(R.string.insert_or_swipe_card_or_tap_another_card);
		} else if (displayText == DisplayText.INSERT_OR_SWIPE_CARD) {
			msg = getString(R.string.insert_or_swipe_card);
		} else if (displayText == DisplayText.MULTIPLE_CARDS_DETECTED) {
			msg = getString(R.string.multiple_cards_detected);
		}
		
		statusEditText.setText(msg);
	}
	
	@Override
	public void onRequestClearDisplay() {
		dismissDialog();
		statusEditText.setText("");
	}
	
	@Override
	public void onRequestReferProcess(String pan) {
		dismissDialog();
		dialog = new Dialog(BBPosMainActivity.this);
		dialog.setContentView(R.layout.refer_process_dialog);
		dialog.setTitle(getString(R.string.call_your_bank));
		
		dialog.findViewById(R.id.approvedButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				emvSwipeController.sendReferProcessResult(ReferralResult.APPROVED);
			}
		});
		
		dialog.findViewById(R.id.declinedButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				emvSwipeController.sendReferProcessResult(ReferralResult.DECLINED);
			}
		});
		
		dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				emvSwipeController.cancelReferProcess();
			}
		});
	}
	
	@Override
	public void onRequestAdviceProcess(String tlv) {
		dismissDialog();
		statusEditText.setText(getString(R.string.advice_process));
	}
	
	@Override
	public void onRequestFinalConfirm() {
		dismissDialog();
		if(!isPinCanceled) {
			dialog = new Dialog(BBPosMainActivity.this);
			dialog.setContentView(R.layout.confirm_dialog);
			dialog.setTitle(getString(R.string.confirm_amount));
			
			String message = getString(R.string.amount) + ": $" + amount;
			if(!cashbackAmount.equals("")) {
				message += "\n" + getString(R.string.cashback_amount) + ": $" + cashbackAmount;
			}
			
			((TextView)dialog.findViewById(R.id.messageTextView)).setText(message);
			
			dialog.findViewById(R.id.confirmButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					emvSwipeController.sendFinalConfirmResult(true);
					dialog.dismiss();
				}
			});
			
			dialog.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					emvSwipeController.sendFinalConfirmResult(false);
					dialog.dismiss();
				}
			});
			
			dialog.show();
		} else {
			emvSwipeController.sendFinalConfirmResult(false);
		}
	}
	
	@Override
	public void onAutoConfigProgressUpdate(double percentage) {
		if(progressDialog != null) {
			progressDialog.setProgress((int)percentage);
		}
	}
	
	@Override
	public void onAutoConfigCompleted(boolean isDefaultSettings, String autoConfigSettings) {
		if(progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		
		String outputDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bbpos.emvswipe.ui/";
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
		statusEditText.setText(content);
	}
	
	@Override
	public void onAutoConfigError(AutoConfigError autoConfigError) {
		if(progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		
		if(autoConfigError == AutoConfigError.PHONE_NOT_SUPPORTED) {
			statusEditText.setText(getString(R.string.auto_config_error_phone_not_supported));
		} else if(autoConfigError == AutoConfigError.INTERRUPTED) {
			statusEditText.setText(getString(R.string.auto_config_error_interrupted));
		}
	}

	@Override
	public void onBatteryLow(BatteryStatus batteryStatus) {
		if(batteryStatus == BatteryStatus.LOW) {
			statusEditText.setText(getString(R.string.battery_low));
		} else if(batteryStatus == BatteryStatus.CRITICALLY_LOW) {
			statusEditText.setText(getString(R.string.battery_critically_low));
		}
	}
	
	@Override
	public void onNoDeviceDetected() {
		dismissDialog();
		statusEditText.setText(getString(R.string.no_device_detected));
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(currentActivity, getString(R.string.no_device_detected), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void onDevicePlugged() {
		dismissDialog();
		statusEditText.setText(getString(R.string.device_plugged));
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(currentActivity, getString(R.string.device_plugged), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void onDeviceUnplugged() {
		dismissDialog();
		statusEditText.setText(getString(R.string.device_unplugged));
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(currentActivity, getString(R.string.device_unplugged), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void onDeviceHere(boolean isHere) {
	}

	@Override
	public void onError(Error errorState, String errorMessage) {
		dismissDialog();
		if(progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		String content = "";
		if(errorState == Error.CMD_NOT_AVAILABLE) {
			content += getString(R.string.command_not_available);
		} else if(errorState == Error.TIMEOUT) {
			content += getString(R.string.device_no_response);
		} else if(errorState == Error.DEVICE_RESET) {
			content += getString(R.string.device_reset);
		} else if(errorState == Error.UNKNOWN) {
			content += getString(R.string.unknown_error);
		} else if(errorState == Error.DEVICE_BUSY) {
			content += getString(R.string.device_busy);
		} else if(errorState == Error.INPUT_OUT_OF_RANGE) {
			content += getString(R.string.out_of_range);
		} else if(errorState == Error.INPUT_INVALID_FORMAT) {
			content += getString(R.string.invalid_format);
		} else if(errorState == Error.INPUT_ZERO_VALUES) {
			content += getString(R.string.zero_values);
		} else if(errorState == Error.INPUT_INVALID) {
			content += getString(R.string.input_invalid);
		} else if(errorState == Error.CASHBACK_NOT_SUPPORTED) {
			content += getString(R.string.cashback_not_supported);
		} else if(errorState == Error.CRC_ERROR) {
			content += getString(R.string.crc_error);
		} else if(errorState == Error.COMM_ERROR) {
			content += getString(R.string.comm_error);
		} else if(errorState == Error.VOLUME_WARNING_NOT_ACCEPTED) {
			content += getString(R.string.volume_warning_not_accepted);
		} else if(errorState == Error.FAIL_TO_START_AUDIO) {
			content += getString(R.string.fail_to_start_audio);
		} else if(errorState == Error.COMM_LINK_UNINITIALIZED) {
			content += getString(R.string.communication_link_uninitialized);
		} else if(errorState == Error.INVALID_FUNCTION_IN_CURRENT_MODE) {
			content += getString(R.string.invalid_function_in_current_mode);
		} else if(errorState == Error.USB_DEVICE_NOT_FOUND) {
			content += getString(R.string.usb_device_not_found);
		} else if(errorState == Error.USB_DEVICE_PERMISSION_DENIED) {
			content += getString(R.string.usb_device_permission_denied);
		}
		if(!errorMessage.equals("")) {
			content += "\n" + getString(R.string.error_message) + errorMessage;
		}
		statusEditText.setText(content);
	}
	
	@Override
	public void onPowerDown() {
		//statusEditText.setText(getString(R.string.device_off));
	}

    class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			statusEditText.setText("");
			
			if(v == checkCardButton) {
				promptForCheckCard();
			} else if(v == menuBtn) {
				initPopWindow();
				popupwindow.showAsDropDown(v, 0, 5);
//				menuBtnSelected();
			}
			
		}
    }
    
    //by Lu
    public void closePopWindow() {
    	if (popupwindow != null && popupwindow.isShowing()) {  
            popupwindow.dismiss();  
            popupwindow = null;  
        }  
    }
    //by Lu
    public void initPopWindow() {
    	View customView = getLayoutInflater().inflate(R.layout.popwindow_item_view, null);
    	popupwindow = new PopupWindow(customView, 250, 400);
    	popupwindow.setOutsideTouchable(true);
    	customView.setOnTouchListener(new OnTouchListener() {  
            @Override  
            public boolean onTouch(View v, MotionEvent event) {  
                closePopWindow();
                return false;  
            }  
        });  
    	//by Lu
    	//menu item's init
    	menuStartConnection = (TextView) customView.findViewById(R.id.menu_start_connection);
        menuStopConnection = (TextView) customView.findViewById(R.id.menu_stop_connection);
        menuGetDeivceInfo = (TextView) customView.findViewById(R.id.menu_get_deivce_info);
        menuGetKsn = (TextView) customView.findViewById(R.id.menu_get_ksn);
        menuCancelCheckCard = (TextView) customView.findViewById(R.id.menu_cancel_check_card);
        menuAutoConfig = (TextView) customView.findViewById(R.id.menu_auto_config);
        menuEncryptData = (TextView) customView.findViewById(R.id.menu_encrypt_data);
        iccActivity = (TextView) customView.findViewById(R.id.icc_activity);
        nfcActivity = (TextView) customView.findViewById(R.id.nfc_activity);
        capkActivity = (TextView) customView.findViewById(R.id.capk_activity);
        integrityCheck = (TextView) customView.findViewById(R.id.integrity_check);
        //menu item's click listener
        menuItemClickListener = new MenuItemClickListener();
        menuStartConnection.setOnClickListener(menuItemClickListener);
        menuStopConnection.setOnClickListener(menuItemClickListener);
        menuGetDeivceInfo.setOnClickListener(menuItemClickListener);
        menuGetKsn.setOnClickListener(menuItemClickListener);
        menuCancelCheckCard.setOnClickListener(menuItemClickListener);
        menuAutoConfig.setOnClickListener(menuItemClickListener);
        menuEncryptData.setOnClickListener(menuItemClickListener);
        iccActivity.setOnClickListener(menuItemClickListener);
        nfcActivity.setOnClickListener(menuItemClickListener);
        capkActivity.setOnClickListener(menuItemClickListener);
        integrityCheck.setOnClickListener(menuItemClickListener);
    }

	@Override
	public void onUsbConnected() {
		statusEditText.setText(getString(R.string.usb_connected));
	}

	@Override
	public void onUsbDisconnected() {
		statusEditText.setText(getString(R.string.usb_disconnected));
	}

	@Override
	public void onReturnNfcDataExchangeResult(boolean isSuccess, Hashtable<String, String> data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReturnNfcDetectCardResult(Hashtable<String, Object> data) {
		// TODO Auto-generated method stub
		
	}
	
	//by Lu : menu btn's listener
	class MenuItemClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			menuBtnSelected(v);
		}
	}
	
	//by Lu : menu btn
    public boolean menuBtnSelected(View v) {
    	if(v.getId() == R.id.menu_start_connection) {
    		promptForConnection();
    	} else if(v.getId() == R.id.menu_stop_connection) {
    		stopConnection();
    	} else if(v.getId() == R.id.menu_get_deivce_info) {
    		statusEditText.setText(R.string.getting_info);
    		emvSwipeController.getDeviceInfo();
    	} else if(v.getId() == R.id.menu_cancel_check_card) {
    		statusEditText.setText("");
    		emvSwipeController.cancelCheckCard();
    	} else if(v.getId() == R.id.menu_get_ksn) {
    		statusEditText.setText(R.string.getting_ksn);
    		emvSwipeController.getKsn();
    	} else if(v.getId() == R.id.menu_auto_config) {
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
					emvSwipeController.cancelAutoConfig();
				}
			});
    		progressDialog.show();
    		emvSwipeController.startAutoConfig();
    	} else if(v.getId() == R.id.menu_encrypt_data) {
    		statusEditText.setText(R.string.encrypting_data);
    		String encWorkingKey = "12042B145F8516D74F0B96AAA5A8B548";
			String workingKeyKcv = "C257CC0FD286CDC4";
			
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
    		
    		emvSwipeController.encryptDataWithSettings(data);
    	} else if(v.getId() == R.id.icc_activity) {
    		isSwitchingActivity = true;
    		finish();
    		Intent intent = new Intent(this, IccActivity.class);
    		startActivity(intent);
    	} else if(v.getId() == R.id.nfc_activity) {
    		isSwitchingActivity = true;
    		finish();
    		Intent intent = new Intent(this, NfcActivity.class);
    		startActivity(intent);
    	} else if(v.getId() == R.id.capk_activity) {
    		isSwitchingActivity = true;
    		finish();
    		Intent intent = new Intent(this, CAPKActivity.class);
    		startActivity(intent);
    	}
    	closePopWindow();
    	return true;
    }
}
