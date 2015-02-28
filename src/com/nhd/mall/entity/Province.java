package com.nhd.mall.entity;

/**
 * 省份实体类实体类
 * 
 * @author vendor
 * @version 2012-11-21 10:49:02
 * @see     Class
 * @since   JDK1.0
 */
public class Province {
	private String province;
	private City[] cities;
	
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public City[] getCities() {
		return cities;
	}
	public void setCities(City[] cities) {
		this.cities = cities;
	}
}
