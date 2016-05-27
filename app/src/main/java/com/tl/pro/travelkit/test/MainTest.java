package com.tl.pro.travelkit.test;

import android.database.sqlite.SQLiteDatabase;

import com.tl.pro.travelkit.internet.ServerTalk;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 测试
 * Created by Administrator on 2016/4/24.
 */
public class MainTest {

	public static void main(String[] args) throws Exception {

//		HttpClient client = new HttpClient();
//		PostMethod post = new PostMethod("http://gbk.sms.webchinese.cn");
//		post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");//在头文件中设置转码
//		NameValuePair[] data = {
//				new NameValuePair("Uid", "本站用户名"),
//				new NameValuePair("Key", "接口安全秘钥"),
//				new NameValuePair("smsMob", "手机号码"),
//				new NameValuePair("smsText", "验证码：8888")};
//		post.setRequestBody(data);
//
//		client.executeMethod(post);
//		Header[] headers = post.getResponseHeaders();
//		int statusCode = post.getStatusCode();
//		System.out.println("statusCode:" + statusCode);
//		for (Header h : headers) {
//			System.out.println(h.toString());
//		}
//		String result = new String(post.getResponseBodyAsString().getBytes("gbk"));
//		System.out.println(result); //打印返回消息状态
//		post.releaseConnection();
	}



	private static void createTable(SQLiteDatabase db) {
		//创建表SQL语句
		String stu_table = "create table usertable(_id integer primary key autoincrement, sname varchar(20),snumber integer(2))";
		//执行SQL语句
		db.execSQL(stu_table);
	}

	public static String requestData(String address, String params) {

		HttpURLConnection conn = null;
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			}};

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());

			//ip host verify
			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					return urlHostName.equals(session.getPeerHost());
				}
			};

			//set ip host verify
			HttpsURLConnection.setDefaultHostnameVerifier(hv);

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			URL url = new URL(address);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");// POST
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			// set params ;post params
			if (params != null) {
				conn.setDoOutput(true);
				DataOutputStream out = new DataOutputStream(conn.getOutputStream());
				out.write(params.getBytes(Charset.forName("UTF-8")));
				out.flush();
				out.close();
			}
			conn.connect();
			//get result
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String result = ServerTalk.readFromServer(conn.getInputStream());
//				String aa = ServerTalk.readFromServer(c)
				return result;
			} else {
				System.out.println(conn.getResponseCode() + " " + conn.getResponseMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		return null;
	}

}
