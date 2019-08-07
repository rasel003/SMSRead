package com.rasel.smsread;

import com.google.gson.annotations.SerializedName;

public class ForgetPasswordResponse {

    @SerializedName("user")
    private String user;

    @SerializedName("details")
    private String details;

    public void setUser(String user) {
        this.user = user;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUser() {
        return user;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "ForgetPasswordResponse{" +
                "user ='" + user + '\'' +
                ", details ='" + details + '\'' +
                '}';
    }
}
