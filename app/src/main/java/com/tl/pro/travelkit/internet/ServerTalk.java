package com.tl.pro.travelkit.internet;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 与服务器的数据传输
 * Created by Administrator on 2016/5/1.
 */
public class ServerTalk {

	@Deprecated
	public static JSONObject readFromServerBack(InputStream in) throws IOException {

		String temp;
		//线程安全stringbuilder//单线程中使用stringbuffer
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		br.close();

		if (0 == sb.length()) {
			return null;
		}
		JSONObject object = null;
		try {
			object = new JSONObject(sb.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

	public static String readFromServer(InputStream in) throws IOException {

		String temp;
		//线程安全string builder//单线程中使用string buffer
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		br.close();

		if (0 == sb.length()) {
			return null;
		}
		return sb.toString();
	}

	/**
	 * @param writer
	 * @param objectString
	 * @return
	 */
	public static boolean writeToServer(OutputStream writer, String objectString) {
		try {
			writer.write(objectString.getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
			L.e("编码错误或写入服务器失败！");
			return false;
		}
		return true;
	}

	public static boolean writeToServer(OutputStream writer, JSONObject object) {

		return writeToServer(writer, object.toString());
	}
	public static boolean writeToServer(OutputStream writer, ServerInfoObj object) {

		Gson gson = new Gson();
		return writeToServer(writer, gson.toJson(object));
	}
}
