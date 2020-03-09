package com.cloudcreativity.peoplepass.utils;

import java.text.DecimalFormat;

/**
 * 数字格式工具
 */
public class NumberUtils {
    /**
     *
     * @param number 数字
     * @return 一位小数
     */
    public static double m1(double number){
        DecimalFormat df = new DecimalFormat("#.0");
        return Double.parseDouble(df.format(number));
    }

    /**
     *
     * @param number 数字
     * @return 二位小数
     */
    public static double m2(double number){
        DecimalFormat df = new DecimalFormat("#.00");
        return Double.parseDouble(df.format(number));
    }

    /**
     *
     * @param number 数字
     * @return 三位小数
     */
    public static double m3(double number){
        DecimalFormat df = new DecimalFormat("#.000");
        return Double.parseDouble(df.format(number));
    }
}
