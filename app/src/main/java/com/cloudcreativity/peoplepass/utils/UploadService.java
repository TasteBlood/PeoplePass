package com.cloudcreativity.peoplepass.utils;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 七牛文件上传服务
 */
public class UploadService extends AsyncTask<Void,Integer,String> {

    private String token;
    private List<String> images;
    private String voicePath;
    private ProgressDialog dialog;

    private int completeNum = 0;
    private List<String> uploadUrls = new ArrayList<>();
    private String uploadVoicePath = null;
    private UploadManager uploadManager;
    private Map<String,Double> uploadProgress = new HashMap<>();

    private int totalSize;//全部上传文件

    public UploadService(String token, List<String> images, String voicePath, ProgressDialog dialog) {
        this.token = token;
        this.images = images;
        this.voicePath = voicePath;
        this.dialog = dialog;
        if(images!=null)
            this.totalSize = this.images.size();
        if(!TextUtils.isEmpty(this.voicePath))
            this.totalSize += 1;

        final Configuration config = new Configuration.Builder()
                .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
                .responseTimeout(60)          // 服务器响应超时。默认60秒
                //.recorder(recorder)           // recorder分片上传时，已上传片记录器。默认null
                //.recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(FixedZone.zone0)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();

        uploadManager = new UploadManager(config);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        if(TextUtils.isEmpty(voicePath)&&(this.images==null||this.images.isEmpty())){
            return "finish";
        }else{
            //开始上传
            Luban.with(dialog.getContext())
                    .load(images)
                    .ignoreBy(100)
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(File file) {
                            uploadPicFile(file);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    }).launch();

            if(!TextUtils.isEmpty(voicePath))
                uploadVoiceFile(voicePath);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        LogUtils.e(this.getClass().getName(),"progress="+values[0]);
        dialog.setProgress(values[0]);
        dialog.setMessage("上传中 "+values[0]+"%");
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        if("finish".equals(aVoid)){
            //确定是完成

            dialog.dismiss();

            Map<String,Object> data = new HashMap<>();
            data.put("images",uploadUrls);
            data.put("voice",uploadVoicePath);

            EventBus.getDefault().post(data);
        }
    }

    /**
     *
     * @param path 文件路径
     */
    private void uploadPicFile(File path){
        uploadManager.put(path,
                String.format(Locale.CHINA,
                        AppConfig.FILE_NAME,System.currentTimeMillis(),"jpg"),
                token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        uploadUrls.add(key);
                        completeNum++;
                        if(completeNum==totalSize){
                            //说明上传完成
                            onPostExecute("finish");
                        }
                    }
                },new UploadOptions(null, null, false, new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        uploadProgress.put(key,percent);
                        //刷新进度
                        Set<String> stringSet = uploadProgress.keySet();
                        double per = 0;
                        for(String str:stringSet){
                            per += uploadProgress.get(str);
                        }
                        publishProgress((int) (per/totalSize*100));
                    }
                },null));
    }

    private void uploadVoiceFile(String path){
        uploadManager.put(path,
                String.format(Locale.CHINA,AppConfig.FILE_NAME,System.currentTimeMillis(),"mp3"),
                token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        uploadVoicePath = key;
                        completeNum ++;
                        if(completeNum==totalSize){
                            //说明上传完成
                            onPostExecute("finish");
                        }
                    }
                },new UploadOptions(null, null, false, new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        uploadProgress.put(key,percent);
                        //刷新进度
                        if(images==null||images.isEmpty()){
                            Set<String> stringSet = uploadProgress.keySet();
                            double per = 0;
                            for(String str:stringSet){
                                per += uploadProgress.get(str);
                            }
                            publishProgress((int) (per/totalSize*100));
                        }
                    }
                },null));
    }

}
