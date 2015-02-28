package com.nhd.mall.entity;

/**
 * Created by Administrator on 14-4-28.
 */
public class BeanDetailEntity {
    private Integer id;
    private Long memberId;
    private double num;
    private String useDate;
    private String status;
    private FormEntity order;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FormEntity getOrder() {
        return order;
    }

    public void setOrder(FormEntity order) {
        this.order = order;
    }
}
