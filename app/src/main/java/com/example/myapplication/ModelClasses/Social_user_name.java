package com.example.myapplication.ModelClasses;

public class Social_user_name {
    private String image_user_list_profile;
    private String text_user_list_nickname;
    private String friendid;
    Boolean followerStatus;
    String friendStatus;

    public Boolean getFollowerStatus() {
        return followerStatus;
    }

    public void setFollowerStatus(Boolean followerStatus) {
        this.followerStatus = followerStatus;
    }

    public String getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }

    public String getFriendid() {
        return friendid;
    }

    public void setFriendid(String friendid) {
        this.friendid = friendid;
    }

    public Social_user_name(String image_user_list_profile, String text_user_list_nickname,String friendid,Boolean followerStatus,String friendStatus) {
        this.image_user_list_profile = image_user_list_profile;
        this.text_user_list_nickname = text_user_list_nickname;
        this.friendid = friendid;
        this.followerStatus=followerStatus;
        this.friendStatus=friendStatus;
    }

    public String getImage_user_list_profile() {
        return image_user_list_profile;
    }

    public void setImage_user_list_profile(String image_user_list_profile) {
        this.image_user_list_profile = image_user_list_profile;
    }

    public String getText_user_list_nickname() {
        return text_user_list_nickname;
    }

    public void setText_user_list_nickname(String text_user_list_nickname) {
        this.text_user_list_nickname = text_user_list_nickname;
    }
}
