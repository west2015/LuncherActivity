package com.nhd.mall.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.adapter.FeedBackAdapter;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.FeedBackCommentPost;
import com.nhd.mall.asyncTask.FeedBackListGet;
import com.nhd.mall.entity.FeedBackEntity;
import com.nhd.mall.entity.PostFeedBackEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.nhd.mall.widget.PullDownView;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**投诉建议页面
 * Created by caili on 14-4-13.
 */
public class CustomerCommentActivity extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener, PullDownView.OnPullDownListener {

    private PostFeedBackEntity postFeedBack;
    private FeedBackEntity[] entity;
    private EditText etComment;
    private Long memberId;
    private ImageView ivMore;
    private FeedBackAdapter adapter;
    private FeedBackListGet fbg;
    private PullDownView listView;
    private RelativeLayout rlMore;
    private Integer pageNum=1;
    private ArrayList<FeedBackEntity>feedList = new ArrayList<FeedBackEntity>();
    private View vHeader;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("投诉建议");
        setContentView(R.layout.customer_comment_layout);
        find();
    }

    private void find() {
        vHeader = LayoutInflater.from(this).inflate(R.layout.customer_comment_header_layout, null);
        etComment = (EditText)findViewById(R.id.etSend);
        listView = (PullDownView)findViewById(R.id.commentListView);
        findViewById(R.id.btnSend).setOnClickListener(this);
        rlMore = (RelativeLayout) vHeader.findViewById(R.id.rlMore);
        ivMore = (ImageView)vHeader.findViewById(R.id.ivProgress);
        rlMore.setOnClickListener(this);
        listView.getListView().addHeaderView(vHeader);
        adapter = new FeedBackAdapter(this,entity);
        listView.setAdapter(adapter);
        listView.setOnPullDownListener(this);
        listView.getListView().setFooterDividersEnabled(false);
        listView.enableAutoFetchMore(true, 1);
        listView.setHideFooter();
        listView.setHideHeader();
        listView.getListView().setDivider(null);
        initComment();
    }
    private void initComment() {
        if(MainApplication.getInstance().getMember()==null){
            rlMore.setVisibility(View.GONE);
            return;
        }
        rlMore.setVisibility(View.VISIBLE);
        memberId = MainApplication.getInstance().getMember().getId();
        if(fbg==null){
            pageNum=1;
            fbg = new FeedBackListGet(this,memberId,pageNum);
            fbg.setListener(this);
        }
        else{
            pageNum=1;
            fbg.update(pageNum,memberId,feedList);
        }
    }

    @Override
    public void onRefresh() {
        initComment();
    }
    @Override
    public void onMore() {
    }
    public String getTime(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date);
        return time;
    }

    public void start(){
        ivMore.setVisibility(View.VISIBLE);
        Animation operatingAnim = AnimationUtils.loadAnimation(CustomerCommentActivity.this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        ivMore.startAnimation(operatingAnim);
    }
    public void stop(){
        ivMore.clearAnimation();
        ivMore.setVisibility(View.GONE);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.rlMore: //查看更多
                if(MainApplication.getInstance().getMember()==null){
                    return;
                }
                memberId = MainApplication.getInstance().getMember().getId();
                if(entity!=null&&entity.length>0){
                    fbg.update(++pageNum,memberId,feedList);
                    start();
                }
                break;
            case R.id.btnSend:
                if(etComment.getText()==null||etComment.getText().toString().equals("")){
                    Toast.makeText(CustomerCommentActivity.this,"请输入建议",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(MainApplication.getInstance().getMember()==null){
                    new startIntent(CustomerCommentActivity.this,LoginActivity.class);
                    return;
                }
                memberId = MainApplication.getInstance().getMember().getId();
                postFeedBack = new PostFeedBackEntity();
                postFeedBack.setCategory("c_back");
                postFeedBack.setMemberId(memberId);
                postFeedBack.setMessage(etComment.getText().toString());
                new FeedBackCommentPost(CustomerCommentActivity.this,postFeedBack).setListener(CustomerCommentActivity.this);
                etComment.setText("");
                break;
        }
    }
    @Override
    public void getData(Object obj, String message) {
        listView.RefreshComplete();
        listView.notifyDidMore();
        stop();
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null)
            return;
        if(obj instanceof  FeedBackEntity[]){
            entity = (FeedBackEntity[]) obj;
            adapter.update(entity);
            return;
        }
        HashMap<String,String> map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
            //在这里刷新适配器
            FeedBackEntity back = new FeedBackEntity();
            if(postFeedBack!=null){
                back.setMessage(postFeedBack.getMessage());
                back.setMemberId(postFeedBack.getMemberId());
                back.setCategory(postFeedBack.getCategory());
                back.setCrDate(getTime());
                ArrayList<FeedBackEntity>backList = new ArrayList<FeedBackEntity>();
                if(entity!=null){
                    for(int i=0;i<entity.length;i++){
                        backList.add(entity[i]);
                    }
                }
                backList.add(back);
                feedList.add(back);
                FeedBackEntity[] backTemp = new  FeedBackEntity[backList.size()];
                backTemp = backList.toArray(backTemp);
                entity = backTemp;
                adapter.update(entity);
                listView.getListView().setSelection(adapter.getCount());
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
}
