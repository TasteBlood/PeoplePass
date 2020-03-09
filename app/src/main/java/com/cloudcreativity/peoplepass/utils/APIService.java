package com.cloudcreativity.peoplepass.utils;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 整个程序的网络接口配置
 */
public interface APIService {

    interface URL{
        String URL_LAW = "/jcy/lawsAndRegulations/lawsAndRegulations.html";
        String URL_CASE = "/jcy/documentCase/lawsAndRegulations.html";
        String URL_PUBLIC = "/jcy/lawsuit/lawsAndRegulations.html";
        String URL_CLASSIC = "/jcy/classicCase/lawsAndRegulations.html";
        String URL_ORGANIZATION = "/jcy/organization/organization.html";
        String URL_PROTOCOL = "/jcy/userAgreement/particulars.html";
    }

    /**
     * 网络请求的配置
     */
    long timeOut =10;//网络超时
    /**
     * 整体的接口配置
     */
//    String TEST_HOST = "http://192.168.31.158";
    String TEST_HOST = "http://jiancha.yungoux.com";
    String ONLINE_HOST = "http://jiancha.yungoux.com";
//    String HOST = AppConfig.DEBUG ? TEST_HOST : ONLINE_HOST;
    String HOST_APP = AppConfig.DEBUG ? TEST_HOST : ONLINE_HOST;
    String QINIU_HOST = "http://qiniu.yungoux.com/";


    /**
     * 获取七牛token
     */
    @GET("/user/getQiNiuToken")
    Observable<String> getQiNiuToken();

    /**
     * 注册发送验证码
     * @param mobile 手机号
     */
    @POST("/user/sendSms")
    @FormUrlEncoded
    Observable<String> sendSmsByRegister(@Field("userMobile") String mobile);

    /**
     * 忘记密码发送验证码
     * @param mobile 手机号
     */
    @POST("/user/sendSmsForEditPsw")
    @FormUrlEncoded
    Observable<String> sendSmsByForget(@Field("userMobile") String mobile);

    /**
     * 修改密码
     * @param mobile 手机号
     * @param newPwd 新密码
     * @param sms 验证码
     */
    @POST("/user/editPsw")
    @FormUrlEncoded
    Observable<String> editPwd(@Field("userMobile") String mobile,
                               @Field("newPwd") String newPwd,
                               @Field("sms") String sms);

    /**
     * 注册
     * @param mobile 手机号
     * @param pwd 密码
     * @param sms 验证码
     * @param sex  性别 1 男  2  女
     * @param username 姓氏
     */
    @POST("/user/saveUser")
    @FormUrlEncoded
    Observable<String> register(@Field("userMobile") String mobile,
                                @Field("userPasswd") String pwd,
                                @Field("sms") String sms,
                                @Field("userName") String username,
                                @Field("userSex") int sex);

    /**
     *
     * @param mobile 手机号
     * @param pwd 密码
     * 登录
     */
    @POST("/user/login")
    @FormUrlEncoded
    Observable<String> login(@Field("userMobile") String mobile,
                             @Field("userPasswd") String pwd);

    /**
     *
     * @param mobile 手机号
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     */
    @POST("/user/updatePsw")
    @FormUrlEncoded
    Observable<String> modifyPwd(@Field("userMobile") String mobile,
                                 @Field("userPasswd") String oldPwd,
                                 @Field("newPwd") String newPwd);

    /**
     * 获取app版本
     */
    @GET("/user/getMaxVersion")
    Observable<String> getLastVersion();

    /**
     * 退出登录
     */
    @GET("/user/outLogin")
    Observable<String> logout();


    /**
     * 举报接口
     * @param state 状态  0:草稿 1：未处理 2：已处理
     * @param areaId 地区id
     * @param title 标题
     * @param content 内容
     * @param provinceId 省
     * @param cityId 市
     * @param streetId 街道
     * @param address 详细地址
     * @param voice 声音文件
     * @param images 图片文件列表
     */
    @POST("/user/addReport")
    @FormUrlEncoded
    Observable<String> addPass(@Field("imei") String imei,
                               @Field("state") int state,
                               @Field("areaId") int areaId,
                               @Field("title") String title,
                               @Field("content") String content,
                               @Field("provinceId") int provinceId,
                               @Field("cityId") int cityId,
                               @Field("streetId") int streetId,
                               @Field("detailAddress") String address,
                               @Field("voiceFile") String voice,
                               @Field("reImgs") String images);

    /**
     *
     * 修改举报信息
     * @param id  原纪录id
     * @param state 状态  0:草稿 1：未处理 2：已处理
     * @param areaId 地区id
     * @param title 标题
     * @param content 内容
     * @param provinceId 省
     * @param cityId 市
     * @param streetId 街道
     * @param address 详细地址
     * @param voice 声音文件
     * @param images 图片文件列表
     */
    @POST("/user/editReport")
    @FormUrlEncoded
    Observable<String> editPass(@Field("imei") String imei,
                                @Field("id") int id,
                                @Field("state") int state,
                                @Field("areaId") int areaId,
                                @Field("title") String title,
                                @Field("content") String content,
                                @Field("provinceId") int provinceId,
                                @Field("cityId") int cityId,
                                @Field("streetId") int streetId,
                                @Field("detailAddress") String address,
                                @Field("voiceFile") String voice,
                                @Field("reImgs") String images);


    /**
     * 查询举报记录信息
     * @param state 状态 0 草稿 1 未处理 2 已处理
     * @param pageNum 页码
     */
    @GET("/user/getPageReport")
    Observable<String> getPageReport(@Query("state") int state,
                                     @Query("pageNum") int pageNum);

    /**
     * 查询回复完的举报信息
     * @param pageNum 页码
     */
    @GET("/user/getReplyByUserId")
    Observable<String> getFinishReport(@Query("pageNum") int pageNum);

    /**
     * 根据id删除记录
     * @param id 记录Id
     */
    @POST("/user/deleteReportById")
    @FormUrlEncoded
    Observable<String> deletePassById(@Field("id") int id);

    /**
     * 获取banner列表
     */
    @GET("/user/getPageCarousel")
    Observable<String> getBanner();

    /**
     * @param state  状态 常量值 2
     * @param pageNum 页数
     * @param citLib 市区id
     * @param type 类型
     */
    @GET("/user/getPageExample")
    Observable<String> getCases(@Query("state") int state,
                                @Query("pageNum") int pageNum,
                                @Query("citLib") int citLib,
                                @Query("type") int type);
}
