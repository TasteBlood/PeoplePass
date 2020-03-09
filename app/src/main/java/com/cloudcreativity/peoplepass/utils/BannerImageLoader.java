package com.cloudcreativity.peoplepass.utils;

import android.content.Context;
import android.widget.ImageView;

import com.youth.banner.loader.ImageLoader;

/**
 * banner加载图片的loader
 */
public class BannerImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        GlideUtils.load(context,String.valueOf(path),imageView);
    }
}
