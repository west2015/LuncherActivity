package com.nhd.mall.entity;

/**商品集合实体类
 * Created by Administrator on 14-4-21.
 */
public class ProductList {
    private ProductDetailEntity[] products;

    public ProductDetailEntity[] getProducts() {
        return products;
    }

    public void setProducts(ProductDetailEntity[] products) {
        this.products = products;
    }
}
