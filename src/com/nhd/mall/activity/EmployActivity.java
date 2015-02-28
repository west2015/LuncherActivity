package com.nhd.mall.activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.CollectStoreAdapter;
import com.nhd.mall.adapter.EmployAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.asyncTask.EmployListGet;
import com.nhd.mall.entity.EmployEntity;
import com.nhd.mall.entity.EmployList;
import com.nhd.mall.util.NetCheck;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.widget.ModelActivity;
import com.nhd.mall.widget.PullDownView;

/**招聘页面
 * Created by Administrator on 14-5-29.
 */
public class EmployActivity extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener,PullDownView.OnPullDownListener,EmployAdapter.folderListener {

    private PullDownView listView;
    private TextView tvEmail;
    private EmployAdapter adapter;
    private EmployList list;
    private EmployEntity[] entity;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employ_layout);
        setTitle("招贤纳士");
        find();
    }
    private void find() {
        adapter = new EmployAdapter(this,entity);
        listView = (PullDownView)findViewById(R.id.lvEmploy);
        listView.setOnPullDownListener(this);
        listView.getListView().setFooterDividersEnabled(false);
        listView.setHideHeader();
        listView.setHideFooter();
        listView.setAdapter(adapter);
        listView.enableAutoFetchMore(true, 1);
        listView.getListView().setDividerHeight(0);
        tvEmail = (TextView)findViewById(R.id.tvEmail);
        if(NetCheck.checkNet(this)){
            new EmployListGet(this).setListener(EmployActivity.this);
        }
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
        }
    }
    @Override
    public void getData(Object obj, String message) {
        if(message!=null){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }
        if(obj == null ) return;
        if(obj instanceof EmployList){
            list = (EmployList) obj;
            entity = list.getAdvertises();
            adapter.update(entity);
            if(list.getEmail()!=null){
                tvEmail.setText(list.getEmail());
            }
        }
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

    @Override
    public void onRefresh() {

    }

    @Override
    public void onMore() {

    }

    @Override
    public void folder(int position) {

    }
}
