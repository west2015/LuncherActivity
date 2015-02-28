package com.nhd.mall.entity;

/**专柜实体类
 * Created by Administrator on 14-4-16.
 */
public class CounterEntity {
    private Integer counterId;
    private String name;
    private String createDate;
    private String uoDate;
    private String detail;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCounterId() {
        return counterId;
    }

    public void setCounterId(Integer counterId) {
        this.counterId = counterId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUoDate() {
        return uoDate;
    }

    public void setUoDate(String uoDate) {
        this.uoDate = uoDate;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
