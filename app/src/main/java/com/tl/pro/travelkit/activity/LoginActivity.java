package com.tl.pro.travelkit.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.L;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.internet.ServerInfoObj;
import com.tl.pro.travelkit.internet.ServerTalk;
import com.tl.pro.travelkit.internet.UrlSource;
import com.tl.pro.travelkit.util.check.StringToJsonObject;
import com.tl.pro.travelkit.util.encryption.KitAESCoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class LoginActivity extends Activity implements View.OnClickListener {

	private static final int LOGIN_TIME_LIMIT = 1;

	private Button mLoginOrRegisterButton;

	private EditText mUserNameEdit;

	private EditText mUserPasswordEdit;

	private TextView mForgotPasswordText;

	private LoginAsyncTask mAsyncTask = null;

	View mTmpLayout;

	View mLoginLayout;

	boolean backGroundSuccess = false;

	int loginTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login2);
		initView();
	}

	private void initView() {
		mLoginOrRegisterButton = (Button) findViewById(R.id.login_login_or_register_button);
		mUserNameEdit = (EditText) findViewById(R.id.login_name_edit);
		mUserPasswordEdit = (EditText) findViewById(R.id.login_password_edit);
		mForgotPasswordText = (TextView) findViewById(R.id.login_forget_password);
		mTmpLayout = findViewById(R.id.login_frame_tmp);
		mLoginLayout = findViewById(R.id.login_frame_login);
		mLoginOrRegisterButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.login_login_or_register_button:
				goToLogin();
				break;
			case R.id.login_forget_password:
				goToFindPassword();
			default:
				break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	boolean register = false;

	private void goToLogin() {

		loginTime++;
		if (mAsyncTask != null && !backGroundSuccess) {
			return;
		}
		String name = mUserNameEdit.getText().toString();
		String password = mUserPasswordEdit.getText().toString();
		boolean cancel = false;

		if (TextUtils.isEmpty(password)) {
			mUserPasswordEdit.requestFocus();
			mUserPasswordEdit.setError(getString(R.string.error_field_required));
			cancel = true;
		}
//		if(!NormalCheck.passwordStrong(password)){
//			mUserPasswordEdit.requestFocus();
//			mUserPasswordEdit.setError(getString(R.string.error_invalid_password));
//			cancel = true;
//		}
		if (TextUtils.isEmpty(name)) {
			mUserPasswordEdit.requestFocus();
			mUserNameEdit.setError(getString(R.string.error_field_required));
			cancel = true;
		}

		if (cancel) {
			return;
		}
		if(loginTime > LOGIN_TIME_LIMIT){
			//验证码
			Toast.makeText(LoginActivity.this, "请输入验证码！", Toast.LENGTH_LONG).show();
		}
		JSONObject objectIn = new JSONObject();
		try {

			objectIn.put("userName", name);
			objectIn.put("userPassword", password);

			mAsyncTask = new LoginAsyncTask();
			mAsyncTask.execute(objectIn);

		} catch (JSONException e) {
			e.printStackTrace();
			if (mAsyncTask != null) {
				mAsyncTask.onCancelled();
				mAsyncTask = null;
			}
		}
	}
	private void goToSignUp() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
		dialog.setMessage("你还没用成为本系统用户，快来加入吧~");
		dialog.setIcon(R.mipmap.ic_launcher);
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				register = true;
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				intent.putExtra("userName", mUserNameEdit.getText().toString());
				startActivity(intent);
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.create();
		dialog.show();
	}

	/**
	 * 找回密码
	 */
	private void goToFindPassword(){

	}

	private class LoginAsyncTask extends AsyncTask<JSONObject, Float, ServerInfoObj> {
		HttpURLConnection con;

		@Override
		protected void onPreExecute() {
			backGroundSuccess = false;
			enableView(false);
			onPause();
		}

		/**
		 * 让背景不可点击
		 *
		 * @param enable 是否使能
		 */
		private void enableView(boolean enable) {
			mLoginOrRegisterButton.setEnabled(enable);
			mUserNameEdit.setEnabled(enable);
			mUserPasswordEdit.setEnabled(enable);
			if (enable) {
				mLoginLayout.bringToFront();
				mTmpLayout.setVisibility(View.INVISIBLE);
			} else {
				mTmpLayout.bringToFront();
				mTmpLayout.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected ServerInfoObj doInBackground(JSONObject... params) {
			ServerInfoObj<JSONObject> serverInfoObj = null;// = new ServerInfoObj<>();
			try {
				publishProgress(20f);
				con = ServerConfigure.getConnection(UrlSource.SIGNIN, ServerConfigure.Request.POST);
				con.connect();
				JSONObject object = params[0];
				publishProgress(50f);
				if (object.has("userName")) {
					String tmpName = KitAESCoder.encrypt(object.getString("userName"));
					object.put("userName", tmpName);
				}
				if (object.has("userPassword")) {
					String tmpPass = KitAESCoder.encrypt(object.getString("userPassword"));
					object.put("userPassword", tmpPass);
				}

				ServerTalk.writeToServer(con.getOutputStream(), params[0]);
				L.i("server response code is: " + con.getResponseCode());

				serverInfoObj = StringToJsonObject.toJsonObject(ServerTalk.readFromServer(con.getInputStream()));
				publishProgress(100f);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				con.disconnect();
			}
			return serverInfoObj;
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			L.d("progress value is: " + values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(ServerInfoObj serverObject) {
			boolean login = false;

			backGroundSuccess = true;
			enableView(true);
			if(serverObject == null) {
				Toast.makeText(LoginActivity.this, "请稍后再试", Toast.LENGTH_SHORT).show();

				//测试登录后的过程
				login = true;
//				Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
//				startActivity(intent);

				return;
			}
			if(!serverObject.isSuccess()){
				L.e("服务器返回操作失败");
				Toast.makeText(LoginActivity.this, "请重试", Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONObject object = (JSONObject) serverObject.getData();

				if(object == null){
					Toast.makeText(LoginActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
					return;
				}
				if(!object.has("status")){
					L.w("服务器数据格式需要检查");
					return;
				}
				if (object.getBoolean("status")) {
					login = true;
					Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
					intent.putExtra("userId", mUserNameEdit.getText().toString());

					startActivity(intent);

				} else {
					login = false;
					String message = object.getString("message");
					if("userNotExist".equals(message)){
						goToSignUp();
					} else if("passwordNotMatch".equals(message)){
						Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_LONG).show();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				if(login || register){
					finish();
				} else {
					enableView(true);
				}
			}
		}

		@Override
		protected void onCancelled() {
			mAsyncTask.onCancelled();
			mAsyncTask = null;
		}
	}
	@Override
	protected void onStop() {
		//1、打开Preferences，名称为setting，如果存在则打开它，否则创建新的Preferences
		SharedPreferences settings = getSharedPreferences("userLogin", MODE_PRIVATE);
		//2、让setting处于编辑状态
		SharedPreferences.Editor editor = settings.edit();
		//3、存放数据
		editor.putString("name", mUserNameEdit.getText().toString());
		//4、完成提交
		editor.apply();
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//1、获取Preferences
		SharedPreferences settings = getSharedPreferences("userLogin", MODE_PRIVATE);
		//2、取出数据
		String name = settings.getString("name", "");
		mUserNameEdit.setText(name);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}