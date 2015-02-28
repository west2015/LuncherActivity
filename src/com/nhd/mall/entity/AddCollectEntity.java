package com.nhd.mall.entity;

/**
 * Created by Administrator on 14-4-21.
 */
public class AddCollectEntity {
    private Long memberId;
    private Integer type;   //1品牌 2商品
    private Integer collectionId;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }
}
