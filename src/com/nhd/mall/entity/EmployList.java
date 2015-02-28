package com.nhd.mall.entity;

/**
 * Created by Administrator on 14-5-30.
 */
public class EmployList {
    EmployEntity[]advertises;
    private String email;

    public EmployEntity[] getAdvertises() {
        return advertises;
    }

    public void setAdvertises(EmployEntity[] advertises) {
        this.advertises = advertises;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
