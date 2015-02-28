package com.nhd.mall.entity;

/**
 * Created by Administrator on 14-4-22.
 */
public class CollectionEntity {
    private Integer id;
    private Integer type;
    private BrandEntity brands;
    private ProductDetailEntity products;
    private Integer count;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BrandEntity getBrands() {
        return brands;
    }

    public void setBrands(BrandEntity brands) {
        this.brands = brands;
    }

    public ProductDetailEntity getProducts() {
        return products;
    }

    public void setProducts(ProductDetailEntity products) {
        this.products = products;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
