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
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyMsgAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.entity.Message;
import com.nhd.mall.push.PushMsgDataHelper;
import com.nhd.mall.util.CommonUtils;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

/**消息详情页面
 * Created by caili on 14-4-9.
 */
public class MessageDetailActivity extends ModelActivity {

    private TextView tvDate,tvContent;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("消息");
        setContentView(R.layout.message_detail_layout);
        init();
        fillData();
    }
    private void init() {
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    private void fillData(){
        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().getInt("from")==TwoDimCodeActivity.REQUIRE_CODE){
                setTitle("扫码结果");
                String str = getIntent().getExtras().getString("DOI");
                tvDate.setText("文本内容:");
                tvContent.setText(str);
            }else{
                Message msg = (Message) getIntent().getExtras().getSerializable("message");
                tvDate.setText(msg.getCrDate());
                tvContent.setText(msg.getMessage());
            }

        }
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
