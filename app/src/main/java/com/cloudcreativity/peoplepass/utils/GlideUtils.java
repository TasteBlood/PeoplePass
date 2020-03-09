package com.cloudcreativity.peoplepass.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cloudcreativity.peoplepass.R;

import java.io.File;

/**
 * Glide加载图片的自定义
 */
public class GlideUtils {
    /**
     *
     * @param context 上下文
     * @param url 网络图片或者本地文件
     * @param imageView 目标控件
     */
    public static void loadCircle(Context context, String url, final ImageView imageView){
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image_black_24dp)
                        .error(R.drawable.ic_image_black_24dp)
                        .transform(new GlideCircleTransform()))
                .into(imageView);
    }

    /**
     *
     * @param context 上下文
     * @param resource 本地的资源id
     * @param imageView 目标控件
     */
    public static void loadCircle(Context context,  int resource, final ImageView imageView){
        Glide.with(context)
                .load(resource)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image_black_24dp)
                        .error(R.drawable.ic_image_black_24dp)
                        .transform(new GlideCircleTransform()))
                .into(imageView);
    }

    /**
     *
     * @param context 上下文
     * @param file 文件
     * @param imageView 目标控件
     *                  加载缩略图
     */
    public static void loadFileThumbs(Context context, File file, final ImageView imageView){
        Glide.with(context)
                .load(file)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image_black_24dp)
                        .error(R.drawable.ic_image_black_24dp))
                .thumbnail(0.2f)
                .into(imageView);
    }

    /**
     *
     * @param context 上下文
     * @param url 图片路径
     * @param imageView 目标控件
     */
    public static void loadThumbs(Context context, String url, ImageView imageView){
        Glide.with(context)
                .load(url)
                .thumbnail(0.2f)
                .into(imageView);
    }
    /**
     *
     * @param context 上下文
     * @param url 路径
     * @param imageView 目标控件
     */
    public static void load(Context context, String url, final ImageView imageView){
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image_black_24dp)
                        .error(R.drawable.ic_image_black_24dp))
                .into(imageView);
    }

    /**
     *
     * @param context 上下文
     * @param file 本地文件
     * @param imageView 目标控件
     */
    public static void load(Context context, File file, ImageView imageView){
        Glide.with(context)
                .load(file)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image_black_24dp)
                        .error(R.drawable.ic_image_black_24dp))
                .into(imageView);
    }

    /**
     *
     * @param context 上下文
     * @param resource 本地的资源id
     * @param imageView 目标控件
     */
    public static void load(Context context, int resource, final ImageView imageView){
        Glide.with(context)
                .load(resource)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image_black_24dp)
                        .error(R.drawable.ic_image_black_24dp))
                .into(imageView);
    }


    /**
     *
     * @param context 上下文
     * @param file 本地文件
     * @param imageView 目标控件
     */
    public static void loadCircle(Context context, File file, ImageView imageView){
        Glide.with(context)
                .load(file)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_image_black_24dp)
                        .error(R.drawable.ic_image_black_24dp)
                        .transform(new GlideCircleTransform()))
                .into(imageView);
    }
}
