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
import com.tl.pro.travelkit.bean.ShoppingCartDo;
import com.tl.pro.travelkit.internet.RequestMethod;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.internet.ServerTalk;
import com.tl.pro.travelkit.internet.UrlSource;
import com.tl.pro.travelkit.util.listener.ProgressResponseListener;
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
	 * @param params 文字信息
	 * @param filePaths 文件路径
	 * @param uiProgressListener 文件进度侦听
	 *
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
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
	 * @param userId
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
//			JsonElement jsonElement = jp.parse(new JsonReader(new InputStreamReader(con.getInputStream())));
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
			state = IndentDo.State.PAY_SUCCESS.getValue();
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
			System.out.println("while--" + cartDo.toString());
			list.add(cartDo);
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
