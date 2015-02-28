package com.nhd.mall.entity;

/**
 * Created by Administrator on 14-4-18.
 */
public class PublicCommentEntity {
    private Long memberId;
    private Integer productId;
    private String message;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
