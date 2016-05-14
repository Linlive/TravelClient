package com.tl.pro.travelkit.util.check;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * do some simple check.
 * Created by Administrator on 2016/4/24.
 */
public class NormalCheck {

	private static final String EMAIL_REGEX = "^([a-z0-9A-Z]+[-_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-_[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	private static final String PHONE_REGEX = "^[1][3458][0-9]{9}$";
	private static final String PASSWORD_REGEX = "^.(6, 12)(?=(\\d)+)(?=(\\w+)+)$";

	private static boolean innerCheck(String textCheck, Pattern pattern) {
		boolean ret = false;

		final Matcher mat = pattern.matcher(textCheck);

		if (mat.find()) {
			ret = true;
		}
		return ret;
	}

	/**
	 * whether the email valid or not
	 * @param emailString emailAddress
	 * @return true if valid
	 */
	public static boolean isEmail(String emailString) {

		final Pattern pattern = Pattern.compile(EMAIL_REGEX);

		return innerCheck(emailString, pattern);
	}

	/**
	 * whether the phone_number valid or not.
	 * @param phoneNumber phone number
	 * @return true if phoneNumber is valid.
	 */
	public static boolean isPhoneNumber(String phoneNumber) {
		Pattern pattern = Pattern.compile(PHONE_REGEX);
		return innerCheck(phoneNumber, pattern);
	}

	/**
	 *
	 * @param password
	 * @return true 符合密码标准
	 */
	public static boolean passwordStrong(String password){

		Pattern pattern = Pattern.compile(PASSWORD_REGEX);
		return innerCheck(password, pattern);
	}
}
