package com.thtfit.pos.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.thtfit.pos.R;

public class OptionList {

	private List<String> settingList;
	private List<String> searchList;
	private List<String> manageList;
	private List<String> reportList;
	private List<String> languageList;
	private List<String> list;



	public final static int TYPE_SETTING = 1;
	public final static int TYPE_SEARCH = 2;
	public final static int TYPE_MANAGE = 3;
	public final static int TYPE_REPORT = 4;
	public final static int TYPE_LANGUAGE = 5;
	public OptionList(Context context){
		this.mContext = context;
	}

	public void parse(int type) {
		switch (type) {
		case TYPE_SETTING:
			settingList = new ArrayList<String>();
			settingList.add((String) mContext.getResources().getText(R.string.general_settings));
			settingList.add((String) mContext.getResources().getText(R.string.hardware_setup));
			settingList.add((String) mContext.getResources().getText(R.string.invoice_information));
			settingList.add((String) mContext.getResources().getText(R.string.home));
			settingList.add((String) mContext.getResources().getText(R.string.trend_chart));
			settingList.add((String) mContext.getResources().getText(R.string.languages));
			setList(settingList);
			break;
		case TYPE_SEARCH:
			searchList = new ArrayList<String>();
			searchList.add((String) mContext.getResources().getText(R.string.name_search));
			searchList.add((String) mContext.getResources().getText(R.string.number_search));
			searchList.add((String) mContext.getResources().getText(R.string.price_search));
			searchList.add((String) mContext.getResources().getText(R.string.date_search));
			setList(searchList);
			break;
		case TYPE_MANAGE:
			manageList = new ArrayList<String>();
			manageList.add((String) mContext.getResources().getText(R.string.account_manager));
			manageList.add((String) mContext.getResources().getText(R.string.add_saler));
			manageList.add((String) mContext.getResources().getText(R.string.add_products));
			manageList.add((String) mContext.getResources().getText(R.string.add_type));
			manageList.add((String) mContext.getResources().getText(R.string.inventory_management));
			setList(manageList);
			break;
		case TYPE_REPORT:
			reportList = new ArrayList<String>();
			reportList.add((String) mContext.getResources().getText(R.string.sales_trends));
			reportList.add((String) mContext.getResources().getText(R.string.purchase_and_sale));
			reportList.add((String) mContext.getResources().getText(R.string.profit));
			setList(reportList);
			break;
		case TYPE_LANGUAGE:
			reportList = new ArrayList<String>();
			reportList.add((String) mContext.getResources().getText(R.string.flow_system));
			reportList.add("English");
			reportList.add("中文");
			setList(reportList);
			break;
			
		default:
			break;
		}

	}

	public List<String> getSettingList() {
		return settingList;
	}

	public void setSettingList(List<String> settingList) {
		this.settingList = settingList;
	}

	public List<String> getSearchList() {
		return searchList;
	}

	public void setSearchList(List<String> searchList) {
		this.searchList = searchList;
	}

	public List<String> getManageList() {
		return manageList;
	}

	public void setManageList(List<String> manageList) {
		this.manageList = manageList;
	}

	public List<String> getReportList() {
		return reportList;
	}

	public void setReportList(List<String> reportList) {
		this.reportList = reportList;
	}
	
	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}
	private Context mContext;

}
