package com.thtfit.pos.fragment;
/**
 * CoyRight  THTFIT (Smart Home Cloud System) 
 */

import com.thtfit.pos.R;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import android.widget.ImageButton;
import com.thtfit.pos.activity.MainActivity;
import java.lang.ClassCastException;
import android.app.Activity;
import android.text.InputType;
import android.util.Log;
import android.text.Selection;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.LinearLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.view.WindowManager;
import android.media.MediaRecorder; 
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.sql.Date;
import java.io.File;

/**
 * Author:winnie (winnie@thtfit.com)
 */
public class DailFragment extends Fragment implements OnClickListener{
	public final static String LOG_TAG = "DailFragment";
	private Context context;
	public TextView dail_desc, dial_name=null;
	private ImageButton btnDailCancel,btnDailDel,btnDailVoiceRecord;
    private ImageButton mVoiceRecord,mVoiceRecordCancel;
    private TextView mVoiceRecordTime;
	public static String dailnum = "";
	private String mDialName="";//added by lxj 2013-4-26
	//private String mDialNumber = MainActivity.dail_num;
	private String[] mVoiceToNum;


	public EditText dail_edit;
    private MediaRecorder mMediaRecorder;
	public static boolean isVoiceMsgRecording = false;
    int time =0;
    int minute,second;



	public final static int TAG_DAIL_DELETE = 1;
	public final static int TAG_DAIL_CANCEL = 2;
    public final static int TAG_DAIL_VOICE_RECORD = 3;
	private String mCallStateDispText = "";

	onDialKeyEventListener mListener;
	
	public static DailFragment newInstance(Context context){
		DailFragment f = new DailFragment();
		f.context = context;
		return f;		
	}


   private TextWatcher conTextWatcher = new TextWatcher()
	 {
	  int Num = 0;
	  int tempLength = 0;
	  @Override
	  public void afterTextChanged(Editable s) {
	  
	   if(s.length() > tempLength )
	   {
	   if( s.charAt(Num)  == '#' ){
		   s.delete(Num,Num + 1 );
		 // ((MainActivity)context).doOnClick(MainActivity.TAG_PHONE_CALL_ON);
		 }				
	  else if( s.charAt(Num) == '*'  ) {
		   if(Num >0) {
		     s.delete(Num -1, Num + 1);
		   }else {
		     s.delete(Num,Num + 1 );
		 	}
			
		}
		
	   }
	  }
	  @Override
	  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	   tempLength = s.length();
	   Num = start;	  
	  }
	  @Override
	  public void onTextChanged(CharSequence s, int start, int before, int count) {
	  }  
	 };
	
	

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			mListener =(onDialKeyEventListener)activity;
		}catch(ClassCastException e){
			throw new ClassCastException(activity.toString()+"must implement onDialKeyEventListener");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dail_main, container, false);
		dail_edit = (EditText) v.findViewById(R.id.dail_edit);
		dail_edit.setInputType(InputType.TYPE_CLASS_TEXT);

		//add by rocky

		if (android.os.Build.VERSION.SDK_INT <= 10) 
		{ 
			dail_edit.setInputType(InputType.TYPE_NULL);
		} else {
			try {
				Class<EditText> cls = EditText.class;
				Method setSoftInputShownOnFocus = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
				setSoftInputShownOnFocus.setAccessible(true);
				setSoftInputShownOnFocus.invoke(dail_edit, false);
			} catch (Exception e) {}
		}
		//end add
		dail_desc = (TextView) v.findViewById(R.id.txt_dail_desc);
		dail_desc.setText(mCallStateDispText);
		dial_name = (TextView) v.findViewById(R.id.txt_dail_name);//added by lxj 2013-4-26
		btnDailDel = (ImageButton)v.findViewById(R.id.btn_dail_delete);
		btnDailCancel = (ImageButton)v.findViewById(R.id.btn_dail_cancel);
        btnDailVoiceRecord = (ImageButton)v.findViewById(R.id.btn_dail_voice_record);

		btnDailDel.setTag(TAG_DAIL_DELETE);
		btnDailCancel.setTag(TAG_DAIL_CANCEL);
        btnDailVoiceRecord.setTag(TAG_DAIL_VOICE_RECORD);
		btnDailDel.setOnClickListener(this);
		btnDailDel.setOnLongClickListener(new DialDelLongClickListener());
		btnDailCancel.setOnClickListener(this);
        btnDailVoiceRecord.setOnClickListener(this);

		//setDailNumberDisplay(mDialNumber);
		dial_name.setText(mDialName);//added by lxj 2013-4-26


	//	SharedPreferences colorSetting = context.getSharedPreferences(MainActivity.PREFS_SIP_CONFIG, Context.MODE_PRIVATE);
		//dail_edit.setBackgroundResource(colorSetting.getInt("color",R.color.green));
		dail_edit.addTextChangedListener(conTextWatcher);
		return v;
	}

	class DialDelLongClickListener implements OnLongClickListener{
		public boolean onLongClick (View v){
			dail_edit.setText("");
			//MainActivity.mutil_edit_call.setText("");
			return true;
		}
	}
	@Override
	public void onClick(View v) {		
		int tag = (Integer) v.getTag();

		switch(tag){
			case TAG_DAIL_DELETE:

				if(dail_edit.getSelectionStart() >= 1)
					dail_edit.getText().delete(dail_edit.getSelectionStart()-1, dail_edit.getSelectionStart());  
				dailnum = dail_edit.getText().toString();
				mListener.onDailDelete(dailnum);
				break;
			case TAG_DAIL_CANCEL:
				setCallStateDisplay("");
				mListener.onDailCancel();
                isVoiceMsgRecording = false;

                
				break;
            case TAG_DAIL_VOICE_RECORD:
                 /*if (null != ((MainActivity)context).voiceToNum(dail_edit.getText().toString()) && (null == ((MainActivity)context).mCall)){
                    mVoiceToNum = ((MainActivity)context).voiceToNum(dail_edit.getText().toString());
              	    initVoiceRecord();
                }*/
                break;
			default:
				break;
		}	
	}



   public void initVoiceRecord(){
/*
		LinearLayout mLayout = (LinearLayout)getActivity().findViewById(R.id.dail_main);
        mLayout.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(context);
	    View view = inflater.inflate(R.layout.voice_record, null);
        mLayout.addView(view);
        mVoiceRecordTime = (TextView)view.findViewById(R.id.voice_record_time);
        mVoiceRecord = (ImageButton)view.findViewById(R.id.voice_record_startOrStop);
        mVoiceRecordCancel = (ImageButton)view.findViewById(R.id.voice_record_cancel);
        mVoiceRecord.setOnClickListener(new OnClickListener(){
         	@Override
         	public void onClick(View v){
            
            if (!isVoiceMsgRecording){
				mVoiceRecord.setBackgroundResource(R.drawable.stop_voice_record);
            	startVoiceRecord();
             
         	}else{
				mVoiceRecord.setBackgroundResource(R.drawable.voice_record);
				stopVoiceRecord();             	
                if (null != mVoiceToNum){
    				if (mVoiceToNum.length > 1){
						((MainActivity)context).mVoiceRecordIsDoubleIp = true;
					}
             	  	for (int i=0;i<mVoiceToNum.length;i++){
                         if ( null != mVoiceToNum[i]){
					  		((MainActivity)context).VoicesRecordFileSend(mVoiceToNum[i]);
                         }
                 	}
	           }
            }
            }
         } );
        mVoiceRecordCancel.setOnClickListener(new OnClickListener(){
         	@Override
         	public void onClick(View v){
             stopVoiceRecord();
			 ((MainActivity)context).VoicesRecordFileDel();
			 isVoiceMsgRecording = false;
             mListener.onDailCancel();
             

         	}
         } );
*/
       
    }
    
	public void startVoiceRecord(){
		//((MainActivity)context).VoicesRecordStart();
        //((MainActivity)context).mVoiceRecordIsDoubleIp = false;
		isVoiceMsgRecording = true;
		time = 0;
		minute = 0;
		second = 0;
		mhandler.post(runnable);	 
	}


  private Handler mhandler=new Handler() {

		@Override
		public void handleMessage(Message msg) {
				
		}
		
	};    

   
   
     @Override
	   public void onStop() {
		   super.onPause();
           isVoiceMsgRecording = false;
		   mhandler.removeCallbacks(runnable);
	   }


   private Runnable runnable = new Runnable(){
   
	   @Override
	   public void run() {
		   if (isVoiceMsgRecording) {
			   mhandler.removeCallbacks(runnable);
			   mhandler.postDelayed(runnable, 1000);
			   ++time;
			   if (time < 60) {
				   second = time;
			   }else if(time <= 300) {
				   minute = time / 60;
				   second = time % 60;			  
			   }else{
				   isVoiceMsgRecording =false;
				   stopVoiceRecord();
      
				   //((MainActivity)context).VoicesRecordFileSend(((MainActivity)context).onDailNumToIPStr(mVoiceToNum));

                if (null != mVoiceToNum){
    				if (mVoiceToNum.length > 1){
						//((MainActivity)context).mVoiceRecordIsDoubleIp = true;
					}
             	  	for (int i=0;i<mVoiceToNum.length;i++){
                        if ( null != mVoiceToNum[i]){
  							//((MainActivity)context).VoicesRecordFileSend(mVoiceToNum[i]);
                        }
                 	}
	           }
	           

   
			   }		  
			   mVoiceRecordTime.setText(format(minute) + ":" + format(second));
   
		   }
	   }
   };

   public String format(int i){
		String s = i + "";
		if(s.length() == 1){
			s = "0" + s;
		}
		return s;
	}
    
	public void stopVoiceRecord(){
	
		isVoiceMsgRecording = false; 
		//((MainActivity)context).VoicesRecordStop();
		mVoiceRecord.setBackgroundResource(R.drawable.voice_record);
		mhandler.removeCallbacks(runnable);
		time = 0;
		minute = 0;
		second = 0;
		mVoiceRecordTime.setText(format(minute) + ":" + format(second));
	}
	
	public String getCallNumber()
	{
		if(null != dail_edit){
			return dail_edit.getText().toString();
		}else{
			return null;
		}
	}
	public void setDailNumberDisplay(String dail_number){
		if(dail_number.equals(""))
			return;
		if(null != dail_edit){
			if(dail_edit.getSelectionStart() == 0 && dail_edit.getText().length() ==0)
			{
				dail_edit.getText().insert(dail_edit.getText().length(), dail_number);
				dail_edit.setSelection(1);
			}else{
				dail_edit.setSelection(dail_edit.getSelectionStart());
				dail_edit.getText().insert(dail_edit.getSelectionStart(), dail_number);
			}
		}else{
			//mDialNumber = dail_number;
		}
	}

	public void setCallStateDisplay(String dail_desc_txt){
		if(null != dail_desc)
		{
			dail_desc.setText(dail_desc_txt);
		}
		else
		{
			mCallStateDispText = dail_desc_txt;
		}
	}

	public void setDialNameDisplay(String strName){//added by lxj 2013-4-26
		if( null != dial_name ){
			dial_name.setText(strName);
		}else{
			mDialName = strName;
		}
	}

	//Container Activity must implement this interface
	public interface onDialKeyEventListener{
		public void onDailDelete(String dail_number);
		public void onDailCancel();
	}
} 
