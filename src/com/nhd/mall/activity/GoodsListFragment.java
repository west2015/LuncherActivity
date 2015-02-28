package com.nhd.mall.activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.nhd.mall.R;
import com.nhd.mall.adapter.GoodsAdapter;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.ProductListGet;
import com.nhd.mall.entity.MainEntity;
import com.nhd.mall.entity.ProductDetailEntity;
import com.nhd.mall.entity.ProductList;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.PullDownView;

/**筛选货物列表
 * Created by caili on 14-4-3.
 */
public class GoodsListFragment extends Fragment implements PullDownView.OnPullDownListener,AdapterView.OnItemClickListener,OnAsyncTaskUpdateListener{
    private String sort;
    private String orderBy ; //判断是货物列表的哪一个（默认，销量，价格）
    private PullDownView listView;
    private GoodsAdapter ga;
    private ProductList product;
    private ProductDetailEntity[]entity;
    private Integer pageNum=1;
    private ProductListGet plg;
    private Integer storeId =1;
    private final int MAX=10;
    //如果是从标签跳过来的则需要这几个参数
    private String search_for;
    private Integer tagId;
    //如果是从主题购物跳过来的则需要这几个参数nteger
    private Integer themeId;
    //如果是从筛选跳过来的则需要这几个参数
    private Integer categoryId;
    //从品牌商店传过来的则需要这个参数
    private Integer brandId;



    static GoodsListFragment getInstance(String sort,String search_for,Integer tagId,Integer themeId,Integer categoryId,Integer branchId,String orderBy ){
        GoodsListFragment mFragment=new GoodsListFragment();
        Bundle bundle=new Bundle();
        bundle.putString("sort",sort);
        bundle.putString("orderBy",orderBy);
        bundle.putString("search_for",search_for);
        bundle.putInt("tagId", tagId);
        bundle.putInt("themeId", themeId);
        bundle.putInt("categoryId", categoryId);
        bundle.putInt("brandId", branchId);
        mFragment.setArguments(bundle);
        return mFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.goods_list_layout, container, false);
       findView(view);
        return view;
    }
    private void findView(View view) {
        sort = getArguments().getString("sort");
        orderBy = getArguments().getString("orderBy");
        search_for = getArguments().getString("search_for");
        tagId = getArguments().getInt("tagId");
        themeId = getArguments().getInt("themeId");
        categoryId = getArguments().getInt("categoryId");
        brandId = getArguments().getInt("branchId");
        if(MainApplication.getInstance().getStore()!=null){
            storeId = MainApplication.getInstance().getStore().getId();
        }
        listView = (PullDownView)view.findViewById(R.id.goodListView);
        ga = new GoodsAdapter(getActivity(),entity);
        listView.setAdapter(ga);
        listView.setOnPullDownListener(this);
        listView.getListView().setFooterDividersEnabled(false);
        listView.enableAutoFetchMore(true, 1);
        listView.setOnItemClickListener(this);
        listView.setHideFooter();
        getGoodsList();
    }

    private void getGoodsList() {
        //是品牌商店传递过来的
        if(sort.equals("branch")){
            plg = new ProductListGet(getActivity(),storeId,pageNum,search_for);
            plg.setBranchParemeter(brandId,orderBy);
            plg.setListener(this);
        }
        //是首页标签传递过来的
        if(sort.equals("tag")){
            plg = new ProductListGet(getActivity(),storeId,pageNum,search_for);
            plg.setTagParemeter(tagId,orderBy);
            plg.setListener(this);
        }
        //是首页主题传递过来的
       else if(sort.equals("theme")){
            plg = new ProductListGet(getActivity(),storeId,pageNum,search_for);
            plg.setThemesParemeter(themeId,orderBy);
            plg.setListener(this);
        }
        //是筛选传递过来的
        else if(sort.equals("filter")){
            plg = new ProductListGet(getActivity(),storeId,pageNum,search_for);
            plg.setFilterParemeter(categoryId,orderBy);
            plg.setListener(this);
        }
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
        pageNum=1;
        plg.update(1,null);
    }
    @Override
    public void onMore() {
        plg.update(++pageNum,product);
    }

    @Override
    public void getData(Object obj, String message) {
        listView.RefreshComplete();
        listView.notifyDidMore();
        if (message != null){
            if(getActivity()!=null){
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
        if (obj == null)
            return;
        if(obj instanceof ProductList){
            product = (ProductList) obj;
            entity = product.getProducts();
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
        ga.update(entity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(ga!=null){
            ga.imageLoader.clearMemory();
        }
    }
}
