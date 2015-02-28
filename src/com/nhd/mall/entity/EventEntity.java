package com.nhd.mall.entity;

/**首页banner活动实体类
 * Created by Administrator on 14-4-17.
 */
public class EventEntity {
    private Integer id;
    private String crDate;
    private String upDate;
    private String status;
    private Integer storeId ;
    private String title;
    private String thumb;
    private String entry;// url 是直接跳转的路径 // product 商品id,app通过商品ID访问，访问活动商品的详细页面
    private String value;
    private String detail;
    private ProductDetailEntity products[];
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ProductDetailEntity[] getProducts() {
        return products;
    }

    public void setProducts(ProductDetailEntity[] products) {
        this.products = products;
    }

    public String getCrDate() {
        return crDate;
    }

    public void setCrDate(String crDate) {
        this.crDate = crDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUpDate() {
        return upDate;
    }

    public void setUpDate(String upDate) {
        this.upDate = upDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
