package com.nhd.mall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.nhd.mall.R;
import com.nhd.mall.adapter.CollectStoreAdapter;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.CollectionGet;
import com.nhd.mall.asyncTask.DeleteCollectionGet;
import com.nhd.mall.entity.CollectionEntity;
import com.nhd.mall.entity.CollectionList;
import com.nhd.mall.entity.ProductDetailEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.Utils;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.PullDownView;
import java.util.ArrayList;
import java.util.HashMap;

/**我的物品收藏
 * Created by caili on 14-4-10.
 */
public class CollectGoodsFragment extends Fragment implements PullDownView.OnPullDownListener,AdapterView.OnItemClickListener,OnAsyncTaskUpdateListener,CompoundButton.OnCheckedChangeListener {

    private PullDownView listView;
    private CollectStoreAdapter gca;
    private CollectionGet cg;
    private final int GOOD =2;
    private Integer pageNum =1;
    private Long memberId;
    private CollectionList cl;
    private CollectionEntity[] entity;
    private final int MAXCOUNT=10;
    private TextView tvTitle;
    private CheckBox cbAll;
    //用来保存删除的商品id
    ArrayList<Integer>idList = new ArrayList<Integer>();
    private RelativeLayout nothing;

    static CollectGoodsFragment getInstance(){
        CollectGoodsFragment mFragment=new CollectGoodsFragment();
        return mFragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.collect_goods_layout, container, false);
        findView(view);
        return view;
    }
    private void findView(View view) {
        nothing = (RelativeLayout)view.findViewById(R.id.rl_no_store);
        cbAll = (CheckBox)view.findViewById(R.id.check);
        cbAll.setOnCheckedChangeListener(this);
        tvTitle = (TextView)view.findViewById(R.id.tvTitle);
        tvTitle.setText("全部商品");
        listView = (PullDownView)view.findViewById(R.id.lvStore);
        gca = new CollectStoreAdapter(getActivity(),entity);
        listView.setAdapter(gca);
        listView.setOnPullDownListener(this);
        listView.setOnItemClickListener(this);
        listView.getListView().setFooterDividersEnabled(false);
        listView.setHideHeader();
        listView.enableAutoFetchMore(true, 1);
        listView.getListView().setDividerHeight(0);
        if(MainApplication.getInstance().getMember()!=null){
            memberId = MainApplication.getInstance().getMember().getId();
            cg = new CollectionGet(getActivity(),pageNum,memberId,GOOD);
            cg.setListener(this);
        }

        view.findViewById(R.id.rl_no_store).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.backToMain(CollectGoodsFragment.this.getActivity());
            }
        });

    }
    //删除收藏的商品
    public void delete() {
        if(gca==null)return;
        if(gca.selectMap.size()<=0){
            Toast.makeText(getActivity(),"请选择删除的商品",Toast.LENGTH_SHORT).show();
            return;
        }
        idList.clear();
        for(int i:gca.selectMap.keySet()){
           if(gca.selectMap.get(i)){
               CollectionEntity collect = entity[i];
               idList.add(collect.getId());
           }
        }
        Integer[]ids = new Integer[idList.size()];
        ids = idList.toArray(ids);
        new DeleteCollectionGet(getActivity(),ids).setListener(this);

    }
    //删除成功后返回进行刷新
    private void successNotify() {
     ArrayList<CollectionEntity>entityList = new ArrayList<CollectionEntity>();
        for(int i=0;i<entity.length;i++){
            entityList.add(entity[i]);
        }
        for(int i=0;i<idList.size();i++){
            for(int j=0;j<entityList.size();j++){
                if(entityList.get(j).getId()==idList.get(i)){
                    entityList.remove(j);
                    break;
                }
            }
        }
        CollectionEntity[] collect = new CollectionEntity[entityList.size()];
        collect = entityList.toArray(collect);
        entity = collect;
        if(entity==null||entity.length<=0){
            nothing.setVisibility(View.VISIBLE);
        }
        else{
            nothing.setVisibility(View.GONE);
        }
        gca.update(entity);
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int pos = i-1;
        ProductDetailEntity product = entity[pos].getProducts();
        Bundle bundleTh = new Bundle();
        bundleTh.putInt("productId",product.getProductId());
        new startIntent(getActivity(),GoodsDetailActivity.class,bundleTh);
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onMore() {
        cg.update(++pageNum);
    }
    @Override
    public void getData(Object obj, String message) {
        listView.RefreshComplete();
        listView.notifyDidMore();
        if (message != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        if (obj == null){
            nothing.setVisibility(View.VISIBLE);
            return;
        }
        if(obj instanceof CollectionList){
            cl = (CollectionList) obj;
            entity = cl.getCollections();
            if(entity.length<=0){
                nothing.setVisibility(View.VISIBLE);
            }
            else{
                nothing.setVisibility(View.GONE);
            }
            gca.update(entity);
            if(entity.length>=MAXCOUNT){
                listView.setShowFooter();
            }
            else{
                listView.setHideFooter();
            }
            return;
        }
        HashMap<String,String> map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
            Toast.makeText(getActivity(),"删除商品成功",Toast.LENGTH_SHORT).show();
            successNotify();
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(gca==null)return;
        if(entity==null||entity.length<=0)return;
        //全选
        if(b){
            gca.selectAll(entity);
        }
        else{
            gca.noSelectAll(entity);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    if(gca!=null){
        gca.imageLoader.clearMemory();
        gca.imageLoader=null;
    }
    }
}
