package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;

import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.Member;
import com.nhd.mall.entity.MemberRespondEntity;
import com.nhd.mall.push.BaidupushUtils;
import com.nhd.mall.push.DbBaiduPush;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;

import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;

/**
 * Created by Administrator on 14-5-15.
 */
public class BeanLoginPost {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Member member;
    private Context context;

    public BeanLoginPost(Context context, Member member) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "api/v1/wind/login");
        this.context = context;
        this.member = member;
        new DownloadTask().execute();
    }

    class DownloadTask extends AsyncTaskEx<Void, Void,MemberRespondEntity> {
        public DownloadTask() {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingProgressDialog(context);
        }

        @Override
        protected MemberRespondEntity doInBackground(Void... params) {
            MemberRespondEntity status=null;
            try {
                status = AndroidServerFactory.getServer().postBeanLogin(member);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                if (e instanceof HttpServerErrorException) {
                    message = ((HttpServerErrorException) e).getResponseBodyAsString();
                    Log.e("HttpServerErrorException", message);
                } else {
                    final String TAG = "asynctask";
                    final String msg = e.getMessage();

                    if (msg != null) {
                        Log.e(TAG, msg);
                    }
                }
            }
            return status;
        }
        @Override
        protected void onPostExecute(MemberRespondEntity result) {
            super.onPostExecute(result);
            dismissProgressDialog(context);
            if(result!=null && result.getDatas()!=null && result.getStatus().getSuccess().equals("true")){
                //登陆成功后，把百度推送的绑定信息发送给后台服务端
                if(BaidupushUtils.hasBind(context)){
                    new BaiduPushMsgPost(context,result.getDatas().getId(),
                            new DbBaiduPush(context).getBaiduBindingMsg()).setListener(listener);
                }
            }
            if(result!=null && result.getMessage()!=null){
                message=result.getMessage();
            }
            if (listener != null) {
                listener.getData(result, message);
            }
        }
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
