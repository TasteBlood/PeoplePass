package com.cloudcreativity.peoplepass.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseDialogImpl;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 这是app的热更新工具
 */
public class UpdateManager{

    public static void checkVersion(Context context, BaseDialogImpl baseDialog){
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            checkLastVersionFromServer(context,info.versionName,info.versionCode,baseDialog);
        } catch (PackageManager.NameNotFoundException e) {
            ToastUtils.showShortToast(context,"获取版本信息失败");
        }
    }

    /**
     *
     * @param context 上下文
     * @param versionName
     * @param versionCode
     */
    private static void checkLastVersionFromServer(final Context context, String versionName, final int versionCode, BaseDialogImpl baseDialog){

        HttpUtils.getInstance().getLastVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>(baseDialog,false) {
                    @Override
                    public void onSuccess(String t) {
                        try {
                            final JSONObject object = new JSONObject(t);
                            int version = object.getInt("mark");
                            if(version>versionCode){
                                final Dialog dialog = new Dialog(context, R.style.myDialogStyleAnim);
                                View view = LayoutInflater.from(context).inflate(R.layout.layout_download_apk_dialog, null, false);
                                dialog.setCancelable(false);
                                dialog.setCanceledOnTouchOutside(false);

                                WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
                                dialog.setContentView(view);
                                attributes.width = context.getResources().getDisplayMetrics().widthPixels*4/5;

                                TextView tv_version = view.findViewById(R.id.tv_version);
                                TextView tv_content = view.findViewById(R.id.tv_content);

                                tv_version.setText(object.getString("num"));
                                tv_content.setText(object.getString("content"));

                                view.findViewById(R.id.tv_download).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        try {
                                            new DownloadApkDialogUtils(context).execute(object.getString("url"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            ToastUtils.showShortToast(context,"下载安装包失败");
                                        }
                                    }
                                });
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showShortToast(context,"获取版本信息失败");
                        }
                    }

                    @Override
                    public void onFail(ExceptionReason msg) {
                        ToastUtils.showShortToast(context,"获取版本信息失败");
                    }
                });
    }
}
