package com.thtfit.pos.iChart;

import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONObject;

public class PackageChartData {
	
	/**
	 * 将单一数据源打包成JSON格式
	 * @param chartData 存储单一数据源的数组
	 * @return
	 */
	public static String PackageData(Vector<Item> chartData){
		JSONArray root = new JSONArray();
		try {
			for(int i = 0; i < chartData.size(); ++i){
				JSONObject temp = new JSONObject();
				temp.put("name", chartData.get(i).getName());
				temp.put("value", chartData.get(i).getValue());
				temp.put("color", chartData.get(i).getColor());
				
				root.put(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return root.toString();
	}
	
	/**
	 * 将多值数据源打包成JSON格式
	 * @param chartData
	 * @return
	 */
	public static String PackageDoubleData(Vector<Item> chartData){
		JSONArray root = new JSONArray();
		try {
			for(int i = 0; i < chartData.size(); ++i){
				JSONObject temp = new JSONObject();
				temp.put("name", chartData.get(i).getName());
				
				JSONArray values = new JSONArray();
				for(int j = 0; j < chartData.get(i).getValues().length; ++j){
					values.put(chartData.get(i).getValues()[j]);
				}
				temp.put("value", values);
				temp.put("color", chartData.get(i).getColor());
				
				root.put(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return root.toString();
	}
	
	/**
	 * 将多值数据源相应的data_labels打包成JSON格式
	 * @param dataLabels
	 * @return
	 */
	public static String PackageDataLabels(String[] dataLabels){
		JSONArray root = new JSONArray();
		try {
			for(int i = 0; i < dataLabels.length; ++i){
				root.put(dataLabels[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return root.toString();
	}
}
