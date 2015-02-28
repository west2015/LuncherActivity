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
import com.nhd.mall.adapter.MyCouponAdapter;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.BranchGet;
import com.nhd.mall.asyncTask.CouponListGet;
import com.nhd.mall.asyncTask.DoorDaohanGet;
import com.nhd.mall.entity.BranchEntity;
import com.nhd.mall.entity.BranchList;
import com.nhd.mall.entity.CouponListEntity;
import com.nhd.mall.entity.DoorEntity;
import com.nhd.mall.util.NetCheck;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.PullDownView;

/**商户楼层搜索品牌页面
 * Created by Administrator on 14-6-25.
 */
public class ShDoorFragment extends Fragment implements OnAsyncTaskUpdateListener,PullDownView.OnPullDownListener {
    private ListView doorList;
    private PullDownView branchList;
    //与品牌有关的类
    private BranchGet get;   //按楼层id获取品牌列表的接口
    private int pageNum=1;
    private Integer storeId;
    private BranchEntity[]branchEntity;
    private BranchList list;
    private BranchAdapter branchAdapter;  //品牌列表适配器
    private Integer floorId;
    //与楼层有关的属性
    private DoorDaohanGet ddg;
    private DoorEntity[] doorEntity;
    private BranchDoorAdapter doorAdapter; //楼层列表适配器
    private boolean needBack = false;

    static ShDoorFragment getInstance(){
        Bundle bundle = new Bundle();
        ShDoorFragment mFragment=new ShDoorFragment();
        return mFragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.door_branch_layout, container, false);
        findView(view);
        register();
        return view;
    }
    private void findView(View view) {
        doorList = (ListView)view.findViewById(R.id.lvDoor);
        branchAdapter = new BranchAdapter(getActivity(),branchEntity);
        doorAdapter = new BranchDoorAdapter(getActivity(),doorEntity);
        branchList = (PullDownView)view.findViewById(R.id.lvBranch);
        branchList.setOnPullDownListener(this);
        branchList.setAdapter(branchAdapter);
        doorList.setAdapter(doorAdapter);
        branchList.getListView().setFooterDividersEnabled(false);
        doorList.setFooterDividersEnabled(false);
        branchList.setHideFooter();
        doorList.setDivider(getResources().getDrawable(R.drawable.live_item_line));
        if(MainApplication.getInstance().getStore()!=null){
            storeId = MainApplication.getInstance().getStore().getId();
        }
        if(NetCheck.checkNet(getActivity())){
            ddg = new DoorDaohanGet(getActivity(),storeId);
            ddg.setListener(this);
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
        if(obj instanceof DoorEntity[]){
            doorEntity = (DoorEntity[]) obj;
            doorAdapter.update(doorEntity);
        }
        if(obj instanceof BranchList){
            list = (BranchList) obj;
            branchEntity = list.getBrands();
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
            if(branchList!=null&&doorList!=null){
                branchList.setVisibility(View.GONE);
                doorList.setVisibility(View.VISIBLE);
            }
            needBack = false;
        }
    }
    private void register() {
        doorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(doorEntity==null||doorEntity.length<=0)return;
                doorList.setVisibility(View.GONE);
                branchList.setVisibility(View.VISIBLE);
                needBack = true;
                floorId = doorEntity[i].getId();
                pageNum=1;
                if(get==null){
                    get = new BranchGet(getActivity(),pageNum,storeId,doorEntity[i].getId(),null,null);
                    get.setListener(ShDoorFragment.this);
                }
                else{
                    get.update(pageNum,storeId,doorEntity[i].getId(),null,null);
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
        get.update(pageNum, storeId, floorId, null, null);

    }
    @Override
    public void onMore() {
        pageNum++;
        if(get!=null){
        get.getMore(list, pageNum, storeId,floorId, null,null);
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
}
