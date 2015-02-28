package com.nhd.mall.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.nhd.mall.R;
import com.nhd.mall.adapter.MyBeanDetailAdapter;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.BeanDetailGet;
import com.nhd.mall.asyncTask.DeleteBeanGet;
import com.nhd.mall.entity.BeanDetailEntity;
import com.nhd.mall.entity.BeanDetailList;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.Utils;
import com.nhd.mall.widget.PullDownView;
import java.util.ArrayList;
import java.util.HashMap;

/**疯豆详情fragment
 * Created by caili on 14-4-9.
 */
public class BeanDetailFragment extends Fragment implements OnAsyncTaskUpdateListener,PullDownView.OnPullDownListener,CompoundButton.OnCheckedChangeListener {
    private Button btnDelete;
    private PullDownView lvDetail;
    private MyBeanDetailAdapter mbda;
    private Long memberId;
    private int pageNum=1;
    private final int MAXCOUNT=10;
    private BeanDetailList beanList;
    private BeanDetailEntity[] entity;
    private CheckBox cbAll;
    private BeanDetailGet get;
    //用来保存删除的疯豆id
    ArrayList<Integer> idList = new ArrayList<Integer>();
    private RelativeLayout nothing;
    private Dialog deleteDialog;

    static BeanDetailFragment getInstance(Button btn){
        BeanDetailFragment mFragment=new BeanDetailFragment();
        mFragment.btnDelete = btn;
        return mFragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bean_detail_layout, container, false);
        findView(view);
        return view;
    }
    private void findView(View view) {
        nothing = (RelativeLayout)view.findViewById(R.id.rl_no_bean);
        lvDetail = (PullDownView)view.findViewById(R.id.lvBean);
        cbAll = (CheckBox)view.findViewById(R.id.all_check);
        cbAll.setOnCheckedChangeListener(this);
        mbda = new MyBeanDetailAdapter(getActivity(),entity);
        lvDetail.setAdapter(mbda);
        lvDetail.setOnPullDownListener(this);
        lvDetail.getListView().setFooterDividersEnabled(false);
        lvDetail.setHideHeader();
        lvDetail.enableAutoFetchMore(true, 1);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mbda==null)return;
                if(mbda.selectMap.size()<=0){
                    return;
                }
                deleteDialog();
            }
        });
        if(MainApplication.getInstance().getMember()!=null){
            memberId = MainApplication.getInstance().getMember().getId();
            get = new BeanDetailGet(getActivity(),pageNum,memberId);
            get.setListener(this);
        }
        //点击返回首页
        view.findViewById(R.id.gtToMall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.backToMain(getActivity());
            }
        });

    }
    //删除疯豆的详细
    public void delete() {
        idList.clear();
        for(int i:mbda.selectMap.keySet()){
            if(mbda.selectMap.get(i)){
                BeanDetailEntity bean = entity[i];
                idList.add(bean.getId());
            }
        }
        Integer[]ids = new Integer[idList.size()];
        ids = idList.toArray(ids);
        new DeleteBeanGet(getActivity(),ids).setListener(this);
    }
    private void deleteDialog() {
        View view = null;
        view = LayoutInflater.from(getActivity()).inflate(R.layout.delete_dialog, null);
        view.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
                delete();
            }
        });
        view.findViewById(R.id.btnQuit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog = new Dialog(getActivity(), R.style.planDialog);
        deleteDialog.setCancelable(true);
        deleteDialog.setContentView(view);
        deleteDialog.show();
    }
    //删除成功后返回进行刷新
    private void successNotify() {
        ArrayList<BeanDetailEntity>entityList = new ArrayList<BeanDetailEntity>();
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
        BeanDetailEntity[] collect = new BeanDetailEntity[entityList.size()];
        collect = entityList.toArray(collect);
        entity = collect;
        if(entity==null||entity.length<=0){
            nothing.setVisibility(View.VISIBLE);
        }
        else{
            nothing.setVisibility(View.GONE);
        }
        mbda.update(entity);
    }
    @Override
    public void getData(Object obj, String message) {
        lvDetail.RefreshComplete();
        lvDetail.notifyDidMore();
        if (message != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        if (obj == null){
            nothing.setVisibility(View.VISIBLE);
            return;
        }
        if(obj instanceof BeanDetailList){
            beanList = (BeanDetailList) obj;
            entity = beanList.getRecords();
            if(entity.length<=0){
               nothing.setVisibility(View.VISIBLE);
            }
            else{
                nothing.setVisibility(View.GONE);
            }
            mbda.update(entity);
            if(entity.length>=MAXCOUNT){
                lvDetail.setShowFooter();
            }
            else{
                lvDetail.setHideFooter();
            }
            return;
        }
        HashMap<String,String> map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
            Toast.makeText(getActivity(),"操作成功",Toast.LENGTH_SHORT).show();
            successNotify();
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(mbda==null)return;
        if(entity==null||entity.length<=0)return;
        //全选
        if(b){
            mbda.selectAll(entity);
        }
        else{
            mbda.noSelectAll(entity);
        }

    }
    @Override
    public void onRefresh() {

    }
    @Override
    public void onMore() {
        get.update(++pageNum,memberId);
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
        if(mbda!=null){
            mbda.imageLoader.clearMemory();
            mbda.imageLoader=null;
        }
    }
}
