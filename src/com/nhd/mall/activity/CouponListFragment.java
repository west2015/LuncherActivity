package com.nhd.mall.activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nhd.mall.R;
import com.nhd.mall.adapter.MyCouponAdapter;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.CouponListGet;
import com.nhd.mall.asyncTask.CouponUseGet;
import com.nhd.mall.asyncTask.DeleteCouponGet;
import com.nhd.mall.entity.Coupon;
import com.nhd.mall.entity.CouponListEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.widget.PullDownView;

import java.util.ArrayList;
import java.util.HashMap;

/**未使用优惠券
 * Created by caili on 14-4-11.
 */
public class CouponListFragment extends Fragment  implements OnAsyncTaskUpdateListener,PullDownView.OnPullDownListener,AdapterView.OnItemClickListener{
    private PullDownView listView;
    private MyCouponAdapter gca;
    private CouponListEntity lstCoupon;
    private CouponListGet get;
    private Integer status;
    public static final int COUPON_NO_USE=1;
    public static final int COUPON_USED=2;
    public static final int COUPON_OUT=3;
    private Long memberId;
    //用来保存删除的优惠券id
    ArrayList<Integer> idList = new ArrayList<Integer>();
    private Dialog couponUseDialog = null; // 使用优惠券弹出框
    private Dialog deleteDialog;
    //标识是优惠券还是储值卡
    private int sort;

    static CouponListFragment getInstance(Integer status,Integer sort){
        Bundle bundle = new Bundle();
        bundle.putInt("status",status);
        bundle.putInt("sort",sort);
        CouponListFragment mFragment=new CouponListFragment();
        mFragment.setArguments(bundle);
        return mFragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        status = getArguments().getInt("status");
        sort = getArguments().getInt("sort");
        View view = inflater.inflate(R.layout.coupon_layout, container, false);
        findView(view);
        return view;
    }
    private void findView(View view) {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int displayWidth = dm.widthPixels;
        listView = (PullDownView)view.findViewById(R.id.lvCoupon);
        gca = new MyCouponAdapter(getActivity(),status,null,displayWidth);
        listView.setAdapter(gca);
        listView.setOnPullDownListener(this);
        listView.setOnItemClickListener(this);
        listView.getListView().setFooterDividersEnabled(false);
        listView.enableAutoFetchMore(true, 1);
        listView.getListView().setDividerHeight(0);
    }
    public void delete() {
        if(gca==null)return;
        if(gca.selectMap.size()<=0){
            return;
        }
        deleteDialog();
    }
    public void deleteDetail(){
        idList.clear();
        for(int i:gca.selectMap.keySet()){
            if(gca.selectMap.get(i)){
                Coupon coupon = lstCoupon.getCoupons()[i];
                idList.add(coupon.getId());
            }
        }
        Integer[]ids = new Integer[idList.size()];
        ids = idList.toArray(ids);
        new DeleteCouponGet(getActivity(),ids).setListener(this);
    }
    private void deleteDialog() {
        View view = null;
        view = LayoutInflater.from(getActivity()).inflate(R.layout.delete_dialog, null);
        view.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
                deleteDetail();
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
    @Override
    public void getData(Object obj, String message) {
        if(listView!=null){
            listView.notifyDidMore();
            listView.RefreshComplete();
        }
        if(message!=null){
            Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
        }
        if(obj == null) return;
        if(obj instanceof  CouponListEntity){
            lstCoupon = (CouponListEntity) obj;
            gca.update(lstCoupon.getCoupons());
            return;
        }
        HashMap<String,String> map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
            Toast.makeText(getActivity(),"操作成功",Toast.LENGTH_SHORT).show();
            get.refresh();
        }
        else{
            Toast.makeText(getActivity(),map.get("message"),Toast.LENGTH_SHORT).show();
        }
    }
    //弹出使用优惠券密码框
    private void couponUseDialog(final int pos) {
        View view = null;
        view = LayoutInflater.from(getActivity()).inflate(R.layout.use_coupon_dialog, null);
        final EditText etPsw = (EditText)view.findViewById(R.id.etPsw);
        view.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etPsw.getText()==null||etPsw.getText().toString().equals("")){
                   Toast.makeText(getActivity(),"请输入密码",Toast.LENGTH_SHORT).show();
                    couponUseDialog.dismiss();
                    return;
                }
                String psw = etPsw.getText().toString();
                if(lstCoupon.getCoupons()[pos].getPassword()!=null){
                    if(psw.equals(lstCoupon.getCoupons()[pos].getPassword())){
                        new CouponUseGet(getActivity(),lstCoupon.getCoupons()[pos].getId()).setListener(CouponListFragment.this);
                    }
                    else{
                        Toast.makeText(getActivity(),"密码不正确",Toast.LENGTH_SHORT).show();
                        couponUseDialog.dismiss();
                    }
                }
                couponUseDialog.dismiss();
            }
        });
        view.findViewById(R.id.btnQuit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                couponUseDialog.dismiss();
            }
        });
        couponUseDialog = new Dialog(getActivity(), R.style.planDialog);
        couponUseDialog.setCancelable(true);
        couponUseDialog.setContentView(view);
        couponUseDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (status){
            case CouponListFragment.COUPON_NO_USE:
               int pos = i-1;
                if(couponUseDialog==null||!couponUseDialog.isShowing()){
                    couponUseDialog(pos);
                }
                break;
            case CouponListFragment.COUPON_USED:
                break;
            case CouponListFragment.COUPON_OUT:
                break;
        }
    }
    @Override
    public void onRefresh() {
        if(get!=null)get.refresh();
    }
    @Override
    public void onMore() {
        if(get!=null)get.getMore(lstCoupon);
    }
    @Override
    public void onResume() {
        super.onResume();
        if(MainApplication.getInstance().getMember()!=null){
            memberId = MainApplication.getInstance().getMember().getId();
            if(get==null){
                get = new CouponListGet(getActivity(),status,memberId,sort);
                get.setListener(this);
            }
            else{
                get.refresh();
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(gca!=null){
            gca.imageLoader.clearMemory();
        }
    }
}
