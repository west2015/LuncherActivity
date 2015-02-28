package com.nhd.mall.activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.GoodsAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.asyncTask.EventProductGet;
import com.nhd.mall.entity.EventEntity;
import com.nhd.mall.entity.EventEntityList;
import com.nhd.mall.entity.ProductDetailEntity;
import com.nhd.mall.util.NetCheck;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;

/**
 * Created by Administrator on 14-8-1.
 */
public class EventProductActivity extends ModelActivity  implements OnAsyncTaskUpdateListener,AdapterView.OnItemClickListener {

    private int  id;
    private EventEntity activity;
    private EventProductGet get;
    private GoodsAdapter ga;
    private ProductDetailEntity[]entity;
    private ListView lvProduct;
    private TextView tvEventTitle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_layout);
        setTitle("活动详情");
        find();
    }

    private void find() {
        if(getIntent().getExtras()!=null){
            id = getIntent().getExtras().getInt("eventId");
        }
        tvEventTitle = (TextView)findViewById(R.id.textView);
        lvProduct = (ListView)findViewById(R.id.lvProduct);
        ga = new GoodsAdapter(this,entity);
        lvProduct.setAdapter(ga);
        lvProduct.setOnItemClickListener(this);
        if(NetCheck.checkNet(this)){
            get = new EventProductGet(this,id);
            get.setListener(this);
        }
    }
    @Override
    public void getData(Object obj, String message) {
        if (message != null){
            if(EventProductActivity.this!=null){
                Toast.makeText(EventProductActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
        if (obj == null)
            return;
        if(obj instanceof EventEntityList){
            EventEntityList list = (EventEntityList) obj;
            activity = list.getActivity();
            if(activity!=null){
                entity = activity.getProducts();
                ga.update(entity);
                tvEventTitle.setText(activity.getTitle()==null?"":activity.getTitle());
            }
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        Bundle bundle = new Bundle();
        bundle.putInt("productId",entity[pos].getProductId());
        new startIntent(EventProductActivity.this,GoodsDetailActivity.class,bundle);
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(ga!=null){
            ga.imageLoader.clearMemory();
        }
    }
}
