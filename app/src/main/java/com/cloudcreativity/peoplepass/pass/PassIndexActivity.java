package com.cloudcreativity.peoplepass.pass;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseActivity;
import com.cloudcreativity.peoplepass.utils.ToastUtils;
import com.cloudcreativity.peoplepass.databinding.ActivityPassIndexBinding;

/**
 * 举报索引页面
 */
public class PassIndexActivity extends BaseActivity {
    private PassIndexModel indexModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPassIndexBinding binding  = DataBindingUtil.setContentView(this, R.layout.activity_pass_index);
        binding.setIndexModel(indexModel = new PassIndexModel(this));
        binding.tlbPassIndex.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //初始化webview
        WebSettings settings = binding.wvPassIndex.getSettings();
        settings.setUseWideViewPort(true);
        settings.setDefaultTextEncodingName("UTF-8");
        binding.wvPassIndex.loadUrl("file:///android_asset/introduction.html");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(Manifest.permission.CALL_PHONE.equals(permissions[0])&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                indexModel.callPhone();
            }else{
                ToastUtils.showShortToast(this,"无法拨打电话，请确保应用开启通话权限");
            }
        }
    }
}
