package com.unionbigdata.takepicbuy.model;

/**
 * 用户信息数据结构
 * Created by Christain on 15/4/19.
 */
public class UserInfoModel {

    private String userId;
    private String user_photo;
    private int sex;
    private String name;

    public String getUserId() {
        return (userId != null ? userId : "");
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUser_photo() {
        return (user_photo != null ? user_photo : "");
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getName() {
        return (name != null ? name : "");
    }

    public void setName(String name) {
        this.name = name;
    }
}
