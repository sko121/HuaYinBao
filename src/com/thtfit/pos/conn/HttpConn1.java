package com.thtfit.pos.conn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpConn1 {
	public HttpConn1() {
	}

	public static void getServerHttpData(String url, String parameter,
			String SessionID) {
		URL conUrl;
		try {
			conUrl = new URL(url+parameter);
			// 使用HttpURLConnection打开连接
			HttpURLConnection urlConn;
			try {
				urlConn = (HttpURLConnection) conUrl
						.openConnection();
				// 设置输入和输出流
				urlConn.setDoOutput(true);
				urlConn.setDoInput(true);
				urlConn.setConnectTimeout(90000);
				urlConn.setReadTimeout(90000);
				urlConn.setInstanceFollowRedirects(false);
				urlConn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				
				// 得到读取的内容(流)
				InputStreamReader in = new InputStreamReader(
						urlConn.getInputStream());

				// 为输出创建BufferedReader
				BufferedReader buffer = new BufferedReader(in);
				String inputLine = "";
				String resultData = "";
				
				while (((inputLine = buffer.readLine()) != null)) {
					// 我们在每一行后面加上一个"\n"来换行
					resultData += inputLine + "\n";

				}
				// 关闭InputStreamReader
				in.close();
				
				// 关闭http连接
				urlConn.disconnect();
				
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

	}
}
