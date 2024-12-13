package edu.ace.infinite.pojo;

import java.io.Serializable;

public class LoginInformation implements Serializable {
    private String avatarUrl; //头像地址
    private String username; //用户名
    private String nickname; //昵称
    private String sex = "男"; //性别
    private long birthday = 0; //生日
    private String district; //地点
    private String introduce; //用户介绍
    private long createTime; //创建时间



    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }


    public void clear(){
        username = null;
        nickname = null;
        sex = "男";
        district = null;
        introduce = null;
        avatarUrl = null;
        birthday = 0;
    }

    @Override
    public String toString() {
        return '{' +
                ",\"username\":\"" + username + '\"' +
                ",\"nickname\":\"" + nickname + '\"' +
                ",\"sex\":\"" + sex + '\"' +
                ",\"birthday\":" + birthday +
                ",\"district\":\"" + district + '\"' +
                ",\"introduce\":\"" + introduce + '\"' +
                ",\"avatarUrl\":\"" + avatarUrl + '\"' +
                '}';
    }
}