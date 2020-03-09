package com.cloudcreativity.peoplepass.utils;


import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;

/**
 * glide 加载图片库的配置
 */
@com.bumptech.glide.annotation.GlideModule
public class GlideModuleConfig implements GlideModule {

    private int memorySize = (int) (Runtime.getRuntime().maxMemory()/8);//ROM缓存总内存的1/8

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //定义缓存大小和位置
        int diskSize = 1024*1024*1024;
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context,diskSize));//RAM中，不是ROM
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context,AppConfig.CACHE_IMAGE_NAME,diskSize));

//        //默认ROM和图片池大小
//        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
//        int defaultMemoryCahceSize = calculator.getMemoryCacheSize();
//        int bitmapPoolSize = calculator.getBitmapPoolSize();

        //自定义ROM和图片池大小
        builder.setMemoryCache(new LruResourceCache(memorySize));
        builder.setBitmapPool(new LruBitmapPool(memorySize));


    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {

    }
}
