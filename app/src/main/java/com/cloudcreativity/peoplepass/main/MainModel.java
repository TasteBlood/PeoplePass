package com.cloudcreativity.peoplepass.main;

import android.content.Intent;

import com.cloudcreativity.peoplepass.base.BaseDialogImpl;
import com.cloudcreativity.peoplepass.base.CommonWebActivity;
import com.cloudcreativity.peoplepass.databinding.ActivityMainBinding;
import com.cloudcreativity.peoplepass.entity.BannerEntity;
import com.cloudcreativity.peoplepass.entity.NewsEntityWrapper;
import com.cloudcreativity.peoplepass.pass.PassIndexActivity;
import com.cloudcreativity.peoplepass.utils.APIService;
import com.cloudcreativity.peoplepass.utils.AppConfig;
import com.cloudcreativity.peoplepass.utils.BannerImageLoader;
import com.cloudcreativity.peoplepass.utils.DefaultObserver;
import com.cloudcreativity.peoplepass.utils.HttpUtils;
import com.cloudcreativity.peoplepass.utils.UpdateManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainModel {
    private ActivityMainBinding binding;
    private MainActivity context;
    private BaseDialogImpl baseDialog;

    private List<String> bannerImages = new ArrayList<>();
    private List<String> bannerTitles = new ArrayList<>();

    MainModel(ActivityMainBinding binding, final MainActivity context) {
        this.binding = binding;
        this.context = context;
        this.baseDialog = context;

        binding.refreshMain.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                loadBanner();
                UpdateManager.checkVersion(context,context);
            }
        });
    }

    public void onLawClick(){
        CommonWebActivity.startActivity(context,"法律法规",APIService.HOST_APP+APIService.URL.URL_LAW+"?type="+AppConfig.APP_TYPE+"&citLibId="+AppConfig.APP_AREA_CODE);
    }

    public void onPeopleClick(){
        CommonWebActivity.startActivity(context,"民行工作",APIService.HOST_APP+APIService.URL.URL_CASE+"?type="+AppConfig.APP_TYPE+"&citLibId="+AppConfig.APP_AREA_CODE);
    }

    public void onPublicClick(){
        CommonWebActivity.startActivity(context,"公益诉讼",APIService.HOST_APP+APIService.URL.URL_PUBLIC+"?type="+AppConfig.APP_TYPE+"&citLibId="+AppConfig.APP_AREA_CODE);
    }
    public void onOrganizationClick(){
        CommonWebActivity.startActivity(context,"案件公开","http://www.ajxxgk.jcy.gov.cn/html/index.html");

    }
    public void onClassicCaseClick(){
        CommonWebActivity.startActivity(context,"经典案例",APIService.HOST_APP+APIService.URL.URL_CLASSIC+"?type="+AppConfig.APP_TYPE+"&citLibId="+AppConfig.APP_AREA_CODE);
    }
    public void onPassClick(){
        context.startActivity(new Intent(context,PassIndexActivity.class));
    }

    private void loadBanner(){
        HttpUtils.getInstance().getBanner()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>(baseDialog,false) {
                    @Override
                    public void onSuccess(String t) {
                        binding.refreshMain.finishRefreshing();
                        try{
                            Type type = new TypeToken<List<BannerEntity>>() {
                            }.getType();
                            bannerTitles.clear();
                            bannerImages.clear();
                            final List<BannerEntity> list = new Gson().fromJson(t, type);
                            for(BannerEntity entity:list){
                                bannerTitles.add(entity.getTitle());
                                bannerImages.add(entity.getPicUrl());
                            }
                            binding.bannerMain.setImageLoader(new BannerImageLoader())
                                    .setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE)
                                    .setBannerAnimation(Transformer.FlipHorizontal)
                                    .setImages(bannerImages)
                                    .setBannerTitles(bannerTitles)
                                    .setIndicatorGravity(BannerConfig.RIGHT)
                                    .start();

                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onFail(ExceptionReason msg) {
                        binding.refreshMain.finishRefreshing();
                    }
                });
    }
}
