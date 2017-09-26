package com.thtfit.pos.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thtfit.pos.R;
import com.thtfit.pos.debug.DebugPrint;
import com.thtfit.pos.model.CustomDialog;
import com.thtfit.pos.service.PosApplication;
import com.thtfit.pos.util.Config;

public class LoginActivity extends Activity implements OnClickListener
{
	private EditText etLoginName;
	private EditText etPassword;
	private Button btLogin;
	private Button btLogoutManager;
	private Button btSwitchManager;

	int loginStatus;
	int loginStatusManager;

	/* debug */
	private static String TAG = "LoginActivity";
	public static final boolean isDebug = true;
	// public static final boolean isDebug = false;
	public DebugPrint LOG = new DebugPrint(isDebug, TAG);

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals("com.thtfit.pos.service.Receiver.action.Login.RECEIVE_DATA"))
			{
				final String loginResult = intent.getStringExtra("loginResult");
				if (loginResult.equals("success"))
				{
					//by Lu 
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							btLogin.setEnabled(false);
							btLogin.setBackgroundColor(Color.GRAY);
						}
					});
					
					CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);
					builder.setTitle((String) getApplication().getResources().getText(R.string.login_prompt));
					builder.setMessage((String) getApplication().getResources().getText(R.string.login_successfully));
					builder.setPositiveButton((String) getApplication().getResources().getText(R.string.confirm), new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							finish();
						}
					});
					builder.create().show();
				}
				else
				{
					CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);
					builder.setTitle((String) getApplication().getResources().getText(R.string.login_prompt));
					builder.setMessage(loginResult);
					builder.setPositiveButton((String) getApplication().getResources().getText(R.string.confirm), new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
			}
		}
	};

	private void registerBroadcasts()
	{
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.thtfit.pos.service.Receiver.action.Login.RECEIVE_DATA");
		mIntentFilter.setPriority(100);
		registerReceiver(mBroadcastReceiver, mIntentFilter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		InitView();

		// checkLoginStatus();

		registerBroadcasts();
	}

	private void InitView()
	{
		etLoginName = (EditText) findViewById(R.id.loginname_edit);
		etPassword = (EditText) findViewById(R.id.password_edit);
		btLogin = (Button) findViewById(R.id.signin_button);
		btLogoutManager = (Button) findViewById(R.id.logout_manager_button);
		btSwitchManager = (Button) findViewById(R.id.switch_manager_button);
		//by Lu
		SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
		loginStatus = preferences.getInt("loginStatus", 0);
		if(loginStatus == 2) {
			btLogin.setEnabled(false);
			btLogin.setBackgroundColor(Color.GRAY);
		}

		btLogin.setOnClickListener(this);
		btLogoutManager.setOnClickListener(this);
		btSwitchManager.setOnClickListener(this);

		etLoginName.setText(Config.get("loginName"));
		etPassword.setText(Config.get("passWord"));
	}

	private void checkLoginStatus()
	{
		SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
		loginStatus = preferences.getInt("loginStatus", 0);
		loginStatusManager = preferences.getInt("loginStatusManager", 0);// 店长登录状态

		// 判断店长是否登录
		if (0 == loginStatusManager)
		{
			// onStop();
			CustomDialog.Builder builder = new CustomDialog.Builder(this);
			builder.setMessage((String) getApplication().getResources().getText(R.string.please_login_first));
			builder.setTitle((String) getApplication().getResources().getText(R.string.prompt));
			builder.setPositiveButton((String) getApplication().getResources().getText(R.string.confirm), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			builder.create().show();
		}

		if (2 == loginStatus)
		{
			// onStop();
			CustomDialog.Builder builder = new CustomDialog.Builder(this);
			builder.setMessage((String) getApplication().getResources().getText(R.string.you_have_login));
			builder.setTitle((String) getApplication().getResources().getText(R.string.prompt));
			builder.setPositiveButton((String) getApplication().getResources().getText(R.string.confirm), new DialogInterface.OnClickListener()
			{
				/* callback from dialog builder. */
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					LoginActivity.this.finish();
				}
			});
			builder.setNeutralButton((String) getApplication().getResources().getText(R.string.switch_account), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					etLoginName.setText(null);
					etPassword.setText(null);
					logoutManager();
					dialog.dismiss();
				}
			});
			builder.setNegativeButton((String) getApplication().getResources().getText(R.string.logout_account), new android.content.DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					etLoginName.setText(null);
					etPassword.setText(null);
					Config.addSaveOption("loginName", "");
					Config.addSaveOption("passWord", "");
					Config.save();

					SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
					Editor editor = preferences.edit();
					editor.putInt("loginStatus", 0);
					editor.commit();
					finish();
				}
			});
			builder.create().show();
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.signin_button:
				login();
				break;

			case R.id.logout_manager_button:
				CustomDialog.Builder builder = new CustomDialog.Builder(this);
				builder.setMessage((String) getApplication().getResources().getText(R.string.confirm_to_logout));
				builder.setTitle((String) getApplication().getResources().getText(R.string.prompt));
				builder.setPositiveButton((String) getApplication().getResources().getText(R.string.confirm), new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						logoutManager();
						btLogin.setEnabled(true);//by Lu
						dialog.dismiss();
						LoginActivity.this.finish();
					}
				});

				builder.setNegativeButton((String) getApplication().getResources().getText(R.string.cancel), new android.content.DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});
				builder.create().show();
				showLock();
				break;

			case R.id.switch_manager_button:
				CustomDialog.Builder builder2 = new CustomDialog.Builder(this);
				builder2.setMessage((String) getApplication().getResources().getText(R.string.admin_account_will_logout));
				builder2.setTitle((String) getApplication().getResources().getText(R.string.prompt));
				builder2.setPositiveButton((String) getApplication().getResources().getText(R.string.confirm), new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{

						logoutManager();
						dialog.dismiss();
					}
				});

				builder2.setNegativeButton((String) getApplication().getResources().getText(R.string.cancel), new android.content.DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});
				builder2.create().show();
				showLock();
				break;

			default:
				break;
		}
	}

	private void logoutManager()
	{
		SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
		preferences.edit().clear().commit();

		Config.clearOption();

		/*
		 * Config.addSaveOption("loginNameManager", "");
		 * Config.addSaveOption("passWordManager", "");
		 * 
		 * Config.addSaveOption("loginName", "");
		 * Config.addSaveOption("passWord", ""); Config.save();
		 */
	}

	private void login()
	{
		if (etLoginName.getText().toString().equals(""))
		{
			etLoginName.requestFocus();
			ShowMsg((String) getApplication().getResources().getText(R.string.please_input_account));
		}
		else if (etPassword.getText().toString().equals(""))
		{
			etPassword.requestFocus();
			ShowMsg((String) getApplication().getResources().getText(R.string.please_input_password));
		}
		else
		{
			String loginName = loginStatusManager == 0 ? etLoginName.getText().toString() : etLoginName.getText()
					.toString();
			String passWord = etPassword.getText().toString();

			// 发送后台登录请求
			Intent tmpIntent = new Intent("com.thtfit.pos.service.Receiver.action.Service.RECEIVE_REQUEST");
			tmpIntent.putExtra("requestAction", "LoginActivity.login");
			tmpIntent.putExtra("responseFilter", "com.thtfit.pos.service.Receiver.action.Login.RECEIVE_DATA");
			tmpIntent.putExtra("loginName", loginName);
			tmpIntent.putExtra("passWord", passWord);
			sendBroadcast(tmpIntent);
		}
	}

	public void showAlertDialog()
	{
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage((String) getApplication().getResources().getText(R.string.this_is_custom_Dialog));
		builder.setTitle((String) getApplication().getResources().getText(R.string.prompt));
		builder.setPositiveButton((String) getApplication().getResources().getText(R.string.confirm), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// 设置你的操作事项
			}
		});

		builder.setNegativeButton((String) getApplication().getResources().getText(R.string.cancel), new android.content.DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	public void showLock()
	{
		SharedPreferences preferences = getSharedPreferences(PosApplication.LOCK, MODE_PRIVATE);
		String patternString = preferences.getString(PosApplication.LOCK_KEY, null);
		if (patternString != null)
		{
			Intent intent = new Intent(this, LockActivity.class);
			intent.putExtra("TAG", TAG);
			startActivity(intent);
			return;
		}
	}

	public void ShowMsg(CharSequence msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	// 销毁对象
	public void onDestroy()
	{
		try
		{
			unregisterReceiver(mBroadcastReceiver);
		}
		catch (Exception e)
		{
		}
		super.onDestroy();
	}
}
