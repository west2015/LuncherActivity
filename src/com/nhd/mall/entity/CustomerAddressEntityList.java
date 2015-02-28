package com.nhd.mall.entity;

/**用户地址详细列表实体类
 * Created by Administrator on 14-4-18.
 */
public class CustomerAddressEntityList {
    private CustomerAddressEntity[]address;

    public CustomerAddressEntity[] getAddress() {

        return address;
    }

    public void setAddress(CustomerAddressEntity[] address) {
        this.address = address;
    }
}
