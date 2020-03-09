package com.cloudcreativity.peoplepass.utils;

import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.cloudcreativity.peoplepass.base.BaseApp;

import java.io.File;

/**
 * 缓存工具
 */
public class CacheUtils {

    public static String getTotalCacheSize(Context context) {
        //先获取网络请求的缓存数据
        File cacheFile = new File(BaseApp.app.getCacheDir(), AppConfig.CACHE_FILE_NAME);
        //获取图片缓存的数据
        File cacheDir = Glide.getPhotoCacheDir(context);
        File cacheDir1 = Glide.getPhotoCacheDir(context, AppConfig.CACHE_IMAGE_NAME);
        File luBanCache = new File(BaseApp.app.getCacheDir(), AppConfig.CACHE_LUBAN);
        //录音缓存文件的大小
        File musicCache = new File(Environment.getExternalStorageDirectory(),"record");
        double cacheSize = getFolderSize(cacheFile) +
                (cacheDir == null ? 0 : getFolderSize(cacheDir)) +
                (cacheDir1 == null ? 0 : getFolderSize(cacheDir1))
                +getFolderSize(luBanCache)
                +getFolderSize(musicCache);

        if (cacheSize / 1024 / 1024 < 1) {
            return NumberUtils.m2(cacheSize / 1024) + "KB";
        } else if (cacheSize / 1024 / 1024 >= 1 && cacheSize / 1024 / 1024 < 1024) {
            return NumberUtils.m2(cacheSize / 1024 / 1024) + "MB";
        } else {
            return NumberUtils.m2(cacheSize / 1024 / 1024 / 1024) + "GB";
        }
    }

    public static void clearCache(Context context) {
        //先获取网络请求的缓存数据
        File cacheFile = new File(BaseApp.app.getCacheDir(), AppConfig.CACHE_FILE_NAME);
        //获取图片缓存的数据
        File cacheDir = Glide.getPhotoCacheDir(context);
        File cacheDir1 = Glide.getPhotoCacheDir(context, AppConfig.CACHE_IMAGE_NAME);
        File lubanCache = new File(BaseApp.app.getCacheDir(), AppConfig.CACHE_LUBAN);
        File musicCache = new File(Environment.getExternalStorageDirectory(),"record");
        deleteFile(cacheFile);
        if (cacheDir != null)
            deleteFile(cacheFile);
        if (cacheDir1 != null)
            deleteFile(cacheDir1);

        deleteFile(lubanCache);
        deleteFile(musicCache);
    }

    /**
     * @param file 要删除的文件和文件夹
     *             采用递归的方式进行删除
     */
    private static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
        } else {
            if(file.isDirectory()&&file.length()<=0){
                file.delete();
            }else{
                File[] files = file.listFiles();
                if(files!=null&&files.length>0){
                    for (File file1 : files) {
                        if (file1.isFile()) {
                            file1.delete();
                        } else {
                            deleteFile(file1);
                        }
                    }
                }

            }
        }
    }
    /**
     * 2      * 获取文件夹大小
     * 3      * @param file File实例
     * 4      * @return long
     * 5
     */
    private static long getFolderSize(File file) {

        long size = 0;
        try {
            if(file.isDirectory()&&file.length()<=0){
                size += 0;
            }else{
                File[] fileList = file.listFiles();
                if(fileList!=null&&fileList.length>0){
                    for (File file1 : fileList) {
                        if (file1.isDirectory()&&file1.length()>0) {
                            size = size + getFolderSize(file1);
                        } else {
                            size = size + file1.length();
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
}
