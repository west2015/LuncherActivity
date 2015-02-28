package com.nhd.mall.activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.ShopGoodsListAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.GoodsFilterEntity;
import com.nhd.mall.entity.GoodsFilterList;
import com.nhd.mall.entity.GoodsFilterSecondEntity;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;

/**
 * Created by Administrator on 14-5-29.
 */
public class shopGoodsListActivity extends ModelActivity implements AdapterView.OnItemClickListener  {

    private GoodsFilterList list;
    private GoodsFilterEntity[]filter;
    private int position;
    private GoodsFilterSecondEntity[]children;
    private ListView listView;
    private ShopGoodsListAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_goods_list_layout);
        setTitle("");
        find();
    }
    private void find() {
        if(getIntent().getExtras()!=null){
            list = (GoodsFilterList) getIntent().getExtras().getSerializable("goods");
            if(getIntent().getExtras().get("name")!=null){
                String s = (String) getIntent().getExtras().get("name");
                setTitle(s);
            }
            position = getIntent().getExtras().getInt("position",0);
            if(list!=null){
                filter = list.getCategorys();
                if(filter!=null&&filter.length>0){
                    children = filter[position].getChildren();
                }
            }
        }
        listView =(ListView)findViewById(R.id.listView);
        adapter = new ShopGoodsListAdapter(this,children);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        if(children==null||children.length<=0)return ;
        int category = children[pos].getId();
        Bundle bundleTh = new Bundle();
        bundleTh.putInt("category",category);
        bundleTh.putString("sort","filter");
        bundleTh.putString("search_for","normal");
        new startIntent(shopGoodsListActivity.this,ThemeGoodsActivity.class,bundleTh);
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
    protected void onDestroy() {
        super.onDestroy();
    }
}
