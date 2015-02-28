package com.nhd.mall.entity;

/**
 * Created by Administrator on 14-4-30.
 */
public class PostFeedBackEntity {
    private String message;
    private Long memberId;
    private String category;//消息类型 com：普通，c_back：客户端反馈，s_reply：后台回复反馈

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
