package com.nhd.mall.entity;

/**添加进购物车的实体类
 * Created by caili on 14-4-18.
 */
public class AddCarEntity {
    private Long memberId;
    private Integer num;
    private OrderProductEntity orderProduct;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public OrderProductEntity getOrderProduct() {
        return orderProduct;
    }

    public void setOrderProduct(OrderProductEntity orderProduct) {
        this.orderProduct = orderProduct;
    }
}
