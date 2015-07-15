package com.example.ja.diudiu.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by JA on 2015/7/14.
 */
public class Found extends BmobObject{
    private String title;
    private String describe;
    private String phone;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
