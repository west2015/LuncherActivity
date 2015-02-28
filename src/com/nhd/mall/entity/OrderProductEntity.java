package com.nhd.mall.entity;

import java.io.Serializable;

/**订单商品详情类
 * Created by caili on 14-4-21.
 */
public class OrderProductEntity implements Serializable {
    private Integer id;
    private Integer orderId;
    private Integer productId;//商品ID
    private OrderFiledEntity[] orderFields;   //参数
    private Integer num;
    private Double price;//商品单价
    private Integer storeId;
    private Integer brandId;
    private String description;
    private String name;
    private String crDate;
    private String thumb;
    private Integer buyLimit;//商品限购数
    private double freight;//运费
    private Integer total;//库存
    private double freightRule;
    
    private String color;
    private String specification;
    private Integer getway;


	public Integer getGetway() {
		return getway;
	}

	public void setGetway(Integer getway) {
		this.getway = getway;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public double getFreightRule() {
        return freightRule;
    }

    public void setFreightRule(double freightRule) {
        this.freightRule = freightRule;
    }

    public Integer getBuyLimit() {
        return buyLimit;
    }

    public void setBuyLimit(Integer buyLimit) {
        this.buyLimit = buyLimit;
    }

    public double getFreight() {
        return freight;
    }

    public void setFreight(double freight) {
        this.freight = freight;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public OrderFiledEntity[] getOrderFields() {
        return orderFields;
    }

    public void setOrderFields(OrderFiledEntity[] orderFields) {
        this.orderFields = orderFields;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCrDate() {
        return crDate;
    }

    public void setCrDate(String crDate) {
        this.crDate = crDate;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
