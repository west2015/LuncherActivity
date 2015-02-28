
package com.nhd.mall.entity;

/**
* @ClassName: MessageCategory
* @Description: TODO(消息类型，用于标记我的消息中的跳转类型)
* @author EP epowns@gmail.com
* @date 2014-4-17 上午11:51:32
 *
 * com:普通类型，s_reply:服务端回复反馈，c_back:客户端反馈信息
 * feeback_reply：到意见反馈模块 coupon：到我的优惠券列表 ship（发货）mention（自提）：订单表
*/
public enum MessageCategory {
	com,
    feeback_reply,
    coupon,
    ship,
    mention;
/*
    private String mDisplayName;

    MessageCategory(String displayName) {
        mDisplayName = displayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }*/
}
