package com.nhd.mall.activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhd.mall.R;
import com.nhd.mall.asyncTask.CommentListGet;
import com.nhd.mall.entity.CommentEntity;
import com.nhd.mall.entity.CommentList;

/**商品查看信息fragment
 * Created by caili on 14-4-5.
 */
public class GoodsDetailFragment extends Fragment {
   private String detail;
    private TextView tvDetail;

    static GoodsDetailFragment getInstance(String detail){
        GoodsDetailFragment mFragment=new GoodsDetailFragment();
        mFragment.detail = detail;
        return mFragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.goods_detail_fragment_layout, container, false);
        findView(view);
        return view;
    }
    private void findView(View view) {
        tvDetail = (TextView)view.findViewById(R.id.textView);
        tvDetail.setText(detail);
    }
}
