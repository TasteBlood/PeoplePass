package com.cloudcreativity.peoplepass.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.utils.SPUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class IndexActivity extends AppCompatActivity {

    private String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_index);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED){
            start();
        }else{
            ActivityCompat.requestPermissions(this,permission,100);
        }

    }

    private void start(){
        View index = findViewById(R.id.iv_index);
        Animation animation = new ScaleAnimation(1.0f,1.1f,1.0f,1.1f,
                ScaleAnimation.RELATIVE_TO_SELF,0.5f,ScaleAnimation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(2500);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(SPUtils.get().isLogin()){
                    //跳转到主页
                    startActivity(new Intent().setClass(IndexActivity.this,MainActivity.class));
                }else{
                    //跳转到登录
                    startActivity(new Intent().setClass(IndexActivity.this,MainActivity.class));
                }
                onBackPressed();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        index.startAnimation(animation);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==100){
            if((Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[0])&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    &&(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[1])&&grantResults[1]==PackageManager.PERMISSION_GRANTED)
                    &&(Manifest.permission.RECORD_AUDIO.equals(permissions[2])&&grantResults[2]==PackageManager.PERMISSION_GRANTED)){
                start();
            }else{
                onBackPressed();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
