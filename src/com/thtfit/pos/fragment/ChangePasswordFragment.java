package com.thtfit.pos.fragment;

import com.thtfit.pos.R;
import com.thtfit.pos.model.CustomDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordFragment extends Fragment implements OnClickListener
{
	private View mView;
	private EditText edit_password;
	private EditText edit_newPassword;
	private EditText edit_reinputPassword;
	private Button bt_ChangePwdConfirm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_manage_changepassword, container,
				false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		edit_password = (EditText) mView.findViewById(R.id.change_pwd_old);
		edit_newPassword = (EditText) mView.findViewById(R.id.new_password);
		edit_reinputPassword = (EditText) mView.findViewById(R.id.reinput_new_password);
		bt_ChangePwdConfirm = (Button) mView.findViewById(R.id.changepwd_button);
		bt_ChangePwdConfirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.changepwd_button:
				if(edit_newPassword.getText().toString().equals(edit_reinputPassword.getText().toString()))
				{
				}
				else{
					CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
					builder.setMessage((String) this.getResources().getText(R.string.password_err_reinput));
					builder.setTitle((String) this.getResources().getText(R.string.prompt));
					builder.setPositiveButton((String) this.getResources().getText(R.string.confirm),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									edit_newPassword.setText(null);
									edit_reinputPassword.setText(null);
								}
							});
					builder.create().show();
				}
				break;
			default:
				break;
		}

	}

}
