package com.cloudcreativity.peoplepass.utils;

import android.text.TextUtils;

import com.cloudcreativity.peoplepass.base.BaseDialogImpl;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

public abstract class DefaultObserver<T> implements Observer<T> {
    private BaseDialogImpl impl;
    protected DefaultObserver(BaseDialogImpl impl ,boolean isShowProgress){
        this.impl = impl;
        if(isShowProgress)
            impl.showProgress("请稍后");
    }

    @Override
    public void onSubscribe(Disposable d) {
        impl.addRxDestroy(d);
    }

    @Override
    public void onComplete() {
        impl.dismissProgress();
    }

    @Override
    public void onError(Throwable e) {
        if(e != null)
            e.printStackTrace();
        impl.dismissProgress();
        if(e instanceof HttpException){
            impl.showRequestErrorMessage("网络异常");
            onFail(ExceptionReason.BAD_NETWORK);
        }else if(e instanceof ConnectException||e instanceof UnknownHostException||e instanceof NoRouteToHostException){
            impl.showRequestErrorMessage("网络异常");
            onFail(ExceptionReason.CONNECT_ERROR);
        }else if(e instanceof InterruptedException||e instanceof SocketTimeoutException){
            impl.showRequestErrorMessage("网络连接超时");
            onFail(ExceptionReason.CONNECT_TIMEOUT);
        }else if(e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException){
            onFail(ExceptionReason.PARSE_ERROR);
        }else{
            onFail(ExceptionReason.UNKNOWN_ERROR);
        }
    }
    @Override
    public void onNext(T t) {
        impl.dismissProgress();
        if(t instanceof String){
            try {
                JSONObject object = new JSONObject((String)t);
                if(object.getString("status").equals("1")){
                    //成功
                    if(TextUtils.isEmpty(object.getString("info"))){
                        //说明当前的字符串是空的,模拟出空的json串
                        onSuccess("{}");
                    }else{
                        try{
                            JSONObject info = object.getJSONObject("info");
                            onSuccess(info.toString());
                        }catch (JSONException e){
                            //说明info不是json对象
                            onSuccess(object.getString("info"));
                        }
                    }
                }else if(object.getString("status").equals("100")){
                    //用户权限出问题,账号在别处登录
                    //先清空用户数据
                    SPUtils.get().putBoolean(SPUtils.Config.IS_LOGIN,false);
                    SPUtils.get().putInt(SPUtils.Config.UID,0);
                    SPUtils.get().putString(SPUtils.Config.TOKEN,null);
                    onFail(ExceptionReason.PARAMS_ERROR);
                    impl.showUserAuthOutDialog();
                }else{
                    //失败
                    if(!object.has("msg")||TextUtils.isEmpty(object.getString("msg"))){
                        impl.showRequestErrorMessage("请求失败");
                    }else{
                        impl.showRequestErrorMessage(object.getString("msg"));
                    }
                    onFail(ExceptionReason.PARAMS_ERROR);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                impl.showRequestErrorMessage("数据解析异常");
                onFail(ExceptionReason.PARSE_ERROR);
            }
        }else{
            //烂逼接口，数据无法用框架解析，需要进行自己解析
            onSuccess("{}");
        }
    }
    public abstract void onSuccess(String t);

    public abstract void onFail(ExceptionReason msg);

    /**
     * 请求网络失败原因
     */
    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,
        /**
         * 参数错误
         */
        PARAMS_ERROR
    }
}
