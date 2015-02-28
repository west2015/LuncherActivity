package com.nhd.mall.asyncTask;

import android.content.Context;
import android.util.Log;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.FeedBackEntity;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;
import org.springframework.web.client.HttpServerErrorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**获取意见反馈列表
* Created by Administrator on 14-5-4.
*/
public class FeedBackListGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private Integer pageNum;
    private FeedBackEntity[] feedBack;
    private Long memberId;
    private ArrayList<FeedBackEntity>feedList = new ArrayList<FeedBackEntity>();

    public FeedBackListGet(Context context,Long memberId,Integer pageNum) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/member/memberMessages");
        this.context = context;
        this.pageNum  = pageNum;
        this.pageNum  = pageNum;
        this.memberId = memberId;
        new DownloadTask().execute();
    }
    public void update(Integer pageNum,Long memberId,ArrayList<FeedBackEntity>feedList) {
        this.feedList = feedList;
        this.pageNum  = pageNum;
        if(pageNum==1){
            feedBack=null;
            feedList.clear();
        }
        this.memberId = memberId;
        new DownloadTask().execute();
    }

    class DownloadTask extends AsyncTaskEx<Void, Void,FeedBackEntity[]> {

        public DownloadTask() {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingProgressDialog(context);
        }

        @Override
        protected FeedBackEntity[] doInBackground(Void... params) {
            FeedBackEntity[] object = null;
            try {
                object = AndroidServerFactory.getServer().GetFeedBackList(memberId,pageNum);
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
            return object;
        }
        @Override
        protected void onPostExecute(FeedBackEntity[] result) {
            super.onPostExecute(result);
            dismissProgressDialog(context);
            if (listener != null) {
                listener.getData(merge(result),message);
            }
        }
    }
    public FeedBackEntity[] merge(FeedBackEntity[] result){
        if(result==null){
            return toSort(feedBack);
        }
        List<FeedBackEntity> list=new ArrayList<FeedBackEntity>();
        if(feedBack!=null){
            for (FeedBackEntity member : feedBack) {
                list.add(member);
            }
        }
        for (FeedBackEntity member : result) {
            list.add(member);
        }
        FeedBackEntity[] newMembers=new FeedBackEntity[list.size()];
        list.toArray(newMembers);
        feedBack=newMembers;
        return toSort(feedBack);
    }
    public FeedBackEntity[] toSort(FeedBackEntity[] entity){
        if(entity==null||entity.length<=0)return entity;
        List<FeedBackEntity> list=new ArrayList<FeedBackEntity>();
        for(int i=entity.length-1;i>=0;i--){
            list.add(entity[i]);
        }
        if(feedList.size()>0){
            list.addAll(feedList);
        }
        FeedBackEntity[] newMembers=new FeedBackEntity[list.size()];
        list.toArray(newMembers);
        entity=newMembers;
       return entity;
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }
}
