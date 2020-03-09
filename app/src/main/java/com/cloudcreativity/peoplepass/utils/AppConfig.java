package com.cloudcreativity.peoplepass.utils;

/**
 * 这个app的属性配置
 */
public interface AppConfig {
    /**
     * 这是APP类型  0 市  1  区
     */
     int APP_TYPE = 0;

    /**
     * 这是APP范围地区号
     */
     int APP_AREA_CODE = 27;

     String CACHE_LUBAN = "luban_disk_cache";
    /**
     * 是否是开发调试阶段
     */
     boolean DEBUG = true;
    /**
     * 网络数据缓存的文件夹名称
     */
     String CACHE_FILE_NAME = "app_cache";
    /**
     * 网络图片缓存的文件夹名称
     */
     String CACHE_IMAGE_NAME = "app_image_cache";

    /**
     * 这是APP热更新的下载缓存目录
     */
     String APP_HOT_UPDATE_FILE = "pass_app_hot_update.apk";

    /**
     * 这是SharePreference的名称
     */
     String SP_NAME = "people_pass_app_config";

    /**
     * 这是统一的文件名
     */
     String FILE_NAME = "people_pass_file_%d.%s";

}
