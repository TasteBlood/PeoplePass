package com.cloudcreativity.peoplepass.utils;

/**
 *
 *          这是全部请求返回数据的基类
 */
public class BaseResult{
    private int status;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
