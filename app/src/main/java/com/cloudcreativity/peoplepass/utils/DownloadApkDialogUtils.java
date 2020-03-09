package com.cloudcreativity.peoplepass.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;

import com.cloudcreativity.peoplepass.receiver.MyBusinessReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载apk文件
 */
public class DownloadApkDialogUtils extends AsyncTask<String,Integer,String>{
    private static ProgressDialog dialog;

    private File apkFile;
    private Context context;
    private OkHttpClient client;

    private MyResultHandler handler;

    DownloadApkDialogUtils(Context context){
        this.context = context;
        apkFile = new File(context.getExternalCacheDir(),AppConfig.APP_HOT_UPDATE_FILE);
        try {
            if(apkFile.exists()) {
                boolean delete = apkFile.delete();
            }
            boolean newFile = apkFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        client = new OkHttpClient.Builder().build();
        handler = new MyResultHandler((Activity) context);
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("下载提示");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        try{
            Request request=new Request.Builder().url(strings[0]).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message message = Message.obtain();
                    message.what = MyResultHandler.MSG_FAILD;
                    handler.sendMessage(message);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream is=null;
                    byte[] buf=new byte[2048];
                    int len;
                    FileOutputStream fos=null;

                    try{
                        is=response.body().byteStream();
                        long total=response.body().contentLength();
                        fos=new FileOutputStream(apkFile);
                        long sum=0;
                        while((len = is.read(buf))!=-1){
                            fos.write(buf,0,len);
                            sum+=len;
                            int progress=(int)(sum*1.0f/total*100);
                            Message message = Message.obtain();
                            message.what = MyResultHandler.MSG_PROGRESS;
                            message.obj = progress;
                            handler.sendMessage(message);
                        }
                        fos.flush();

                        Message message = Message.obtain();
                        message.what = MyResultHandler.MSG_SUCCESS;
                        message.obj = apkFile;
                        handler.sendMessage(message);
                    }catch (Exception e){
                        Message message = Message.obtain();
                        message.what = MyResultHandler.MSG_FAILD;
                        handler.sendMessage(message);
                    }finally{
                        try{
                            if(is!=null)
                                is.close();
                        }catch (IOException e){
                            Message message = Message.obtain();
                            message.what = MyResultHandler.MSG_FAILD;
                            handler.sendMessage(message);
                        }
                        try {
                            if(fos!=null){
                                fos.close();
                            }
                        }catch (IOException e){
                            Message message = Message.obtain();
                            message.what = MyResultHandler.MSG_FAILD;
                            handler.sendMessage(message);
                        }

                    }
                }
            });
        }catch (Exception e){
            Message message = Message.obtain();
            message.what = MyResultHandler.MSG_FAILD;
            handler.sendMessage(message);
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {

    }

    private static class MyResultHandler extends Handler{

         static final int MSG_FAILD = 0x001;
         static final int MSG_SUCCESS = 0x002;
         static final int MSG_PROGRESS = 0x003;

        private final WeakReference<Activity> activityWeakReference;
        private MyResultHandler(Activity activity){
            this.activityWeakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Activity activity = activityWeakReference.get();
            if(activity!=null&&!activity.isFinishing()){
                switch (msg.what){
                    case MSG_FAILD:
                        dialog.dismiss();
                        ToastUtils.showShortToast(activity,"下载安装包失败,请重试");

                        //直接关闭当前的app
                        Intent broadcast = new Intent();
                        broadcast.setAction(MyBusinessReceiver.ACTION_EXIT_APP);
                        activity.sendBroadcast(broadcast);
                        break;
                    case MSG_PROGRESS:
                        dialog.setProgress((Integer) msg.obj);
                        dialog.setMessage("下载中:"+msg.obj+"%");
                        break;
                    case MSG_SUCCESS:
                        //开始启动安装apk
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //判读版本是否在7.0以上
                        if (Build.VERSION.SDK_INT >= 24) {
                            //provider authorities
                            Uri apkUri = FileProvider.getUriForFile(activity, activity.getApplicationInfo().packageName+".fileprovider", (File) msg.obj);
                            //Granting Temporary Permissions to a URI
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        } else {
                            intent.setDataAndType(Uri.fromFile((File) msg.obj), "application/vnd.android.package-archive");
                        }
                        activity.startActivity(intent);

                        //直接关闭当前的app
                        Intent broadcast1 = new Intent();
                        broadcast1.setAction(MyBusinessReceiver.ACTION_EXIT_APP);
                        activity.sendBroadcast(broadcast1);
                        break;
                }
            }
        }
    }
}
