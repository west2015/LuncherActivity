package com.nhd.mall.entity;

/**
 * 类 <code>Version</code>app版本实体类
 * @author vendor
 * @version 2012年11月3日 23:25:47
 * @see     Class
 * @since   JDK1.0
 */
public class Version {
	private String appCategory;	
	private String appName;
	private int verCode;
	private String verName;
	private String url;
	private Boolean isNew;
    private String intro;

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getAppCategory() {
		return appCategory;
	}
	public void setAppCategory(String appCategory) {
		this.appCategory = appCategory;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public int getVerCode() {
		return verCode;
	}
	public void setVerCode(int verCode) {
		this.verCode = verCode;
	}
	public String getVerName() {
		return verName;
	}
	public void setVerName(String verName) {
		this.verName = verName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Boolean getIsNew() {
		return isNew;
	}
	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	
}
