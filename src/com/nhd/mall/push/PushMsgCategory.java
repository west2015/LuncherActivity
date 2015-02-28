
package com.nhd.mall.push;

/**
* @ClassName: CacheCategory
* @Description: TODO(枚举APP中所有需要缓存的分类，数据库中的category字段通过枚举次序存储，
* 因此不能轻易改变最初的次序或者删除，如需增加，请依次加入。)
* @author EP epowns@gmail.com
* @date 2014-4-17 上午11:51:32
*/
public enum PushMsgCategory {
	READ("read"),//已读
	UNREAD("unread");//未读
	
    private String mDisplayName;

    PushMsgCategory(String displayName) {
        mDisplayName = displayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
}
