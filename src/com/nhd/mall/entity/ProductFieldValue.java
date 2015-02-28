package com.nhd.mall.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 14-4-17.
 */
public class ProductFieldValue implements Serializable {
    private Integer id;
    private String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
