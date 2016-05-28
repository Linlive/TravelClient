package com.tl.pro.travelkit.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 商品 对象，属性包含：
 * 品牌，类型，尺寸，颜色，适合于什么地方，价格（售价）
 * 库存
 * Created by Administrator on 2016/5/21.
 */
public class GoodsDo implements Serializable{

	//common info
	private String goodsId;
	private String goodsName;
	private int goodsBrand;
	private int goodsType;
	private int goodsSize;
	private int goodsColor;
	private String goodsPlace;
	private float goodsPrice;
	private int goodsRepertory;

	//所属商家
	private String shopKeeperId;

	private String goodsBrandValue;
	private String goodsTypeValue;
	private String goodsSizeValue;
	private String goodsColorValue;

	private List<String> imgUrlList;

	// 额外信息
	private String goodsExtras;

	public GoodsDo(){

	}

	public String getGoodsId () {

		return goodsId;
	}

	public void setGoodsId (String goodsId) {

		this.goodsId = goodsId;
	}

	public String getGoodsName () {

		return goodsName;
	}

	public void setGoodsName (String goodsName) {

		this.goodsName = goodsName;
	}

	public int getGoodsBrand () {

		return goodsBrand;
	}

	public void setGoodsBrand (int goodsBrand) {

		this.goodsBrand = goodsBrand;
	}

	public int getGoodsType () {

		return goodsType;
	}

	public void setGoodsType (int goodsType) {

		this.goodsType = goodsType;
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

	public String getGoodsPlace () {

		return goodsPlace;
	}

	public void setGoodsPlace (String goodsPlace) {

		this.goodsPlace = goodsPlace;
	}

	public float getGoodsPrice () {

		return goodsPrice;
	}

	public void setGoodsPrice (float goodsPrice) {

		this.goodsPrice = goodsPrice;
	}

	public int getGoodsRepertory () {

		return goodsRepertory;
	}

	public void setGoodsRepertory (int goodsRepertory) {

		this.goodsRepertory = goodsRepertory;
	}

	public String getShopKeeperId () {

		return shopKeeperId;
	}

	public void setShopKeeperId (String shopKeeperId) {

		this.shopKeeperId = shopKeeperId;
	}

	public String getGoodsBrandValue () {

		return goodsBrandValue;
	}

	public void setGoodsBrandValue (String goodsBrandValue) {

		this.goodsBrandValue = goodsBrandValue;
	}

	public String getGoodsTypeValue () {

		return goodsTypeValue;
	}

	public void setGoodsTypeValue (String goodsTypeValue) {

		this.goodsTypeValue = goodsTypeValue;
	}

	public String getGoodsSizeValue () {

		return goodsSizeValue;
	}

	public void setGoodsSizeValue (String goodsSizeValue) {

		this.goodsSizeValue = goodsSizeValue;
	}

	public String getGoodsColorValue () {

		return goodsColorValue;
	}

	public void setGoodsColorValue (String goodsColorValue) {

		this.goodsColorValue = goodsColorValue;
	}

	public String getGoodsExtras () {

		return goodsExtras;
	}

	public void setGoodsExtras (String goodsExtras) {

		this.goodsExtras = goodsExtras;
	}

	public List<String> getImgUrlList () {

		return imgUrlList;
	}

	public void setImgUrlList (List<String> imgUrlList) {

		this.imgUrlList = imgUrlList;
	}

	@Override
	public String toString () {

		return "GoodsDo:" + goodsId + goodsName + goodsBrandValue + goodsTypeValue + goodsSizeValue
				+ goodsColorValue + shopKeeperId;
	}
}