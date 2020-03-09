package com.cloudcreativity.peoplepass.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class PassEntity implements Parcelable {

        private int areaId;
        private int cityId;
        private String content;
        private String createTime;
        private String detailAddress;
        private int id;
        private int provinceId;
        private String reImgs;
        private int state;
        private int streetId;
        private String title;
        private int type;
        private String updateTime;
        private long userId;
        private String voiceFile;

    protected PassEntity(Parcel in) {
        areaId = in.readInt();
        cityId = in.readInt();
        content = in.readString();
        createTime = in.readString();
        detailAddress = in.readString();
        id = in.readInt();
        provinceId = in.readInt();
        reImgs = in.readString();
        state = in.readInt();
        streetId = in.readInt();
        title = in.readString();
        type = in.readInt();
        updateTime = in.readString();
        userId = in.readLong();
        voiceFile = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(areaId);
        dest.writeInt(cityId);
        dest.writeString(content);
        dest.writeString(createTime);
        dest.writeString(detailAddress);
        dest.writeInt(id);
        dest.writeInt(provinceId);
        dest.writeString(reImgs);
        dest.writeInt(state);
        dest.writeInt(streetId);
        dest.writeString(title);
        dest.writeInt(type);
        dest.writeString(updateTime);
        dest.writeLong(userId);
        dest.writeString(voiceFile);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PassEntity> CREATOR = new Creator<PassEntity>() {
        @Override
        public PassEntity createFromParcel(Parcel in) {
            return new PassEntity(in);
        }

        @Override
        public PassEntity[] newArray(int size) {
            return new PassEntity[size];
        }
    };

    public void setAreaId(int areaId) {
            this.areaId = areaId;
        }
        public int getAreaId() {
            return areaId;
        }

        public void setCityId(int cityId) {
            this.cityId = cityId;
        }
        public int getCityId() {
            return cityId;
        }

        public void setContent(String content) {
            this.content = content;
        }
        public String getContent() {
            return content;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
        public String getCreateTime() {
            return createTime;
        }

        public void setDetailAddress(String detailAddress) {
            this.detailAddress = detailAddress;
        }
        public String getDetailAddress() {
            return detailAddress;
        }

        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setProvinceId(int provinceId) {
            this.provinceId = provinceId;
        }
        public int getProvinceId() {
            return provinceId;
        }

        public void setReImgs(String reImgs) {
            this.reImgs = reImgs;
        }
        public String getReImgs() {
            return reImgs;
        }

        public void setState(int state) {
            this.state = state;
        }
        public int getState() {
            return state;
        }

        public void setStreetId(int streetId) {
            this.streetId = streetId;
        }
        public int getStreetId() {
            return streetId;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setType(int type) {
            this.type = type;
        }
        public int getType() {
            return type;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
        public String getUpdateTime() {
            return updateTime;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }
        public long getUserId() {
            return userId;
        }

        public void setVoiceFile(String voiceFile) {
            this.voiceFile = voiceFile;
        }
        public String getVoiceFile() {
            return voiceFile;
        }

        public String formatTime(){
            return this.createTime.substring(0,this.createTime.length()-2);
        }
}
