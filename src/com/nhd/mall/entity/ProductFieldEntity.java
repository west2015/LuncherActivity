package com.nhd.mall.entity;

import java.io.Serializable;

/**商品拓展参数
 * Created by caili on 14-4-17.
 */
public class ProductFieldEntity  implements Serializable {
    private Integer fieldId;
    private String name;
    private ProductFieldValue[]values;

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductFieldValue[] getValues() {
        return values;
    }

    public void setValues(ProductFieldValue[] values) {
        this.values = values;
    }
}
