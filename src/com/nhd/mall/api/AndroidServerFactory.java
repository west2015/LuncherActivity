package com.nhd.mall.api;

/**********************************************************
 * 内容摘要 �?p>http请求交互�? 作�? ：vendor 创建时间 �?012-12-15 上午11:00:24
 * 
 * 历史记录 : 日期 : 2012-12-15 上午11:00:24 修改人：vendor 描述 :
 *********************************************************** 
 */
public class AndroidServerFactory {
	// 友盟统计的开关 true为关 false为开
	public static final Boolean PRODUCTION_MODEL = false;
	// public static final Boolean PRODUCTION_MODEL = false;
	private static final String API_KEY = "bc543dc89b57df68062c9a25314fbf7f";
	// private final static String SECURE_KEY =
	// "32df2bc3520d53a10ed6eb0807a5a6ab";
	private final static String SECURE_KEY = "1a56a492d666ae2f6a3cbd8f79dc9405";
	/** 远程连接url */
	// public static final String REST_BASE_URL =
	// "http://192.168.1.58:8080/nhd-mall";
	// public static final String REST_BASE_URL = "http://192.168.1.50:8881";
	// public static final String REST_BASE_URL =
	// "http://nhd.api.fzhuake.com:8087";
	public static final String REST_BASE_URL = "http://test1.fzhuake.com";
	private static Server server;

	public static Server getServer() {
		if (server == null) {
			server = new AndroidServiceImpl(REST_BASE_URL, API_KEY, SECURE_KEY);
		}
		return server;
	}

	public static String getBaseUrl() {
		return REST_BASE_URL;
	}
}
