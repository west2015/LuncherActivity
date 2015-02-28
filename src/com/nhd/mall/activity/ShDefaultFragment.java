package com.nhd.mall.activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.nhd.mall.R;
import com.nhd.mall.adapter.BranchAdapter;
import com.nhd.mall.adapter.BranchSortAdapter;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.BranchGet;
import com.nhd.mall.entity.BranchEntity;
import com.nhd.mall.entity.BranchList;
import com.nhd.mall.entity.CouponListEntity;
import com.nhd.mall.util.NetCheck;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.PullDownView;
/**展示默认品牌列表的fragments
 * Created by Administrator on 14-6-25.
 */
public class ShDefaultFragment extends Fragment implements OnAsyncTaskUpdateListener,PullDownView.OnPullDownListener,AdapterView.OnItemClickListener {
    private PullDownView listView;
    private BranchAdapter adapter;
    private Integer storeId;
    private BranchGet get;
    private Integer pageNum=1;
    private ImageView ivLoad;
    private BranchEntity[]entity;
    private BranchList list;
    private final int LIST=0;
    private final int SEARCH =1;
    private int currentTag=0;
    private String searchValue;
    private RelativeLayout rlNoCotent;

    static ShDefaultFragment getInstance(){
        Bundle bundle = new Bundle();
        ShDefaultFragment mFragment=new ShDefaultFragment();
        return mFragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.default_sh_layout, container, false);
        findView(view);
        return view;
    }
    private void findView(View view) {
        rlNoCotent = (RelativeLayout)view.findViewById(R.id.rl_have_no_content);
        ivLoad = (ImageView)view.findViewById(R.id.imageView);
        listView = (PullDownView)view.findViewById(R.id.lvCoupon);
        adapter = new BranchAdapter(getActivity(),entity);
        listView.setAdapter(adapter);
        listView.setOnPullDownListener(this);
        listView.setOnItemClickListener(this);
        listView.getListView().setFooterDividersEnabled(false);
        listView.enableAutoFetchMore(true, 1);
        listView.setHideFooter();
        if(MainApplication.getInstance().getStore()!=null){
            storeId = MainApplication.getInstance().getStore().getId();
        }
        if( NetCheck.checkNet(getActivity())){
            start();
            get = new BranchGet(getActivity(),pageNum,storeId,null,null,null);
            get.setListener(this);
        }
    }
    public void start(){
        ivLoad.setVisibility(View.VISIBLE);
        Animation operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        ivLoad.startAnimation(operatingAnim);
    }
    public void stop(){
        ivLoad.clearAnimation();
        ivLoad.setVisibility(View.GONE);
    }
    @Override
    public void getData(Object obj, String message) {
        stop();
        if(listView!=null){
            listView.notifyDidMore();
            listView.RefreshComplete();
        }
        if(message!=null){
            if(getActivity()!=null){
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
        }
        if(obj == null) {
            return;
        }
        if(obj instanceof BranchList){
            list = (BranchList) obj;
            entity = list.getBrands();
            adapter.update(entity);
            if(entity!=null&&entity.length>0){
                if(entity.length<10){
                    listView.setHideFooter();
                }
                else{
                    listView.setShowFooter();
                }
            }
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(entity==null||entity.length<=0)return;
        Bundle bundle = new Bundle();
        bundle.putInt("brandId",entity[position-1].getBrandId());
        bundle.putInt("floorId",entity[position-1].getFloorId());
        new startIntent(getActivity(),BranchDetailActivity.class,bundle);
    }
    public void getSearchList(boolean boo,String value){
        if(boo){         //true表明是调用接口获取搜索
            searchValue = value;
            currentTag = SEARCH;
            pageNum=1;
            get.update(pageNum,storeId,null,null,value);
        }
        else{
            pageNum=1;
            currentTag = LIST;
            get.update(pageNum,storeId,null,null,null);
        }
    }
    @Override
    public void onRefresh() {
        pageNum=1;
        if(currentTag==LIST){
            get.update(pageNum, storeId, null, null, null);
        }
        else{
            get.update( pageNum, storeId, null, null,searchValue);
        }
    }
    @Override
    public void onMore() {
        pageNum++;
        if(currentTag==LIST){
            get.getMore(list, pageNum, storeId, null, null, null);
        }
        else{
            get.getMore(list, pageNum, storeId, null, null,searchValue);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(adapter!=null){
            adapter.imageLoader.clearMemory();
        }
    }
}
