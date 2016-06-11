package com.tl.pro.travelkit.bean;

import java.io.Serializable;

/**
 * 购物车对象实例，便于数据库的存取
 * Created by Administrator on 2016/5/28.
 */
public class ShoppingCartDo implements Serializable {

	private String userId;
	private String shopKeeperId;

	private String goodsId;

	private int goodsChooseNumber;
	private float goodsPrice;
	private int goodsSize;
	private int goodsColor;
	private int goodsType;

	private String goodsName;
	private String goodsColorValue;
	private String goodsSizeValue;
	private String goodsTypeValue;

	private String goodsExtra;
	private String imgUrl;

	public String getUserId () {

		return userId;
	}

	public void setUserId (String userId) {

		this.userId = userId;
	}

	public String getShopKeeperId () {

		return shopKeeperId;
	}

	public void setShopKeeperId (String shopKeeperId) {

		this.shopKeeperId = shopKeeperId;
	}

	public String getGoodsId () {

		return goodsId;
	}

	public void setGoodsId (String goodsId) {

		this.goodsId = goodsId;
	}

	public int getGoodsChooseNumber () {

		return goodsChooseNumber;
	}

	public void setGoodsChooseNumber (int goodsChooseNumber) {

		this.goodsChooseNumber = goodsChooseNumber;
	}

	public float getGoodsPrice () {

		return goodsPrice;
	}

	public void setGoodsPrice (float goodsPrice) {

		this.goodsPrice = goodsPrice;
	}

	public int getGoodsSize () {

		return goodsSize;
	}

	public void setGoodsSize (int goodsSize) {

		this.goodsSize = goodsSize;
	}

	public int getGoodsColor () {

		return goodsColor;
	}

	public void setGoodsColor (int goodsColor) {

		this.goodsColor = goodsColor;
	}

	public int getGoodsType () {

		return goodsType;
	}

	public void setGoodsType (int goodsType) {

		this.goodsType = goodsType;
	}

	public String getGoodsExtra () {

		return goodsExtra;
	}

	public void setGoodsExtra (String goodsExtra) {

		this.goodsExtra = goodsExtra;
	}

	public String getGoodsColorValue () {

		return goodsColorValue;
	}

	public void setGoodsColorValue (String goodsColorValue) {

		this.goodsColorValue = goodsColorValue;
	}

	public String getGoodsSizeValue () {

		return goodsSizeValue;
	}

	public void setGoodsSizeValue (String goodsSizeValue) {

		this.goodsSizeValue = goodsSizeValue;
	}

	public String getGoodsTypeValue () {

		return goodsTypeValue;
	}

	public void setGoodsTypeValue (String goodsTypeValue) {

		this.goodsTypeValue = goodsTypeValue;
	}

	public String getImgUrl () {

		return imgUrl;
	}

	public void setImgUrl (String imgUrl) {

		this.imgUrl = imgUrl;
	}

	public String getGoodsName () {

		return goodsName;
	}

	public void setGoodsName (String goodsName) {

		this.goodsName = goodsName;
	}
}
