package com.tl.pro.travelkit.util.image;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 保存应用中当前已获取到的图片url，避免通过intent传值，
 * 据资料显示，Intent传值 大小限制在 1 MB 左右，当url
 * 过多时，避免不必要的问题。
 * Created by Administrator on 2016/5/23.
 */
public class ImageUrlHolder {
	public static List<HashMap<Integer, String>> URLS = new LinkedList<>();
}
