package com.nhd.mall.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.PublicCommentPost;
import com.nhd.mall.entity.PublicCommentEntity;
import com.nhd.mall.util.ImageLoader;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**评价商品页面
 * Created by caili on 14-4-8.
 */
public class GoodsCommentActivity extends ModelActivity implements View.OnClickListener,OnAsyncTaskUpdateListener {

    private Long memberId;
    private Integer productId;
    private EditText etComment;
    private TextView tvAllPrice;
    private TextView tvName;
    private TextView tvPrice;
    private TextView tvCount;
    private TextView tvTxtNum;
    private ImageView ivImg;
    private ImageLoader imageLoader;
    private CharSequence temp;
    private int contentNum = 1000;
    private int selectionStart, selectionEnd;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("发表评论");
        setContentView(R.layout.goods_comment_layout);
        find();
    }
    private void find() {
        imageLoader  = new ImageLoader(this);
        imageLoader.setDefaultBackgroup(R.drawable.goods_mr_img);
        imageLoader.setFailBackgroup(R.drawable.goods_mr_img);
        etComment = (EditText)findViewById(R.id.etComment);
        findViewById(R.id.btnComment).setOnClickListener(this);
        tvAllPrice = (TextView)findViewById(R.id.tv_allCount);
        tvName = (TextView)findViewById(R.id.goodsName);
        tvPrice = (TextView)findViewById(R.id.goodsPrice);
        tvCount = (TextView)findViewById(R.id.goodsCount);
        ivImg = (ImageView)findViewById(R.id.imageView);
        tvTxtNum = (TextView)findViewById(R.id.tvCommentCount);
        initDetail();
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int number = contentNum - s.length();
                tvTxtNum.setText(number +"/1000");
                selectionStart = etComment.getSelectionStart();
                selectionEnd = etComment.getSelectionEnd();
                if (temp.length() > contentNum) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    etComment.setText(s);
                    etComment.setSelection(tempSelection);// 设置光标在最后
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });
    }

    private void initDetail() {
        if(getIntent().getExtras()!=null){
            productId = getIntent().getExtras().getInt("productId");
            String name = getIntent().getExtras().getString("name", "");
            String thumb = getIntent().getExtras().getString("thumb","");
            int count = getIntent().getExtras().getInt("count");
            double price = getIntent().getExtras().getDouble("price", 0);
            tvName.setText(name);
            tvCount.setText(count+"");
            tvPrice.setText("￥"+price);
            tvAllPrice.setText("￥"+(price*count));
            imageLoader.setBackgroup(thumb,ivImg);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnComment:
                if(etComment.getText()==null||etComment.getText().toString().equals("")){
                    Toast.makeText(GoodsCommentActivity.this,"请输入评论内容",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(MainApplication.getInstance().getMember()==null){
                    new startIntent(GoodsCommentActivity.this,LoginActivity.class);
                    return;
                }
                memberId = MainApplication.getInstance().getMember().getId();
                PublicCommentEntity entity = new PublicCommentEntity();
                entity.setMemberId(memberId);
                entity.setProductId(productId);
                entity.setMessage(etComment.getText().toString());
                new PublicCommentPost(GoodsCommentActivity.this,entity).setListener(GoodsCommentActivity.this);
                break;
        }
    }
    @Override
    public void getData(Object obj, String message) {
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (obj == null)
            return;
        HashMap<String,String> map = (HashMap<String, String>) obj;
        if(map.get("success").equals("true")){
            Toast.makeText(GoodsCommentActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
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
