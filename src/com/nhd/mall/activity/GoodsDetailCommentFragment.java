package com.nhd.mall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhd.mall.R;
import com.nhd.mall.adapter.GoodsDetailCommentAdapter;
import com.nhd.mall.asyncTask.CommentListGet;
import com.nhd.mall.asyncTask.StoreListGet;
import com.nhd.mall.entity.CommentEntity;
import com.nhd.mall.entity.CommentList;
import com.nhd.mall.entity.StoreEntity;
import com.nhd.mall.entity.StoreListEntity;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.widget.PullDownView;

/**查看商品评价的fragment
 * Created by caili on 14-4-5.
 */
public class GoodsDetailCommentFragment extends Fragment implements PullDownView.OnPullDownListener,OnAsyncTaskUpdateListener {
    private PullDownView listView;
    private GoodsDetailCommentAdapter gca;
    //获取数据
    private Integer productId;
    private CommentListGet commentGet;
    private Integer pageNum =1;
    private CommentList commentList;
    private CommentEntity[]entity;
    private final int MAXCOUNT=10;
    private TextView tvComment;

    static GoodsDetailCommentFragment getInstance(Integer id){
        GoodsDetailCommentFragment mFragment=new GoodsDetailCommentFragment();
        mFragment.productId = id;
        return mFragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.goods_detail_comment_fragment_layout, container, false);
        findView(view);
        return view;
    }
    private void findView(View view) {
        tvComment = (TextView)view.findViewById(R.id.tv_comment);
        listView = (PullDownView)view.findViewById(R.id.lvComment);
        gca = new GoodsDetailCommentAdapter(getActivity(),entity);
        listView.setAdapter(gca);
        listView.setOnPullDownListener(this);
        listView.getListView().setFooterDividersEnabled(false);
        listView.enableAutoFetchMore(true, 1);
        listView.setHideHeader();
        listView.setHideFooter();
        //获取数据
        commentGet = new CommentListGet(getActivity(),pageNum,productId);
        commentGet.setListener(this);
    }
    @Override
    public void getData(Object obj, String message) {
        listView.RefreshComplete();
        listView.notifyDidMore();
        if (message != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        if (obj == null){
            tvComment.setText("全部评价");
            return;
        }
        if(obj instanceof CommentList){
            commentList = (CommentList) obj;
            entity =commentList.getComments();
            gca.update(entity);
            if(entity.length>0){
                tvComment.setText("全部评价"+"("+String.valueOf(entity.length)+")");
            }
            else{
                tvComment.setText("全部评价");
            }
            if(entity.length>=MAXCOUNT){
                listView.setShowFooter();
            }
            else{
                listView.setHideFooter();
            }
        }
    }
    @Override
    public void onRefresh() {
    }

    @Override
    public void onMore() {
        commentGet.update(++pageNum);
    }
}
