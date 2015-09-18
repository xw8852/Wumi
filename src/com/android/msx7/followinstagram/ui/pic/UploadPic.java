package com.android.msx7.followinstagram.ui.pic;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
;

import com.android.msx7.followinstagram.activity.ImageSelectActivity;
import com.android.msx7.followinstagram.util.ImageUtils;
import com.android.msx7.followinstagram.util.MD5Util;
import com.upyun.block.api.listener.CompleteListener;
import com.upyun.block.api.listener.ProgressListener;
import com.upyun.block.api.main.UploaderManager;
import com.upyun.block.api.utils.UpYunUtils;

import java.io.File;
import java.util.Map;

/**
 * Created by Josn on 2015/9/8.
 */
public class UploadPic {
    // 空间名
    String bucket = "yooho";
    // 表单密钥
    String formApiSecret = "lH28fUoHTufJfaDWyDD626pCrL0=";
    // 本地文件路径
    private String localFilePath = null;
    // 保存到又拍云的路径
    String savePath = "";
    UploadListener listener;

    public UploadPic(String path, UploadListener listener) {
        localFilePath = path;
        savePath = "/" + MD5Util.getStringMD5String(path) + ".png";
        this.listener = listener;
    }


    public void execute() {
        new UploadTask().execute();
    }

    public class UploadTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            File localFile = ImageUtils.compress(localFilePath,200, ImageSelectActivity.getImageDir(),480,800);
//            File localFile = new File(localFilePath);
            try {
                /*
                 * 设置进度条回掉函数
				 *
				 * 注意：由于在计算发送的字节数中包含了图片以外的其他信息，最终上传的大小总是大于图片实际大小，
				 * 为了解决这个问题，代码会判断如果实际传送的大小大于图片
				 * ，就将实际传送的大小设置成'fileSize-1000'（最小为0）
				 */
                ProgressListener progressListener = new ProgressListener() {
                    @Override
                    public void transferred(long transferedBytes, long totalBytes) {
                        // do something...
                        System.out.println("trans:" + transferedBytes + "; total:" + totalBytes);
                    }
                };

                CompleteListener completeListener = new CompleteListener() {
                    @Override
                    public void result(boolean isComplete, String result, String error) {
                        // do something...
                        if (isComplete) handler.sendEmptyMessage(0);
                        else handler.sendEmptyMessage(-1);
                        System.out.println("isComplete:" + isComplete + ";result:" + result + ";error:" + error);
                    }
                };

                UploaderManager uploaderManager = UploaderManager.getInstance(bucket);
                uploaderManager.setConnectTimeout(60);
                uploaderManager.setResponseTimeout(60);
                Map<String, Object> paramsMap = uploaderManager.fetchFileInfoDictionaryWith(localFile, savePath);
                //还可以加上其他的额外处理参数...
                paramsMap.put("return_url", "http://httpbin.org/get");
                // signature & policy 建议从服务端获取
                String policyForInitial = UpYunUtils.getPolicy(paramsMap);
                String signatureForInitial = UpYunUtils.getSignature(paramsMap, formApiSecret);
                uploaderManager.upload(policyForInitial, signatureForInitial, localFile, progressListener, completeListener);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "result";
        }

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    if (listener != null)
                        listener.doFinish(localFilePath, "http://yooho.b0.upaiyun.com" + savePath);
                } else {
                    if (listener != null) listener.doFinish(null, null);
                }
            }
        };

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }

    public static interface UploadListener {
        //url为空表示上传文件失败
        public void doFinish(String path, String url);

    }


}
