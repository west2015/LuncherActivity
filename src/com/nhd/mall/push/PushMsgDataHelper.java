
package com.nhd.mall.push;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.v4.content.CursorLoader;

import com.nhd.mall.app.MainApplication;
import com.nhd.mall.entity.Member;
import com.nhd.mall.entity.Message;
import com.nhd.mall.util.CommonUtils;
import com.nhd.mall.util.ParseJson;

/**
* @ClassName: CacheDataHelper
* @Description: TODO(推送消息数据库访问对象，用于增删改查)
* @author EP epowns@gmail.com
* @date 2014-4-17 上午8:51:44
*/
public class PushMsgDataHelper extends BaseDataHelper {
    //消息类型
    private PushMsgCategory mCategory;

    public PushMsgDataHelper(Context context, PushMsgCategory category) {
        super(context);
        mCategory = category;
    }

    @Override
    protected Uri getContentUri() {
    	return CacheProvider.URI_PUSH_MSG_CONTENT;
    }

    //保存接收到的推送消息
    public void insert(final Message msg) {
    	CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                ContentValues values = new ContentValues();
                if(msg.getMemberId()!=null){
                    values.put(CacheDBInfo.MEMBER_ID, msg.getMemberId());
                }
                values.put(CacheDBInfo.CATEGORY, PushMsgCategory.UNREAD.ordinal());
                values.put(CacheDBInfo.JSON, ParseJson.getObjectJackson(msg));
                insert(values);
                return null;
            }
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
            }
        });
    }
    //删除某行消息 id 为 _id 字段
    public int delete(Long id) {
        int row = 0;
        row = delete(null,CacheDBInfo._ID + "=?", new String[] {String.valueOf(id)});
        return row;
    }
    //更新某条消息的状态为已读取
    public void readMsg(Long id){
        ContentValues values = new ContentValues();
        values.put(CacheDBInfo.CATEGORY, PushMsgCategory.READ.ordinal());
        update(values,CacheDBInfo._ID + "=?", new String[] {String.valueOf(id)});
    }

    /**
     * TODO(获取未读消息条数)
     */
    public int getUnreadMsgCount(){

        synchronized (CacheProvider.DBLock) {
            CacheProvider.CacheDBHelper mDBHelper = CacheProvider.getCacheDBHelper();
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            int count = 0;
            if(MainApplication.getInstance().getMember()==null){
                count = DatabaseUtils.queryCount(db,CacheDBInfo.TABLE_NAME,CacheDBInfo.CATEGORY + "=? AND " + CacheDBInfo.MEMBER_ID+" IS NULL ", new String[] {
                        String.valueOf(PushMsgCategory.UNREAD.ordinal())});
            }else{
                count = DatabaseUtils.queryCount(db,CacheDBInfo.TABLE_NAME,CacheDBInfo.CATEGORY + "=? AND ("
                        +CacheDBInfo.MEMBER_ID +"=? OR " + CacheDBInfo.MEMBER_ID+" IS NULL )", new String[] {
                        String.valueOf(PushMsgCategory.UNREAD.ordinal()),
                        String.valueOf(MainApplication.getInstance().getMember().getId())});
            }

            return count;
        }

    }

    public CursorLoader getCursorLoader() {
    	if(MainApplication.getInstance().getMember()==null){
    		return new CursorLoader(getContext(), getContentUri(), null, CacheDBInfo.MEMBER_ID+" IS NULL ",null, CacheDBInfo._ID + " DESC");
        }else{//当用户登录则查询通用消息和用户所属的消息
        	return new CursorLoader(getContext(), getContentUri(), null, CacheDBInfo.MEMBER_ID+" IS NULL OR "
                    + CacheDBInfo.MEMBER_ID +"=?",
                    new String[] {
                        String.valueOf(MainApplication.getInstance().getMember().getId())
                    }, CacheDBInfo._ID + " DESC");
        }
        
    }

    /**
    * @ClassName: CacheDBInfo
    * @Description: TODO(推送消息缓存表，为了简化表结构，仅通过存储json数据进行缓存。)
    * @author EP epowns@gmail.com
    * @date 2014-4-17 上午11:38:26
    */
    public static final class CacheDBInfo implements BaseColumns {
        private CacheDBInfo() {
        }

        public static final String TABLE_NAME = "push_msg";

        /** 消息ID */
        public static final String ID = "id";

        /** 消息分类 */
        public static final String CATEGORY = "category";

        /** 存储消息json数据 */
        public static final String JSON = "json";

        /** 用户ID，用于多个用户区分数据的情况 */
        public static final String MEMBER_ID = "member_id";

        public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
                .addColumn(ID, Column.DataType.INTEGER)
                .addColumn(CATEGORY, Column.DataType.INTEGER)
                .addColumn(JSON, Column.DataType.TEXT)
                .addColumn(MEMBER_ID, Column.DataType.INTEGER);
    }
}
