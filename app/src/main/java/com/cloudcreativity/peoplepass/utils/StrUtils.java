package com.cloudcreativity.peoplepass.utils;

import android.annotation.SuppressLint;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 这是自己定义的字符串处理工具
 */
public class StrUtils {
    /**
     *
     * @param number 数字
     * @return 在前面补零返回数据
     */
    public static String addZeroFormat(int number){
        if(number<10){
            return "0"+number;
        }
        return String.valueOf(number);
    }

    /**
     *
     * @param phone 电话号码
     * @return 是否
     */
    public static boolean isPhone(String phone){
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     *
     * @param email 邮箱
     * @return 是否
     */
    public static boolean isEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     *
     * @param num 身份证号
     * @return 是否
     */
    public static boolean isIDCard(String num) {
        String reg = "^\\d{17}[0-9Xx]$";
        boolean matches = num.matches(reg);
        boolean verify = verify(num.toCharArray());
        return matches&verify;//别问我为什么，这是
    }

    //身份证最后一位的校验算法
    private static boolean verify(char[] id) {
        int sum = 0;
        int w[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
        char[] ch = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
        for (int i = 0; i < id.length - 1; i++) {
            sum += (id[i] - '0') * w[i];
        }
        int c = sum % 11;
        char code = ch[c];
        char last = id[id.length-1];
        last = last == 'x' ? 'X' : last;
        return last == code;
    }

    //获取两位小数，四舍五入
    public static float get2BitDecimal(float number){
        BigDecimal b   =   new   BigDecimal(number);
        return b.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     *
     * @param datetime 日期串
     * @return boolean 是否在7天之内
     */
    public static boolean isEnoughSevenDay(String datetime) {

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date lastDate = dateFormat.parse(datetime);
            long day = (System.currentTimeMillis()-lastDate.getTime())/(24*3600*1000);
            return day <= 7;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param money 浮点数的钱
     * @return 分的整数
     */
    public static int yuan2FenInt(double money){
        BigDecimal fenBd = new BigDecimal(money).multiply(new BigDecimal(100));
        fenBd = fenBd.setScale(0, BigDecimal.ROUND_HALF_UP);
        return fenBd.intValue();
    }

    /**
     *
     * @param number 数字
     * @return 格式化后的数字
     */
    public static String formatNumberByThousands(int number) {
        return number>=9999?"10000+":String.valueOf(number);
    }
}
