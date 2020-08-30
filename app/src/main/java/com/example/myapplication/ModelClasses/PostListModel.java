package com.example.myapplication.ModelClasses;

public class PostListModel {
    private String content;
    private String mediaimage;
    private String mediatype;
    private String post_deslikes;
    private String post_hide;
    private String post_id;
    private String post_likes;
    private String post_profileimg;
    private String post_title;
    private String posted_byuserid;
    private String posted_date;
    private String postedby;
    private String postedby_name;
    private String you_liked;
    private String you_unliked;
    private String thumbnils;
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getThumbnils() {
        return thumbnils;
    }

    public void setThumbnils(String thumbnils) {
        this.thumbnils = thumbnils;
    }

    public String getMediaVideo() {
        return mediaVideo;
    }

    public void setMediaVideo(String mediaVideo) {
        this.mediaVideo = mediaVideo;
    }

    private String mediaVideo;


    public PostListModel(String comment_post2,String mediaVideo,String post_id2, String post_title2, String content2, String postedby2, String postedby_name2, String post_likes2, String posted_date2, String post_profileimg2, String you_liked2, String mediaimage2, String mediatype2, String post_deslikes2, String post_hide2, String posted_byuserid2, String you_unliked2,String thumbnils) {
        this.post_id = post_id2;
        this.post_title = post_title2;
        this.content = content2;
        this.postedby = postedby2;
        this.comment= comment_post2;
        this.postedby_name = postedby_name2;
        this.post_likes = post_likes2;
        this.posted_date = posted_date2;
        this.post_profileimg = post_profileimg2;
        this.you_liked = you_liked2;
        this.mediaimage = mediaimage2;
        this.mediatype = mediatype2;
        this.post_deslikes = post_deslikes2;
        this.post_hide = post_hide2;
        this.posted_byuserid = posted_byuserid2;
        this.you_unliked = you_unliked2;
        this.mediaVideo=mediaVideo;
        this.thumbnils=thumbnils;
    }

    public String getPosted_byuserid() {
        return this.posted_byuserid;
    }

    public void setPosted_byuserid(String posted_byuserid2) {
        this.posted_byuserid = posted_byuserid2;
    }

    public String getPost_hide() {
        return this.post_hide;
    }

    public void setPost_hide(String post_hide2) {
        this.post_hide = post_hide2;
    }

    public String getPost_deslikes() {
        return this.post_deslikes;
    }

    public void setPost_deslikes(String post_deslikes2) {
        this.post_deslikes = post_deslikes2;
    }

    public String getMediatype() {
        return this.mediatype;
    }

    public void setMediatype(String mediatype2) {
        this.mediatype = mediatype2;
    }

    public String getMediaimage() {
        return this.mediaimage;
    }

    public void setMediaimage(String mediaimage2) {
        this.mediaimage = mediaimage2;
    }

    public String getYou_liked() {
        return this.you_liked;
    }

    public void setYou_liked(String you_liked2) {
        this.you_liked = you_liked2;
    }

    public String getPost_id() {

        return this.post_id;
    }

    public void setPost_id(String post_id2) {
        this.post_id = post_id2;
    }

    public String getPost_title() {
        return this.post_title;
    }

    public void setPost_title(String post_title2) {
        this.post_title = post_title2;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content2) {
        this.content = content2;
    }

    public String getPostedby() {
        return this.postedby;
    }

    public void setPostedby(String postedby2) {
        this.postedby = postedby2;
    }

    public String getPostedby_name() {
        return this.postedby_name;
    }

    public void setPostedby_name(String postedby_name2) {
        this.postedby_name = postedby_name2;
    }

    public String   getPost_likes() {
        return this.post_likes;
    }

    public void setPost_likes(String post_likes2) {
        this.post_likes = post_likes2;
    }

    public String getPosted_date() {
        return this.posted_date;
    }

    public void setPosted_date(String posted_date2) {
        this.posted_date = posted_date2;
    }

    public String getPost_profileimg() {
        return this.post_profileimg;
    }

    public void setPost_profileimg(String post_profileimg2) {
        this.post_profileimg = post_profileimg2;
    }

    public String getYou_unliked() {
        return this.you_unliked;
    }

    public void setYou_unliked(String you_unliked2) {
        this.you_unliked = you_unliked2;
    }
}
