package com.thtfit.pos.emvswipe;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import com.bbpos.emvswipe.CAPK;
import com.bbpos.emvswipe.EmvSwipeController.AutoConfigError;
import com.bbpos.emvswipe.EmvSwipeController.BatteryStatus;
import com.bbpos.emvswipe.EmvSwipeController.CheckCardMode;
import com.bbpos.emvswipe.EmvSwipeController.CheckCardResult;
import com.bbpos.emvswipe.EmvSwipeController.ConnectionMode;
import com.bbpos.emvswipe.EmvSwipeController.DisplayText;
import com.bbpos.emvswipe.EmvSwipeController.Error;
import com.bbpos.emvswipe.EmvSwipeController.NfcDataExchangeStatus;
import com.bbpos.emvswipe.EmvSwipeController.ReadNdefRecord;
import com.bbpos.emvswipe.EmvSwipeController.StartEmvResult;
import com.bbpos.emvswipe.EmvSwipeController.TerminalSettingStatus;
import com.bbpos.emvswipe.EmvSwipeController.TransactionResult;
import com.thtfit.pos.R;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NfcActivity extends EMVBaseActivity {
	
	private Button startNfcDetectionButton;
	private Button stopNfcDetectionButton;
	private Button nfcDataExchangeWriteButton;
	private Button nfcDataExchangeRead1StButton;
	private Button nfcDataExchangeReadNextButton;	
	private EditText dataExchangeEditText;
	private EditText statusEditText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.nfc_activity_main);
        
        ((TextView)findViewById(R.id.modelTextView)).setText(Build.MANUFACTURER.toUpperCase(Locale.ENGLISH) + " - " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")");
        
        startNfcDetectionButton = (Button) findViewById(R.id.startNfcDetectionButton);
		stopNfcDetectionButton = (Button) findViewById(R.id.stopNfcDetectionButton);
		nfcDataExchangeWriteButton = (Button) findViewById(R.id.nfcDataExchangeWriteButton);
		nfcDataExchangeRead1StButton = (Button) findViewById(R.id.nfcDataExchangeRead1StButton);
		nfcDataExchangeReadNextButton = (Button) findViewById(R.id.nfcDataExchangeReadNextButton);
		dataExchangeEditText = (EditText)findViewById(R.id.dataExchangeEditText);
        statusEditText = (EditText)findViewById(R.id.statusEditText);
        
        statusEditText.setMovementMethod(new ScrollingMovementMethod());
        
        MyOnClickListener myOnClickListener = new MyOnClickListener();
        startNfcDetectionButton.setOnClickListener(myOnClickListener);
        stopNfcDetectionButton.setOnClickListener(myOnClickListener);
        nfcDataExchangeWriteButton.setOnClickListener(myOnClickListener);
        nfcDataExchangeRead1StButton.setOnClickListener(myOnClickListener);
        nfcDataExchangeReadNextButton.setOnClickListener(myOnClickListener);
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
    	} else if(emvSwipeController != null){
    		if (emvSwipeController.getConnectionMode() == ConnectionMode.AUDIO) {
    			emvSwipeController.stopAudio();
    		} else if (emvSwipeController.getConnectionMode() == ConnectionMode.USB)
    			emvSwipeController.stopUsb();
    		emvSwipeController.resetEmvSwipeController();
    		emvSwipeController = null;
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.nfc_activity_main, menu);
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
    	} else if(item.getItemId() == R.id.menu_get_ksn) {
    		statusEditText.setText(R.string.getting_ksn);
    		emvSwipeController.getKsn();
    	} else if(item.getItemId() == R.id.emv_activity) {
    		isSwitchingActivity = true;
    		finish();
    		Intent intent = new Intent(this, BBPosMainActivity.class);
    		startActivity(intent);
    	} else if(item.getItemId() == R.id.icc_activity) {
    		isSwitchingActivity = true;
    		finish();
    		Intent intent = new Intent(this, IccActivity.class);
    		startActivity(intent);
    	} else if(item.getItemId() == R.id.capk_activity) {
    		isSwitchingActivity = true;
    		finish();
    		Intent intent = new Intent(this, CAPKActivity.class);
    		startActivity(intent);
    	}
    	return true;
    }
    
    protected void setStatus(String message) {
		String tmp = message + "\n" + statusEditText.getText();
		int maxLength = 10000;
		if (tmp.length() >= maxLength) {
			int index = tmp.indexOf("\n", maxLength);
			if (index >= maxLength) {
				statusEditText.setText(tmp.substring(0, index));
				return;
			}
		}
		statusEditText.setText(tmp);
	}
    
	private static String toHexString(byte[] b) {
		String result = "";
		for (int i=0; i < b.length; i++) {
			result += Integer.toString( ( b[i] & 0xFF ) + 0x100, 16).substring( 1 );
		}
		return result;
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
    
	@Override
	public void onWaitingForCard(CheckCardMode checkCardMode) {
	}
	
	@Override
	public void onBatchDataDetected() {
	}

	@Override
	public void onOnlineProcessDataDetected() {
	}

	@Override
	public void onReversalDataDetected() {
	}

	@Override
	public void onReturnCheckCardResult(CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {
	}
	
	@Override
	public void onReturnCancelCheckCardResult(boolean isSuccess) {
	}
	
	@Override
	public void onReturnEncryptPinResult(Hashtable<String, String> data) {
	}
	
	@Override
	public void onReturnEncryptDataResult(boolean isSuccess, Hashtable<String, String> data) {
	}
	
	@Override
	public void onReturnStartEmvResult(StartEmvResult result, String ksn) {
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
		String vendorID = deviceInfoData.get("vendorID") == null? "" : deviceInfoData.get("vendorID");
		String productID = deviceInfoData.get("productID") == null? "" : deviceInfoData.get("productID");
		String terminalSettingVersion = deviceInfoData.get("terminalSettingVersion") == null? "" : deviceInfoData.get("terminalSettingVersion");
		String deviceSettingVersion = deviceInfoData.get("deviceSettingVersion") == null? "" : deviceInfoData.get("deviceSettingVersion");
		String serialNumber = deviceInfoData.get("serialNumber") == null? "" : deviceInfoData.get("serialNumber");
		String modelName = deviceInfoData.get("modelName") == null? "" : deviceInfoData.get("modelName");
		
		String content = "";
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
		content += getString(R.string.format_id) + formatID + "\n";
		content += getString(R.string.vendor_id) + vendorID + "\n";
		content += getString(R.string.product_id) + productID + "\n";
		content += getString(R.string.terminal_setting_version) + terminalSettingVersion + "\n";
		content += getString(R.string.device_setting_version) + deviceSettingVersion + "\n";
		content += getString(R.string.serial_number) + serialNumber + "\n";
		content += getString(R.string.model_name) + modelName + "\n";
		
		statusEditText.setText(content);
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
	public void onReturnTransactionResult(TransactionResult transResult) {
	}

	@Override
	public void onReturnTransactionResult(TransactionResult transResult, Hashtable<String, String> data) {
	}
	
	@Override
	public void onReturnBatchData(String tlv) {
	}
	
	@Override
	public void onReturnTransactionLog(String tlv) {
	}
	
	@Override
	public void onReturnReversalData(String tlv) {
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
	}

	@Override
	public void onReturnViposBatchExchangeApduResult(Hashtable<Integer, String> data) {
	}
	
	@Override
	public void onReturnEmvCardBalance(boolean isSuccess, String tlv) {
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
	}
	
	@Override
	public void onReturnEmvLoadLog(String[] loadLogs) {
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
	}
	
	@Override
	public void onRequestSetAmount() {
	}
	
	@Override
	public void onRequestPinEntry() {
	}

	@Override
	public void onRequestVerifyID(String tlv) {
	}
	
	@Override
	public void onRequestCheckServerConnectivity() {
	}
	
	@Override
	public void onRequestOnlineProcess(String tlv) {
	}
	
	@Override
	public void onRequestTerminalTime() {
	}
	
	@Override
	public void onRequestDisplayText(DisplayText displayText) {
	}
	
	@Override
	public void onRequestClearDisplay() {
	}
	
	@Override
	public void onRequestReferProcess(String pan) {
	}
					
	@Override
	public void onRequestAdviceProcess(String tlv) {
	}
	
	@Override
	public void onRequestFinalConfirm() {
	}
	
	@Override
	public void onAutoConfigProgressUpdate(double percentage) {
	}
	
	@Override
	public void onAutoConfigCompleted(boolean isDefaultSettings, String autoConfigSettings) {
	}
	
	@Override
	public void onAutoConfigError(AutoConfigError autoConfigError) {
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
		statusEditText.setText(getString(R.string.no_device_detected));
	}
	
	@Override
	public void onDevicePlugged() {
		statusEditText.setText(getString(R.string.device_plugged));
	}
	
	@Override
	public void onDeviceUnplugged() {
		statusEditText.setText(getString(R.string.device_unplugged));
	}
	
	@Override
	public void onDeviceHere(boolean isHere) {
	}

	@Override
	public void onError(Error errorState, String errorMessage) {
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
	}
	
	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			if(v == startNfcDetectionButton) {
				Hashtable<String, Object> data = new Hashtable<String, Object>();
				data.put("nfcCardDetectionTimeout", "15");
				emvSwipeController.startNfcDetection(data);
			} else if(v == stopNfcDetectionButton) {
				Hashtable<String, Object> data = new Hashtable<String, Object>();
				data.put("nfcCardRemovalTimeout", "15");
				emvSwipeController.stopNfcDetection(data);
			} else if(v == nfcDataExchangeWriteButton) {
				String input = dataExchangeEditText.getText().toString();
				Hashtable<String, Object> data = new Hashtable<String, Object>();
				data.put("writeNdefRecord", input);
				emvSwipeController.nfcDataExchange(data);
			} else if(v == nfcDataExchangeRead1StButton) {
				Hashtable<String, Object> data = new Hashtable<String, Object>();
				data.put("readNdefRecord", ReadNdefRecord.READ_1ST);
				emvSwipeController.nfcDataExchange(data);
			} else if(v == nfcDataExchangeReadNextButton) {
				Hashtable<String, Object> data = new Hashtable<String, Object>();
				data.put("readNdefRecord", ReadNdefRecord.READ_NEXT);
				emvSwipeController.nfcDataExchange(data);
			}
		}
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
		if (isSuccess) {
			String text = getString(R.string.nfc_data_exchange_success);
			text += "\n" + getString(R.string.ndef_record) + data.get("ndefRecord");
			setStatus(text);
		} else {
			String text = getString(R.string.nfc_data_exchange_fail);
			text += "\n" + getString(R.string.error_message) + data.get("errorMessage");
			setStatus(text);
		}
	}

	@Override
	public void onReturnNfcDetectCardResult(Hashtable<String, Object> data) {
		String text = "";
		text += getString(R.string.nfc_card_detection_result) + data.get("nfcDetectCardResult");
		text += "\n" + getString(R.string.nfc_tag_information) + data.get("nfcTagInfo");
		text += "\n" + getString(R.string.nfc_card_uid) + data.get("nfcCardUID");
		if (data.containsKey("errorMessage")) {
			text += "\n" + getString(R.string.error_message) + data.get("errorMessage");
		}
		setStatus(text);
	}
}
