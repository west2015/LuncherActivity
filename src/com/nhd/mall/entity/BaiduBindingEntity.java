package com.nhd.mall.entity;

public class BaiduBindingEntity {
	/** 应用user id。errorCode非0时为null */
	private String userId;
	/** 应用channel id。errorCode非0时为null */
	private String channelId;
	/** 台球会用户ID，用于和后台数据绑定 */
	private Long memberId;
	/** app应用类型 tqh：台球会，tqsj：台球世界 nhd-mall 新华都*/
	private String category="nhd-mall";
	/** 设备类型 android:3   ios:4 */
	private String deviceType;
	
	
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
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
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	
	
}
