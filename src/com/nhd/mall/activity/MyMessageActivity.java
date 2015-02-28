package com.nhd.mall.activity;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyMsgAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.entity.Message;
import com.nhd.mall.entity.MessageCategory;
import com.nhd.mall.push.PushMsgDataHelper;
import com.nhd.mall.util.CommonUtils;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

/**我的消息页面
 * Created by caili on 14-4-9.
 */
public class MyMessageActivity extends ModelActivity implements AdapterView.OnItemClickListener,LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private ListView lvMessage;
    private MyMsgAdapter mAdapter;
    private PushMsgDataHelper mDataHelper;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("我的消息");
        setContentView(R.layout.my_message_layout);
        init();
    }
    private void init() {
        mDataHelper = new PushMsgDataHelper(MainApplication.getContext(),null);
        lvMessage = (ListView)findViewById(R.id.lvMessage);
        mAdapter = new MyMsgAdapter(this);
        lvMessage.setAdapter(mAdapter);
        lvMessage.setOnItemClickListener(this);
        ((CheckBox)findViewById(R.id.all_check)).setOnCheckedChangeListener(this);
        getSupportLoaderManager().initLoader(0, null, this);
        getButton(R.drawable.shop_car_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... objects) {
                        for (Long key:mAdapter.selectedMap.keySet()){
                            if(mAdapter.selectedMap.get(key)!=null && mAdapter.selectedMap.get(key).equals(true)){
                                mDataHelper.delete(key);
                            }
                        }
                        return null;
                    }
                });

            }
        });
    }



    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mAdapter.selectAll(b);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final Long id = mAdapter.getItemId(i);
        String value = mAdapter.getItem(i).getCategory();
        if(value==null || value.equals("")){
            showMsg(mAdapter.getItem(i));
            CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                @Override
                protected Object doInBackground(Object... objects) {
                    mDataHelper.readMsg(id);//读取消息，并标记为已读
                    return null;
                }
            });
            return;
        }
        //点击消息跳转到相应的界面
        switch (Enum.valueOf(MessageCategory.class,value)){
            case feeback_reply:
                new startIntent(this,CustomerCommentActivity.class);
                break;
            case coupon:
                new startIntent(this,MyCouponActivity.class);
                break;
            case ship:
//                new startIntent(this,MyFormActivity.class);
                break;
            case mention:
//                new startIntent(this,MyFormActivity.class);
                break;
            default://默认查看消息内容
                showMsg(mAdapter.getItem(i));
                break;
        }
        CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... objects) {
                mDataHelper.readMsg(id);//读取消息，并标记为已读
                return null;
            }
        });
    }

    //展示消息
    public void showMsg(Message msg){
        Bundle bundle = new Bundle();
        bundle.putSerializable("message",msg);
        new startIntent(this,MessageDetailActivity.class,bundle);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return mDataHelper.getCursorLoader();//异步加载数据库数据
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }
    @Override
    public void onResume() {
        super.onResume();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onResume(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onPause(this);
        }
    }
}
