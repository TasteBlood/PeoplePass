package com.cloudcreativity.peoplepass.base;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;


public class BaseApp extends Application {

    public static BaseApp app;
    private List<Activity> activities = new ArrayList<>();
    public void addActivity(Activity activity){
        this.activities.add(activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        //初始化百度地图
        SDKInitializer.initialize(app);
        SDKInitializer.setCoordType(CoordType.GCJ02);
        initRefreshLayout();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void destroyActivity(){
        //结束所有的Activity
        for(Activity activity:activities){
            if(activity!=null&&!activity.isDestroyed())
                activity.finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onTerminate() {
        super.onTerminate();
        destroyActivity();
        //onDestroy
        System.exit(0);
    }

    //初始化刷新加载布局
    private void initRefreshLayout(){
        TwinklingRefreshLayout.setDefaultHeader("com.cloudcreativity.peoplepass.view.progresslayout.ProgressLayout");
        TwinklingRefreshLayout.setDefaultFooter("com.lcodecore.tkrefreshlayout.footer.LoadingView");
    }
}
