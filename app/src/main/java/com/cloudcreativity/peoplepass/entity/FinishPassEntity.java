package com.cloudcreativity.peoplepass.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class FinishPassEntity implements Parcelable {
    private int id;
    private String replyContent;
    private String replyTime;
    private PassEntity reportPojo;

    private FinishPassEntity(Parcel in) {
        id = in.readInt();
        replyContent = in.readString();
        replyTime = in.readString();
        reportPojo = in.readParcelable(PassEntity.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(replyContent);
        dest.writeString(replyTime);
        dest.writeParcelable(reportPojo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FinishPassEntity> CREATOR = new Creator<FinishPassEntity>() {
        @Override
        public FinishPassEntity createFromParcel(Parcel in) {
            return new FinishPassEntity(in);
        }

        @Override
        public FinishPassEntity[] newArray(int size) {
            return new FinishPassEntity[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replayContent) {
        this.replyContent = replayContent;
    }

    public PassEntity getReportPojo() {
        return reportPojo;
    }

    public void setReportPojo(PassEntity reportPojo) {
        this.reportPojo = reportPojo;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String formatTime(){
        return this.replyTime.substring(0,this.replyTime.length()-2);
    }
}
