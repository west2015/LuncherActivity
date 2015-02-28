package com.nhd.mall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import com.nhd.mall.R;
import com.nhd.mall.adapter.GoodsAdapter;
import com.nhd.mall.adapter.SearchProductAdapter;
import com.nhd.mall.asyncTask.ProductListGet;
import com.nhd.mall.asyncTask.SearchProductsGet;
import com.nhd.mall.entity.ProductDetailEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.PullDownView;

/**搜索返回回来的商品列表
 * Created by Administrator on 14-4-28.
 */
public class SearchProductFragment extends Fragment implements PullDownView.OnPullDownListener,AdapterView.OnItemClickListener,OnAsyncTaskUpdateListener {
    private String orderBy ; //判断是货物列表的哪一个（默认，销量，价格）
    private PullDownView listView;
    private SearchProductAdapter ga;
    private ProductDetailEntity[]entity;
    private ProductListGet plg;
    private final int MAX=10;
    private String query;
    private SearchProductsGet spg;

    static SearchProductFragment getInstance(String sort,String orderBy ){
        SearchProductFragment mFragment=new SearchProductFragment();
        Bundle bundle=new Bundle();
        bundle.putString("query",sort);
        bundle.putString("orderBy",orderBy);
        mFragment.setArguments(bundle);
        return mFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.goods_list_layout, container, false);
        findView(view);
        return view;
    }
    private void findView(View view) {
        query = getArguments().getString("query");
        orderBy = getArguments().getString("orderBy");
        listView = (PullDownView)view.findViewById(R.id.goodListView);
        ga = new SearchProductAdapter(getActivity(),entity);
        listView.setAdapter(ga);
        listView.setOnPullDownListener(this);
        listView.getListView().setFooterDividersEnabled(false);
        listView.enableAutoFetchMore(true, 1);
        listView.setOnItemClickListener(this);
        listView.setHideFooter();
        spg = new SearchProductsGet(getActivity(),query,orderBy);
        spg.setListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int pos = i-1;
        Bundle bundle = new Bundle();
        bundle.putInt("productId",entity[pos].getProductId());
        new startIntent(getActivity(),GoodsDetailActivity.class,bundle);
    }
    @Override
    public void onRefresh() {
        entity=null;
        spg.update(null);
    }
    @Override
    public void onMore() {
        spg.update(entity);
    }

    @Override
    public void getData(Object obj, String message) {
        listView.RefreshComplete();
        listView.notifyDidMore();
        if (message != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        if (obj == null)
            return;
        if(obj instanceof ProductDetailEntity[]){
            entity = (ProductDetailEntity[]) obj;
            ga.update(entity);
            if(entity.length>MAX){
                listView.setShowFooter();
            }
            else{
                listView.setHideFooter();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    if(ga!=null){
        ga.imageLoader.clearMemory();
        ga.imageLoader=null;
    }
    }

}
