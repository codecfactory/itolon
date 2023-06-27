
package com.example.myapplication.model.signupmodell;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("email")
    @Expose
    private Object email;
    @SerializedName("name")
    @Expose
    private Object name;
    @SerializedName("verified")
    @Expose
    private Object verified;
    @SerializedName("auth_code")
    @Expose
    private String authCode;
    @SerializedName("phone_verified_at")
    @Expose
    private Object phoneVerifiedAt;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Object getEmail() {
        return email;
    }

    public void setEmail(Object email) {
        this.email = email;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getVerified() {
        return verified;
    }

    public void setVerified(Object verified) {
        this.verified = verified;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public Object getPhoneVerifiedAt() {
        return phoneVerifiedAt;
    }

    public void setPhoneVerifiedAt(Object phoneVerifiedAt) {
        this.phoneVerifiedAt = phoneVerifiedAt;
    }

}
