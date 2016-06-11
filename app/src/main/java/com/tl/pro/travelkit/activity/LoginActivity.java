package com.tl.pro.travelkit.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.L;
import com.tl.pro.travelkit.R;
import com.tl.pro.travelkit.internet.RequestMethod;
import com.tl.pro.travelkit.internet.ServerConfigure;
import com.tl.pro.travelkit.internet.ServerInfoObj;
import com.tl.pro.travelkit.internet.ServerTalk;
import com.tl.pro.travelkit.internet.UrlSource;
import com.tl.pro.travelkit.util.CodeUtil;
import com.tl.pro.travelkit.util.CommonText;
import com.tl.pro.travelkit.util.check.NormalCheck;
import com.tl.pro.travelkit.util.check.StringToJsonObject;
import com.tl.pro.travelkit.util.encryption.KitAESCoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class LoginActivity extends Activity implements View.OnClickListener {

	private static final int LOGIN_TIME_LIMIT = 5;

	private static final int STOPSPLASH = 0;
	private static final long SPLASHTIME = 2500;
	private static final String SHAREPREFRENCE = "userLogin";
	private static final String LOGIN_TIME = "logTime";

	private Animation animatinoIn;
	private Animation animatinoAppIn;
	private int status = 1;
	private LinearLayout mGoneLinearLayout;
	private FrameLayout mLaunchFrm;

	private Button mLoginOrRegisterButton;

	private EditText mUserNameEdit;
	private EditText mUserPasswordEdit;
	private EditText mVerificationEdit;
	private ImageView mVerificationImage;
	private TextView mForgotPasswordText;

	private LinearLayout mVerificationLinearLay;

	private LoginAsyncTask mAsyncTask = null;

	private TextView mAdmistratorText;
	private CheckBox mAdmistratorCheckBox;

	View mTmpLayout;

	View mLoginLayout;

	boolean backGroundSuccess = false;
	boolean isAdministrator = false;

	int loginTime = 0;

	CodeUtil mCodeUtil = CodeUtil.getInstance();
	String code;

	Intent mIntent;

	private Handler splashHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case STOPSPLASH:
					mGoneLinearLayout.setVisibility(View.GONE);
					//mLaunchFrm.startAnimation(animatinoAppIn);
					break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);
		mIntent = getIntent();
		initView();

		animatinoIn = AnimationUtils.loadAnimation(this, R.anim.app_launcgh_img_in); //动画效果
		animatinoAppIn = AnimationUtils.loadAnimation(this, R.anim.app_launch_frm_in); //动画效果

		mGoneLinearLayout = (LinearLayout) findViewById(R.id.goneLinear);
		mVerificationLinearLay = (LinearLayout) findViewById(R.id.app_login_verification_linear);
		mVerificationEdit = (EditText) findViewById(R.id.app_login_verification_edit);
		mGoneLinearLayout.startAnimation(animatinoIn);
		mLaunchFrm = (FrameLayout) findViewById(R.id.app_launch_frm);

//		Message msg = new Message();
//		msg.what = STOPSPLASH;
//		splashHandler.sendMessageDelayed(msg, SPLASHTIME);
	}

	private void initView() {
		mLoginOrRegisterButton = (Button) findViewById(R.id.login_login_or_register_button);
		mUserNameEdit = (EditText) findViewById(R.id.login_name_edit);
		mVerificationImage = (ImageView) findViewById(R.id.app_login_verification_img);
		mUserPasswordEdit = (EditText) findViewById(R.id.login_password_edit);
		mForgotPasswordText = (TextView) findViewById(R.id.login_forget_password);
		mTmpLayout = findViewById(R.id.login_frame_tmp);
		mLoginLayout = findViewById(R.id.login_frame_login);

		mAdmistratorCheckBox = (CheckBox) findViewById(R.id.app_administrator_check_box);
		mAdmistratorText = (TextView) findViewById(R.id.app_administrator);

		mLoginOrRegisterButton.setOnClickListener(this);
		mAdmistratorCheckBox.setOnClickListener(this);
		mAdmistratorText.setOnClickListener(this);
		mVerificationImage.setOnClickListener(this);
		mForgotPasswordText.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.login_login_or_register_button:
				isAdministrator = mAdmistratorCheckBox.isChecked();
				goToLogin();
				break;
			case R.id.login_forget_password:
				goToFindPassword();
				break;
			case R.id.app_administrator_check_box:
				break;
			case R.id.app_administrator:
				isAdministrator = !isAdministrator;
				mAdmistratorCheckBox.setChecked(isAdministrator);
				break;
			case R.id.app_login_verification_img:
				code = mCodeUtil.createCode();
				mVerificationImage.setImageBitmap(mCodeUtil.createBitmap(code));
				break;
			default:
				break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	boolean register = false;
	boolean firstLogin = true;

	private void goToLogin() {
		if (mAsyncTask != null && !backGroundSuccess) {
			return;
		}
		if(!ServerConfigure.beforeConnect(LoginActivity.this)){
			Toast.makeText(LoginActivity.this, R.string.haveNotNetwork, Toast.LENGTH_SHORT).show();
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
		if(!NormalCheck.isPhoneNumber(name)){
			if(!isAdministrator) {
				mUserNameEdit.requestFocus();
				mUserNameEdit.setError("不符合电话号码格式");
				cancel = true;
			}
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
		if (loginTime > LOGIN_TIME_LIMIT && firstLogin) {
			//验证码
			//Toast.makeText(LoginActivity.this, "请输入验证码！", Toast.LENGTH_LONG).show();
			mVerificationLinearLay.setVisibility(View.VISIBLE);
			code = mCodeUtil.createCode();
			Bitmap bitmap = mCodeUtil.createBitmap(code);

			mVerificationImage.setImageBitmap(bitmap);
			firstLogin = false;
			return;
		}
		if (loginTime > LOGIN_TIME_LIMIT) {
			if (TextUtils.isEmpty(mVerificationEdit.getText().toString())) {
				Toast.makeText(this, R.string.pleaseInputVerification, Toast.LENGTH_SHORT).show();
				cancel = true;
			}
			if (cancel) {
				return;
			}
			String input = mVerificationEdit.getText().toString();
			String tmpCode = code.toLowerCase();
			if (!tmpCode.equals(input.toLowerCase())) {
				Toast.makeText(this, R.string.inputVerificationError, Toast.LENGTH_SHORT).show();
				cancel = true;
			}
			if (cancel) {
				code = mCodeUtil.createCode();
				mVerificationImage.setImageBitmap(mCodeUtil.createBitmap(code));
				return;
			}
		}

		JSONObject objectIn = new JSONObject();
		try {

			objectIn.put("userId", name);
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
		loginTime++;
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
				intent.putExtra("userId", mUserNameEdit.getText().toString());
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
	private void goToFindPassword() {
		Intent intent = new Intent(LoginActivity.this, PasswordResetActivity.class);
		String userId = mUserNameEdit.getText().toString();
		if (NormalCheck.isPhoneNumber(userId)) {
			intent.putExtra(CommonText.userId, userId);
		}
		if (NormalCheck.isEmail(userId)) {
			intent.putExtra(CommonText.emailAddr, userId);
		}
		startActivity(intent);
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
				con = ServerConfigure.getConnection(UrlSource.SIGNIN, RequestMethod.POST);
				con.connect();
				JSONObject object = params[0];
				publishProgress(50f);
				if (object.has("userId")) {
					String tmpName = KitAESCoder.encrypt(object.getString("userId"));
					object.put("userId", tmpName);
				}
				if (object.has("userPassword")) {
					String tmpPass = KitAESCoder.encrypt(object.getString("userPassword"));
					object.put("userPassword", tmpPass);
				}
				if (isAdministrator) {
					object.put("permissionLevel", 0);
				}
				ServerTalk.writeToServer(con.getOutputStream(), params[0]);
				L.i("server response code is: " + con.getResponseCode());

				serverInfoObj = StringToJsonObject.toJsonObject(ServerTalk.readFromServer(con.getInputStream()));
				publishProgress(100f);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(con != null){
					con.disconnect();
				}
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
			if (serverObject == null) {

				Toast.makeText(LoginActivity.this, R.string.serverNotResponse, Toast.LENGTH_SHORT).show();

				//测试登录后的过程
				login = true;
				return;
//				Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
//				startActivity(intent);
			}
			if (!serverObject.isSuccess()) {
				L.e("服务器返回操作失败");
				Toast.makeText(LoginActivity.this, R.string.pleaseWait, Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONObject object = (JSONObject) serverObject.getData();

				if (object == null) {
					Toast.makeText(LoginActivity.this, R.string.serverNotResponse, Toast.LENGTH_SHORT).show();
					return;
				}
				if (!object.has("status")) {
					L.w("服务器数据格式需要检查");
					return;
				}
				if (object.getBoolean("status")) {
					login = true;
					saveInfo(LOGIN_TIME, "0");
					loginTime = 0;
					if (isAdministrator) {
						Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
						intent.putExtra("userId", mUserNameEdit.getText().toString());
						startActivity(intent);
						return;
					}
//					Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
//					intent.putExtra("userId", mUserNameEdit.getText().toString());
//					startActivity(intent);
					Intent intent = new Intent();
					intent.putExtra("userId", mUserNameEdit.getText().toString());
					setResult(LOG_IN_RESULT, intent);
					finish();

				} else {
					login = false;
					String message = object.getString("message");
					if ("userNotExist".equals(message)) {
						if (isAdministrator) {
							Toast.makeText(LoginActivity.this, R.string.passwordError, Toast.LENGTH_LONG).show();
							return;
						}
						goToSignUp();
					} else if ("passwordNotMatch".equals(message)) {
						Toast.makeText(LoginActivity.this, R.string.passwordError, Toast.LENGTH_LONG).show();
						if (loginTime > LOGIN_TIME_LIMIT) {
							code = mCodeUtil.createCode();
							mVerificationImage.setImageBitmap(mCodeUtil.createBitmap(code));
						}
						return;
					}
					saveInfo(LOGIN_TIME, String.valueOf(loginTime));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				if (login || register) {
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

	public static final int LOG_IN_RESULT = 502;

	private void saveInfo(String key, String value) {
		SharedPreferences settings = getSharedPreferences(SHAREPREFRENCE, MODE_PRIVATE);
		//2、让setting处于编辑状态
		SharedPreferences.Editor editor = settings.edit();
		//3、存放数据
		editor.putString(key, value);
		//4、完成提交
		editor.apply();
	}

	private String readInfo(String key) {
		SharedPreferences settings = getSharedPreferences(SHAREPREFRENCE, MODE_PRIVATE);
		//if(LOGIN_TIME.equals(key))
		{
			//return settings.getInt(key, 0);
		}
		return settings.getString(key, "");
	}

	@Override
	protected void onStop() {
		saveInfo("name", mUserNameEdit.getText().toString());
		saveInfo("password", mUserPasswordEdit.getText().toString());
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mUserNameEdit.setText(readInfo("name"));
		mUserPasswordEdit.setText(readInfo("password"));
		if (0 == readInfo(LOGIN_TIME).length()) {
			loginTime = 0;
		} else {
			loginTime = Integer.valueOf(readInfo(LOGIN_TIME));
		}
		if(null != mIntent && mIntent.getBooleanExtra("system", false)){
			mUserNameEdit.setText(mIntent.getStringExtra("name"));
			mAdmistratorCheckBox.setChecked(true);
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}