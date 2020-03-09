package com.cloudcreativity.peoplepass.pass;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.cloudcreativity.peoplepass.loginAndRegister.LoginActivity;
import com.cloudcreativity.peoplepass.utils.SPUtils;
import com.cloudcreativity.peoplepass.utils.ToastUtils;

/**
 * 举报索引
 */
public class PassIndexModel {

    private PassIndexActivity context;

    PassIndexModel(PassIndexActivity context) {
        this.context = context;
    }

    public void onLinePassClick() {
        callPhone();
    }

    public void onMaterialPassClick() {
        if(SPUtils.get().isLogin()){
            context.startActivity(new Intent(context,PassActivity.class));
            context.finish();
        }else{
            //提示登录
            ToastUtils.showShortToast(context,"请先登录");
            context.startActivity(new Intent(context,LoginActivity.class));
        }
    }

    void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel://09312369666"));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.requestPermissions(new String[]{Manifest.permission.CALL_PHONE},100);
            }
            return;
        }
        context.startActivity(intent);
    }
}
