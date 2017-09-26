package com.thtfit.pos.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bbpos.emvswipe.CAPK;
import com.bbpos.emvswipe.EmvSwipeController;
import com.bbpos.emvswipe.EmvSwipeController.AutoConfigError;
import com.bbpos.emvswipe.EmvSwipeController.BatteryStatus;
import com.bbpos.emvswipe.EmvSwipeController.CheckCardMode;
import com.bbpos.emvswipe.EmvSwipeController.CheckCardResult;
import com.bbpos.emvswipe.EmvSwipeController.ConnectionMode;
import com.bbpos.emvswipe.EmvSwipeController.DisplayText;
import com.bbpos.emvswipe.EmvSwipeController.EmvSwipeControllerListener;
import com.bbpos.emvswipe.EmvSwipeController.Error;
import com.bbpos.emvswipe.EmvSwipeController.NfcDataExchangeStatus;
import com.bbpos.emvswipe.EmvSwipeController.StartEmvResult;
import com.bbpos.emvswipe.EmvSwipeController.TerminalSettingStatus;
import com.bbpos.emvswipe.EmvSwipeController.TransactionResult;
import com.thtfit.pos.R;

import android.app.Dialog;
import android.widget.ListView;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

abstract class EMVBaseActivity extends FragmentActivity {
	protected static EmvSwipeController emvSwipeController;
	protected static EMVBaseActivity currentActivity;
	protected static boolean isSwitchingActivity;
	
	protected static Dialog dialog;
	
	protected static String webAutoConfigString = "";
	protected static boolean isLoadedLocalSettingFile = false;
	protected static boolean isLoadedWebServiceAutoConfig = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(emvSwipeController == null) {
			emvSwipeController = EmvSwipeController.getInstance(this, new MyEmvSwipeControllerListener());
			emvSwipeController.setDetectDeviceChange(true);
		}
		
		try {
			String filename = "settings.txt";
			String inputDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bbpos.emvswipe.ui/";
			
			FileInputStream fis = new FileInputStream(inputDirectory + filename);
			byte[] temp = new byte[fis.available()];
			fis.read(temp);
			fis.close();
			
			isLoadedLocalSettingFile = true;
			emvSwipeController.setAutoConfig(new String(temp));
			
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(currentActivity, getString(R.string.setting_config), Toast.LENGTH_LONG).show();
				}
			});
		} catch(Exception e) {
		}
		
		//Create instance for AsyncCallWS
        AsyncCallWS task = new AsyncCallWS();
        //Call execute 
        task.execute();
	}
	
	public void dismissDialog() {
    	if(dialog != null) {
    		dialog.dismiss();
    		dialog = null;
    	}
    }
	
	public void promptForConnection() {
		dismissDialog();
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.connection_dialog);
		dialog.setTitle(getString(R.string.connection));
		
		String[] connections = new String[2];
		connections[0] = "Audio";
		connections[1] = "USB";
		
		ListView listView = (ListView)dialog.findViewById(R.id.connectionList);
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, connections));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dismissDialog();
				if(position == 0) {
					emvSwipeController.startAudio();
				} else if (position == 1) {
					emvSwipeController.startUsb();
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
	
	public void stopConnection() {
		if(emvSwipeController.getConnectionMode() == ConnectionMode.AUDIO) {
			emvSwipeController.stopAudio();
		} else if (emvSwipeController.getConnectionMode() == ConnectionMode.USB) {
			emvSwipeController.stopUsb();
		}
	}
	
	private class AsyncCallWS extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			if (isLoadedWebServiceAutoConfig == false) {
//				webAutoConfigString = WebService.invokeGetAutoConfigString(Build.MANUFACTURER.toUpperCase(Locale.US), Build.MODEL.toUpperCase(Locale.US), EmvSwipeController.getApiVersion(), "getAutoConfigString");
				//webAutoConfigString = WebService.invokeGetAutoConfigString("LENOVO", "Z2", EmvSwipeController.getApiVersion(), "getAutoConfigString");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (isLoadedWebServiceAutoConfig == false) {
				isLoadedWebServiceAutoConfig = true;
				if (isLoadedLocalSettingFile == false) {
					if (!webAutoConfigString.equalsIgnoreCase("Error occured") && !webAutoConfigString.equalsIgnoreCase("")) {
						emvSwipeController.setAutoConfig(webAutoConfigString);
						
						try {
							String filename = "settings.txt";
							String outputDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bbpos.emvswipe.ui/";

							File directory = new File(outputDirectory);
							if (!directory.isDirectory()) {
								directory.mkdirs();
							}
							FileOutputStream fos = new FileOutputStream(outputDirectory + filename, true);
							fos.write(webAutoConfigString.getBytes());
							fos.flush();
							fos.close();
						} catch (Exception e) {
						}
						
						new Handler().post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(currentActivity, getString(R.string.setting_config_from_web_service), Toast.LENGTH_LONG).show();
							}
						});
					}
				}
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}

	}
	
	public abstract void onWaitingForCard(CheckCardMode checkCardMode);
	public abstract void onBatchDataDetected();
	public abstract void onOnlineProcessDataDetected();
	public abstract void onReversalDataDetected();
	public abstract void onReturnCheckCardResult(CheckCardResult checkCardResult, Hashtable<String, String> decodeData);
	public abstract void onReturnCancelCheckCardResult(boolean isSuccess);
	public abstract void onReturnEncryptPinResult(Hashtable<String, String> data);
	public abstract void onReturnEncryptDataResult(boolean isSuccess, Hashtable<String, String> data);
	public abstract void onReturnStartEmvResult(StartEmvResult result, String ksn);
	public abstract void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData);
	public abstract void onReturnCAPKList(List<CAPK> capkList);
	public abstract void onReturnCAPKDetail(CAPK capk);
	public abstract void onReturnCAPKLocation(String location);
	public abstract void onReturnUpdateCAPKResult(boolean isSuccess);
	public abstract void onReturnEmvReportList(Hashtable<String, String> data);
	public abstract void onReturnEmvReport(String tlv);
	@Deprecated
	public abstract void onReturnTransactionResult(TransactionResult transResult);
	public abstract void onReturnTransactionResult(TransactionResult transResult, Hashtable<String, String> data);
	public abstract void onReturnBatchData(String tlv);
	public abstract void onReturnTransactionLog(String tlv);
	public abstract void onReturnReversalData(String tlv);
	public abstract void onReturnPowerOnIccResult(boolean isSuccess, String ksn, String atr, int atrLength);
	public abstract void onReturnPowerOffIccResult(boolean isSuccess);
	public abstract void onReturnApduResult(boolean isSuccess, String apdu, int apduLength);
	public abstract void onReturnApduResultWithPkcs7Padding(boolean isSuccess, String apdu);
	public abstract void onReturnViposExchangeApduResult(String apdu);
	public abstract void onReturnViposBatchExchangeApduResult(Hashtable<Integer, String> data);
	public abstract void onReturnEmvCardBalance(boolean isSuccess, String tlv);
	public abstract void onReturnEmvCardDataResult(boolean isSuccess, String tlv);
	public abstract void onReturnEmvCardNumber(String cardNumber);
	public abstract void onReturnEmvTransactionLog(String[] transactionLogs);
	public abstract void onReturnEmvLoadLog(String[] loadLogs);
	public abstract void onReturnKsn(Hashtable<String, String> ksntable);
	public abstract void onReturnUpdateTerminalSettingResult(TerminalSettingStatus terminalSettingStatus);
	public abstract void onReturnReadTerminalSettingResult(TerminalSettingStatus terminalSettingStatus, String value);
	public abstract void onReturnNfcDataExchangeResult(boolean isSuccess, Hashtable<String, String> data);
	public abstract void onReturnNfcDetectCardResult(Hashtable<String, Object> data);
	public abstract void onRequestSelectApplication(ArrayList<String> appList);
	public abstract void onRequestSetAmount();
	public abstract void onRequestPinEntry();
	public abstract void onRequestVerifyID(String tlv);
	public abstract void onRequestCheckServerConnectivity();
	public abstract void onRequestOnlineProcess(String tlv);
	public abstract void onRequestTerminalTime();
	public abstract void onRequestDisplayText(DisplayText displayText);
	public abstract void onRequestClearDisplay();
	public abstract void onRequestReferProcess(String pan);
	public abstract void onRequestAdviceProcess(String tlv);
	public abstract void onRequestFinalConfirm();
	public abstract void onAutoConfigProgressUpdate(double percentage);
	public abstract void onAutoConfigCompleted(boolean isDefaultSettings, String autoConfigSettings);
	public abstract void onAutoConfigError(AutoConfigError autoConfigError);
	public abstract void onBatteryLow(BatteryStatus batteryStatus);
	public abstract void onNoDeviceDetected();
	public abstract void onDevicePlugged();
	public abstract void onDeviceUnplugged();
	public abstract void onDeviceHere(boolean isHere);
	public abstract void onError(Error errorState, String errorMessage);
	public abstract void onPowerDown();
	public abstract void onUsbConnected();
	public abstract void onUsbDisconnected();
	
	static class MyEmvSwipeControllerListener implements EmvSwipeControllerListener {
		@Override
		public void onWaitingForCard(CheckCardMode checkCardMode) {
			if(currentActivity != null) currentActivity.onWaitingForCard(checkCardMode);
		}
		@Override
		public void onBatchDataDetected() {
			if(currentActivity != null) currentActivity.onBatchDataDetected();
		}
		@Override
		public void onOnlineProcessDataDetected() {
			if(currentActivity != null) currentActivity.onOnlineProcessDataDetected();
		}
		@Override
		public void onReversalDataDetected() {
			if(currentActivity != null) currentActivity.onReversalDataDetected();
		}
		@Override
		public void onReturnCheckCardResult(CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {
			if(currentActivity != null) currentActivity.onReturnCheckCardResult(checkCardResult, decodeData);
		}
		@Override
		public void onReturnCancelCheckCardResult(boolean isSuccess) {
			if(currentActivity != null) currentActivity.onReturnCancelCheckCardResult(isSuccess);
		}
		@Override
		public void onReturnEncryptPinResult(Hashtable<String, String> data) {
			if(currentActivity != null) currentActivity.onReturnEncryptPinResult(data);
		}
		@Override
		public void onReturnEncryptDataResult(boolean isSuccess, Hashtable<String, String> data) {
			if(currentActivity != null) currentActivity.onReturnEncryptDataResult(isSuccess, data);
		}
		@Override
		public void onReturnStartEmvResult(StartEmvResult result, String ksn) {
			if(currentActivity != null) currentActivity.onReturnStartEmvResult(result, ksn);
		}
		@Override
		public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
			if(currentActivity != null) currentActivity.onReturnDeviceInfo(deviceInfoData);
		}
		@Override
		public void onReturnCAPKList(List<CAPK> capkList) {
			if(currentActivity != null) currentActivity.onReturnCAPKList(capkList);
		}
		@Override
		public void onReturnCAPKDetail(CAPK capk) {
			if(currentActivity != null) currentActivity.onReturnCAPKDetail(capk);
		}
		@Override
		public void onReturnCAPKLocation(String location) {
			if(currentActivity != null) currentActivity.onReturnCAPKLocation(location);
		}
		@Override
		public void onReturnUpdateCAPKResult(boolean isSuccess) {
			if(currentActivity != null) currentActivity.onReturnUpdateCAPKResult(isSuccess);
		}
		@Override
		public void onReturnEmvReportList(Hashtable<String, String> data) {
			if(currentActivity != null) currentActivity.onReturnEmvReportList(data);
		}
		@Override
		public void onReturnEmvReport(String tlv) {
			if(currentActivity != null) currentActivity.onReturnEmvReport(tlv);
		}
		@Override
		@Deprecated
		public void onReturnTransactionResult(TransactionResult transResult) {
			if(currentActivity != null) currentActivity.onReturnTransactionResult(transResult);
		}
		@Override
		public void onReturnTransactionResult(TransactionResult transResult, Hashtable<String, String> data) {
			if(currentActivity != null) currentActivity.onReturnTransactionResult(transResult, data);
		}
		@Override
		public void onReturnBatchData(String tlv) {
			if(currentActivity != null) currentActivity.onReturnBatchData(tlv);
		}
		@Override
		public void onReturnTransactionLog(String tlv) {
			if(currentActivity != null) currentActivity.onReturnTransactionLog(tlv);
		}
		@Override
		public void onReturnReversalData(String tlv) {
			if(currentActivity != null) currentActivity.onReturnReversalData(tlv);
		}
		@Override
		public void onReturnPowerOnIccResult(boolean isSuccess, String ksn, String atr, int atrLength) {
			if(currentActivity != null) currentActivity.onReturnPowerOnIccResult(isSuccess, ksn, atr, atrLength);
		}
		@Override
		public void onReturnPowerOffIccResult(boolean isSuccess) {
			if(currentActivity != null) currentActivity.onReturnPowerOffIccResult(isSuccess);
		}
		@Override
		public void onReturnApduResult(boolean isSuccess, String apdu, int apduLength) {
			if(currentActivity != null) currentActivity.onReturnApduResult(isSuccess, apdu, apduLength);
		}
		@Override
		public void onReturnApduResultWithPkcs7Padding(boolean isSuccess, String apdu) {
			if(currentActivity != null) currentActivity.onReturnApduResultWithPkcs7Padding(isSuccess, apdu);
		}
		@Override
		public void onReturnViposBatchExchangeApduResult(Hashtable<Integer, String> data) {
			if(currentActivity != null) currentActivity.onReturnViposBatchExchangeApduResult(data);
		}
		@Override
		public void onReturnViposExchangeApduResult(String apdu) {
			if(currentActivity != null) currentActivity.onReturnViposExchangeApduResult(apdu);
		}
		@Override
		public void onReturnEmvCardBalance(boolean isSuccess, String tlv) {
			if(currentActivity != null) currentActivity.onReturnEmvCardBalance(isSuccess, tlv);
		}
		@Override
		public void onReturnEmvCardDataResult(boolean isSuccess, String tlv) {
			if(currentActivity != null) currentActivity.onReturnEmvCardDataResult(isSuccess, tlv);
		}
		@Override
		public void onReturnEmvCardNumber(String cardNumber) {
			if(currentActivity != null) currentActivity.onReturnEmvCardNumber(cardNumber);
		}
		@Override
		public void onReturnEmvTransactionLog(String[] transactionLogs) {
			if(currentActivity != null) currentActivity.onReturnEmvTransactionLog(transactionLogs);
		}
		@Override
		public void onReturnEmvLoadLog(String[] loadLogs) {
			if(currentActivity != null) currentActivity.onReturnEmvLoadLog(loadLogs);
		}
		@Override
		public void onReturnKsn(Hashtable<String, String> ksntable) {
			if(currentActivity != null) currentActivity.onReturnKsn(ksntable);
		}
		@Override
		public void onReturnUpdateTerminalSettingResult(TerminalSettingStatus terminalSettingStatus) {
			if(currentActivity != null) currentActivity.onReturnUpdateTerminalSettingResult(terminalSettingStatus);
		}
		@Override
		public void onReturnReadTerminalSettingResult(TerminalSettingStatus terminalSettingStatus, String value) {
			if(currentActivity != null) currentActivity.onReturnReadTerminalSettingResult(terminalSettingStatus, value);
		}
		@Override
		public void onReturnNfcDataExchangeResult(boolean isSuccess, Hashtable<String, String> data) {
			if(currentActivity != null) currentActivity.onReturnNfcDataExchangeResult(isSuccess, data);
		}
		@Override
		public void onReturnNfcDetectCardResult(Hashtable<String, Object> data) {
			if(currentActivity != null) currentActivity.onReturnNfcDetectCardResult(data);
		}
		@Override
		public void onRequestSelectApplication(ArrayList<String> appList) {
			if(currentActivity != null) currentActivity.onRequestSelectApplication(appList);
		}
		@Override
		public void onRequestSetAmount() {
			if(currentActivity != null) currentActivity.onRequestSetAmount();
		}
		@Override
		public void onRequestPinEntry() {
			if(currentActivity != null) currentActivity.onRequestPinEntry();
		}
		@Override
		public void onRequestVerifyID(String tlv) {
			if(currentActivity != null) currentActivity.onRequestVerifyID(tlv);
		}
		@Override
		public void onRequestCheckServerConnectivity() {
			if(currentActivity != null) currentActivity.onRequestCheckServerConnectivity();
		}
		@Override
		public void onRequestOnlineProcess(String tlv) {
			if(currentActivity != null) currentActivity.onRequestOnlineProcess(tlv);
		}
		@Override
		public void onRequestTerminalTime() {
			if(currentActivity != null) currentActivity.onRequestTerminalTime();
		}
		@Override
		public void onRequestDisplayText(DisplayText displayText) {
			if(currentActivity != null) currentActivity.onRequestDisplayText(displayText);
		}
		@Override
		public void onRequestClearDisplay() {
			if(currentActivity != null) currentActivity.onRequestClearDisplay();
		}
		@Override
		public void onRequestReferProcess(String pan) {
			if(currentActivity != null) currentActivity.onRequestReferProcess(pan);
		}
		@Override
		public void onRequestAdviceProcess(String tlv) {
			if(currentActivity != null) currentActivity.onRequestAdviceProcess(tlv);
		}
		@Override
		public void onRequestFinalConfirm() {
			if(currentActivity != null) currentActivity.onRequestFinalConfirm();
		}
		@Override
		public void onAutoConfigProgressUpdate(double percentage) {
			if(currentActivity != null) currentActivity.onAutoConfigProgressUpdate(percentage);
		}
		@Override
		public void onAutoConfigCompleted(boolean isDefaultSettings, String autoConfigSettings) {
			if(currentActivity != null) currentActivity.onAutoConfigCompleted(isDefaultSettings, autoConfigSettings);
		}
		@Override
		public void onAutoConfigError(AutoConfigError autoConfigError) {
			if(currentActivity != null) currentActivity.onAutoConfigError(autoConfigError);
		}
		@Override
		public void onBatteryLow(BatteryStatus batteryStatus) {
			if(currentActivity != null) currentActivity.onBatteryLow(batteryStatus);
		}
		@Override
		public void onNoDeviceDetected() {
			if(currentActivity != null) currentActivity.onNoDeviceDetected();
		}
		@Override
		public void onDevicePlugged() {
			if(currentActivity != null) currentActivity.onDevicePlugged();
		}
		@Override
		public void onDeviceUnplugged() {
			if(currentActivity != null) currentActivity.onDeviceUnplugged();
		}
		@Override
		public void onDeviceHere(boolean isHere) {
			if(currentActivity != null) currentActivity.onDeviceHere(isHere);
		}
		@Override
		public void onError(Error errorState, String errorMessage) {
			if(currentActivity != null) currentActivity.onError(errorState, errorMessage);
		}
		@Override
		public void onPowerDown() {
			if(currentActivity != null) currentActivity.onPowerDown();
		}
		@Override
		public void onUsbConnected() {
			if(currentActivity != null) currentActivity.onUsbConnected();
		}
		@Override
		public void onUsbDisconnected() {
			if(currentActivity != null) currentActivity.onUsbDisconnected();
		}
	}
}
