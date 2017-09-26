package com.thtfit.pos.conn;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


public class Upload {
	public Upload() {
	}

	public static String[] execute(String responseFilter, String actionUrl, Map<String, String> params, Map<String, File> files) {
		String[] Content = { "", "0", "0", "" };
		try {
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";
			URL uri = new URL(actionUrl);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(30 * 1000); // 缓存的最长时间
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); 
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", CHARSET);
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
			conn.connect();
			// 首先组拼文本类型的参数
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue());
				sb.append(LINEND);
			}

			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());
			// 发送文件数据
			if (files != null) {
				int tmpFileNum = 1;
				for (Map.Entry<String, File> file : files.entrySet()) {
					StringBuilder sb1 = new StringBuilder();
					sb1.append(PREFIX);
					sb1.append(BOUNDARY);
					sb1.append(LINEND);
					sb1.append("Content-Disposition: form-data; name=\"uploadFile\"; filename=\"" + file.getKey() + "\"" + LINEND);
					sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes());
					InputStream is = new FileInputStream(file.getValue());
					long fileSize = file.getValue().length();
					byte[] buffer = new byte[1024];
					long sentSize = 0;
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						sentSize += len;
						outStream.write(buffer, 0, len);
						//SystemClock.sleep(100);
						// 发送进度到界面
					/*	Intent tmpIntent = new Intent(responseFilter);
						tmpIntent.putExtra("ACTION", "UploadProgress");
						tmpIntent.putExtra("ProgressValue", "第" + tmpFileNum + "张_" + PublicClass.autoConvertByteUint(fileSize) + "："
								+ new DecimalFormat(".00%").format((float) sentSize / (float) fileSize));
						service.sendBroadcast(tmpIntent);*/
					}
					is.close();
					outStream.write(LINEND.getBytes());
					tmpFileNum++;
				}
			}
			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			Content[1] = outStream.size() + "";
			
/*			// 发送进度到界面
			Intent tmpIntent = new Intent(responseFilter);
			tmpIntent.putExtra("ACTION", "UploadProgress");
			tmpIntent.putExtra("ProgressValue", "100");
			service.sendBroadcast(tmpIntent);
			*/
			
			// 得到响应码
			int res = conn.getResponseCode();
			Content[0] = res + "";
			if (res == 200) {
				InputStream is = conn.getInputStream();
				StringBuffer StrBuff = new StringBuffer();
				String data = "";
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
				while ((data = br.readLine()) != null) {
					StrBuff.append(data);
				}
				is.close();
				Content[2] = StrBuff.length() + "";
				Content[3] = StrBuff.toString();
			} else {
				Content[3] = res + "";
			}

			outStream.close();

			conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Content;
	}
}
