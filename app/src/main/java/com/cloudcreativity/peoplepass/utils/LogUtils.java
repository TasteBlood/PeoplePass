package com.cloudcreativity.peoplepass.utils;

import android.util.Log;

/**
 * 日志输出工具
 */
public class LogUtils {
    public static void i(String tag,String message){
        if(AppConfig.DEBUG){
            Log.i(tag,message);
        }
    }
    public static void e(String tag,String message){
        if(AppConfig.DEBUG){
            Log.e(tag,message);
        }
    }
    public static void w(String tag,String message){
        if(AppConfig.DEBUG){
            Log.w(tag,message);
        }
    }
    public static void d(String tag,String message){
        if(AppConfig.DEBUG){
            Log.d(tag,message);
        }
    }
}
