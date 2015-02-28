package com.nhd.mall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.nhd.mall.R;
import com.nhd.mall.adapter.SearchAdapter;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.SearchHotTagGet;
import com.nhd.mall.datebase.DbHistory;
import com.nhd.mall.entity.MemberRespondEntity;
import com.nhd.mall.entity.SearchHotEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;

/**搜索页面的热门搜索小页面
 * Created by caili on 14-4-4.
 */
public class SearchHotFragment extends Fragment implements AdapterView.OnItemClickListener,OnAsyncTaskUpdateListener {
    private GridView gv;
    private SearchAdapter sa;
    private SearchHotTagGet shtg;
    private SearchHotEntity entity;
    private String[] hot;

    static SearchHotFragment getInstance(){
        SearchHotFragment mFragment=new SearchHotFragment();
        return mFragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_grid_layout, container, false);
        findView(view);
        return view;
    }
    private void findView(View view) {
        gv = (GridView)view.findViewById(R.id.searchGridview);
        sa = new SearchAdapter(getActivity(),hot);
        gv.setAdapter(sa);
        gv.setOnItemClickListener(this);
        shtg = new SearchHotTagGet(getActivity());
        shtg.setListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(hot==null)return;
        new DbHistory(getActivity()).insertHistory(hot[i]);
        Bundle bundleTh = new Bundle();
        bundleTh.putString("query",hot[i]);
        new startIntent(getActivity(),SearchProductActivity.class,bundleTh);
    }
    @Override
    public void getData(Object obj, String message) {
        if(message!=null){
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
        if(obj == null ) return;
        if(obj instanceof SearchHotEntity){
            entity = (SearchHotEntity) obj;
            hot = entity.getHotTags();
            sa.update(hot);
        }
    }
}
