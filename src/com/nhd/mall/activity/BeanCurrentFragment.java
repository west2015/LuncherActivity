package com.nhd.mall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nhd.mall.R;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.BeanUrlGet;
import com.nhd.mall.asyncTask.MemberBeanGet;
import com.nhd.mall.entity.CollectionList;
import com.nhd.mall.util.OnAsyncTaskDataListener;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;

import java.util.HashMap;

/**当前疯豆
 * Created by caili on 14-4-9.
 */
public class BeanCurrentFragment extends Fragment implements OnAsyncTaskUpdateListener,View.OnClickListener,OnAsyncTaskDataListener {
    private TextView tvBeanCount;
    private Long memberId;
    private Button btnCz;
    static BeanCurrentFragment getInstance(){
        BeanCurrentFragment mFragment=new BeanCurrentFragment();
        return mFragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bean_current_layout, container, false);
        findView(view);
        return view;
    }
    private void findView(View view) {
        tvBeanCount = (TextView)view.findViewById(R.id.tvBeanCount);
        btnCz = (Button)view.findViewById(R.id.btnCz);
        btnCz.setOnClickListener(this);
        if(MainApplication.getInstance().getMember()!=null){
            memberId = MainApplication.getInstance().getMember().getId();
            new MemberBeanGet(getActivity(),memberId).setListener(this);
        }
    }
    @Override
    public void getData(Object obj, String message) {
        if (message != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        if (obj == null){
            return;
        }
        HashMap<String,String> map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
            Object objCount = map.get("marks");
            tvBeanCount.setText(objCount.toString());
            String sCount = objCount.toString();
            MainApplication.getInstance().setBeanCount(Integer.parseInt(sCount));
        }
        else{
            Toast.makeText(getActivity(),map.get("message"),Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnCz:
                if(MainApplication.getInstance().getMember()==null){
                    new startIntent(getActivity(),LoginActivity.class);
                    return;
                }
                memberId = MainApplication.getInstance().getMember().getId();
                new BeanUrlGet(getActivity(),memberId).setListener(this);
                break;
        }
    }
   //疯豆充值调用返回
    @Override
    public void getDataSort(Object obj, String message, String sort) {
        if (message != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        if (obj == null){
            return;
        }
        HashMap<String,String> map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
            if(map.containsKey("url")){
                String url = map.get("url");
                Bundle bundle = new Bundle();
                bundle.putString("url",url);
                new startIntent(getActivity(),BeanWebViewActivity.class,bundle);
            }
        }
        else if(map.get("success").equals("false")){
            Toast.makeText(getActivity(),map.get("message"),Toast.LENGTH_SHORT).show();

        }
    }
}
