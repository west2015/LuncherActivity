package com.nhd.mall.entity;

/**楼层导航实体类
 * Created by Administrator on 14-5-6.
 */
public class DoorEntity {
    private Integer id;
    private String name;
    private String navigateImg; //楼层导航图，放外面
    private String floorImg;//楼层平面图,放里面
    private String crDate;
    private String upDate;
    private String status;

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

    public String getNavigateImg() {
        return navigateImg;
    }

    public void setNavigateImg(String navigateImg) {
        this.navigateImg = navigateImg;
    }

    public String getFloorImg() {
        return floorImg;
    }

    public void setFloorImg(String floorImg) {
        this.floorImg = floorImg;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
