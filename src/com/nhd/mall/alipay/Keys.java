package com.nhd.mall.alipay;
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {
	//合作身份者id，以2088开头的16位纯数字
	public static  String DEFAULT_PARTNER = "";
	//收款支付宝账号
	public static  String DEFAULT_SELLER = "";
	//商户私钥，自助生成
	public static  String PRIVATE = "";
	public static  String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDU4oBAUDBB3tGjQJLunh8tCOipHeBO+1pbljWQK13x46bofpSlNkkDaBJq4f9qjWLOy7scU45Ov5Pv+GwjxKkptjUYuvQVmWPQaTHbm3x5iKLxKnMuJTA8xmjaGqHkUQ7vJMGBUR7Vh361FjW/UR6P4SDHzw4vcX23i+CxCT0vZQIDAQAB";
}
