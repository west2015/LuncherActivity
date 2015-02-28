package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;

import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.MemberRespondEntity;
import com.nhd.mall.entity.Member;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;

import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;

/**修改用户资料异步类
 * Created by linchunwei on 14-4-24.
 */
public class MemberModifyPost {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Member member;
    private Context context;

    public MemberModifyPost(Context context, Member member) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/member/update");
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
                status = AndroidServerFactory.getServer().postMemberModify(member);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                if (e instanceof HttpServerErrorException) {
                    message = ((HttpServerErrorException) e)
                            .getResponseBodyAsString();
                    message = ParseJson.getStatusAsString(message);
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
