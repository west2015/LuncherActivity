package com.nhd.mall.entity;

import java.io.Serializable;

/**优惠券实体
 * Created by linchunwei on 14-4-22.
 */
public class Coupon implements Serializable {
    /**优惠券ID */
    private Integer id;
    /**会员优惠券：  1:未使用、2:已使用、3:已过期
     * 门店优惠券：  1：有效 ; 0: 无效；2: 过期;*/
    private Integer status;
    /**优惠数，X元，X折 */
    private String money;
    /**优惠券使用规则 */
    private String rule;
    /**  1： 现金券  2： 折扣券    3 是储值卡*/
    private Integer type;
    /**生效时间 */
    private String issueDate;
    /**优惠券密码 */
    private String password;
    /**门店ID */
    private Integer storeId;
    /**失效时间 */
    private String dueDate;
    private String counter;
    private String number;   		//编号
    private double price;  			//价格
    private double oldPrice; 		//市场价
    private Integer circulation; 	//库存
    private Integer counterId;
    private String detail;
    private String thumb;
    private String name;  			//储值卡的名称
    private int isLimit;			//是否限制购买 1:限制 2:不限制
	private int limitNum;			//限制购买的数量

	public int getIsLimit() {
		return isLimit;
	}

	public void setIsLimit(int isLimit) {
		this.isLimit = isLimit;
	}
	
    public int getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(int limitNum) {
		this.limitNum = limitNum;
	}

	public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public Integer getCounterId() {
        return counterId;
    }

    public void setCounterId(Integer counterId) {
        this.counterId = counterId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getCirculation() {
        return circulation;
    }

    public void setCirculation(Integer circulation) {
        this.circulation = circulation;
    }
}
