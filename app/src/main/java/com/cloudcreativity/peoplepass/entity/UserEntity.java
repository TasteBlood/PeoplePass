package com.cloudcreativity.peoplepass.entity;

/**
 * 这是用户数据
 */
public class UserEntity{
    private String birthDay;
    private String createTime;
    private String email;
    private String headPic;
    private int id;
    private String idCard;
    private String loginTime;
    private String userMobile;
    private String password;
    private String rank;
    private String realName;
    private String regDate;
    private int userSex;
    private String token;
    private int type;
    private String updateTime;
    private String userName;
    private String balance;
    private int isBind;//是否绑定微信
    private String areaId;
    private String areaName;
    private String cityId;
    private String cityName;
    private String provinceId;
    private String provinceName;
    private String storeName;//店铺名称

    private int returnOrderNum;//这是退换货的数量
    private int unOrderNum;//这是待收货的数量

    private WXUser wxOfficial;//这是微信信息

    private int isFinance;//金融是否有新消息
    private int isNews;//新闻是否有新消息
    private int isNotice;//公告是否有新消息
    private int isRecruit;//招聘是否有新消息


    public int getIsFinance() {
        return isFinance;
    }

    public void setIsFinance(int isFinance) {
        this.isFinance = isFinance;
    }

    public int getIsNews() {
        return isNews;
    }

    public void setIsNews(int isNews) {
        this.isNews = isNews;
    }

    public int getIsNotice() {
        return isNotice;
    }

    public void setIsNotice(int isNotice) {
        this.isNotice = isNotice;
    }

    public int getIsRecruit() {
        return isRecruit;
    }

    public void setIsRecruit(int isRecruit) {
        this.isRecruit = isRecruit;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public WXUser getWxOfficial() {
        return wxOfficial;
    }

    public void setWxOfficial(WXUser wxOfficial) {
        this.wxOfficial = wxOfficial;
    }

    public int getIsBind() {
        return isBind;
    }

    public void setIsBind(int isBind) {
        this.isBind = isBind;
    }

    public int getReturnOrderNum() {
        return returnOrderNum;
    }

    public void setReturnOrderNum(int returnOrderNum) {
        this.returnOrderNum = returnOrderNum;
    }

    public int getUnOrderNum() {
        return unOrderNum;
    }

    public void setUnOrderNum(int unOrderNum) {
        this.unOrderNum = unOrderNum;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 这是微信用户信息
     */
    public static class WXUser{
        private String headImgUrl;
        private String nickName;
        private String openId;

        public String getHeadImgUrl() {
            return headImgUrl;
        }

        public void setHeadImgUrl(String headImgUrl) {
            this.headImgUrl = headImgUrl;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }
    }
}
