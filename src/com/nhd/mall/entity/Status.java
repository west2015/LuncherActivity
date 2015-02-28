package com.nhd.mall.entity;

/**
 * 类 <code>Status</code>http返回状态
 * 
 * @author vendor
 * @version 2012年11月3日 23:01:35
 * @see     Class
 * @since   JDK1.0
 */
public class Status {
	private String errorMessage;
	private String status;
	private String code;
    private String SUCCESS;
	/** 用于标记有列表的情况下的位置 */
	private int position;
    private String success;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getSUCCESS() {
        return SUCCESS;
    }

    public void setSUCCESS(String SUCCESS) {
        this.SUCCESS = SUCCESS;
    }

    public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
