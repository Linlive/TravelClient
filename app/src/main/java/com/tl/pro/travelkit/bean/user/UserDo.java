package com.tl.pro.travelkit.bean.user;

/**
 * 用户实体
 * 数据库中的表
 * Created by Administrator on 2016/5/1.
 */
public class UserDo {

	private int id;

	private String userId;
	private String userName;
	private String userPassword;
	/**
	 * 必须为Integer,否则数据库查询异常
	 */
	private Integer userAge;
	private String userPhone;
	private String userEmail;
	private String userSex;
	private String shopKeeper;
	private String shopKeeperId;

	private int permissionLevel;

	public UserDo(){

	}
	/**
	 * 数据库字段，不提供外部设置，仅能获取该值
	 * @return
	 */

	public void setId(int id){
		this.id = id;
	}

	public int getId () {

		return id;
	}

	public String getUserId () {

		return userId;
	}

	public void setUserId (String userId) {

		this.userId = userId;
	}

	public String getUserName () {

		return userName;
	}

	public void setUserName (String userName) {

		this.userName = userName;
	}

	public String getUserPassword () {

		return userPassword;
	}

	public void setUserPassword (String userPassword) {

		this.userPassword = userPassword;
	}

	public Integer getUserAge () {

		return userAge;
	}

	public void setUserAge (Integer userAge) {

		this.userAge = userAge;
	}

	public String getUserPhone () {

		return userPhone;
	}

	public void setUserPhone (String userPhone) {

		this.userPhone = userPhone;
	}

	public String getUserEmail () {

		return userEmail;
	}

	public void setUserEmail (String userEmail) {

		this.userEmail = userEmail;
	}

	public String getUserSex () {

		return userSex;
	}

	public void setUserSex (String userSex) {

		this.userSex = userSex;
	}

	public String getShopKeeper () {

		return shopKeeper;
	}

	public void setShopKeeper (String shopKeeper) {

		this.shopKeeper = shopKeeper;
	}

	public int getPermissionLevel () {

		return permissionLevel;
	}

	public void setPermissionLevel (int permissionLevel) {

		this.permissionLevel = permissionLevel;
	}

	public String getShopKeeperId () {

		return shopKeeperId;
	}

	public void setShopKeeperId (String shopKeeperId) {

		this.shopKeeperId = shopKeeperId;
	}
}
