package com.nhd.mall.push;

/**
* @ClassName: BaiduBindingMsg
* @Description: TODO(绑定百度推送后，返回的信息，userId等可用户服务器向客户端定向推送内容)
* @author EP epowns@gmail.com
* @date 2014-2-28 上午8:44:53
* 
*  * 返回值中的errorCode，解释如下：
 * 0 - Success
 * 10001 - Network Problem
 * 30600 - Internal Server Error
 * 30601 - Method Not Allowed
 * 30602 - Request Params Not Valid
 * 30603 - Authentication Failed
 * 30604 - Quota Use Up Payment Required
 * 30605 - Data Required Not Found
 * 30606 - Request Time Expires Timeout
 * 30607 - Channel Token Timeout
 * 30608 - Bind Relation Not Found
 * 30609 - Bind Number Too Many
*/
public class BaiduBindingMsg {
	/** 绑定接口返回值，0 - 成功 */
	private int errorCode;
	/** 应用id。errorCode非0时为null */
	private String appid; 
	/** 应用user id。errorCode非0时为null */
	private String userId;
	/** 应用channel id。errorCode非0时为null */
	private String channelId;
	/** 向服务端发起的请求id。在追查问题时有用； */
	private String requestId;
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	
}
