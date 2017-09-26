package com.thtfit.pos.fragment;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.thtfit.pos.R;
import com.thtfit.pos.model.CustomDialog;
import com.thtfit.pos.model.tipInputListener;

public class SettingNormalFragment extends Fragment implements OnClickListener
{
	private View mView;
	private LinearLayout settingNormal1_1;
	private LinearLayout settingNormal1_2;
	private LinearLayout settingNormal1_3;
	private LinearLayout settingNormal2_1;
	private LinearLayout settingNormal2_2;
	private LinearLayout settingNormal2_3;
	private LinearLayout settingNormal3_1;
	private LinearLayout settingNormal3_2;
	private LinearLayout settingNormal3_3;
	
	private ImageView imgSettingNormal1_1;
	private ImageView imgSettingNormal1_2;
	private ImageView imgSettingNormal1_3;
	private ImageView imgSettingNormal2_1;
	private ImageView imgSettingNormal2_2;
	private ImageView imgSettingNormal2_3;
	private ImageView imgSettingNormal3_1;
	private TextView textSettingNormal3_2;
	private TextView textSettingNormal3_3;
	
	private Boolean settingConfig1;
	private Boolean settingConfig2;
	private Boolean settingConfig3;
	private Boolean settingConfig4;
	private Boolean settingConfig5;
	private Boolean settingConfig6;
	private Boolean settingConfig7;
	private String settingConfig8;
	private String settingConfig9;

	Context ctx;
	SharedPreferences spSettingConfig ;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_setting_normal, container,
				false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		settingNormal1_1 = (LinearLayout) mView.findViewById(R.id.setting_changgui1_1);		
		settingNormal1_1.setOnClickListener(this);
		settingNormal1_2 = (LinearLayout) mView.findViewById(R.id.setting_changgui1_2);		
		settingNormal1_2.setOnClickListener(this);
		settingNormal1_3 = (LinearLayout) mView.findViewById(R.id.setting_changgui1_3);		
		settingNormal1_3.setOnClickListener(this);
		settingNormal2_1 = (LinearLayout) mView.findViewById(R.id.setting_changgui2_1);		
		settingNormal2_1.setOnClickListener(this);
		settingNormal2_2 = (LinearLayout) mView.findViewById(R.id.setting_changgui2_2);		
		settingNormal2_2.setOnClickListener(this);
		settingNormal2_3 = (LinearLayout) mView.findViewById(R.id.setting_changgui2_3);		
		settingNormal2_3.setOnClickListener(this);
		settingNormal3_1 = (LinearLayout) mView.findViewById(R.id.setting_changgui3_1);		
		settingNormal3_1.setOnClickListener(this);
		settingNormal3_2 = (LinearLayout) mView.findViewById(R.id.setting_changgui3_2);		
		settingNormal3_2.setOnClickListener(this);
		settingNormal3_3 = (LinearLayout) mView.findViewById(R.id.setting_changgui3_3);		
		settingNormal3_3.setOnClickListener(this);
		
		imgSettingNormal1_1 = (ImageView) mView.findViewById(R.id.setting_changgui1_1_imageview);
		imgSettingNormal1_2 = (ImageView) mView.findViewById(R.id.setting_changgui1_2_imageview);
		imgSettingNormal1_3 = (ImageView) mView.findViewById(R.id.setting_changgui1_3_imageview);
		imgSettingNormal2_1 = (ImageView) mView.findViewById(R.id.setting_changgui2_1_imageview);
		imgSettingNormal2_2 = (ImageView) mView.findViewById(R.id.setting_changgui2_2_imageview);
		imgSettingNormal2_3 = (ImageView) mView.findViewById(R.id.setting_changgui2_3_imageview);
		imgSettingNormal3_1 = (ImageView) mView.findViewById(R.id.setting_changgui3_1_imageview);
		textSettingNormal3_2 = (TextView) mView.findViewById(R.id.setting_changgui3_2_textview);
		textSettingNormal3_3 = (TextView) mView.findViewById(R.id.setting_changgui3_3_textview);
		
		//update the imageview's source and the testview's source
		getSettingConfig();
		if(settingConfig1 == false)
			updateImageView(imgSettingNormal1_1, settingConfig1);
		if(settingConfig2 == false)
			updateImageView(imgSettingNormal1_2, settingConfig2);
		if(settingConfig3 == false)
			updateImageView(imgSettingNormal1_3, settingConfig3);
		if(settingConfig4 == false)
			updateImageView(imgSettingNormal2_1, settingConfig4);
		if(settingConfig5 == false)
			updateImageView(imgSettingNormal2_2, settingConfig5);
		if(settingConfig6 == false)
			updateImageView(imgSettingNormal2_3, settingConfig6);
		if(settingConfig7 == false)
			updateImageView(imgSettingNormal2_1, settingConfig4);
		updateTextView(textSettingNormal3_2, settingConfig8);
		updateTextView(textSettingNormal3_3, settingConfig9);
				
	}
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.setting_changgui1_1:
				settingConfig1 = !settingConfig1;
				updateImageView(imgSettingNormal1_1, settingConfig1);
				break;
			case R.id.setting_changgui1_2:
				settingConfig2 = !settingConfig2;
				updateImageView(imgSettingNormal1_2, settingConfig2);
				break;
			case R.id.setting_changgui1_3:
				settingConfig3 = !settingConfig3;
				updateImageView(imgSettingNormal1_3, settingConfig3);
				break;
			case R.id.setting_changgui2_1:
				settingConfig4 = !settingConfig4;
				updateImageView(imgSettingNormal2_1, settingConfig4);
				break;
			case R.id.setting_changgui2_2:
				settingConfig5 = !settingConfig5;
				updateImageView(imgSettingNormal2_2, settingConfig5);
				break;
			case R.id.setting_changgui2_3:
				settingConfig6 = !settingConfig6;
				updateImageView(imgSettingNormal2_3, settingConfig6);
				break;
			case R.id.setting_changgui3_1:
				settingConfig7 = !settingConfig7;
				updateImageView(imgSettingNormal3_1, settingConfig7);
				break;
			case R.id.setting_changgui3_2:
				final CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
				final EditText textEnterInput = (EditText)builder.getEditText();
				textEnterInput.setText("$0");
				textEnterInput.setSelection(2);
				textEnterInput.addTextChangedListener(new tipInputListener(textEnterInput, true));
				builder.setTitle("输入你的值");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						if(!(textEnterInput.getText().toString().equals(""))){
							settingConfig8 = textEnterInput.getText().toString();
							updateTextView(textSettingNormal3_2,settingConfig8);
						}
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});
				builder.setTipInMoneyButton(new DialogInterface.OnClickListener()
				{
					@SuppressLint("ResourceAsColor") @Override
					public void onClick(DialogInterface dialog, int which)
					{
						// TODO Auto-generated method stub
						textEnterInput.setText("$0");
						textEnterInput.setSelection(2);
						((ImageButton) builder.getTipInMoneyButton()).setBackgroundResource(R.color.yellow);
						((ImageButton) builder.getTipInPercentButton()).setBackgroundResource(R.color.gray);
					}
				});
				builder.setTipInPercentButton(new DialogInterface.OnClickListener()
				{
					@SuppressLint("ResourceAsColor") @Override
					public void onClick(DialogInterface dialog, int which)
					{
						// TODO Auto-generated method stub;
						textEnterInput.setText("0%");
						textEnterInput.setSelection(1);
						((ImageButton) builder.getTipInMoneyButton()).setBackgroundResource(R.color.gray);
						((ImageButton) builder.getTipInPercentButton()).setBackgroundResource(R.color.yellow);
					}
				});
				builder.create().show();
				break;
			case R.id.setting_changgui3_3:
				final CustomDialog.Builder builder2 = new CustomDialog.Builder(getActivity());
				final EditText textEnterInput2 = (EditText)builder2.getEditText();
				textEnterInput2.setText("$0");
				textEnterInput2.setSelection(2);
				textEnterInput2.addTextChangedListener(new tipInputListener(textEnterInput2, true));
				builder2.setTitle("输入你的值");
				builder2.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						if(!(textEnterInput2.getText().toString().equals(""))){
							settingConfig9 = textEnterInput2.getText().toString();
							updateTextView(textSettingNormal3_3,settingConfig9);
						}
						
						dialog.dismiss();
					}
				});

				builder2.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});
				builder2.setTipInMoneyButton(new DialogInterface.OnClickListener()
				{
					@SuppressLint("ResourceAsColor") @Override
					public void onClick(DialogInterface dialog, int which)
					{
						// TODO Auto-generated method stub
						textEnterInput2.setText("$0");
						textEnterInput2.setSelection(2);
						((ImageButton) builder2.getTipInMoneyButton()).setBackgroundResource(R.color.yellow);
						((ImageButton) builder2.getTipInPercentButton()).setBackgroundResource(R.color.gray);
					}
				});
				builder2.setTipInPercentButton(new DialogInterface.OnClickListener()
				{
					@SuppressLint("ResourceAsColor") @Override
					public void onClick(DialogInterface dialog, int which)
					{
						// TODO Auto-generated method stub
						textEnterInput2.setText("0%");
						textEnterInput2.setSelection(1);
						((ImageButton) builder2.getTipInMoneyButton()).setBackgroundResource(R.color.gray);
						((ImageButton) builder2.getTipInPercentButton()).setBackgroundResource(R.color.yellow);
					}
				});
				builder2.create().show();
				break;

			default:
				break;
		}

	}
	private void getSettingConfig(){
		ctx = getActivity();
		spSettingConfig = ctx.getSharedPreferences("SETTINGCONFIG", Context.MODE_PRIVATE);
		settingConfig1 = spSettingConfig.getBoolean("settingconfig1", true);
		settingConfig2 = spSettingConfig.getBoolean("settingconfig2", true);
		settingConfig3 = spSettingConfig.getBoolean("settingconfig3", true);
		settingConfig4 = spSettingConfig.getBoolean("settingconfig4", true);
		settingConfig5 = spSettingConfig.getBoolean("settingconfig5", true);
		settingConfig6 = spSettingConfig.getBoolean("settingconfig6", true);
		settingConfig7 = spSettingConfig.getBoolean("settingconfig7", true);
		settingConfig8 = spSettingConfig.getString("settingconfig8", "0%");
		settingConfig9 = spSettingConfig.getString("settingconfig9", "0%");
	}
	private void saveSettingConfig(){
		ctx = getActivity();
		spSettingConfig = ctx.getSharedPreferences("SETTINGCONFIG", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spSettingConfig.edit();
		editor.putBoolean("settingconfig1", settingConfig1);
		editor.putBoolean("settingconfig2", settingConfig2);
		editor.putBoolean("settingconfig3", settingConfig3);
		editor.putBoolean("settingconfig4", settingConfig4);
		editor.putBoolean("settingconfig5", settingConfig5);
		editor.putBoolean("settingconfig6", settingConfig6);
		editor.putBoolean("settingconfig7", settingConfig7);
		editor.putString("settingconfig8", settingConfig8);
		editor.putString("settingconfig9", settingConfig9);
		editor.commit();
	}
	private void updateImageView(ImageView imgView,Boolean boollean){
		saveSettingConfig();
		if(boollean){
			imgView.setImageResource(R.drawable.check_mark);
			imgView.invalidate();
		}else{
			imgView.setImageResource(R.drawable.biz_pc_main_money_icon);
			imgView.invalidate();
		}
		
	}
	
	private void updateTextView(TextView textView,String thetext){
		saveSettingConfig();
		textView.setText(thetext);
		textView.invalidate();
	}

}
