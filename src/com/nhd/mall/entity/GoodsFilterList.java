package com.nhd.mall.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 14-4-22.
 */
public class GoodsFilterList  implements Serializable {
    private GoodsFilterEntity[]categorys;

    public GoodsFilterEntity[] getCategorys() {
        return categorys;
    }

    public void setCategorys(GoodsFilterEntity[] categorys) {
        this.categorys = categorys;
    }
}
