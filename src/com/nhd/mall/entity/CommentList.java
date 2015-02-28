package com.nhd.mall.entity;

/**商品评论列表集合
 * Created by caili on 14-4-18.
 */
public class CommentList {
    private CommentEntity[]comments;

    public CommentEntity[] getComments() {
        return comments;
    }

    public void setComments(CommentEntity[] comments) {
        this.comments = comments;
    }
}
