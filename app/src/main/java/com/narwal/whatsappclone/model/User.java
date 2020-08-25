package com.narwal.whatsappclone.model;

public class User {
    String name;
    String imgUrl;
    String thumbImgUrl;
    String deviceToken;
    String status;
    String onlineStatus;
    String uid;

    public User() {
    }

    public User(String name, String imgUrl, String thumbImgUrl, String uid) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.thumbImgUrl = thumbImgUrl;
        this.deviceToken = "";
        this.status = "Hey there I am using Whatsapp";
        this.onlineStatus = "";
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getThumbImgUrl() {
        return thumbImgUrl;
    }

    public void setThumbImgUrl(String thumbImgUrl) {
        this.thumbImgUrl = thumbImgUrl;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
