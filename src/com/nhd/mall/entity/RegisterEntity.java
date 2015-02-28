package com.nhd.mall.entity;

import java.io.Serializable;

/**
 * 注册实体类
 * 
 * @author zxd
 * 
 */

@SuppressWarnings("serial")
public class RegisterEntity implements Serializable{
    private String password;
    private String name;
    private String code;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
