package com.cloudcreativity.peoplepass.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.View;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseActivity;
import com.cloudcreativity.peoplepass.databinding.ActivitySettingBinding;
import com.cloudcreativity.peoplepass.utils.SPUtils;
import com.cloudcreativity.peoplepass.utils.ToastUtils;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity {

    private SettingModel setModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        setSupportActionBar(binding.tlbSetting);
        setModel = new SettingModel(binding, this);
        binding.setSetModel(setModel);
        binding.tlbSetting.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setModel.isLogin.set(SPUtils.get().isLogin());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(Manifest.permission.READ_PHONE_STATE.equals(permissions[0])
                &&grantResults[0]==PackageManager.PERMISSION_GRANTED){

            TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setModel.imei.set(manager.getImei());
                SPUtils.get().setIMEI(manager.getImei());
            }else{
                setModel.imei.set(manager.getDeviceId());
                SPUtils.get().setIMEI(manager.getDeviceId());
            }
        }else{
            ToastUtils.showShortToast(this,"获取imei失败，请确保程序打开读取手机状态权限");
        }
    }
}
