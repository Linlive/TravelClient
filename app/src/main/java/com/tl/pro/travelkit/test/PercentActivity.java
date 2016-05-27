package com.tl.pro.travelkit.test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.tl.pro.travelkit.R;

import java.util.Random;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class PercentActivity extends AppCompatActivity implements Handler.Callback {
	private static String APPKEY = "12870306bcc85";
	private static String APPSECRET = "7510c6cc86654e58da1261b8018372cc";

	// 短信注册，随机产生头像
	private static final String[] AVATARS = {
			"http://tupian.qqjay.com/u/2011/0729/e755c434c91fed9f6f73152731788cb3.jpg",
			"http://99touxiang.com/public/upload/nvsheng/125/27-011820_433.jpg",
			"http://img1.touxiang.cn/uploads/allimg/111029/2330264224-36.png",
			"http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339485237265.jpg",
			"http://diy.qqjay.com/u/files/2012/0523/f466c38e1c6c99ee2d6cd7746207a97a.jpg",
			"http://img1.touxiang.cn/uploads/20121224/24-054837_708.jpg",
			"http://img1.touxiang.cn/uploads/20121212/12-060125_658.jpg",
			"http://img1.touxiang.cn/uploads/20130608/08-054059_703.jpg",
			"http://diy.qqjay.com/u2/2013/0422/fadc08459b1ef5fc1ea6b5b8d22e44b4.jpg",
			"http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339510584349.jpg",
			"http://img1.touxiang.cn/uploads/20130515/15-080722_514.jpg",
			"http://diy.qqjay.com/u2/2013/0401/4355c29b30d295b26da6f242a65bcaad.jpg"
	};

	private boolean ready;
	private boolean gettingFriends;
	ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_tool_bar);

//		initSDK();
//		RegisterPage registerPage = new RegisterPage();
//		registerPage.setRegisterCallback(new EventHandler() {
//			public void afterEvent(int event, int result, Object data) {
//				// 解析注册结果
//				if (result == SMSSDK.RESULT_COMPLETE) {
//					@SuppressWarnings("unchecked")
//					HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
//					String country = (String) phoneMap.get("country");
//					String phone = (String) phoneMap.get("phone");
//					// 提交用户信息
//					registerUser(country, phone);
//				}
//			}
//		});
//		registerPage.show(this);
	}


	private void initSDK() {
		// 初始化短信SDK
		SMSSDK.initSDK(this, APPKEY, APPSECRET, true);
		final Handler handler = new Handler(this);
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
		ready = true;

		// 获取新好友个数
		SMSSDK.getNewFriendsCount();
		gettingFriends = true;
	}

	// 提交用户信息
	private void registerUser(String country, String phone) {
		Random rnd = new Random();
		int id = Math.abs(rnd.nextInt());
		String uid = String.valueOf(id);
		String nickName = "SmsSDK_User_" + uid;
		String avatar = AVATARS[id % 12];
		SMSSDK.submitUserInfo(uid, nickName, avatar, country, phone);
	}

	@Override
	public boolean handleMessage(Message msg) {

		int event = msg.arg1;
		int result = msg.arg2;
		Object data = msg.obj;
		if (event == SMSSDK.EVENT_SUBMIT_USER_INFO) {
			// 短信注册成功后，返回MainActivity,然后提示新好友
			if (result == SMSSDK.RESULT_COMPLETE) {
				Toast.makeText(this, R.string.smssdk_user_info_submited, Toast.LENGTH_SHORT).show();
			} else {
				((Throwable) data).printStackTrace();
			}
		} else if (event == SMSSDK.EVENT_GET_NEW_FRIENDS_COUNT) {
			if (result == SMSSDK.RESULT_COMPLETE) {

				gettingFriends = false;
			} else {
				((Throwable) data).printStackTrace();
			}
		}
		return false;
	}
}
