package com.tl.pro.travelkit.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.L;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.internet.RequestMethod;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.internet.ServerInfoObj;
import com.tl.pro.travelkit.internet.ServerTalk;
import com.tl.pro.travelkit.internet.UrlSource;
import com.tl.pro.travelkit.util.check.NormalCheck;
import com.tl.pro.travelkit.util.check.StringToJsonObject;
import com.tl.pro.travelkit.util.encryption.KitAESCoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

	private EditText mUserNameEdit;

	private EditText mUserPassword;

	private EditText mUserPassword2;

	private Button mRegisterButton;

	private SignInAsyncTask mSignInTask = null;

	boolean backGroundSuccess = false;
	Intent mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_register);
		mIntent = getIntent();
		initViews();
	}

	private void initViews() {
		mRegisterButton = (Button) findViewById(R.id.register_register_button);
		mUserNameEdit = (EditText) findViewById(R.id.register_name_edit);
		mUserPassword = (EditText) findViewById(R.id.register_password_edit1);
		mUserPassword2 = (EditText) findViewById(R.id.register_password_edit2);
		mRegisterButton.setOnClickListener(this);
		if(mIntent != null){
			mUserNameEdit.setText(mIntent.getStringExtra("userName"));
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.register_register_button:
				gotoSignIn();
				break;
			default:
				break;
		}
	}

	private void gotoSignIn() {

		if (mSignInTask != null) {
			return;
		}
		boolean cancel = false;
		String nameEditText = mUserNameEdit.getText().toString();
		String passwordText = mUserPassword.getText().toString();
		String passwordText2 = mUserPassword2.getText().toString();
		if (TextUtils.isEmpty(nameEditText)) {
			mUserNameEdit.setError("不能为空");
			mUserNameEdit.requestFocus();
			cancel = true;
		}
		if (TextUtils.isEmpty(passwordText)) {
			mUserPassword.setError("密码不能为空");
			mUserPassword.requestFocus();
			cancel = true;
		}
		if (TextUtils.isEmpty(passwordText2)) {
			mUserPassword2.setError("确认密码不能为空");
			mUserPassword2.requestFocus();
			cancel = true;
		}
		if (!NormalCheck.passwordStrong(passwordText)) {
			mUserPassword.requestFocus();
			mUserPassword.setError(getString(R.string.error_invalid_password));
			cancel = true;
		}

		if (cancel || !passwordText.equals(passwordText2)) {
			//return;
		}
		//do sign in
		JSONObject objectIn = new JSONObject();
		try {

			objectIn.put("userName", nameEditText);
			objectIn.put("userPassword", passwordText);
			objectIn.put("permissionLevel", 1);
			if(!ServerConfigure.beforeConnect(this)) {
				Toast.makeText(this, R.string.haveNotNetwork, Toast.LENGTH_SHORT).show();
				return;
			}
			mSignInTask = new SignInAsyncTask();
			mSignInTask.execute(objectIn);

		} catch (JSONException e) {
			e.printStackTrace();
			if (mSignInTask != null) {
				mSignInTask.onCancelled();
				mSignInTask = null;
			}
		}
	}

	private class SignInAsyncTask extends AsyncTask<JSONObject, Float, ServerInfoObj> {

		HttpURLConnection con;

		public SignInAsyncTask() {
			super();
			backGroundSuccess = false;
		}

		@Override
		protected ServerInfoObj doInBackground(JSONObject... params) {
			ServerInfoObj<JSONObject> serverInfoObj = new ServerInfoObj<>();
			try {
				publishProgress(20f);
				con = ServerConfigure.getConnection(RegisterActivity.this, UrlSource.SIGNUP, RequestMethod.POST);
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
		protected void onCancelled() {
			mSignInTask.onCancelled();
			mSignInTask = null;
		}

		@Override
		protected void onPostExecute(ServerInfoObj serverInfoObj) {
			if(serverInfoObj == null){
				return;
			}
			boolean registerFlag = false;

			backGroundSuccess = true;

			if(!serverInfoObj.isSuccess()){
				L.e("服务器返回操作失败");
				Toast.makeText(RegisterActivity.this, "请重试", Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONObject object = (JSONObject) serverInfoObj.getData();

				if(object == null){
					Toast.makeText(RegisterActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
					return;
				}
				if(!object.has("status")){
					L.w("服务器数据格式需要检查");
					return;
				}
				if (object.getBoolean("status")) {
					registerFlag = true;
					Intent intent = new Intent(RegisterActivity.this, IndexActivity.class);
					startActivity(intent);

				} else {
					registerFlag = false;
					String message = object.getString("message");
					if("userNotExist".equals(message)){
						//goToSignUp();
						//可以注册

					} else if("userIsExist".equals(message)){
						Toast.makeText(RegisterActivity.this, "该用户名已注册", Toast.LENGTH_LONG).show();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				if(registerFlag){
					finish();
				}
			}
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			super.onProgressUpdate(values);
		}
	}
}
