package com.nhd.mall.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 14-4-21.
 */
public class CarList implements Serializable {
    private CarEntity[] cars;

    public CarEntity[] getCars() {

        return cars;
    }

    public void setCars(CarEntity[] cars) {
        this.cars = cars;
    }
}
