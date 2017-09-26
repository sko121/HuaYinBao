package com.thtfit.pos.bbpos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;

import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bbpos.bbdevice.BBDeviceController;
import com.thtfit.pos.R;

public class BBPosBTMainActivity extends BaseActivity {

	protected static String webAutoConfigString = "";
	protected static boolean isLoadedLocalSettingFile = false;
	protected static boolean isLoadedWebServiceAutoConfig = false;
	private Button menuBtn;//by Lu
	private PopupWindow popupwindow;// by Lu
	 //by Lu : menu item 
	private MenuItemClickListener menuItemClickListener;
    private TextView menuStartConnection;
    private TextView menuStopConnection;
    private TextView menuUnpairAll;
    private TextView menuInitializeSession;
    private TextView menuResetSession;
    private TextView menuGetDeivceInfo;
    private TextView menuCancelCheckCard;
    private TextView menuAutoConfig;
    private TextView menuEnableInputAmount;
    private TextView menuEncryptPin;
    private TextView menuEncryptData;
    private TextView menuSendApdu;
    private TextView menuPrintSample;
    private TextView menuMainActivity;
    private TextView menuApdu;
    private TextView capkActivity;
    private TextView menuWifi;
    private TextView menuInjectSessionKey;
    
    private String mReceiveAmount;//by Lu 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.bbpos_activity_main);

		//by Lu
		mReceiveAmount = getIntent().getStringExtra("amount");
		setMReceiveAmount(mReceiveAmount);
		
		menuBtn = (Button) findViewById(R.id.btn_menu); // by Lu
		((TextView) findViewById(R.id.modelTextView)).setText(Build.MANUFACTURER.toUpperCase(Locale.ENGLISH) + " - " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")");

		fidSpinner = (Spinner) findViewById(R.id.fidSpinner);
		startButton = (Button) findViewById(R.id.startButton);
		amountEditText = (EditText) findViewById(R.id.amountEditText);
		statusEditText = (EditText) findViewById(R.id.statusEditText);

		MyOnClickListener myOnClickListener = new MyOnClickListener();
		startButton.setOnClickListener(myOnClickListener);
		menuBtn.setOnClickListener(myOnClickListener); // by Lu

		String[] fids = new String[] { "FID22", "FID36", "FID46", "FID54", "FID55", "FID60", "FID61", "FID64", "FID65", };
		fidSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.my_spinner_item, fids));
		fidSpinner.setSelection(5);

		currentActivity = this;
		
		try {
			String filename = "settings.txt";
			String inputDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.bbpos.bbdevice.ui/";
			
			FileInputStream fis = new FileInputStream(inputDirectory + filename);
			byte[] temp = new byte[fis.available()];
			fis.read(temp);
			fis.close();
			
			isLoadedLocalSettingFile = true;
			bbDeviceController.setAutoConfig(new String(temp));
			
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

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			statusEditText.setText("");

			if (v == startButton) {
				isPinCanceled = false;
				amountEditText.setText("");

				statusEditText.setText(R.string.starting);
				//promptForStartEmv();
				promptForCheckCard();
			} else if(v == menuBtn) {
				closePopWindow();
				initPopWindow();
				popupwindow.showAsDropDown(v, 0, 5);
//				menuBtnSelected();
			}
		}
	}
	
	private class AsyncCallWS extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			if (isLoadedWebServiceAutoConfig == false) {
				webAutoConfigString = WebService.invokeGetAutoConfigString(Build.MANUFACTURER.toUpperCase(Locale.US), Build.MODEL.toUpperCase(Locale.US), BBDeviceController.getApiVersion(), "getAutoConfigString");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (isLoadedWebServiceAutoConfig == false) {
				isLoadedWebServiceAutoConfig = true;
				if (isLoadedLocalSettingFile == false) {
					if (!webAutoConfigString.equalsIgnoreCase("Error occured") && !webAutoConfigString.equalsIgnoreCase("")) {
						bbDeviceController.setAutoConfig(webAutoConfigString);
						
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
	
	//by Lu
    public void initPopWindow() {
    	View customView = getLayoutInflater().inflate(R.layout.bbpos_popwindow_item_view, null);
    	popupwindow = new PopupWindow(customView, 250, 400);
    	popupwindow.setOutsideTouchable(true);
    	popupwindow.setBackgroundDrawable(new BitmapDrawable());
//    	customView.setOnTouchListener(new OnTouchListener() {  
//            @Override  
//            public boolean onTouch(View v, MotionEvent event) {  
//                closePopWindow();
//                return false;  
//            }  
//        });  
    	//by Lu
    	//menu item's init
    	menuStartConnection = (TextView) customView.findViewById(R.id.menu_start_connection);
        menuStopConnection = (TextView) customView.findViewById(R.id.menu_stop_connection);
        menuUnpairAll = (TextView) customView.findViewById(R.id.menu_unpair_all);
        menuInitializeSession = (TextView) customView.findViewById(R.id.menu_initialize_session);
        menuResetSession = (TextView) customView.findViewById(R.id.menu_reset_session);
        menuGetDeivceInfo = (TextView) customView.findViewById(R.id.menu_get_deivce_info);
        menuCancelCheckCard = (TextView) customView.findViewById(R.id.menu_cancel_check_card);
        menuAutoConfig = (TextView) customView.findViewById(R.id.menu_auto_config);
        menuEnableInputAmount = (TextView) customView.findViewById(R.id.menu_enable_input_amount);
        menuEncryptPin = (TextView) customView.findViewById(R.id.menu_encrypt_pin);
        menuEncryptData = (TextView) customView.findViewById(R.id.menu_encrypt_data);
        menuSendApdu = (TextView) customView.findViewById(R.id.menu_send_apdu);
        menuPrintSample = (TextView) customView.findViewById(R.id.menu_print_sample);
        menuMainActivity = (TextView) customView.findViewById(R.id.menu_mainactivity);
        menuApdu = (TextView) customView.findViewById(R.id.menu_apdu);
        capkActivity = (TextView) customView.findViewById(R.id.capk_activity);
        menuWifi = (TextView) customView.findViewById(R.id.menu_wifi);
        menuInjectSessionKey = (TextView) customView.findViewById(R.id.menu_inject_session_key);
        //menu item's click listener
        menuItemClickListener = new MenuItemClickListener();
        menuStartConnection.setOnClickListener(menuItemClickListener);
        menuStopConnection.setOnClickListener(menuItemClickListener);
        menuUnpairAll.setOnClickListener(menuItemClickListener);
        menuInitializeSession.setOnClickListener(menuItemClickListener);
        menuResetSession.setOnClickListener(menuItemClickListener);
        menuGetDeivceInfo.setOnClickListener(menuItemClickListener);
        menuCancelCheckCard.setOnClickListener(menuItemClickListener);
        menuAutoConfig.setOnClickListener(menuItemClickListener);
        menuEncryptPin.setOnClickListener(menuItemClickListener);
        menuEncryptData.setOnClickListener(menuItemClickListener);
        menuSendApdu.setOnClickListener(menuItemClickListener);
        menuPrintSample.setOnClickListener(menuItemClickListener);
        menuMainActivity.setOnClickListener(menuItemClickListener);
        menuApdu.setOnClickListener(menuItemClickListener);
        capkActivity.setOnClickListener(menuItemClickListener);
        menuWifi.setOnClickListener(menuItemClickListener);
        menuInjectSessionKey.setOnClickListener(menuItemClickListener);
    }
    
    //by Lu
    public void closePopWindow() {
    	if (popupwindow != null && popupwindow.isShowing()) {  
            popupwindow.dismiss();  
            popupwindow = null;  
        }  
    }
    
  //by Lu : menu btn's listener
  	class MenuItemClickListener implements OnClickListener {
  		@Override
  		public void onClick(View v) {
  			callMenuSelected(v.getId());
  			closePopWindow();
  		}
  	} 
}
