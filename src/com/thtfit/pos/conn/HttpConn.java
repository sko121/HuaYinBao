package com.thtfit.pos.conn;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.NameValuePair;

public class HttpConn {
	public HttpConn() {
	}

	public static String[] getServerHttpData(String url, List<NameValuePair> Parameter,String SessionID) {
		String[] Content = { "", "", "0", "0", "" };
		try {

			// 创建URL对象
			URL loginUrl = new URL(url);

			// 初始化连接对象
			HttpURLConnection connection = (HttpURLConnection) loginUrl
					.openConnection();

			// 设置连接参数
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setConnectTimeout(6000);
			connection.setReadTimeout(6000);
			// HttpURLConnection.setFollowRedirects(true);connection.connect();
			connection.setInstanceFollowRedirects(false);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			if (!SessionID.equals(""))
				connection.setRequestProperty("Cookie", SessionID);


			// 开始连接
			connection.connect();
			// 创建输出流对象
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			// 处理提交参数
			String content = "";
			if(Parameter != null){
				System.out.println("Parameter.size()==========="+Parameter.size());
				for (int i = 0; i < Parameter.size(); i++) {
					if(i > 0){
						content += "&";
					}
					content += URLEncoder.encode(Parameter.get(i).getName(), "UTF-8") + "=" + URLEncoder.encode(Parameter.get(i).getValue(), "UTF-8");
				}
			}

			out.writeBytes(content);
			out.flush();

			// 获得上行数据大小
			Content[2] = out.size() + "";

			// 关闭输出流
			out.close();

			// 获得响应代码,200正常,404未找到
			Content[0] = connection.getResponseCode() + "";
			
			//处理Session
			String key = "";
			// 当提交空会话ID时，服务器返回新会话ID，否则服务不返回会话ID
			if (SessionID.equals("")) {
				if (connection != null) {
					for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
						if (key.equalsIgnoreCase("set-cookie")) {
							SessionID = connection.getHeaderField(key);

							System.out.println("SessionID:" + SessionID);

							SessionID = SessionID.substring(0, SessionID.indexOf(";"));
						}
					}
				}
			}
			SessionID = SessionID.indexOf("JSESSIONID=") >= 0 ? SessionID : "";
			// System.out.println("HTTPSessionID:"+SessionID);
			Content[1] = SessionID;
			

			// 创建输入流对象
			InputStream is = connection.getInputStream();

			// 检索编码
			String WebEncoding = "UTF-8";
			if (connection.getHeaderFields().toString().indexOf("UTF-8") > -1) {
				WebEncoding = "UTF-8";
			}

			// 创建缓冲对象
			StringBuffer sb = new StringBuffer();

			String data = "";

			// 创建缓冲读取对象
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					WebEncoding));
			while ((data = br.readLine()) != null) {
				sb.append(data);
			}
			is.close();

			// 保存流量
			Content[3] = sb.length() + "";
			Content[4] = sb.toString();

			connection.disconnect();
			// System.out.println("连接结束");
		} catch (IOException e) {
		}
		return Content;
	}
}
