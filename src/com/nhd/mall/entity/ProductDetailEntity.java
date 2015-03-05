package com.nhd.mall.entity;

import java.io.Serializable;

/**商品详情实体类
 * Created by caili on 14-4-17.
 */
public class ProductDetailEntity implements Serializable {
    private Integer productId;//商品ID
    private Integer brandId;//品牌ID
    private Integer categoryId;//分类ID
    private Integer counterId; //专柜ID
    private String name;//商品名称
    private String description;//详细
    private String thumb;
    private double price;//价格
    private Integer total;//库存
    private Integer sales;//销量
    private String crDate;
    private String upDate;
    private String brandName;  //品牌名称
    private String storeName;
    private Integer storeId;
    private double freight; //运费
    private Integer buyLimit;//限购
    private ProductImageEntity[] productDetails;  //图片
    private ProductFieldEntity[] productFields;   //参数
    private String pictureCode;
    private double freightRule;
    private String number;
    private double oldPrice;
    private Integer getway;

	public Integer getGetway() {
		return getway;
	}

	public void setGetway(Integer getway) {
		this.getway = getway;
	}

	public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getFreightRule() {
        return freightRule;
    }

    public void setFreightRule(double freightRule) {
        this.freightRule = freightRule;
    }

    public String getPictureCode() {
        return pictureCode;
    }

    public void setPictureCode(String pictureCode) {
        this.pictureCode = pictureCode;
    }

    public double getFreight() {
        return freight;
    }

    public void setFreight(double freight) {
        this.freight = freight;
    }

    public Integer getBuyLimit() {
        return buyLimit;
    }

    public void setBuyLimit(Integer buyLimit) {
        this.buyLimit = buyLimit;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCounterId() {
        return counterId;
    }

    public void setCounterId(Integer counterId) {
        this.counterId = counterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public String getCrDate() {
        return crDate;
    }

    public void setCrDate(String crDate) {
        this.crDate = crDate;
    }

    public String getUpDate() {
        return upDate;
    }

    public void setUpDate(String upDate) {
        this.upDate = upDate;
    }

    public ProductImageEntity[] getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(ProductImageEntity[] productDetails) {
        this.productDetails = productDetails;
    }

    public ProductFieldEntity[] getProductFields() {
        return productFields;
    }

    public void setProductFields(ProductFieldEntity[] productFields) {
        this.productFields = productFields;
    }
}
