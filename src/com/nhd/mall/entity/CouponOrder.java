package com.nhd.mall.entity;

/**全订单实体类
 * Created by Administrator on 14-7-1.
 */
public class CouponOrder {
  private Integer id;
  private int num;
  private double price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
