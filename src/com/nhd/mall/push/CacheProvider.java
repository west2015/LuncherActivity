package com.nhd.mall.push;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.nhd.mall.app.MainApplication;

/**
* @ClassName: CacheProvider
* @Description: TODO(推送消息json数据缓存,使用ContentProvider是为了方便使用CursorLoader和CursorAdapter)
* @author EP epowns@gmail.com
* @date 2014-4-16 上午9:31:27
*/
public class CacheProvider extends ContentProvider{
	private static final String TAG = "CacheProvider";
	
	static final Object DBLock = new Object();
	
    public static final String AUTHORITY = "com.nhd.mall.provider";

    public static final String SCHEME = "content://";
    
    // cache(简化缓存，把所有的json数据存在一张表中)
    public static final String PATH_PUSH_MSG = "push_msg";
    public static final Uri URI_PUSH_MSG_CONTENT = Uri.parse(SCHEME + AUTHORITY + "/"+ PATH_PUSH_MSG);
    private static final int CODE_PUSH_MSG = 0;
    public static final String TYPE_PUSH_MSG_CONTENT = "vnd.android.cursor.dir/vnd.nhd.mall.cache";

    private static final UriMatcher sUriMatcher;
    
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, PATH_PUSH_MSG, CODE_PUSH_MSG);
    }
    
    private static CacheDBHelper mCacheDBHelper;
    
    public static CacheDBHelper getCacheDBHelper(){
    	if(mCacheDBHelper==null){
    		mCacheDBHelper = new CacheDBHelper(MainApplication.getContext());
    	}
    	return mCacheDBHelper;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        	case CODE_PUSH_MSG:
        		return TYPE_PUSH_MSG_CONTENT;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private String matchTable(Uri uri) {
        String table = null;
        switch (sUriMatcher.match(uri)) {
            case CODE_PUSH_MSG:
                table = PushMsgDataHelper.CacheDBInfo.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return table;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        synchronized (DBLock) {
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            String table = matchTable(uri);
            queryBuilder.setTables(table);

            SQLiteDatabase db = getCacheDBHelper().getReadableDatabase();
            Cursor cursor = queryBuilder.query(db, // The database to
                                                   // queryFromDB
                    projection, // The columns to return from the queryFromDB
                    selection, // The columns for the where clause
                    selectionArgs, // The values for the where clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    sortOrder // The sort order
                    );

            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) throws SQLException {
        synchronized (DBLock) {
            String table = matchTable(uri);
            SQLiteDatabase db = getCacheDBHelper().getWritableDatabase();
            long rowId = 0;
            db.beginTransaction();
            try {
                rowId = db.insert(table, null, values);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
            if (rowId > 0) {
                Uri returnUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnUri;
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        synchronized (DBLock) {
            String table = matchTable(uri);
            SQLiteDatabase db = getCacheDBHelper().getWritableDatabase();
            db.beginTransaction();
            try {
                for (ContentValues contentValues : values) {
                    db.insertWithOnConflict(table, BaseColumns._ID, contentValues,
                            SQLiteDatabase.CONFLICT_IGNORE);
                }
                db.setTransactionSuccessful();
                getContext().getContentResolver().notifyChange(uri, null);
                return values.length;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        synchronized (DBLock) {
            SQLiteDatabase db = getCacheDBHelper().getWritableDatabase();

            int count = 0;
            String table = matchTable(uri);
            db.beginTransaction();
            try {
                count = db.delete(table, selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        synchronized (DBLock) {
            SQLiteDatabase db = getCacheDBHelper().getWritableDatabase();
            int count;
            String table = matchTable(uri);
            db.beginTransaction();
            try {
                count = db.update(table, values, selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);

            return count;
        }
    }
	
	static class CacheDBHelper extends SQLiteOpenHelper{
        // 数据库名
        private static final String DB_NAME = "nhd_cache.db";

        // 数据库版本
        private static final int VERSION = 1;
        
		public CacheDBHelper(Context context) {
			super(context, DB_NAME, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
            PushMsgDataHelper.CacheDBInfo.TABLE.create(db);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
