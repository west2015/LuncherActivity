package com.nhd.mall.entity;

/**首页实体类
 * Created by Administrator on 14-4-17.
 */
public class MainEntity {
    private EventEntity[] activitys;
    private TagEntity[] tags;
    private ThemesEntity[] themes;


    public EventEntity[] getActivitys() {
        return activitys;
    }

    public void setActivitys(EventEntity[] activitys) {
        this.activitys = activitys;
    }

    public TagEntity[] getTags() {
        return tags;
    }

    public void setTags(TagEntity[] tags) {
        this.tags = tags;
    }

    public ThemesEntity[] getThemes() {
        return themes;
    }

    public void setThemes(ThemesEntity[] themes) {
        this.themes = themes;
    }
}
