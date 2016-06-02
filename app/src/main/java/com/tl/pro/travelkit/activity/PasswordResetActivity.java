package com.tl.pro.travelkit.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.internet.RequestMethod;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.internet.ServerTalk;
import com.tl.pro.travelkit.internet.SessionWrapper;
import com.tl.pro.travelkit.internet.UrlSource;
import com.tl.pro.travelkit.util.CommonText;
import com.tl.pro.travelkit.util.check.NormalCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

public class PasswordResetActivity extends AppCompatActivity implements View.OnClickListener {

	private Button phoneSend;
	private Button emailSend;
	private Button verificationSend;
	private Button confirmSend;

	private EditText phoneNumber;
	private EditText emailAddress;
	private EditText verificationCode;
	private EditText newPassword;

	static String sessionId = "";
	Intent mIntent;
	String userId;
	String email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pssword_reset);
		mIntent = getIntent();
		userId = mIntent.getStringExtra(CommonText.userId);
		email = mIntent.getStringExtra(CommonText.emailAddr);
		initView();
	}

	private void initView() {
		phoneSend = (Button) findViewById(R.id.app_password_reset_phone_number_button);
		emailSend = (Button) findViewById(R.id.app_password_reset_email_address_button);
		verificationSend = (Button) findViewById(R.id.app_password_reset_verification_send_button);
		confirmSend = (Button) findViewById(R.id.app_reset_password_confirm);

		phoneNumber = (EditText) findViewById(R.id.app_password_reset_phone_number);
		emailAddress = (EditText) findViewById(R.id.app_password_reset_email_address);
		verificationCode = (EditText) findViewById(R.id.app_password_reset_verification_send);
		newPassword = (EditText) findViewById(R.id.app_reset_password_new_password_edit);

		phoneSend.setOnClickListener(this);
		emailSend.setOnClickListener(this);
		verificationSend.setOnClickListener(this);
		confirmSend.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mIntent != null){
			String phone = mIntent.getStringExtra("userId");
			if(NormalCheck.isPhoneNumber(phone)){
				phoneNumber.setText(phone);
				emailAddress.setEnabled(false);
			}
			if(NormalCheck.isEmail(email)){
				emailAddress.setText(email);
			}
		}
	}

	@Override
	public void onClick(View v) {
		String phoneNumberValue;
		switch (v.getId()) {
			case R.id.app_password_reset_phone_number_button:
				phoneNumberValue = phoneNumber.getText().toString();
				if (!NormalCheck.isPhoneNumber(phoneNumberValue)) {
					Toast.makeText(this, R.string.resetPasswordPhoneEmpty, Toast.LENGTH_SHORT).show();
					break;
				}
				// send sms
//				if (resetPassword(phoneNumberValue, null)) {
//					Toast.makeText(this, R.string.resetPasswordSuccess, Toast.LENGTH_SHORT).show();
//				}
				new RequestSmsAsync().execute(phoneNumberValue);

				break;
			case R.id.app_password_reset_email_address_button:
				String emailAddressValue = emailAddress.getText().toString();
				if (!NormalCheck.isEmail(emailAddressValue)) {
					Toast.makeText(this, R.string.resetPasswordEmailEmpty, Toast.LENGTH_SHORT).show();
					break;
				}
				// send email
//				if (resetPassword(null, emailAddressValue)) {
//					Toast.makeText(this, R.string.resetPasswordSuccess, Toast.LENGTH_SHORT).show();
//				}

				break;
			case R.id.app_password_reset_verification_send_button:

			case R.id.app_reset_password_confirm:
				String code = verificationCode.getText().toString();
				String newPasswordValue = newPassword.getText().toString();
				if((code.length() == 0) || (newPasswordValue.length() == 0)){
					Toast.makeText(this, R.string.resetPasswordPhoneEmpty, Toast.LENGTH_SHORT).show();
					break;
				}
				phoneNumberValue = phoneNumber.getText().toString();
				if (!NormalCheck.isPhoneNumber(phoneNumberValue)) {
					Toast.makeText(this, R.string.resetPasswordPhoneEmpty, Toast.LENGTH_SHORT).show();
					break;
				}
				new CheckSmsAsync().execute(phoneNumberValue, code, newPasswordValue);
				break;
			default:
				break;
		}
	}

	@Deprecated
	private boolean resetPassword(String phoneNumber, String emailAddr) {

		HttpURLConnection conn = null;
		try {
			conn = ServerConfigure.getConnection(this, UrlSource.RESET_PASSWORD, RequestMethod.POST);
			JSONObject obj = new JSONObject();
			if (null != phoneNumber) {
				obj.put("phone", phoneNumber);
			}
			if (null != emailAddr) {
				obj.put("email", emailAddr);
			}
			ServerTalk.writeToServer(conn.getOutputStream(), obj);

			String serverStr = ServerTalk.readFromServer(conn.getInputStream());

			if (null == serverStr || serverStr.length() == 0) {
				return false;
			}

			JSONObject strObj = new JSONObject(serverStr);
			if (!strObj.has("status")) {
				return false;
			}
			if (!strObj.getBoolean("status")) {
				return false;
			}
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return true;
	}

	/**
	 * 参数为手机号
	 */
	private class RequestSmsAsync extends AsyncTask<String, Void, Boolean> {
		public RequestSmsAsync() {
			super();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return requestPassword(null, params[0]);
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			if(aBoolean){
				Toast.makeText(PasswordResetActivity.this, R.string.verificationCodeSendSuccess, Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(aBoolean);
		}
	}

	/**
	 * 参数为验证码
	 */
	private class CheckSmsAsync extends AsyncTask<String, Void, Boolean> {
		public CheckSmsAsync() {
			super();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return checkPassword(params[0], params[1], params[2]);
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			if(aBoolean){
				Toast.makeText(PasswordResetActivity.this, R.string.passwordResetSuccess, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	/**
	 * 向服务器请求手机验证码
	 *
	 * @return
	 */
	private boolean requestPassword(String userId, String phone) {
		HttpURLConnection con = null;
		try {
			con = ServerConfigure.getSessionConnection(UrlSource.RESET_PASSWORD, null);

			JSONObject obj = new JSONObject();
			if(null != userId){
				obj.put("userId", userId);
			}
			obj.put("phone", phone);
			ServerTalk.writeToServer(con.getOutputStream(), obj);

			String cookieval = con.getHeaderField("set-cookie");
			String sessionid = "";
			if (cookieval != null) {
				sessionid = cookieval.substring(0, cookieval.indexOf(";"));
			}
			System.out.println("session 1=======" + sessionid.split("JSESSIONID=")[1]);
			/////////////////////

			if (con.getResponseCode() != ServerConfigure.SERVER_OK) {
				return false;
			}
			String ser = ServerTalk.readFromServer(con.getInputStream());
			if(null == ser || ser.length() == 0){
				return false;
			}
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(ser);
			if (!je.isJsonObject()) {
				return false;
			}
			Gson g = new Gson();
			SessionWrapper sessionWrapperOut = g.fromJson(je, SessionWrapper.class);
			if (sessionWrapperOut.isSuccess()) {
				sessionId = sessionid.split("JSESSIONID=")[1];
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
	 * 检查验证码正确性
	 *
	 * @param code
	 * @return
	 */
	private boolean checkPassword(String userId, String code, String newPassword) {
		HttpURLConnection con;
		try {
			con = ServerConfigure.getSessionConnection(UrlSource.RESET_PASSWORD_CHECK, sessionId);
			SessionWrapper sw = new SessionWrapper();
			sw.setSessionId(sessionId);

			sw.setExtraInfo(newPassword);
			if(null != userId){
				sw.setUserId(userId);
			}
			sw.setCode(code);

			ServerTalk.writeToServer(con.getOutputStream(), sw);

			String str = ServerTalk.readFromServer(con.getInputStream());
			if (null == str) {
				return false;
			}
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(str);
			if (!je.isJsonObject()) {
				return false;
			}
			Gson g = new Gson();
			SessionWrapper wrapper = g.fromJson(je, SessionWrapper.class);
			if (!wrapper.isSuccess()) {
				return false;
			}
			sessionId = wrapper.getSessionId();
			String extra = wrapper.getExtraInfo();
			/////////////
			System.out.println("session 2=======" + sessionId);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
