package com.thtfit.pos.model;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

public class tipInputListener implements TextWatcher
{

	public tipInputListener(EditText edittext,Boolean isMoneyType){
		this.isMoneyType = isMoneyType;
		this.edittext = edittext;
	}
	private String textShow;
	private String textget;
	private Boolean isMoneyType;
	private Boolean isChanged = false;
	private EditText edittext;
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if(isChanged){
			return;
		}
		textget = edittext.getText().toString().trim();
		if(textget.indexOf("$")!= -1){
			isMoneyType = true;
		}else{
			isMoneyType = false;
		}
		if(textget.equals("")){
			textget = "0";
		}
		if(isMoneyType){
			textget = textget.substring(textget.indexOf("$")+1,textget.length());
			if(textget.equals("")){
				textget = "0";
			}
			if(Integer.parseInt(textget) >99){
				textShow = "$99";
			}else if(Integer.parseInt(textget) < 0){
				textShow = "$0";
			}else if(Integer.parseInt(textget)>= 0 && Integer.parseInt(textget)<= 99){
				textShow = "$"+Integer.parseInt(textget);
			}
		}else{
			if(textget.indexOf("%") == -1){
				textget = textget.substring(0,textget.length());
			}else{
				textget = textget.substring(0,textget.indexOf("%"));
			}
			if(textget.equals("")){
				textget = "0";
			}
			if(Integer.parseInt(textget) >99){
				textShow = "99%";
			}else if(Integer.parseInt(textget) < 0){
				textShow = "0%";
			}else{
				textShow = Integer.parseInt(textget)+"%";
			}
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		//edittext.setSelection(1);
		if(isChanged){
			return;
		}
	}
	
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		if(isChanged){
			return;
		}
		isChanged = true;
		edittext.setText(textShow);
		if(isMoneyType){
			edittext.setSelection(textShow.length());
		}
		else{
			edittext.setSelection(textShow.length()-1);
		}
		isChanged = false;
	}


}
