package com.tl.pro.travelkit.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tl.pro.travelkit.bean.CartDo;
import com.tl.pro.travelkit.bean.GoodsDo;
import com.tl.pro.travelkit.bean.IndentDo;
import com.tl.pro.travelkit.bean.IndentViewDo;
import com.tl.pro.travelkit.bean.MyPage;
import com.tl.pro.travelkit.bean.ShoppingCartDo;
import com.tl.pro.travelkit.bean.UserApplication;
import com.tl.pro.travelkit.bean.user.UserDo;
import com.tl.pro.travelkit.internet.RequestMethod;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.internet.ServerTalk;
import com.tl.pro.travelkit.internet.UrlSource;
import com.tl.pro.travelkit.util.listener.ProgressResponseListener;
import com.tl.pro.travelkit.util.log.L;
import com.tl.pro.travelkit.util.pay.PayResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import cn.edu.zafu.coreprogress.helper.ProgressHelper;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener;


/**
 * 复杂请求构造类
 * 文件上传等
 * Created by Administrator on 2016/5/19.
 */
public class PostMultipart {

	public static final String TAG = "PostMultipart";
	private static final String IMG_CLIENT_ID = UUID.randomUUID().toString();
	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

	private static final OkHttpClient client = new OkHttpClient();

	private static MyPage pageInfo;

	public static MyPage getPageInfo() {
		return pageInfo;
	}


	/**
	 * @param userId  userId
	 * @param request 查询 还是请求
	 * @return
	 */
	public static String userIsShopkeeper(String userId, String request) {
		HttpURLConnection con = null;
		try {
			// 申请
			if ("true".equals(request)) {
				con = ServerConfigure.getConnection(UrlSource.USER_APPLICATION, RequestMethod.POST);
			}
			//查询
			if ("false".equals(request)) {
				con = ServerConfigure.getConnection(UrlSource.QUERY_USER_APPLICATION, RequestMethod.POST);
			}
			if (null == con) {
				return null;
			}
			JSONObject outObj = new JSONObject();
			outObj.put("userId", userId);
			ServerTalk.writeToServer(con.getOutputStream(), outObj);
			if (con.getResponseCode() != ServerConfigure.SERVER_OK) {
				return null;
			}
			String serverStr = ServerTalk.readFromServer(con.getInputStream());
			JSONObject inObj = new JSONObject(serverStr);
			if (!inObj.has("status")) {
				return null;
			}
			if ("true".equals(request)) {
				if (inObj.getBoolean("status")) {
					return "申请成功";
				}
			}
			return inObj.getString("applicationState");

		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static MultipartBuilder addFilePart(MultipartBuilder multipartBuilder, ArrayList<String> filePaths) {
		int i = 0;
		for (String path : filePaths) {
			i++;
			File file = new File(path);
			multipartBuilder.addFormDataPart("photo_" + i, file.getName(), RequestBody.create(null, file));
		}
		return multipartBuilder;
	}

	private static void addTextPart(MultipartBuilder multipartBuilder, HashMap<String, String> param) {
		for (String key : param.keySet()) {
//			multipartBuilder.addFormDataPart(key, param.get(key));
			multipartBuilder.addFormDataPart(key, null, RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf8"), param.get(key)));
		}
	}

	/**
	 * 发布商品
	 *
	 * @param params             文字信息
	 * @param filePaths          文件路径
	 * @param uiProgressListener 文件进度侦听
	 */
	public static void upLoadGoods(List<HashMap<String, String>> params, ArrayList<String> filePaths, UIProgressListener uiProgressListener) {
		MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);

		for (HashMap<String, String> map : params) {
			addTextPart(multipartBuilder, map);
		}
		addFilePart(multipartBuilder, filePaths);
		RequestBody requestBody = multipartBuilder.build();
		//进行包装，使其支持进度回调
		final Request request = new Request.Builder()
				.url(ServerConfigure.SERVER_ADDRESS + UrlSource.UPLOAD_PHOTO_AND_DETAIL)
				.post(ProgressHelper.addProgressRequestListener(
						requestBody, uiProgressListener))
				.build();

		//开始请求
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				Log.e("TAG", "error " + e.getMessage());
			}

			@Override
			public void onResponse(Response response) throws IOException {
				Log.e("TAG", response.body().string());
			}
		});

	}

	/**
	 * 进入系统主页
	 * //			Request request = new Request.Builder()
	 * //					.url(ServerConfigure.SERVER_ADDRESS + UrlSource.GETGOODS)
	 * //					.post(RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), "null"))
	 * //					.build();
	 *
	 * @return 所有查询到的货物对象
	 */
	public static List<GoodsDo> getGoods() {

		List<GoodsDo> list = null;
		HttpURLConnection connection;
		try {
			connection = ServerConfigure.getConnection(UrlSource.GETGOODS, RequestMethod.POST);
			ServerTalk.writeToServer(connection.getOutputStream(), "body");


			if (!(connection.getResponseCode() == ServerConfigure.SERVER_OK)) {
				return null;
			}
			String serString = ServerTalk.readFromServer(connection.getInputStream());
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(serString);
			boolean jo = je.isJsonObject();

			if (!jo) {
				return null;
			}
			JsonObject dataObj = je.getAsJsonObject();
			if (dataObj.has("data")) {
				JsonElement s = dataObj.get("data");
				list = transGoodsDos(s);
			}
			if (dataObj.has("page")) {
				JsonElement page = dataObj.get("page");
				Gson g = new Gson();

				pageInfo = g.fromJson(page, MyPage.class);
			}
		} catch (IOException e) {
			e.printStackTrace();
			pageInfo = null;
		}
		return list;
	}

	public static List<GoodsDo> getGoodsByShopkeeper(String keeperId, MyPage page) {

		List<GoodsDo> list = null;
		HttpURLConnection connection;
		try {
			connection = ServerConfigure.getConnection(UrlSource.GET_GOODS_BY_SHOPKEEPER, RequestMethod.POST);

			UserDo userDo = new UserDo();
			userDo.setShopKeeperId(keeperId);
			Gson gUser = new Gson();
			StringBuffer sb = new StringBuffer();
			sb.append("{user:");
			sb.append(gUser.toJson(userDo)).append(",page:");
			Gson gPage = new Gson();
			sb.append(gPage.toJson(page)).append("}");

			ServerTalk.writeToServer(connection.getOutputStream(), sb.toString());

			if (!(connection.getResponseCode() == ServerConfigure.SERVER_OK)) {
				return null;
			}
			String serString = ServerTalk.readFromServer(connection.getInputStream());
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(serString);
			boolean jo = je.isJsonObject();

			if (!jo) {
				return null;
			}
			JsonObject dataObj = je.getAsJsonObject();
			if (dataObj.has("data")) {
				JsonElement s = dataObj.get("data");
				list = transGoodsDos(s);
			}
			if (dataObj.has("page")) {
				JsonElement p = dataObj.get("page");
				Gson g = new Gson();
				pageInfo = g.fromJson(p, MyPage.class);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 搜索用户指定的关键字商品
	 *
	 * @param key 关键字
	 * @param page 分页
	 * @return 搜索到的商品列表
	 */
	public static List<GoodsDo> getGoodsBySpecialKey(String key, MyPage page) {
		List<GoodsDo> list = null;
		HttpURLConnection connection;
		try {
			connection = ServerConfigure.getConnection(UrlSource.GET_GOODS_BY_SPECIAL_KEY, RequestMethod.POST);

			StringBuffer sb = new StringBuffer();
			sb.append("{key:").append(key);
			sb.append(",page:");
			Gson gPage = new Gson();
			sb.append(gPage.toJson(page)).append("}");

			ServerTalk.writeToServer(connection.getOutputStream(), sb.toString());
			if(ServerConfigure.SERVER_OK != connection.getResponseCode()){
				return null;
			}
			String serString = ServerTalk.readFromServer(connection.getInputStream());
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(serString);
			boolean jo = je.isJsonObject();

			if (!jo) {
				return null;
			}
			JsonObject dataObj = je.getAsJsonObject();
			if (dataObj.has("data")) {
				JsonElement s = dataObj.get("data");
				list = transGoodsDos(s);
			}
			if (dataObj.has("page")) {
				JsonElement p = dataObj.get("page");
				Gson g = new Gson();
				pageInfo = g.fromJson(p, MyPage.class);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<GoodsDo> getGoodsLoadMore(MyPage page) {

		page.setPageNo(page.getPageNo() + 1);
		//default size
		//int size = 2;
		List<GoodsDo> list = null;
		HttpURLConnection connection;
		try {
			connection = ServerConfigure.getConnection(UrlSource.LOAD_GOODS, RequestMethod.POST);

			Gson gson = new Gson();
			ServerTalk.writeToServer(connection.getOutputStream(), gson.toJson(page));
			if (!(connection.getResponseCode() == ServerConfigure.SERVER_OK)) {
				return null;
			}
			String serString = ServerTalk.readFromServer(connection.getInputStream());
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(serString);
			boolean jo = je.isJsonObject();

			if (!jo) {
				return null;
			}
			JsonObject dataObj = je.getAsJsonObject();
			if (dataObj.has("data")) {
				JsonElement s = dataObj.get("data");
				list = transGoodsDos(s);
			}
			if (dataObj.has("page")) {
				JsonElement p = dataObj.get("page");
				Gson g = new Gson();
				pageInfo = g.fromJson(p, MyPage.class);
			}
		} catch (IOException e) {
			e.printStackTrace();
			pageInfo = null;
		}
		return list;
	}

	public static List<IndentDo> getGoodsShopIndent(String keeperId) {

		return null;
	}

	public static List<IndentDo> getGoodsUserIndent(String userId) {

		return null;
	}

	public static boolean addToShoppingCart(String userId, GoodsDo goodsDo, UIProgressListener uiProgressListener) {

		HttpURLConnection con = null;

		try {
			Gson cartGson = new Gson();
			CartDo cartDo = new CartDo();
			cartDo.setUserId(userId);
			cartDo.setGoodsDo(goodsDo);
			cartDo.setGoodsCount(1);

			con = ServerConfigure.getConnection(UrlSource.ADD_TO_SHOPPING_CART, RequestMethod.POST);
			con.connect();
			ServerTalk.writeToServer(con.getOutputStream(), cartGson.toJson(cartDo));

			if (!(con.getResponseCode() == ServerConfigure.SERVER_OK)) {
				return false;
			}
			String serverMessage = ServerTalk.readFromServer(con.getInputStream());

			JsonParser jsonParser = new JsonParser();
			JsonElement jsonElement = jsonParser.parse(serverMessage);
			if (!jsonElement.isJsonObject()) {
				return false;
			}
			JsonObject object = jsonElement.getAsJsonObject();

			if (object.has("status")) {
				System.out.println(object.get("status").getAsBoolean());
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return false;
	}

	public static boolean deleteFromShoppingCart(String goodsId) {
		HttpURLConnection con = null;

		try {
			con = ServerConfigure.getConnection(UrlSource.DELETE_FROM_SHOPPING_CART, RequestMethod.POST);
			JSONObject object = new JSONObject();
			object.put("goodsId", goodsId);
			ServerTalk.writeToServer(con.getOutputStream(), object);

			String serverStr = ServerTalk.readFromServer(con.getInputStream());

			JSONObject objectIn = new JSONObject(serverStr);
			if (!objectIn.has("status")) {
				return false;
			}
			if (objectIn.getBoolean("status")) {
				return true;
			}

		} catch (JSONException | IOException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return false;
	}

	/**
	 * 根据用户的id 查询用户的购物车
	 *
	 * @param userId userId
	 * @return
	 */
	public static List<ShoppingCartDo> queryUserCart(String userId) {

		List<ShoppingCartDo> retDataList = null;
		HttpURLConnection con = null;
		try {
			con = ServerConfigure.getConnection(UrlSource.QUERY_SHOPPING_CART, RequestMethod.POST);
			JSONObject objOut = new JSONObject();
			objOut.put(CommonText.userId, userId);
			ServerTalk.writeToServer(con.getOutputStream(), objOut);

			String serverStr = ServerTalk.readFromServer(con.getInputStream());
			JsonParser jp = new JsonParser();
			JsonElement jsonElement = jp.parse(serverStr);

			if (jsonElement.isJsonObject()) {
				System.out.println("obj");
				return null;
			}
			if (jsonElement.isJsonArray()) {
				System.out.println("json array");
				retDataList = transCartDos(jsonElement);
			}

		} catch (IOException | JSONException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return retDataList;
	}


	public static boolean buyGoods(String buyerId, ShoppingCartDo goodsDo, int state) {

		IndentDo indentDo = new IndentDo();
		indentDo.setBuyerId(buyerId);
		indentDo.setShopKeeperId(goodsDo.getShopKeeperId());
		indentDo.setGoodsId(goodsDo.getGoodsId());
		indentDo.setGoodsSize(goodsDo.getGoodsSize());
		//支付成功
		if (state == PayResult.PAY_SUCCESS) {
			state = IndentDo.State.WAIT_DELIVER.getValue();
		}
		indentDo.setIndentState(state);
		indentDo.setGoodsType(goodsDo.getGoodsType());

		Gson gsonIndent = new Gson();
		String jsonString = gsonIndent.toJson(indentDo);

		HttpURLConnection con = null;
		try {
			con = ServerConfigure.getConnection(UrlSource.CREATE_INDENT, RequestMethod.POST);
			ServerTalk.writeToServer(con.getOutputStream(), jsonString);

			String serverString = ServerTalk.readFromServer(con.getInputStream());
			JSONObject object = new JSONObject(serverString);
			if (!object.has("status")) {
				return false;
			}
			if (object.getBoolean("status")) {
				return true;
			}

		} catch (IOException | JSONException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return false;
	}

	/**
	 * 查看所有订单
	 *
	 * @return 已查询到的订单列表
	 */
	public static ArrayList<IndentViewDo> queryIndentAll(String userId) {
		ArrayList<IndentViewDo> resultList = null;
		HttpURLConnection connection = null;

		try {
			connection = ServerConfigure.getConnection(UrlSource.QUERY_INDENT_ALL, RequestMethod.POST);
			JSONObject objOut = new JSONObject();
			objOut.put("userId", userId);
			ServerTalk.writeToServer(connection.getOutputStream(), objOut);

			String serverStr = ServerTalk.readFromServer(connection.getInputStream());
			if (null == serverStr || serverStr.length() == 0) {
				L.e(TAG, "server response nothing !!!");
				return null;
			}
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(serverStr);
			if (!je.isJsonObject()) {
				L.e(TAG, "server response format error");
				return null;
			}
			JsonObject jo = je.getAsJsonObject();
			je = jo.get("data");
			if (!je.isJsonArray()) {
				L.e(TAG, "server response format error");
				return null;
			}
			resultList = transIndentViewDao(je);

		} catch (IOException | JSONException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return resultList;
	}

	/**
	 * 查看所有订单
	 *
	 * @return 已查询到的订单列表
	 */
	public static ArrayList<IndentViewDo> queryIndentByState(IndentViewDo indentViewDo) {
		ArrayList<IndentViewDo> resultList = new ArrayList<>();

		Gson g = new Gson();
		String str = g.toJson(indentViewDo);
		HttpURLConnection con;
		try {
			con = ServerConfigure.getConnection(UrlSource.QUERY_INDENT_CUSTOM, RequestMethod.POST);
			ServerTalk.writeToServer(con.getOutputStream(), str);

			String serverStr = ServerTalk.readFromServer(con.getInputStream());
			L.e(TAG, serverStr);
			JsonParser jsonParser = new JsonParser();
			JsonElement je = jsonParser.parse(serverStr);
			if (!je.isJsonObject()) {
				L.e(TAG, "server data format error!");
				return null;
			}
			//规定是：JsonArray
			JsonObject data = je.getAsJsonObject();

			JsonArray ja = data.getAsJsonArray("data");
			if (null == ja || ja.size() == 0) {
				return resultList;
			}
			for (JsonElement aJa : ja) {
				IndentViewDo viewDo = g.fromJson(aJa, IndentViewDo.class);
				resultList.add(viewDo);
			}
		} catch (IOException e) {
			L.e(TAG, e.toString());
			e.printStackTrace();
		}
		return resultList;
	}

	public static ArrayList<UserDo> queryAllUser() {
		ArrayList<UserDo> userDos = null;
		HttpURLConnection con = null;
		try {
			con = ServerConfigure.getConnection(UrlSource.QUERY_ALL_USER, RequestMethod.POST);
			if (con.getResponseCode() != ServerConfigure.SERVER_OK) {
				return null;
			}
			String serverStr = ServerTalk.readFromServer(con.getInputStream());
			System.out.println(serverStr);
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(serverStr);
			if (!je.isJsonObject()) {
				return null;
			}
			JsonObject jo = je.getAsJsonObject();
			if (jo.has("data")) {
				userDos = transToUserDo(jo.get("data"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return userDos;
	}

	/**
	 * 查看所有的申请（未审批的申请）
	 *
	 * @return
	 */
	public static ArrayList<UserApplication> queryUserApplication() {
		ArrayList<UserApplication> userApplications = null;
		HttpURLConnection con = null;
		try {
			con = ServerConfigure.getConnection(UrlSource.QUERY_ALL_USER_APPLICATION, RequestMethod.POST);
			if (con.getResponseCode() != ServerConfigure.SERVER_OK) {
				return null;
			}
			String serverStr = ServerTalk.readFromServer(con.getInputStream());
			System.out.println(serverStr);
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(serverStr);
			if (!je.isJsonObject()) {
				return null;
			}
			JsonObject jo = je.getAsJsonObject();
			if (jo.has("data")) {
				userApplications = transToUserApplication(jo.get("data"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return userApplications;
	}

	/**
	 * 处理用户的申请信息
	 *
	 * @return true if 服务器接受请求
	 */
	public static boolean handleUserApplication(int applicationId, boolean isAccept) {
		HttpURLConnection con = null;
		try {
			con = ServerConfigure.getConnection(UrlSource.HANDLE_USER_APPLICATION, RequestMethod.POST);
			JSONObject outObj = new JSONObject();
			outObj.put("applicationId", applicationId);
			outObj.put("isAccept", isAccept);
			ServerTalk.writeToServer(con.getOutputStream(), outObj);
			if (con.getResponseCode() != ServerConfigure.SERVER_OK) {
				return false;
			}
			String serverStr = ServerTalk.readFromServer(con.getInputStream());
			if (null == serverStr) {
				return false;
			}
			JSONObject jo = new JSONObject(serverStr);
			if (!jo.has("status")) {
				return false;
			}
			return jo.getBoolean("status");
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return false;
	}


	public static boolean deleteUser(String userId) {
		HttpURLConnection con;
		try {
			con = ServerConfigure.getConnection(UrlSource.DELETE_USER, RequestMethod.POST);
			JSONObject obj = new JSONObject();
			obj.put("userId", userId);
			ServerTalk.writeToServer(con.getOutputStream(), obj);
			if (con.getResponseCode() != ServerConfigure.SERVER_OK) {
				return false;
			}
			String serverStr = ServerTalk.readFromServer(con.getInputStream());
			JSONObject jo = new JSONObject(serverStr);
			if (!jo.has("status")) {
				return false;
			}
			return jo.getBoolean("status");


		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}

		return false;
	}


	private static ArrayList<UserApplication> transToUserApplication(JsonElement jsonElement) {
		JsonArray jsonArray = jsonElement.getAsJsonArray();
		ArrayList<UserApplication> retList = new ArrayList<>();
		for (JsonElement aJsonArray : jsonArray) {
			Gson g = new Gson();
			retList.add(g.fromJson(aJsonArray, UserApplication.class));
		}
		return retList;
	}

	private static ArrayList<UserDo> transToUserDo(JsonElement jsonElement) {
		JsonArray jsonArray = jsonElement.getAsJsonArray();
		ArrayList<UserDo> retList = new ArrayList<>();
		for (JsonElement aJsonArray : jsonArray) {
			Gson g = new Gson();
			retList.add(g.fromJson(aJsonArray, UserDo.class));
		}
		return retList;
	}


	/**
	 * 转换成goodsDodu集合
	 *
	 * @param jsonElement
	 * @return
	 */
	private static List<GoodsDo> transGoodsDos(JsonElement jsonElement) {
		if (!jsonElement.isJsonArray()) {
			return null;
		}
		JsonArray jsonArray = jsonElement.getAsJsonArray();
		Iterator<JsonElement> it = jsonArray.iterator();
		List<GoodsDo> list = new ArrayList<>();
		while (it.hasNext()) {
			JsonElement element = it.next();
			Gson gson = new Gson();
			GoodsDo goodsDo = gson.fromJson(element, GoodsDo.class);
			System.out.println("while--" + goodsDo.toString());
			list.add(goodsDo);
		}
		return list;
	}

	/**
	 * 转换成cartList集合
	 *
	 * @param jsonElement
	 * @return
	 */
	private static List<ShoppingCartDo> transCartDos(JsonElement jsonElement) {
		if (!jsonElement.isJsonArray()) {
			return null;
		}
		JsonArray jsonArray = jsonElement.getAsJsonArray();
		Iterator<JsonElement> it = jsonArray.iterator();
		List<ShoppingCartDo> list = new ArrayList<>();
		while (it.hasNext()) {
			JsonElement element = it.next();
			Gson gson = new Gson();
			ShoppingCartDo cartDo = gson.fromJson(element, ShoppingCartDo.class);
			list.add(cartDo);
		}
		return list;
	}

	private static ArrayList<IndentViewDo> transIndentViewDao(JsonElement jsonElement) {
		if (!jsonElement.isJsonArray()) {
			return null;
		}
		JsonArray jsonArray = jsonElement.getAsJsonArray();
		Iterator<JsonElement> it = jsonArray.iterator();
		ArrayList<IndentViewDo> list = new ArrayList<>();
		while (it.hasNext()) {
			JsonElement element = it.next();
			Gson gson = new Gson();
			IndentViewDo viewDo = gson.fromJson(element, IndentViewDo.class);
			list.add(viewDo);
		}
		return list;
	}

	// 下载
	private void download() {
		//这个是非ui线程回调，不可直接操作UI
		final ProgressResponseListener progressResponseListener = new ProgressResponseListener() {
			@Override
			public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
				Log.e("TAG", "bytesRead:" + bytesRead);
				Log.e("TAG", "contentLength:" + contentLength);
				Log.e("TAG", "done:" + done);
				if (contentLength != -1) {
					//长度未知的情况下回返回-1
					Log.e("TAG", (100 * bytesRead) / contentLength + "% done");
				}
				Log.e("TAG", "================================");
			}
		};


		//这个是ui线程回调，可直接操作UI
		final UIProgressResponseListener uiProgressResponseListener = new UIProgressResponseListener() {
			@Override
			public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
				Log.e("TAG", "bytesRead:" + bytesRead);
				Log.e("TAG", "contentLength:" + contentLength);
				Log.e("TAG", "done:" + done);
				if (contentLength != -1) {
					//长度未知的情况下回返回-1
					Log.e("TAG", (100 * bytesRead) / contentLength + "% done");
				}
				Log.e("TAG", "================================");
				//ui层回调
				//downloadProgeress.setProgress((int) ((100 * bytesRead) / contentLength));
				//Toast.makeText(getApplicationContext(), bytesRead + " " + contentLength + " " + done, Toast.LENGTH_LONG).show();
			}
		};

		//构造请求
		final Request request1 = new Request.Builder()
				.url("http://121.41.119.107:81/test/1.doc")
				.build();

		//包装Response使其支持进度回调
		ProgressHelper.addProgressResponseListener(client, uiProgressResponseListener).newCall(request1).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				Log.e("TAG", "error ", e);
			}

			@Override
			public void onResponse(Response response) throws IOException {
				Log.e("TAG", response.body().string());
			}
		});
	}
}
