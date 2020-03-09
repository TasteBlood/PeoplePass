package com.cloudcreativity.peoplepass.pass;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseActivity;
import com.cloudcreativity.peoplepass.base.BaseApp;
import com.cloudcreativity.peoplepass.databinding.ActivityPassBinding;
import com.cloudcreativity.peoplepass.utils.ToastUtils;
import com.donkingliang.imageselector.utils.ImageSelector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 举报页面
 */
public class PassActivity extends BaseActivity {

    private PassModel model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if((ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)){
            ActivityPassBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_pass);
            setSupportActionBar(binding.tlbPass);
            binding.tlbPass.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            binding.setPassModel(model = new PassModel(this,binding));
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},100);
            }else{
                ToastUtils.showShortToast(BaseApp.app,"请在手机设置中打开定位权限");
                onBackPressed();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK)
            return;
        if(requestCode==100&&data!=null){
            ArrayList<String> listExtra = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
            model.adapter.update(listExtra);

            //地址选择
        }else if(requestCode==PassModel.REQUEST_CODE&&data!=null){
            String address = data.getStringExtra("address");
            String province = data.getStringExtra("province");
            String city = data.getStringExtra("city");
            String area = data.getStringExtra("area");
            String street = data.getStringExtra("street");
            String name = data.getStringExtra("name");
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);

            HashMap<String,Object> result = new HashMap<>();
            result.put("address",address);
            result.put("province",province);
            result.put("city",city);
            result.put("area",area);
            result.put("street",street);
            result.put("latitude",latitude);
            result.put("longitude",longitude);
            result.put("name",name);

            model.onLocationResult(result);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        model.destroy();
    }

    @Subscribe
    public void onEvent(Map<String,Object> data){
        if(data!=null){
            model.uploadSuccess(data);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if((Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[0])&&grantResults[0]==PackageManager.PERMISSION_GRANTED)&&
                Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[1])&&grantResults[1]==PackageManager.PERMISSION_GRANTED){
            //显示
            ActivityPassBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_pass);
            setSupportActionBar(binding.tlbPass);
            binding.tlbPass.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            binding.setPassModel(model = new PassModel(this,binding));
        }else{
            ToastUtils.showShortToast(BaseApp.app,"请在手机设置中打开定位权限");
            onBackPressed();
        }
    }
}
