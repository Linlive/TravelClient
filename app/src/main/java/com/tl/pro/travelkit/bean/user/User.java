package com.tl.pro.travelkit.bean.user;

/**
 * 用来描述本系统的用户，主要指客户端的买家和商家，管理员
 * Created by Administrator on 2016/4/23.
 */
public class User {

	protected String name;

	protected int age;

	protected String phoneNumber;

	protected String emailAddress;

	/**
	 * 应该有支付宝账号，目前的设计是由支护宝账号进行支付
	 */
	protected String payID;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPayID() {
		return payID;
	}

	public void setPayID(String payID) {
		this.payID = payID;
	}
}
