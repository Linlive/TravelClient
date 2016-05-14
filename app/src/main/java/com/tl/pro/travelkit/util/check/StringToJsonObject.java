package com.tl.pro.travelkit.util.check;

import com.google.gson.Gson;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.internet.ServerInfoObj;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 将从服务器得到的json字符串封装成与服务器同意的通信对象
 * Created by Administrator on 2016/5/8.
 */
public class StringToJsonObject {

	/**
	 * 将解析从服务器得到的封装后的data
	 * @param jsonString 从服务器读取到的数据
	 * @return 服务器与客户端通信的格式（模板）
	 */
	public static ServerInfoObj<JSONObject> toJsonObject(String jsonString){
		Gson gson = new Gson();
		ServerInfoObj tmpServerInfoObj;
		ServerInfoObj<JSONObject> retServerInfoObj;
		tmpServerInfoObj = gson.fromJson(jsonString, ServerInfoObj.class);

		if (tmpServerInfoObj == null) return null;
		String map = tmpServerInfoObj.getData().toString();
		JSONObject object = null;
		try {
			object = new JSONObject(map).getJSONObject(ServerConfigure.DATA_IN_SERVER_FLAG);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		retServerInfoObj = new ServerInfoObj<>();
		retServerInfoObj.setData(object);
		retServerInfoObj.setOperateSuccess(tmpServerInfoObj.getOperateSuccess());
		retServerInfoObj.setErrorCode(tmpServerInfoObj.getErrorCode());
		retServerInfoObj.setDigestMessage(tmpServerInfoObj.getDigestMessage());

		return retServerInfoObj;
	}

}
