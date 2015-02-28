package com.nhd.mall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nhd.mall.R;
import com.nhd.mall.adapter.BranchAdapter;
import com.nhd.mall.adapter.BranchDoorAdapter;
import com.nhd.mall.adapter.BranchSortAdapter;
import com.nhd.mall.adapter.MyCouponAdapter;
import com.nhd.mall.adapter.ShSortAdapter;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.BranchGet;
import com.nhd.mall.asyncTask.CouponListGet;
import com.nhd.mall.asyncTask.DoorDaohanGet;
import com.nhd.mall.asyncTask.GoodsFilterGet;
import com.nhd.mall.entity.BranchEntity;
import com.nhd.mall.entity.BranchList;
import com.nhd.mall.entity.CouponListEntity;
import com.nhd.mall.entity.GoodsFilterEntity;
import com.nhd.mall.entity.GoodsFilterList;
import com.nhd.mall.util.NetCheck;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.PullDownView;

/**商户分类搜索品牌页面
 * Created by Administrator on 14-6-25.
 */
public class ShSortFragment extends Fragment implements OnAsyncTaskUpdateListener,PullDownView.OnPullDownListener {
    //与分类有关的属性
    private ListView sortList;
    private ShSortAdapter sortAdapter;
    private GoodsFilterList list;
    private GoodsFilterEntity[]filter;
    private GoodsFilterGet gfg;
    //与品牌有关的类
    private PullDownView branchList;
    private BranchGet get;   //按楼层id获取品牌列表的接口
    private int pageNum=1;
    private Integer storeId;
    private BranchEntity[]branchEntity;
    private BranchList oldbranchList;
    private BranchAdapter branchAdapter;  //品牌列表适配器
    private Integer sortId;
    private boolean needBack = false;

    static ShSortFragment getInstance(){
        Bundle bundle = new Bundle();
        ShSortFragment mFragment=new ShSortFragment();
        return mFragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.door_branch_layout, container, false);
        findView(view);
        register();
        return view;
    }
    private void findView(View view) {
        sortList = (ListView)view.findViewById(R.id.lvDoor);
        branchAdapter = new BranchAdapter(getActivity(),null);
        sortAdapter = new ShSortAdapter(getActivity(),filter);
        branchList = (PullDownView)view.findViewById(R.id.lvBranch);
        branchList.setOnPullDownListener(this);
        branchList.setAdapter(branchAdapter);
        sortList.setAdapter(sortAdapter);
        branchList.getListView().setFooterDividersEnabled(false);
        sortList.setFooterDividersEnabled(false);
        branchList.setHideFooter();
        sortList.setDivider(getResources().getDrawable(R.drawable.live_item_line));
        if(MainApplication.getInstance().getStore()!=null){
            storeId = MainApplication.getInstance().getStore().getId();
        }
        if(NetCheck.checkNet(getActivity())){
            gfg = new GoodsFilterGet(getActivity());
            gfg.setListener(this);
        }
        else{
            Toast.makeText(getActivity(),"网络不给力",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void getData(Object obj, String message) {
        if(branchList!=null){
            branchList.notifyDidMore();
            branchList.RefreshComplete();
        }
        if(message!=null){
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
        if(obj == null) return;
        if(obj instanceof GoodsFilterList){
            list = (GoodsFilterList) obj;
            filter = list.getCategorys();
            sortAdapter.update(filter);
        }
        if(obj instanceof BranchList){
            oldbranchList = (BranchList) obj;
            branchEntity = oldbranchList.getBrands();
            branchAdapter.update(branchEntity);
            if(branchEntity!=null){
                if(branchEntity.length<10){
                    branchList.setHideFooter();
                }
                else{
                    branchList.setShowFooter();
                }
            }
        }
    }
    public void setCheckSort(){
        if(needBack){
            needBack = false;
            if(branchList!=null&&sortList!=null){
                branchList.setVisibility(View.GONE);
                sortList.setVisibility(View.VISIBLE);
            }
        }
    }
    private void register() {
        sortList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(filter==null||filter.length<=0)return;
                sortList.setVisibility(View.GONE);
                branchList.setVisibility(View.VISIBLE);
                needBack = true;
                sortId = filter[i].getId();
                pageNum=1;
                if(get==null){
                    get = new BranchGet(getActivity(),pageNum,storeId,null,sortId,null);
                    get.setListener(ShSortFragment.this);
                }
                else{
                    get.update(pageNum,storeId,null,sortId,null);
                }
            }
        });
        branchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(branchEntity==null||branchEntity.length<=0)return;
                Bundle bundle = new Bundle();
                bundle.putInt("brandId",branchEntity[i-1].getBrandId());
                bundle.putInt("floorId",branchEntity[i-1].getFloorId());
                new startIntent(getActivity(),BranchDetailActivity.class,bundle);
            }
        });
    }
    @Override
    public void onRefresh() {
        pageNum=1;
        get.update(pageNum, storeId, null, sortId, null);
    }
    @Override
    public void onMore() {
        pageNum++;
        if(get!=null){
            get.getMore(oldbranchList, pageNum, storeId,null, sortId,null);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(branchAdapter!=null){
            branchAdapter.imageLoader.clearMemory();
        }
    }
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
               }
        else {
    }    }
}
