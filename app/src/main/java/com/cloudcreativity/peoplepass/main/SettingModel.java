package com.cloudcreativity.peoplepass.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.ObservableField;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.cloudcreativity.peoplepass.base.BaseDialogImpl;
import com.cloudcreativity.peoplepass.base.CommonWebActivity;
import com.cloudcreativity.peoplepass.databinding.ActivitySettingBinding;
import com.cloudcreativity.peoplepass.receiver.MyBusinessReceiver;
import com.cloudcreativity.peoplepass.utils.CacheUtils;
import com.cloudcreativity.peoplepass.utils.DefaultObserver;
import com.cloudcreativity.peoplepass.utils.HttpUtils;
import com.cloudcreativity.peoplepass.utils.SPUtils;
import com.cloudcreativity.peoplepass.utils.UpdateManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SettingModel {

    public ObservableField<String> cache = new ObservableField<>();
    public ObservableField<String> version = new ObservableField<>();
    public ObservableField<String> imei = new ObservableField<>();
    public ObservableField<Boolean> isLogin = new ObservableField<>();

    private ActivitySettingBinding binding;
    private SettingActivity context;
    private BaseDialogImpl baseDialog;

    SettingModel(ActivitySettingBinding binding, SettingActivity context) {
        this.binding = binding;
        this.context = context;
        this.baseDialog = context;
        TelephonyManager manager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.context.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},100);
            }
        }else{
            // TODO: Consider calling
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.imei.set(manager.getImei());
                SPUtils.get().setIMEI(manager.getImei());
            }else{
                this.imei.set(manager.getDeviceId());
                SPUtils.get().setIMEI(manager.getDeviceId());
            }
        }

        this.isLogin.set(false);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cache.set(CacheUtils.getTotalCacheSize(SettingModel.this.context));
                try {
                    PackageInfo packageInfo = SettingModel.this.context.getPackageManager()
                            .getPackageInfo(SettingModel.this.context.getPackageName(), 0);
                    version.set("v"+packageInfo.versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    version.set("v1.0");
                }
            }
        });
    }

    /**
     * 退出登录点击
     */
    public void onExitLoginClick(){
        HttpUtils.getInstance().logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>(baseDialog,true) {
                    @Override
                    public void onSuccess(String t) {
                        Intent intent = new Intent();
                        intent.setAction(MyBusinessReceiver.ACTION_LOGOUT);
                        context.sendBroadcast(intent);
                        context.finish();
                    }

                    @Override
                    public void onFail(ExceptionReason msg) {

                    }
                });
    }

    /**
     * 清空缓存点击
     */
    public void onClearCacheClick(){
        CacheUtils.clearCache(context);
        cache.set("0.0KB");
    }

    /**
     * 版本介绍
     */
    public void onVersionDescClick(){
        CommonWebActivity.startActivity(context,"版本说明","file:///android_asset/version_desc.html");
    }

    /**
     * 二维码点击
     */
    public void onAppCodeClick(){
        CommonWebActivity.startActivity(context,"APP下载二维码","file:///android_asset/app_code.html");
    }

    /**
     * 修改密码点击
     */
    public void onModifyPwdClick(){
        context.startActivity(new Intent(context,ModifyPwdActivity.class));
    }

    /**
     * 检查更新app版本
     */
    public void onUpdateClick(){
        UpdateManager.checkVersion(context,baseDialog);
    }
}
