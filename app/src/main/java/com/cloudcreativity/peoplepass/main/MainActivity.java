package com.cloudcreativity.peoplepass.main;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseActivity;
import com.cloudcreativity.peoplepass.databinding.ActivityMainBinding;
import com.cloudcreativity.peoplepass.entity.UserEntity;
import com.cloudcreativity.peoplepass.loginAndRegister.LoginActivity;
import com.cloudcreativity.peoplepass.pass.PassRecordActivity;
import com.cloudcreativity.peoplepass.receiver.MyBusinessReceiver;
import com.cloudcreativity.peoplepass.utils.LogUtils;
import com.cloudcreativity.peoplepass.utils.SPUtils;
import com.cloudcreativity.peoplepass.utils.ToastUtils;
import com.donkingliang.imageselector.utils.ImageSelector;

import java.util.ArrayList;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private MyBusinessReceiver receiver;
    private TextView tv_login;
    private void show1Hide2(final View layout1, final View layout2){
        final Animation anim1 = new AlphaAnimation(1.0f,0.0f);
        anim1.setDuration(800);
        anim1.setFillAfter(false);
        final Animation anim2 = new AlphaAnimation(0f,1.0f);
        anim2.setDuration(800);
        anim2.setFillAfter(false);
        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                layout1.startAnimation(anim2);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        layout2.startAnimation(anim1);
    }

    private void show2Hide1(final View layout1,final View layout2){
        final Animation anim1 = new AlphaAnimation(1.0f,0.0f);
        anim1.setDuration(800);
        anim1.setFillAfter(false);
        final Animation anim2 = new AlphaAnimation(0f,1.0f);
        anim2.setDuration(800);
        anim2.setFillAfter(false);
        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                layout2.setVisibility(View.VISIBLE);
                layout1.setVisibility(View.GONE);
                layout2.startAnimation(anim2);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        layout1.startAnimation(anim1);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取imei
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
            }
        } else {
            // TODO: Consider calling
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SPUtils.get().setIMEI(manager.getImei());
            } else {
                SPUtils.get().setIMEI(manager.getDeviceId());
            }
        }

        //注册广播
        receiver = new MyBusinessReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyBusinessReceiver.ACTION_LOGOUT);
        filter.addAction(MyBusinessReceiver.ACTION_EXIT_APP);
        filter.addAction(MyBusinessReceiver.ACTION_RE_LOGIN);
        registerReceiver(receiver, filter);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMainModel(new MainModel(binding, this));
        setSupportActionBar(binding.tlbMain);
        binding.dwlMain.setStatusBarBackgroundColor(Color.argb(255, 0, 0, 0));
        binding.tlbMain.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dwlMain.openDrawer(Gravity.START, true);
            }
        });

        binding.nvMain.setNavigationItemSelectedListener(this);

        View headerView = binding.nvMain.getHeaderView(0);
        tv_login = headerView.findViewById(R.id.tv_loginOrRegister);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SPUtils.get().isLogin()) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    //跳转到资料页面
                    ToastUtils.showShortToast(MainActivity.this, "资料页面");
                }
            }
        });

        binding.refreshMain.startRefresh();

        //初始化margin
        final View layout1 = findViewById(R.id.layout1);
        final View layout2 = findViewById(R.id.layout2);

        findViewById(R.id.ic_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout1.getVisibility()==View.GONE){
                    show1Hide2(layout1,layout2);
                }else{
                    show2Hide1(layout1,layout2);
                }
            }
        });

        findViewById(R.id.ic_center1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout1.getVisibility()==View.GONE){
                    show1Hide2(layout1,layout2);
                }else{
                    show2Hide1(layout1,layout2);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁广播
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String title = getResources().getString(R.string.str_login_or_register);
        if (SPUtils.get().isLogin()) {
            UserEntity user = SPUtils.get().getUser();
            title = user.getUserName().concat(user.getUserSex() == 1 ? "先生" : "女士");
        }

        tv_login.setText(title);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        binding.dwlMain.closeDrawer(Gravity.START, true);
        switch (item.getItemId()) {
//            case R.id.nav_collect:
//                //测试  打开imageSelector
//                ToastUtils.showShortToast(this,"该功能正在开发中");
//                return true;
            case R.id.nav_settings:
                startActivity(new Intent().setClass(this, SettingActivity.class));
                return true;
            case R.id.nav_report:
                if (SPUtils.get().isLogin()) {
                    //跳转到记录页面
                    startActivity(new Intent(this, PassRecordActivity.class));
                } else {
                    ToastUtils.showShortToast(this, "请先登录账户");
                }
                return true;
//            case R.id.nav_prize:
//                ToastUtils.showShortToast(this,"该功能正在开发中");
//                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && data != null) {
            ArrayList<String> listExtra = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
            for (String str : listExtra) {
                LogUtils.e("xuxiwu", str);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //如果你需要考虑更好的体验，可以这么操作
    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        binding.bannerMain.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        binding.bannerMain.stopAutoPlay();
    }

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            ToastUtils.showShortToast(this, "再按一次退出程序");
            firstTime = secondTime;
        } else {
            sendBroadcast(new Intent(MyBusinessReceiver.ACTION_EXIT_APP));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Manifest.permission.READ_PHONE_STATE.equals(permissions[0])
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            String imei = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = manager.getImei();
            } else {
                imei = manager.getDeviceId();
            }
            SPUtils.get().setIMEI(imei);
        } else {
            ToastUtils.showShortToast(this, "获取imei失败，请确保程序打开读取手机状态权限");
        }
    }
}
