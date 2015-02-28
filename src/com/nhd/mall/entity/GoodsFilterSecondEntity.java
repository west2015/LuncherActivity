package com.nhd.mall.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 14-4-22.
 */
public class GoodsFilterSecondEntity  implements Serializable {
    private Integer id;
    private String name;
    private Integer ref; //父级ID
    private String crDate;
    private String upDate;
    private String stauts;
    private String thumb;
    private GoodsFilterThirdEntity[]children;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRef() {
        return ref;
    }

    public void setRef(Integer ref) {
        this.ref = ref;
    }

    public String getCrDate() {
        return crDate;
    }

    public void setCrDate(String crDate) {
        this.crDate = crDate;
    }

    public String getUpDate() {
        return upDate;
    }

    public void setUpDate(String upDate) {
        this.upDate = upDate;
    }

    public String getStauts() {
        return stauts;
    }

    public void setStauts(String stauts) {
        this.stauts = stauts;
    }

    public GoodsFilterThirdEntity[] getChildren() {
        return children;
    }

    public void setChildren(GoodsFilterThirdEntity[] children) {
        this.children = children;
    }
}
