package com.example.e_commerce_app.models;

import java.util.Date;
import java.util.List;

public class UserInfo {
    private String uid;
    private String displayName;
    private Date createDate;
    private String phone;
    private String email;
    private List<String> CCCDImg;
    private String avatarLink;
    private String status;

    public UserInfo() {
    }

    public UserInfo(String uid, String displayName, Date createDate, String phone,
                    String email, List<String> CCCDImg, String avatarLink, String status) {
        this.uid = uid;
        this.displayName = displayName;
        this.createDate = createDate;
        this.phone = phone;
        this.email = email;
        this.CCCDImg = CCCDImg;
        this.avatarLink = avatarLink;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getCCCDImg() {
        return CCCDImg;
    }

    public void setCCCDImg(List<String> CCCDImg) {
        this.CCCDImg = CCCDImg;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
