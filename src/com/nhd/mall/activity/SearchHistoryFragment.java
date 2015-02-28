package com.nhd.mall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.adapter.SearchAdapter;
import com.nhd.mall.datebase.DbHistory;
import com.nhd.mall.util.startIntent;

/**搜索页面的历史记录小页面
 * Created by caili on 14-4-4.
 */
public class SearchHistoryFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener  {
    private GridView gv;
    private String[]name;
    private SearchAdapter sa;
    private RelativeLayout rlHistory;
    private DbHistory dbHistory;
    private TextView tvClear;
    static SearchHistoryFragment getInstance(){
        SearchHistoryFragment mFragment=new SearchHistoryFragment();
        return mFragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_grid_layout, container, false);
        findView(view);
        return view;
    }
    private void findView(View view) {
        dbHistory = new DbHistory(getActivity());
        tvClear = (TextView)view.findViewById(R.id.tcClear);
        tvClear.setOnClickListener(this);
        gv = (GridView)view.findViewById(R.id.searchGridview);
        rlHistory = (RelativeLayout)view.findViewById(R.id.rl_have_no_history);
        sa = new SearchAdapter(getActivity(),name);
        gv.setAdapter(sa);
        gv.setOnItemClickListener(this);
    }

    private void initSearch() {
        name = dbHistory.getHistory();
        sa.update(name);
        if(name==null||name.length<=0){
            gv.setVisibility(View.GONE);
            rlHistory.setVisibility(View.VISIBLE);
            tvClear.setVisibility(View.GONE);
        }
        else{
            tvClear.setVisibility(View.VISIBLE);
            gv.setVisibility(View.VISIBLE);
            rlHistory.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
       switch(view.getId()){
           case R.id.tcClear:
               name=null;
               sa.update(name);
               dbHistory.delect();
               tvClear.setVisibility(View.GONE);
               rlHistory.setVisibility(View.VISIBLE);
       }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle bundleTh = new Bundle();
        bundleTh.putString("query",name[i]);
        new startIntent(getActivity(),SearchProductActivity.class,bundleTh);
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        initSearch();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

