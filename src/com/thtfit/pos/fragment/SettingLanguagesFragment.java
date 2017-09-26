package com.thtfit.pos.fragment;



import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.thtfit.pos.R;
import com.thtfit.pos.activity.MainActivity;
import com.thtfit.pos.activity.SettingActivity;
import com.thtfit.pos.iChart.IChartFragment;
import com.thtfit.pos.model.CustomDialog;
import com.thtfit.pos.model.tipInputListener;
import com.thtfit.pos.util.OptionList;

public class SettingLanguagesFragment extends Fragment
{
	private View mView;
	private ListView listView;
	private static final int TYPE_LANGUAGE = 5;

	private ArrayAdapter<String> adapter;
	private List<String> data;
	private String languages;
	
	Resources resources;
	Configuration config;
	DisplayMetrics dm;

	Context ctx;
	SharedPreferences spLanguageConfig ;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mView = inflater.inflate(R.layout.fragment_settings_languages, container,
				false);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		listView = (ListView) mView.findViewById(R.id.language_list);
		getSettingConfig();
		
		resources =getResources();
		config = resources.getConfiguration();
		dm = resources.getDisplayMetrics();
//		config.locale = Locale.SIMPLIFIED_CHINESE;
//		resources.updateConfiguration(config, dm);
		//对应逻辑判断
		if(!languages.equals("系统当前语言")){
			
		}
		if(languages.equals("default")){
			config.locale = Locale.getDefault();
		}else if(languages.equals("zh_CN")){
			config.locale = Locale.CHINESE;
		}else if(languages.equals("en")){
			config.locale = Locale.ENGLISH;
		}else{
			config.locale = Locale.ENGLISH;
		}
		data = new ArrayList<String>();
		OptionList optionList = new OptionList(getActivity());
		optionList.parse(TYPE_LANGUAGE);

		data = optionList.getList();

		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, data);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					config.locale = Locale.getDefault();
					resources.updateConfiguration(config, dm);
					languages = "default";
					saveSettingConfig();
					resources.flushLayoutCache();
					break;
				case 1:
					config.locale = Locale.ENGLISH;
					resources.updateConfiguration(config, dm);
					languages = "en";
					saveSettingConfig();
					resources.flushLayoutCache();
					break;
				case 2:
					config.locale = Locale.SIMPLIFIED_CHINESE;
					resources.updateConfiguration(config, dm);
					languages = "zh_CN";
					saveSettingConfig();
					resources.flushLayoutCache();
					break;
				default:
					config.locale = Locale.getDefault();
					resources.updateConfiguration(config, dm);
					languages = "default";
					saveSettingConfig();
					resources.flushLayoutCache();
					break;
				}
				//getActivity().finish();
				Intent intent=new Intent();
				intent.setClass(getActivity(),MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				getActivity().startActivity(intent);
			}
		});
	}
	private void getSettingConfig(){
		ctx = getActivity();
		spLanguageConfig = ctx.getSharedPreferences("LANGUAGECONFIG", Context.MODE_PRIVATE);
		languages = spLanguageConfig.getString("languages", "default");
	}
	private void saveSettingConfig(){
		ctx = getActivity();
		spLanguageConfig = ctx.getSharedPreferences("LANGUAGECONFIG", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spLanguageConfig.edit();
		editor.putString("languages", languages);
		editor.commit();
	}
}
