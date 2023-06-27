package com.example.myapplication.model.postsmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Comment implements Serializable {

    @SerializedName("comment_id")
    @Expose
    private Integer commentId;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("parent_id")
    @Expose
    private Integer parentId;
    @SerializedName("comment_by")
    @Expose
    private Integer commentBy;
    @SerializedName("user_info")
    @Expose
    private UserInfo userInfo;

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getCommentBy() {
        return commentBy;
    }

    public void setCommentBy(Integer commentBy) {
        this.commentBy = commentBy;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

}