package com.tl.pro.travelkit.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.internet.ServerTalk;
import com.tl.pro.travelkit.internet.SessionWrapper;
import com.tl.pro.travelkit.internet.UrlSource;
import com.tl.pro.travelkit.view.custom.ViewPagerIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PercentActivity extends AppCompatActivity {
	private List<Fragment> mTabContents = new ArrayList<Fragment>();
	private FragmentPagerAdapter mAdapter;
	private ViewPager mViewPager;
	private List<String> mDatas = Arrays.asList("短信1", "短信2", "短信3", "短信4",
			"短信5", "短信6", "短信7", "短信8", "短信9");
//	private List<String> mDatas = Arrays.asList("短信", "收藏", "推荐");

	private ViewPagerIndicator mIndicator;

	private Button mButton1;
	private Button mButton2;
	private EditText mEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vp_indicator);
		mButton1 = (Button) findViewById(R.id.button1);
		mButton2 = (Button) findViewById(R.id.button2);
		mEditText = (EditText) findViewById(R.id.edit);

		mButton1.setOnClickListener(new View.OnClickListener() {
			HttpURLConnection con = null;
			@Override
			public void onClick(View v) {
				new Th1().start();
			}
		});
		mButton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Th2().start();
			}
		});

	}
	HttpURLConnection con = null;

	private class Th1 extends Thread{
		@Override
		public void run() {
			try {
				con = ServerConfigure.getSessionConnection(UrlSource.RESET_PASSWORD, null);

				JSONObject obj = new JSONObject();
				obj.put("userId", "aa");
				obj.put("phone", "18380165651");
				ServerTalk.writeToServer(con.getOutputStream(), obj);

				String cookieval = con.getHeaderField("set-cookie");
				String sessionid = "";
				if(cookieval != null) {
					sessionid = cookieval.substring(0, cookieval.indexOf(";"));
				}
				System.out.println("session 1=======" + sessionid.split("JSESSIONID=")[1]);
				/////////////////////


				if(con.getResponseCode() == ServerConfigure.SERVER_OK){
					//return;
				}
				String ser = ServerTalk.readFromServer(con.getInputStream());
				JsonParser jp = new JsonParser();
				JsonElement je = jp.parse(ser);
				if(!je.isJsonObject()){
					return;
				}
				Gson g = new Gson();
				SessionWrapper sessionWrapperOut = g.fromJson(je, SessionWrapper.class);
				if(sessionWrapperOut.isSuccess()){
					//return true;
				}
				sessionId = sessionid.split("JSESSIONID=")[1];

			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	private class Th2 extends Thread{
		@Override
		public void run() {
			try {
				con = ServerConfigure.getSessionConnection(UrlSource.RESET_PASSWORD_CHECK, sessionId);

				SessionWrapper sw = new SessionWrapper();
				sw.setSessionId(sessionId);
				sw.setExtraInfo("new password");
				sw.setUserId("aa");
				sw.setCode(mEditText.getText().toString());

				ServerTalk.writeToServer(con.getOutputStream(), sw);

				String str = ServerTalk.readFromServer(con.getInputStream());
				if(null == str){
					return;
				}
				JsonParser jp = new JsonParser();
				JsonElement je = jp.parse(str);
				if(!je.isJsonObject()){
					return;
				}
				Gson g = new Gson();
				SessionWrapper wrapper = g.fromJson(je, SessionWrapper.class);
				if(!wrapper.isSuccess()){
					return;
				}
				sessionId = wrapper.getSessionId();
				String extra = wrapper.getExtraInfo();
				/////////////
				System.out.println("session 2=======" + sessionId);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	static String sessionId = "";
}
