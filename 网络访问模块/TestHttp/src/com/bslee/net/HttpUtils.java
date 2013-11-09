package com.bslee.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtils {
	

	/**
	 * Post�����ȡJSON����
	 * 
	 * @param paramMap
	 * @return
	 */
	public static String GetJsonByPost(Map<String, String> paramMap) {
		String result = null;
		HttpPost httpRequest = new HttpPost(paramMap.get("suburl"));
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> param : paramMap.entrySet()) {
			params.add(new BasicNameValuePair(param.getKey(), param.getValue()));
		}
		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httpRequest.setEntity(httpEntity);
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(httpResponse.getEntity());
				return result;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	/**
	 * GET�����ȡJSON����
	 * @param urlStr
	 * @return
	 */
	public static String getJsonByGet(String urlStr) {
		try {// ��ȡHttpURLConnection���Ӷ���
			URL url = new URL(urlStr);
			HttpURLConnection httpConn = (HttpURLConnection) url
					.openConnection();
			// ������������
			httpConn.setConnectTimeout(3000);
			httpConn.setDoInput(true);
			httpConn.setRequestMethod("GET");
			// ��ȡ��Ӧ��
			int respCode = httpConn.getResponseCode();
			if (respCode == 200) {
				return ConvertStream2Json(httpConn.getInputStream());
			}
		} catch (MalformedURLException e) {	
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String ConvertStream2Json(InputStream inputStream)
			throws IOException {
		String jsonStr = "";
		// ByteArrayOutputStream�൱���ڴ������
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		// ��������ת�Ƶ��ڴ��������
		while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
			out.write(buffer, 0, len);
		}
		// ���ڴ���ת��Ϊ�ַ���
		jsonStr = new String(out.toByteArray());
		return jsonStr;
	}
}
