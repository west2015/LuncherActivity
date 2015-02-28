package com.nhd.mall.entity;

/**自提门店实体类
 * Created by Administrator on 14-4-24.
 */
public class FormStoreEntity {
    private Integer storeId;//自提门店Id，
    private String getDate; //"2014-04-24 08",//自提时间
    private String detail;
    private String storeName;
    private String storePhone;

    public String getStorePhone() {
		return storePhone;
	}

	public void setStorePhone(String storePhone) {
		this.storePhone = storePhone;
	}

	public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getGetDate() {
        return getDate;
    }

    public void setGetDate(String getDate) {
        this.getDate = getDate;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
