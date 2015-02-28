package com.nhd.mall.asyncTask;
import android.content.Context;
import android.util.Log;
import com.nhd.mall.api.AndroidServerFactory;
import com.nhd.mall.entity.CommentEntity;
import com.nhd.mall.entity.CommentList;
import com.nhd.mall.util.AsyncTaskEx;
import com.nhd.mall.util.OnAsyncTaskUpdateListener;
import com.nhd.mall.util.ParseJson;
import com.umeng.analytics.MobclickAgent;
import org.springframework.web.client.HttpServerErrorException;
import java.io.IOException;
import java.util.ArrayList;

/**获取商品评论
 * Created by caili on 14-4-18.
 */
public class CommentListGet {
    private OnAsyncTaskUpdateListener listener;
    private String message;
    private Context context;
    private Integer pageNum;
    private CommentList commentList;
    private Integer productId;

    public CommentListGet(Context context,Integer pageNum,Integer productId) {
        if(!AndroidServerFactory.PRODUCTION_MODEL)
            MobclickAgent.onEvent(context, "path", "/api/v1/comment/list");
        this.context = context;
        this.pageNum  = pageNum;
        this.productId = productId;
        new DownloadTask().execute();
    }
    public void update(Integer pageNum) {
        this.pageNum  = pageNum;
        new DownloadTask().execute();
    }

    class DownloadTask extends AsyncTaskEx<Void, Void,CommentList> {

        public DownloadTask() {
            super();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected CommentList doInBackground(Void... params) {
            CommentList object = null;
            try {
                object = AndroidServerFactory.getServer().getCommentList(productId,pageNum);
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
        protected void onPostExecute(CommentList result) {
            super.onPostExecute(result);
            if (listener != null) {
                listener.getData( merge(result),message);
            }
        }
    }
    public CommentList merge(CommentList result){
        if(commentList==null||commentList.getComments().length==0){
            commentList = result;
            return result;
        }
        CommentEntity[]entity =commentList.getComments();
        CommentEntity[]reEntity = result.getComments();
        ArrayList<CommentEntity> storeList = new ArrayList<CommentEntity>();
        for(int i=0;i<entity.length;i++){
            storeList.add(entity[i]);
        }
        for(int i=0;i<reEntity.length;i++){
            storeList.add(reEntity[i]);
        }
        CommentEntity[]finalEntity = new CommentEntity[storeList.size()];
        finalEntity = storeList.toArray(finalEntity);
        commentList.setComments(finalEntity);
        return commentList;
    }
    public void setListener(OnAsyncTaskUpdateListener listener) {
        this.listener = listener;
    }


}

