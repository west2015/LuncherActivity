package com.nhd.mall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.GoodsFilterAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.asyncTask.GoodsFilterGet;
import com.nhd.mall.entity.GoodsFilterEntity;
import com.nhd.mall.entity.GoodsFilterList;
import com.nhd.mall.entity.GoodsFilterSecondEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;

/**货物筛选页面
 * Created by caili on 14-4-3.
 */
public class GoodsFilterFragment extends Fragment implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener,OnAsyncTaskUpdateListener {

    private ExpandableListView exMainList;
    private GoodsFilterAdapter gfa;
    //调用筛选接口
    private GoodsFilterList list;
    private GoodsFilterEntity[]filter;
    private GoodsFilterGet gfg;
    //加载进度
    private ImageView ivLoad;
    private RelativeLayout rlLoad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goods_filter_fragment, null);
        findView(view);
        return view;
    }
    private void findView(View view) {
        ivLoad = (ImageView)view.findViewById(R.id.ivLoad);
        rlLoad = (RelativeLayout)view.findViewById(R.id.rl_progress);
        exMainList = (ExpandableListView) view.findViewById(R.id.exMainList);
        exMainList.setOnChildClickListener(this);
        exMainList.setOnGroupClickListener(this);
        gfg = new GoodsFilterGet(getActivity());
        gfg.setListener(this);
        start();
    }
    @Override
    public void getData(Object obj, String message) {
        stop();
        if (message != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        if (obj == null)
            return;
        if(obj instanceof GoodsFilterList){
            list = (GoodsFilterList) obj;
            filter = list.getCategorys();
            gfa = new GoodsFilterAdapter(getActivity(),filter);
             exMainList.setAdapter(gfa);
        }
    }
    public void start(){
        rlLoad.setVisibility(View.VISIBLE);
        Animation operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        ivLoad.startAnimation(operatingAnim);
    }
    public void stop(){
        rlLoad.setVisibility(View.GONE);
        ivLoad.clearAnimation();
    }
    @Override
    public boolean onChildClick(ExpandableListView arg0, View arg1, int groupPosition, int childPosition, long arg4) {
        if(filter==null||filter[groupPosition].getChildren()==null)return true;
         GoodsFilterSecondEntity[] secondList = filter[groupPosition].getChildren();
        if(secondList==null||secondList.length<=0)return true;
        int category = secondList[childPosition].getId();
         Bundle bundleTh = new Bundle();
        bundleTh.putInt("category",category);
        bundleTh.putString("sort","filter");
        bundleTh.putString("search_for","normal");
        new startIntent(getActivity(),ThemeGoodsActivity.class,bundleTh);
        return true;
    }
    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
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
}
