package com.nhd.mall.activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nhd.mall.R;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.app.MainApplication;
import com.nhd.mall.asyncTask.DelDeviceInfoPost;
import com.nhd.mall.datebase.DbConfig;
import com.nhd.mall.push.BaidupushUtils;
import com.nhd.mall.share.ShareContentActivity;
import com.nhd.mall.util.CheckUpdates;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.startIntent;
import com.nhd.mall.widget.ModelActivity;
import com.umeng.analytics.MobclickAgent;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;


/**
 * 内容摘要 ：
 * <p>
 * 新华都更多页面
 * 作者 ：caili 
 */
public class MallSettingActivity extends ModelActivity implements View.OnClickListener,CheckUpdates.CheckVison {
    private ImageView ivMessage;
    private DbConfig db;
    private PopupWindow popShare;
    private TextView tvKefu;
    private TextView tvVerName;
    private Button tvZx;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("更多");
        getButtonBack().setVisibility(View.GONE);
        setContentView(R.layout.setting_layout);
        find();
    }
    private void find() {
        db = new DbConfig(this);
        ivMessage = (ImageView)findViewById(R.id.ivMessageIcon);
        tvVerName = (TextView)findViewById(R.id.tvVersionDetail);
        ivMessage.setOnClickListener(this);
        tvKefu = (TextView)findViewById(R.id.tvKefuPhone);
        tvZx = (Button)findViewById(R.id.btnZx);
        tvZx.setOnClickListener(this);
        if(db.getNotification()){
            ivMessage.setBackgroundResource(R.drawable.more_message_open);
        }
        else{
            ivMessage.setBackgroundResource(R.drawable.more_message_close);
        }
        findViewById(R.id.rl_Comment).setOnClickListener(this);
        findViewById(R.id.rl_share).setOnClickListener(this);
        findViewById(R.id.rl_kefu).setOnClickListener(this);
        findViewById(R.id.rl_version).setOnClickListener(this);
        findViewById(R.id.rl_Message).setOnClickListener(this);
        findViewById(R.id.rl_shop).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.rl_kefu:
                if(tvKefu.getText()==null||tvKefu.getText().toString().equals(""))return;
                AlertDialog.Builder builderPhone = new AlertDialog.Builder(MallSettingActivity.this);
                builderPhone.setTitle("拨打客服");
                builderPhone.setMessage("您确定要拨打该电话吗?");
                builderPhone.setPositiveButton("确定",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.DIAL");
                        intent.setData(Uri.parse("tel:"+""));
                        MallSettingActivity.this.startActivity(intent);
                    }
                });
                builderPhone.setNegativeButton("取消", null);
                builderPhone.show();
                break;
            case R.id.rl_Message:
                if(db.getNotification()){
                AlertDialog.Builder builder = new AlertDialog.Builder(MallSettingActivity.this);
                builder.setMessage("关掉消息通知将接受不到我们发送的消息?");
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ivMessage.setBackgroundResource(R.drawable.more_message_close);
                        db.setNotification(false);
                        BaidupushUtils.stopWorkding(MallSettingActivity.this);
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();
            }
                else{
                    ivMessage.setBackgroundResource(R.drawable.more_message_open);
                    db.setNotification(true);
                    BaidupushUtils.init(MallSettingActivity.this);
                }
                break;
            case R.id.rl_Comment:  //投诉建议
                new startIntent(MallSettingActivity.this,CustomerCommentActivity.class);
                break;
            case R.id.rl_share:  //分享
                showShare(view);
                break;
            case R.id.rl_shop:  //分享
                new startIntent(MallSettingActivity.this,ShopDetailActivity.class);
                break;
            case R.id.rl_sina:  //新浪分享
                Bundle sinaBundle = new Bundle();
                sinaBundle.putInt("shareto", ShareContentActivity.SHARE_TO_SINA);
                new startIntent(MallSettingActivity.this,ShareContentActivity.class,sinaBundle);
                popShare.dismiss();
                break;
            case R.id.rl_friends:  //朋友圈分享
                Bundle wechatMomentBundle = new Bundle();
                wechatMomentBundle.putInt("shareto",ShareContentActivity.SHARE_TO_WECHAT_MOMENTS);
                new startIntent(MallSettingActivity.this,ShareContentActivity.class,wechatMomentBundle);
                popShare.dismiss();
                break;
            case R.id.rl_weChat:  //微信分享
                Bundle wechatBundle = new Bundle();
                wechatBundle.putInt("shareto",ShareContentActivity.SHARE_TO_WECHAT);
                new startIntent(MallSettingActivity.this,ShareContentActivity.class,wechatBundle);
                popShare.dismiss();
                break;
            case R.id.btnZx:
                if(MainApplication.getInstance().getMember()!=null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MallSettingActivity.this);
                    builder.setMessage("确定注销?");
                    builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //删除用户已经绑定的设备信息
                            new DelDeviceInfoPost(getApplicationContext(),MainApplication.getInstance().getMember().getId()).setListener(new OnAsyncTaskUpdateListener() {
                                @Override
                                public void getData(Object obj, String message) {
                                    if(message!=null){
                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                }
                                    Toast.makeText(getApplicationContext(),"注销成功",Toast.LENGTH_SHORT).show();
                                    tvZx.setText("登录");
                                }
                            });
                            MainApplication.getInstance().setMember(null);
                            MainApplication.getInstance().setBeanCount(0);
                            Platform[] platforms = ShareSDK.getPlatformList(getApplicationContext());
                            for (i = 0; i<platforms.length;i++){
                                platforms[i].removeAccount();
                            }
                        }
                    });
                    builder.setNegativeButton("取消",null);
                    builder.show();
                }
                else{
                    new startIntent(MallSettingActivity.this,LoginActivity.class);
                }
                break;
            case R.id.rl_version://版本检查
                CheckUpdates check = new CheckUpdates(MallSettingActivity.this);
                check.setCheckVisonlistener(MallSettingActivity.this);
                check.getCheckUpdate();
                break;
        }
    }
    /**
     * 弹出分享窗口
     * @param view
     */
    private void showShare(View view) {
        if (popShare == null) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            View shareView = LayoutInflater.from(MallSettingActivity.this).inflate(R.layout.share_dialog, null);
            popShare = new PopupWindow(shareView, width, LinearLayout.LayoutParams.WRAP_CONTENT);
            shareView.findViewById(R.id.rl_sina).setOnClickListener(MallSettingActivity.this);
            shareView.findViewById(R.id.rl_friends).setOnClickListener(MallSettingActivity.this);
            shareView.findViewById(R.id.rl_weChat).setOnClickListener(MallSettingActivity.this);
            shareView.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popShare.dismiss();
                }
            });
        }
        popShare.setBackgroundDrawable(getResources().getDrawable(R.drawable.share_dialog_bg));
        popShare.setFocusable(true);
        popShare.setOutsideTouchable(false);
        popShare.update();
        popShare.setAnimationStyle(R.style.AnimBottom);
        popShare.showAtLocation(view, Gravity.BOTTOM, 0,0);

}

    @Override
    protected void onPause() {
        super.onPause();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onPause(this);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!AndroidServerFactory.PRODUCTION_MODEL){
            StatService.onResume(this);
        }
        if(MainApplication.getInstance().getMember()!=null){
            tvZx.setText("注销");
        }
        else{
            tvZx.setText("登录");
        }
        if(MainApplication.getInstance().getVerName()!=null){
            tvVerName.setText("当前版本:V"+MainApplication.getInstance().getVerName());
        }
        if(MainApplication.getInstance().getStore()!=null){
            tvKefu.setText(MainApplication.getInstance().getStore().getTel()==null?"":MainApplication.getInstance().getStore().getTel());
        }
        else{
            tvKefu.setText("");
        }
    }

    @Override
    public void printString() {
        Toast.makeText(MallSettingActivity.this,"当前是最新版本",Toast.LENGTH_SHORT).show();
    }
}
