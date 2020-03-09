package com.cloudcreativity.peoplepass.utils;

import android.support.annotation.NonNull;

import com.cloudcreativity.peoplepass.base.BaseApp;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * 自定义的网络请求框架，采用Retrofit+RxJava+OkHttp
 * description:
 * 1: 实现了自定义的错误处理方式。
 * 2：实现了缓存，有网络时在一定的时间内加载缓存，没有网络时直接加载缓存的内容。
 * 3：实现自定义OkHttp，可以在请求时加入统一参数，比如用户token和appKey等。
 */
public class HttpUtils {
    private APIService apiService;

    private HttpUtils(){
        LoggingInterceptor interceptor = new LoggingInterceptor();
        //初始化缓存
        File cacheFile = new File(BaseApp.app.getExternalCacheDir(),AppConfig.CACHE_FILE_NAME);
        Cache cache = new Cache(cacheFile,1024*1024*100);//100M的缓存
        //初始化OkHttp
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(APIService.timeOut, TimeUnit.SECONDS)
                .connectTimeout(APIService.timeOut,TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addNetworkInterceptor(new HttpCacheInterceptor())
                .cache(cache)
                .build();
//        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
        Retrofit retrofit = new Retrofit.Builder().client(client)
//                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(StringConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(APIService.HOST_APP)
                .build();
        apiService = retrofit.create(APIService.class);
    }
    //创建单利模式
    private static class SingleHolder{
        private static final HttpUtils UTILS = new HttpUtils();
    }
    //获取实例
    public static APIService getInstance(){
        return SingleHolder.UTILS.apiService;
    }

    //网络缓存
    class HttpCacheInterceptor implements Interceptor{

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtils.isAvailable()) {  //没网强制从缓存读取
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                LogUtils.d(HttpUtils.this.getClass().getName(), "no network");
            }

            Response originalResponse = chain.proceed(request);
            if (NetworkUtils.isAvailable()) {
                int maxAge = 0;// 有网，1个小时可用
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("Pragma")
                        .build();
            } else {
                int maxStale = 60 * 60 * 6;// 没网 就6小时可用
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    }
}
